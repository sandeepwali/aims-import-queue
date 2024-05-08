package com.solum.lidl.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;


//@ContextConfiguration(classes = {RabbitMqConfig.class})
//@ExtendWith(SpringExtension.class)

//@SpringBootTest(classes = {RabbitMqConfig.class},
//properties = {"spring.cloud.config.enabled=false"})
//@RunWith(SpringRunner.class)
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@ContextConfiguration(classes = {RabbitMqConfig.class})
//@ExtendWith(SpringExtension.class)
@Disabled
public class RabbitMqConfigTest {
    /**
     * @sandeep
     */

    @MockBean
    private ConnectionFactory connectionFactory;

    @MockBean
    private ObjectMapper objectMapper;

    @Test
    void testMessageConverter() {
    	
    	RabbitMqConfig rabbitMqConfig = new RabbitMqConfig();
        assertTrue(rabbitMqConfig.messageConverter() instanceof Jackson2JsonMessageConverter);
    }

    @Test
    void testRabbitTemplate() {
    	RabbitMqConfig rabbitMqConfig = new RabbitMqConfig();
        rabbitMqConfig.rabbitTemplate();
    }

}
