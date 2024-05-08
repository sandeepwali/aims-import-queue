package com.solum.lidl.custom.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class RabbitMQDownExceptionTest {
	/**
	 * @sandeep
	 *
	 * 
	 */
	
    @Test
    void testConstructor() {
        RabbitMQDownException actualRabbitMQDownException = new RabbitMQDownException("RabbitMq Exception");
        assertEquals(0, actualRabbitMQDownException.getSuppressed().length);
        assertEquals("RabbitMq Exception", actualRabbitMQDownException.getMessage());
        
    }
	
}
