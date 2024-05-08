package com.solum.lidl.entity;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public class RetryCorrelationData extends CorrelationData{

    public RetryCorrelationData(String id) {
        super(id);
    }

}
