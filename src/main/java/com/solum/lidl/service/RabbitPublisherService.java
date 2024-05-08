package com.solum.lidl.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;

import com.solum.lidl.custom.exception.RabbitMQDownException;

import com.solum.lidl.entity.RetryCorrelationData;
import com.solum.lidl.service.dto.AimsPortalBatch;

public interface RabbitPublisherService {
	
	void handleRabbitAcknowledgement(boolean isAck, RetryCorrelationData retryCorrelationData) throws RabbitMQDownException;
	
	Confirm publishMessage(AimsPortalBatch aimsPortalBatch,CorrelationData correlationData) throws InterruptedException, ExecutionException, TimeoutException;
}
