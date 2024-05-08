package com.solum.lidl.consumer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.StaticMessageHeaderAccessor;
import org.springframework.integration.acks.AckUtils;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;

import com.solace.spring.cloud.stream.binder.util.SolaceAcknowledgmentException;
import com.solum.lidl.custom.exception.AimsPortalDownException;
import com.solum.lidl.entity.CounterState;
import com.solum.lidl.entity.queue.SolaceMessageDto;
import com.solum.lidl.entity.solacemessage.SolaceArticleMessage;
import com.solum.lidl.entity.solacemessage.SolaceStatusMessage;
import com.solum.lidl.entity.solacemessage.metadata.BusinessIdentifier;
import com.solum.lidl.entity.solacemessage.metadata.Correlation;
import com.solum.lidl.entity.solacemessage.metadata.MessageIdentifier;
import com.solum.lidl.entity.solacemessage.metadata.MessageSpecification;
import com.solum.lidl.entity.solacemessage.metadata.MetaData;
import com.solum.lidl.entity.solacemessage.metadata.SenderOrReceiver;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeTwo;
import com.solum.lidl.entity.solacemessage.payload.statusmessage.SourceLocation;
import com.solum.lidl.entity.solacemessage.payload.statusmessage.StatusDetails;
import com.solum.lidl.entity.solacemessage.payload.statusmessage.StatusMessage;
import com.solum.lidl.entity.stats.StatsPK;
import com.solum.lidl.metrics.handler.MetricsHandler;
import com.solum.lidl.service.AimsPortalService;
import com.solum.lidl.service.AimsPortalUpdateArticle;
import com.solum.lidl.service.dto.ArticleParam;

//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ConditionalOnProperty(value = "aims.portal.queue.enabled",havingValue = "false")
public class ArticleConsumerSolaceNewImplementation {

	@Autowired
	public StreamBridge streamBridge;

	@Autowired
	private AimsPortalService aimsPortalService;

	@Value("${aims-properties.businessIdentifier.key}")
	private String businessIdentifierKey;

	@Value("${aims-properties.businessIdentifier.value}")
	private String businessIdentifierValue;

	//@Value("${aims-properties.version}")
	//private String version;

	@Value("${aims-properties.statusCode}")
	private String statusCode;

	@Value("${aims-properties.statusType}")
	private String statusType;

	@Value("${aims-properties.sourceHost}")
	private String sourceHost;

	@Value("${aims-properties.port}")
	private String port;

	@Value("${aims-properties.max-storage-size}")
	private int storageSize;

	@Value("${aims-properties.sleep-time-once-storage-reach-max-size}")
	private int sleepTimeOnceStorageReachMaxSize;

	@Value("${aims-properties.batch-size}")
	private int batchSize;
	
    @Value("${aims.import.queue.statistics.enable}")
    private boolean isMetricHandlerEnabled;

	public ConcurrentLinkedQueue<SolaceMessageDto> storage = new ConcurrentLinkedQueue<SolaceMessageDto>();


	@Autowired private MetricsHandler metricHandler;
	@Autowired private PrometheusMeterRegistry registry;



	@Bean
	public Consumer<Message<SolaceArticleMessage>> articleConsumeSolace() {
		return (solaceMessage) -> {
			AcknowledgmentCallback acknowledgmentCallback = StaticMessageHeaderAccessor.getAcknowledgmentCallback(solaceMessage);
			acknowledgmentCallback.noAutoAck();

			SolaceArticleMessage solaceMessagePayload = solaceMessage.getPayload();
			log.trace("Message from solace: {}", solaceMessagePayload);

			SolaceMessageDto solaceMessageDto = new SolaceMessageDto();
			solaceMessageDto.setPayload(solaceMessagePayload);
			solaceMessageDto.setCallback(acknowledgmentCallback);
			storage.add(solaceMessageDto);

			while (storage.size() >= storageSize) {
				try {
					log.debug("Consumer thread is going to sleep for :{} miliseconds as storage size is :{}",
							sleepTimeOnceStorageReachMaxSize, storageSize);
					Thread.sleep(sleepTimeOnceStorageReachMaxSize);
				} catch (InterruptedException e) {
					log.warn("Thread is interrupted while sleeping: {}", e);
				}
			}
		};
	}



	@Scheduled(fixedDelayString = "${aims-properties.sleep-time-for-batch-process-if-storage-is-empty}")
	public void batchProcessing() throws InterruptedException {

		while (!storage.isEmpty()) {
			Map<StatsPK, CounterState> dataMap = new HashMap<>();
			List<SolaceMessageDto> batchList = new ArrayList<>();
			//List<ArticleTypeTwo> articleList = new ArrayList<ArticleTypeTwo>();
			ConcurrentHashMap<String, ArticleTypeTwo> articleList = new ConcurrentHashMap<>();
			List<AcknowledgmentCallback> solaceAcknowledgmentCallbackList = new ArrayList<AcknowledgmentCallback>();
			List<String> idStationCodeList = new ArrayList<String>();

			long startTime = System.currentTimeMillis();		

			// Fetch messages untill batchSize or Timeout 1 Second
			while (batchList.size() < batchSize && (System.currentTimeMillis()-startTime) < 1000 ){
				SolaceMessageDto poll = storage.poll();
				if (poll != null) {
					batchList.add(poll);
				} else {
					Thread.sleep(10);
				}
			}

			// batch processing part
			log.info("Batch size: {} ", batchList.size());

			try {
				processBatchList(dataMap, batchList, articleList, solaceAcknowledgmentCallbackList, idStationCodeList);
			} catch (Exception e) {
				log.warn("Unable to process processBatchList: {}", e);
			}

			try {
				processAimsPortal(dataMap, batchList, articleList, solaceAcknowledgmentCallbackList, idStationCodeList);
			} catch (AimsPortalDownException e) {
				log.error("Aims Portal Down : {}", e);
			} catch (Exception e) {
				log.warn("Unable to process processAimsPortal: {}", e);
			}



			idStationCodeList.clear();
			solaceAcknowledgmentCallbackList.clear();
			articleList.clear();
			batchList.clear();

			log.info("Storage queue size: {} ", storage.size());
		}

		log.debug("Storage is empty, exiting the thread will return shortly");
		return;

	}

	private void processAimsPortal(Map<StatsPK, CounterState> dataMap, List<SolaceMessageDto> batchList, ConcurrentHashMap<String, ArticleTypeTwo> articleList,
			List<AcknowledgmentCallback> solaceAcknowledgmentCallbackList, List<String> idStationCodeList) throws AimsPortalDownException {
		// Creating articleParam for aims-portal (body)
		ArticleParam articleParam = new ArticleParam();
		articleParam.setDataList(new ArrayList<>(articleList.values()));

		if (ObjectUtils.isEmpty(articleParam.getDataList())) {
			log.info("ArticleParam is empty...");
		} else {

			UUID randomUUID = UUID.randomUUID();
			AimsPortalUpdateArticle updateArticles = aimsPortalService.updateArticles(randomUUID, articleParam);
			Integer statusCodeOfApiCall = updateArticles.getStatusCode();
			int count = 0;
			int counter503Exception = 0;
			while (!(statusCodeOfApiCall == 200 || statusCodeOfApiCall == 202)) {
				try {
					count++;
					log.warn("Update {} to portal failed, statusCode: {}, error message: {} in  attempt", randomUUID,statusCodeOfApiCall,
							updateArticles.getErrorMessage(), count);
					log.info("Thread is sleeping for 5 minutes...");
					Thread.sleep(300000);
					updateArticles = aimsPortalService.updateArticles(randomUUID, articleParam);
					statusCodeOfApiCall = updateArticles.getStatusCode();
					if(statusCodeOfApiCall == 503) {
						counter503Exception++;
					}
					if(counter503Exception> 5) {
						throw new AimsPortalDownException("Portal down for long time, hence breaking the infnite loop");
					}

				} catch (InterruptedException e) {
					log.warn("Thread is interrupted while sleeping: {}", e);
				}
			}

			for ( ArticleTypeTwo a : articleParam.getDataList()) {
				registry.counter("aims_article", "result", "updated", "station_code", a.getStationCode()).increment();
				//registry.gauge("aims_article_last_update", Arrays.asList(Tag.of("station_code", a.getStationCode())), Instant.now().toEpochMilli());
			}

	        if (isMetricHandlerEnabled && metricHandler != null) {
	        	metricHandler.updateMetrics(dataMap);
	        }

			for (AcknowledgmentCallback a : solaceAcknowledgmentCallbackList) {
				try {
					AckUtils.accept(a);
				} catch (SolaceAcknowledgmentException e) {
					log.warn("Unable to Acknowledge Message: {}", e);
				}

				//System.out.println("Message acknowldged");
			}

			log.info("Acknowledged message having id: {}", idStationCodeList);

		}
	}




	private void processBatchList(Map<StatsPK, CounterState> dataMap,List<SolaceMessageDto> batchList,ConcurrentHashMap<String, ArticleTypeTwo> articleList,
			List<AcknowledgmentCallback> solaceAcknowledgmentCallbackList,List<String> idStationCodeList) {
		for (SolaceMessageDto solaceMessageDto : batchList) {
			
			ArticleTypeTwo solaceArticle = new ArticleTypeTwo();
			// Get solaceMessage from messageDto
			SolaceArticleMessage solaceMessage = solaceMessageDto.getPayload();
			// Get solacePayload from solaceMessage
			List<ArticleTypeTwo> solacePayload = solaceMessage.getPayload();

			// Check if solacePayload and continue to next
			if (solacePayload.isEmpty()) {
				AckUtils.accept(solaceMessageDto.getCallback());
				continue;
			}
			// Get first solaceArticle from solacePayload
			solaceArticle = solacePayload.get(0);

			// Add stationCode and id to idStationCodeList
			idStationCodeList.add(solaceArticle.getStationCode() + "-" + solaceArticle.getId());

			// Check if solacePayload is of type article
			// Check if stationCode, Id and Name
			if (!ObjectUtils.isEmpty(solaceArticle.getStationCode())
					&& !ObjectUtils.isEmpty(solaceArticle.getId())
					&& !ObjectUtils.isEmpty(solaceArticle.getName())) {

				if (solaceArticle instanceof ArticleTypeTwo) {
					solaceAcknowledgmentCallbackList
							.add(solaceMessageDto.getCallback());

					solacePayload.forEach(payload -> {
						String deduplicationKey = payload.getStationCode() + "-" + payload.getId();

						deduplicateArticle(payload, articleList,deduplicationKey);

						if (isMetricHandlerEnabled && metricHandler != null) {
							StatsUpdate(solaceMessage, payload, dataMap);
						}
					});

				} else {
					AckUtils.accept(solaceMessageDto.getCallback());
					log.warn(
							"Message does not match with any of the pre-defined schema having id: {}",
							solaceArticle);
					sendMessageToErrorQueue(solaceMessage,
							"Message does not match with any of the pre-defined schema");
				}
			} else {
				AckUtils.accept(solaceMessageDto.getCallback());
				log.warn(
						"Message rejected because article.id, article.stationCode or article.name mandatory parameter is either empty or null: {}",
						solaceMessageDto);
				sendMessageToErrorQueue(solaceMessage,
						"Message rejected because article.id, article.stationCode or article.name mandatory parameter is either empty or null");
			}
		}
	}
	
	private void StatsUpdate(SolaceArticleMessage solaceMessage,ArticleTypeTwo payload,Map<StatsPK, CounterState> dataMap) {
		final StatsPK key = new StatsPK(solaceMessage.getMetaData(), payload.getStationCode());
		CounterState val = dataMap.get(key);
		if(val != null) {//if key already exists increment count
			val.process(payload);
		}else {
			val = new CounterState();
			dataMap.put(key,val);
			val.process(payload);
		}
	}
	
	private void deduplicateArticle(ArticleTypeTwo payload,ConcurrentHashMap<String, ArticleTypeTwo> articleList,String deduplicationKey) {
		//ArticleTypeTwo existingArticle = deduplicationMap.get(payload.getStationCode() + "-" + payload.getId());
		articleList.compute(deduplicationKey, (key, existingArticle) -> {
			if (existingArticle == null) {
				return payload;
			} else {
				ArticleTypeTwo mergedArticle = mergeTwoArticle(existingArticle,payload);
				registry.counter("aims_article", "result", "deduplicated","station_code", payload.getStationCode()).increment();
				log.info("Two duplicate articles are merged.");
				return mergedArticle;
			}
		});
	}
		
	
	private ArticleTypeTwo mergeTwoArticle(ArticleTypeTwo articleFirst,ArticleTypeTwo articleSecond) {
		// Merge EANs
		String[] mergedEans = Stream.concat(Arrays.stream(articleFirst.getEans()),Arrays.stream(articleSecond.getEans())).distinct().toArray(String[]::new);
		articleFirst.setEans(mergedEans);

		// Merge Data
		Map<String, String> dataOfFirst = articleFirst.getData();
		Map<String, String> dataOfSecond = articleSecond.getData();

		Map<String, String> mergedData = Stream.concat(dataOfFirst.entrySet().stream(),dataOfSecond.entrySet().stream())
											.filter(entry -> entry.getValue() != null)
											.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(value1, value2) -> value2));
		articleFirst.setData(mergedData);
		return articleFirst;
	}

	
	public void sendMessageToErrorQueue(SolaceArticleMessage solaceArticleMessage, String statusMessageString) {

		// Create SolaceStatusMessage
		SolaceStatusMessage solaceStatusMessage = new SolaceStatusMessage();

		// Create SolaceStatusMessage.MetaData
		MetaData metaData = new MetaData();

		// Create SolaceStatusMessage.StatusMessage as Payload
		StatusMessage statusMessage = new StatusMessage();

		// Setting MetaData.Version
		String version = solaceArticleMessage.getMetaData().getVersion();
		metaData.setVersion(version);

		// Setting MetaData.Sender
		List<SenderOrReceiver> receiverList = solaceArticleMessage.getMetaData().getReceiver();
		SenderOrReceiver receiver = receiverList.get(0);
		metaData.setSender(receiver);

		// Setting MetaData.Receiver
		SenderOrReceiver sender = solaceArticleMessage.getMetaData().getSender();
		List<SenderOrReceiver> senderList = new ArrayList<SenderOrReceiver>();
		senderList.add(sender);
		metaData.setReceiver(senderList);

		// Setting MetaData.MessageSpecification
		MessageSpecification messageSpecification = new MessageSpecification();
		messageSpecification.setType("StatusMessage");
		metaData.setMessageSpecification(messageSpecification);


		// Setting MetaData.MessageIdentifier
		MessageIdentifier messageIdentifier = new MessageIdentifier();
		messageIdentifier.setUID(UUID.randomUUID().toString());

		// Setting MetaData.MessageIdentifier.BusinessIdentifier
		List<BusinessIdentifier> businessIdentifierList = solaceArticleMessage.getMetaData().getMessageIdentifier().getBusinessIdentifier();
		BusinessIdentifier businessIdentifier = new BusinessIdentifier();
		businessIdentifier.setKey(businessIdentifierKey);
		businessIdentifier.setValue(businessIdentifierValue);

		businessIdentifierList.add(businessIdentifier);
		messageIdentifier.setBusinessIdentifier(businessIdentifierList);
		metaData.setMessageIdentifier(messageIdentifier);

		// Setting MetaData.Correlation
		Correlation correlation = new Correlation();
		correlation.setCorrelationID(solaceArticleMessage.getMetaData().getMessageIdentifier().getUID());
		metaData.setCorrelation(correlation);

		// Setting MetaData.CreationDate
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		metaData.setCreationDate(dateFormat.format(date));

		// Setting Payload
		statusMessage.setStatusCode(statusCode);
		statusMessage.setStatusType(statusType);
		statusMessage.setStatusMessage(statusMessageString);

		StatusDetails statusDetails = new StatusDetails();
		statusDetails.setSourceHost(sourceHost);
		statusDetails.setStatusTimeStamp(new Date());
		statusDetails.setDetailedDescription(statusMessageString);
		statusDetails.setSourcePrimaryPort(port);

		SourceLocation sourceLocation = new SourceLocation();
		sourceLocation.setCompany(receiver.getCompany());
		sourceLocation.setSystemId(receiver.getSystemId());
		sourceLocation.setSystemName(receiver.getSystemName());
		statusDetails.setSourceLocation(sourceLocation);

		statusMessage.setStatusDetails(statusDetails);

		// Create solaceStatusMessage
		solaceStatusMessage.setMetaData(metaData);
		solaceStatusMessage.setPayload(statusMessage);

		boolean send = streamBridge.send("articleError-out-0", solaceStatusMessage);
		log.info("Message sent status to error queue is: {}", send);
		// aims_article_count result=failed, storeCode=null, count+1
		registry.counter("aims_article", "result", "failed", "station_code", "").increment();
	}

}
