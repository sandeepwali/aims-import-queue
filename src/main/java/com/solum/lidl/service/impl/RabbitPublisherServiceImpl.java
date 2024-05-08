package com.solum.lidl.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.solum.lidl.config.RabbitMqConfig;

import com.solum.lidl.entity.RetryCorrelationData;
import com.solum.lidl.service.RabbitPublisherService;
import com.solum.lidl.service.dto.AimsPortalBatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RabbitPublisherServiceImpl implements RabbitPublisherService {
	
	
	@Value("${aims.portal.queue.batch.article}")
	private String articleQueue;

	private final RabbitTemplate rabbitTemplate;

	public RabbitPublisherServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@PostConstruct
	public void postConstruct() {
		rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
			log.info("reason : {} ", reason);
			if (correlation == null) {
				return;
			}
			RetryCorrelationData retryCorrelationData = (RetryCorrelationData) correlation;
			handleRabbitAcknowledgement(ack, retryCorrelationData);
		});
	}

	public void handleRabbitAcknowledgement(boolean isAck,RetryCorrelationData retryCorrelationData){
		final String id = retryCorrelationData.getId();
		if (!isAck) {
			log.info("customBatchId {} got nack-ed",id);
		} else {
			log.info("customBatchId : {} got acked",id);
		}
	}

	public Confirm publishMessage(AimsPortalBatch aimsPortalBatch,CorrelationData correlationData) throws InterruptedException, ExecutionException, TimeoutException{
		rabbitTemplate.convertAndSend(articleQueue, aimsPortalBatch, correlationData);
		return correlationData.getFuture().get(5,TimeUnit.SECONDS);
	}
}
