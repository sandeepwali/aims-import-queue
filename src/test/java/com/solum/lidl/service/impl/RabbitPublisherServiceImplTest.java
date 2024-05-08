package com.solum.lidl.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rabbitmq.client.impl.AMQImpl.Connection;
import com.solum.lidl.entity.RetryCorrelationData;

@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class})
@ExtendWith(SpringExtension.class)
class RabbitPublisherServiceImplTest {
	/**
	 * @sandeep
	 *
	 * 
	 */
	
	
	
    @Autowired
    private RabbitPublisherServiceImpl rabbitPublisherServiceImpl;

    @MockBean
    private RabbitTemplate rabbitTemplate;
    
    //@MockBean
    //private Connection connection;

    @Test
    void testPostConstruct() {
    	//rabbitTemplate.setConfirmCallback(RabbitTemplate.ConfirmCallback);
        doNothing().when(rabbitTemplate).setConfirmCallback(Mockito.<RabbitTemplate.ConfirmCallback>any());
        rabbitPublisherServiceImpl.postConstruct();
        verify(rabbitTemplate).setConfirmCallback(Mockito.<RabbitTemplate.ConfirmCallback>any());
    }

    @Test
    void testHandleRabbitAcknowledgement() {
        RetryCorrelationData retryCorrelationData = new RetryCorrelationData("231004040051299212");
        rabbitPublisherServiceImpl.handleRabbitAcknowledgement(true, retryCorrelationData);
        assertEquals("231004040051299212", retryCorrelationData.getId());
    }

    @Test
    void testHandleRabbitAcknowledgementAckFalse() {
        RetryCorrelationData retryCorrelationData = new RetryCorrelationData("231004040051299212");
        rabbitPublisherServiceImpl.handleRabbitAcknowledgement(false, retryCorrelationData);
        assertEquals("231004040051299212", retryCorrelationData.getId());
    }

    @Test
    void testHandleRabbitAcknowledgementGetId() {
        RetryCorrelationData retryCorrelationData = mock(RetryCorrelationData.class);
        when(retryCorrelationData.getId()).thenReturn("231004040051299212");
        rabbitPublisherServiceImpl.handleRabbitAcknowledgement(true, retryCorrelationData);
        verify(retryCorrelationData).getId();
    }

}

