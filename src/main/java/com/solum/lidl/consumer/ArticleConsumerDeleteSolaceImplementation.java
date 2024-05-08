package com.solum.lidl.consumer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.StaticMessageHeaderAccessor;
import org.springframework.integration.acks.AckUtils;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.messaging.Message;
import org.springframework.util.ObjectUtils;

import com.solace.spring.cloud.stream.binder.util.SolaceAcknowledgmentException;
import com.solum.lidl.entity.CounterState;
import com.solum.lidl.entity.queue.SolaceDeleteMessageDto;
import com.solum.lidl.entity.solacemessage.SolaceArticleMessage;
import com.solum.lidl.entity.solacemessage.SolaceDeleteArticleMessage;
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

import feign.RetryableException;
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ArticleConsumerDeleteSolaceImplementation {
	
	@Autowired
	public StreamBridge streamBridge;

	@Value("${aims-properties.businessIdentifier.key}")
	private String businessIdentifierKey;
	
	@Value("${aims-properties.businessIdentifier.value}")
	private String businessIdentifierValue;
	
	@Value("${aims-properties.statusCode}")
	private String statusCode;

	@Value("${aims-properties.statusType}")
	private String statusType;
	
	@Value("${aims-properties.sourceHost}")
	private String sourceHost;

	@Value("${aims-properties.port}")
	private String port;
	
    @Value("${aims.import.queue.statistics.enable}")
    private boolean isMetricHandlerEnabled;

	@Autowired
	private AimsPortalService aimsPortalService;

	@Autowired private MetricsHandler metricHandler;



	@Autowired private PrometheusMeterRegistry registry;

	@Bean
	public Consumer<Message<SolaceDeleteArticleMessage>> articleDeleteConsumeSolace(){
		return (solaceMessage) -> {
			AcknowledgmentCallback acknowledgmentCallback = StaticMessageHeaderAccessor.getAcknowledgmentCallback(solaceMessage);
			acknowledgmentCallback.noAutoAck();
			SolaceDeleteArticleMessage solaceMessagePayload = solaceMessage.getPayload();
			log.trace("Message from solace: {}", solaceMessagePayload);
			SolaceDeleteMessageDto solaceMessageDto = new SolaceDeleteMessageDto();
			solaceMessageDto.setPayload(solaceMessagePayload);
			solaceMessageDto.setCallback(acknowledgmentCallback);
			process(solaceMessageDto);


		};
	}

	private void process(SolaceDeleteMessageDto solaceMessageDto) {
		Map<StatsPK, CounterState> dataMap = new HashMap<>();
		boolean ackFlag = true;
		if (ObjectUtils.isEmpty(solaceMessageDto)) {
			log.info("solaceArticleMessagePayload is empty...");
		} else {
			final SolaceDeleteArticleMessage solaceMessage = solaceMessageDto.getPayload();
			String stationCode = solaceMessage.getPayload().getStationCode();//15222

			if(stationCode == null) {
				return;
			}
			List<String> items = solaceMessage.getPayload().getItems();
			for(String articleId :items) {
				AimsPortalUpdateArticle  response = null;
				try {
					int count = 0; int statusCodeOfApiCall = 0;
					retry:do {
						try {
							count++;
							if(count > 6) {
								log.info("Portal unavailable for long time/some other issue, hence breaking the retry loop {}, {}", articleId, stationCode);
								break retry;
							}else {
								response = aimsPortalService.deleteArticle(articleId, stationCode); 
								statusCodeOfApiCall = response.getStatusCode();
								if(!(statusCodeOfApiCall == 200 || statusCodeOfApiCall == 202)) {
									if(statusCodeOfApiCall == 400 || statusCodeOfApiCall == 404) {
										sendMessageToErrorQueue(solaceMessage,"Message rejected because article.id mapped to the article.stationCode was not found resulting to bad request");
										break retry;
									}
									log.info("Thread is sleeping for 5 minutes...");
									Thread.sleep(300000);
								}
							}
						}catch (RetryableException e) {
							log.warn("Client unavailable/portal down: {}", e);
							ackFlag = false;
						}catch(InterruptedException e) {
							log.warn("Thread is interrupted while sleeping: {}", e);
						}
					} while (!(statusCodeOfApiCall == 200 || statusCodeOfApiCall == 202)) ;

					if(response != null && response.getSuccess()) { 
						registry.counter("aims_article_delete", "result", "success", "station_code",stationCode).increment();
					}else {
						registry.counter("aims_article_delete", "result", "failed", "station_code",stationCode).increment();
					}
					if (isMetricHandlerEnabled && metricHandler != null) {
						StatsUpdate(solaceMessage,dataMap,articleId);
					}
				} catch (Exception e) {
					log.error("{}", e);
				}
			}
			if(ackFlag) {
				acknowledgeMessage(solaceMessageDto);
		        if (isMetricHandlerEnabled && metricHandler != null) {
		        	metricHandler.updateMetrics(dataMap);
		        }
			}


		}
	}

	private void acknowledgeMessage(SolaceDeleteMessageDto solaceMessageDto) {
		try {
			AckUtils.accept(solaceMessageDto.getCallback());
		} catch (SolaceAcknowledgmentException e) {
			log.warn("Unable to Acknowledge Message: {}", e);
		}
	}
	
	private void StatsUpdate(SolaceDeleteArticleMessage solaceMessage,Map<StatsPK, CounterState> dataMap,String articleId) {
		final StatsPK key = new StatsPK(solaceMessage.getMetaData(), solaceMessage.getPayload().getStationCode());
		CounterState val = dataMap.get(key);
		if(val != null) {//if key already exists increment count
			val.process(articleId);
		}else {
			val = new CounterState();
			dataMap.put(key,val);
			val.process(articleId);
		}
	}

	private void sendMessageToErrorQueue(SolaceDeleteArticleMessage solaceMessage, String statusMessageString) {

		// Create SolaceStatusMessage
		SolaceStatusMessage solaceStatusMessage = new SolaceStatusMessage();

		// Create SolaceStatusMessage.MetaData
		MetaData metaData = new MetaData();

		// Create SolaceStatusMessage.StatusMessage as Payload
		StatusMessage statusMessage = new StatusMessage();

		// Setting MetaData.Version
		String version = solaceMessage.getMetaData().getVersion();
		metaData.setVersion(version);

		// Setting MetaData.Sender
		List<SenderOrReceiver> receiverList = solaceMessage.getMetaData().getReceiver();
		SenderOrReceiver receiver = receiverList.get(0);
		metaData.setSender(receiver);

		// Setting MetaData.Receiver
		SenderOrReceiver sender = solaceMessage.getMetaData().getSender();
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
		List<BusinessIdentifier> businessIdentifierList = solaceMessage.getMetaData().getMessageIdentifier().getBusinessIdentifier();
		BusinessIdentifier businessIdentifier = new BusinessIdentifier();
		businessIdentifier.setKey(businessIdentifierKey);
		businessIdentifier.setValue(businessIdentifierValue);

		businessIdentifierList.add(businessIdentifier);
		messageIdentifier.setBusinessIdentifier(businessIdentifierList);
		metaData.setMessageIdentifier(messageIdentifier);

		// Setting MetaData.Correlation
		Correlation correlation = new Correlation();
		correlation.setCorrelationID(solaceMessage.getMetaData().getMessageIdentifier().getUID());
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

		boolean send = streamBridge.send("deletearticle-out-0", solaceStatusMessage);
		log.info("Message sent status to error queue is: {}", send);
		// aims_article_count result=failed, storeCode=null, count+1
		//registry.counter("aims_article", "result", "failed", "station_code", "").increment();
	}


}
