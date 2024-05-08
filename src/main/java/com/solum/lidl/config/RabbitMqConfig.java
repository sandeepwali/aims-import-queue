package com.solum.lidl.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableRabbit
@ConditionalOnProperty(value = "aims.portal.queue.enabled", havingValue = "true")
public class RabbitMqConfig {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ConnectionFactory rabbitConnectionFactory;

	@Bean
	public MessageConverter messageConverter() {
		return (MessageConverter) new Jackson2JsonMessageConverter(
				this.objectMapper);
	}
	
    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
    
}
