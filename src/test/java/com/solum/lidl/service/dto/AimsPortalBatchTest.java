package com.solum.lidl.service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AimsPortalBatchTest {
	/**
	 * @sandeep
	 *
	 * 
	 */
	//@Autowired
	//AimsPortalBatch actualAimsPortalBatch;
	
    @Test
    void testConstructor() {
    	//actualAimsPortalBatch.setId(1L);
        AimsPortalBatch actualAimsPortalBatch = new AimsPortalBatch();
        actualAimsPortalBatch.setServerId(303);
        actualAimsPortalBatch.setStationCode("CITEST");
        actualAimsPortalBatch.setRequestSequence(231004040051299212L);
        actualAimsPortalBatch.setNumberOfItems(1);
        assertNull(actualAimsPortalBatch.getCreated());
        assertNull(actualAimsPortalBatch.getType());
        assertNull(actualAimsPortalBatch.getStatus());
        assertEquals("CITEST",actualAimsPortalBatch.getStationCode());
        //assertNull(actualAimsPortalBatch.getStationCode());
        assertEquals(303,actualAimsPortalBatch.getServerId());
        assertEquals(231004040051299212L,actualAimsPortalBatch.getRequestSequence());
        assertEquals(1,actualAimsPortalBatch.getNumberOfItems());
        assertNull(actualAimsPortalBatch.getMessageProvider());
        assertNull(actualAimsPortalBatch.getMessageParam());
        assertNull(actualAimsPortalBatch.getLastModified());
        assertNull(actualAimsPortalBatch.getId());
        assertNull(actualAimsPortalBatch.getCustomBatchId());
    }

    @Test
    void testConstructorWithArgs() {
        AimsPortalBatch actualAimsPortalBatch = new AimsPortalBatch(1L);
        actualAimsPortalBatch.setServerId(303);
        actualAimsPortalBatch.setStationCode("CITEST");
        actualAimsPortalBatch.setRequestSequence(231004040051299212L);
        actualAimsPortalBatch.setNumberOfItems(1);
        assertNull(actualAimsPortalBatch.getCreated());
        assertNull(actualAimsPortalBatch.getType());
        assertNull(actualAimsPortalBatch.getStatus());
        assertEquals("CITEST",actualAimsPortalBatch.getStationCode());
        assertEquals(303,actualAimsPortalBatch.getServerId());
        assertEquals(231004040051299212L,actualAimsPortalBatch.getRequestSequence());
        assertEquals(1,actualAimsPortalBatch.getNumberOfItems());
        assertNull(actualAimsPortalBatch.getMessageProvider());
        assertNull(actualAimsPortalBatch.getMessageParam());
        assertNull(actualAimsPortalBatch.getLastModified());
        assertEquals(1L,actualAimsPortalBatch.getId().longValue());
        assertNull(actualAimsPortalBatch.getCustomBatchId());
    }
}

