package com.solum.lidl.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeTwo;
import com.solum.lidl.service.dto.AimsPortalBatch;
import com.solum.lidl.service.dto.ArticleParam;

/*@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class})
@ExtendWith(SpringExtension.class)*/

/*@SpringBootTest
@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class,RabbitMqConfig.class}) 
*/
//@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class})
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class})

//@SpringBootTest
//@RunWith(MockitoJUnitRunner.class)
//@ContextConfiguration(classes = RabbitPublisherServiceImpl.class)
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ContextConfiguration(classes = {RabbitPublisherServiceImpl.class})

//@SpringBootTest


//@ContextConfiguration(classes = {ArticleConsumerSolaceNewImplementationMQ.class, StreamBridge.class,FunctionRegistry.class})
//@ExtendWith(SpringExtension.class)
public class ArticleConsumerSolaceNewImplementationMQTest {
	/**
	 * @sandeep
	 *
	 * 
	 */
	
    //@MockBean
    //private MetricsHandler metricsHandler;
    //@MockBean
    //private PrometheusMeterRegistry prometheusMeterRegistry;

    //@MockBean
    //private RabbitPublisherService rabbitPublisherService;

    @Test
    void testUpdateArticleBatch() throws JsonProcessingException {
        ArticleConsumerSolaceNewImplementationMQ articleConsumerSolaceNewImplementationMQ = new ArticleConsumerSolaceNewImplementationMQ();

        ArticleParam param = new ArticleParam();
        param.setCustomBatchId("20231004040001377446");
        param.setDataList(new ArrayList<>());

        AimsPortalBatch actualUpdateArticleBatchResult = articleConsumerSolaceNewImplementationMQ
                .updateArticleBatch(param);

        assertEquals(AimsPortalBatch.AimsPortalBatchType.ARTICLE, actualUpdateArticleBatchResult.getType());
        assertEquals(BatchStatus.STARTING, actualUpdateArticleBatchResult.getStatus());
        assertEquals("", actualUpdateArticleBatchResult.getStationCode());
        assertEquals(0, actualUpdateArticleBatchResult.getNumberOfItems());
        assertEquals("API", actualUpdateArticleBatchResult.getMessageProvider());
        assertEquals("{\"customBatchId\":\"20231004040001377446\",\"dataList\":[]}", actualUpdateArticleBatchResult.getMessageParam());
        assertEquals("20231004040001377446", actualUpdateArticleBatchResult.getCustomBatchId());
    }
    
    @Test
    void testUpdateArticleBatchPartial() throws JsonProcessingException {
        ArticleConsumerSolaceNewImplementationMQ articleConsumerSolaceNewImplementationMQ = new ArticleConsumerSolaceNewImplementationMQ();

        ArrayList<ArticleTypeTwo> dataList = new ArrayList<>();
        dataList.add(new ArticleTypeTwo());

        ArticleParam param = new ArticleParam();
        param.setCustomBatchId("20231004040001377446");
        param.setDataList(dataList);

        AimsPortalBatch actualUpdateArticleBatchResult = articleConsumerSolaceNewImplementationMQ.updateArticleBatch(param);

        assertEquals(AimsPortalBatch.AimsPortalBatchType.ARTICLE, actualUpdateArticleBatchResult.getType());
        assertEquals(BatchStatus.STARTING, actualUpdateArticleBatchResult.getStatus());
        assertEquals("", actualUpdateArticleBatchResult.getStationCode());
        assertEquals(0, actualUpdateArticleBatchResult.getNumberOfItems());
        assertEquals("API", actualUpdateArticleBatchResult.getMessageProvider());
        assertEquals("20231004040001377446", actualUpdateArticleBatchResult.getCustomBatchId());
    }
    
    @Test
    void testPut() throws JsonProcessingException {
        ArticleConsumerSolaceNewImplementationMQ articleConsumerSolaceNewImplementationMQ = new ArticleConsumerSolaceNewImplementationMQ();

        ArticleParam articleParam = new ArticleParam();
        articleParam.setCustomBatchId("20231004040001377446");
        articleParam.setDataList(new ArrayList<>());

        AimsPortalBatch actualPutResult = articleConsumerSolaceNewImplementationMQ.put("20231004040001377446", 231004040051299212L, "API",
                "CITEST", AimsPortalBatch.AimsPortalBatchType.ARTICLE, articleParam);

        assertEquals(AimsPortalBatch.AimsPortalBatchType.ARTICLE, actualPutResult.getType());
        assertEquals(BatchStatus.STARTING, actualPutResult.getStatus());
        assertEquals("CITEST", actualPutResult.getStationCode());
        assertEquals(231004040051299212L, actualPutResult.getRequestSequence());
        assertEquals(0, actualPutResult.getNumberOfItems());
        assertEquals("API", actualPutResult.getMessageProvider());
        assertEquals("{\"customBatchId\":\"20231004040001377446\",\"dataList\":[]}", actualPutResult.getMessageParam());
        assertEquals("20231004040001377446", actualPutResult.getCustomBatchId());
    }
	
}
