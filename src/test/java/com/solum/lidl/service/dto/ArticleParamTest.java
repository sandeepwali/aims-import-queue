package com.solum.lidl.service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ArticleParamTest {
	/**
	 * @sandeep
	 *
	 * 
	 */
	
    @Test
    void testSetCustomBatchId() {
        ArticleParam articleParam = new ArticleParam();
        articleParam.setCustomBatchId("123");
        assertEquals("123", articleParam.getCustomBatchId());
    }
  
    @Test
    void testGetCustomBatchId() {
        ArticleParam articleParam = new ArticleParam();
        articleParam.getCustomBatchId();
        assertNull(articleParam.getDataList());
    }
    //Null Check
    @Test
    void testSetCustomBatchIdNull() {
        ArticleParam articleParam = new ArticleParam();
        articleParam.setCustomBatchId(null);
        assertNull(articleParam.getDataList());
    }
    //Empty Check
    @Test
    void testSetCustomBatchIdEmpty() {
        ArticleParam articleParam = new ArticleParam();
        articleParam.setCustomBatchId("");
        assertNull(articleParam.getDataList());
    }
	
}
