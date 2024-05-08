package com.solum.lidl.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.solum.lidl.custom.exception.AimsPortalDownException;
import com.solum.lidl.feign.AimsServiceProxy;
import com.solum.lidl.service.AimsPortalService;
import com.solum.lidl.service.AimsPortalUpdateArticle;
import com.solum.lidl.service.dto.ArticleParam;

import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.FeignException.ServiceUnavailable;

@Service
@Transactional
public class AimsPortalServiceImpl implements AimsPortalService {

	private final Logger log = LoggerFactory.getLogger(AimsPortalServiceImpl.class);

	@Value("${aims.url.articles}")
	private String aimsArticleUri;

	@Value("${aims.apikey}")
	private String aimsKey;

	@Autowired
	private AimsServiceProxy aimsServiceProxy;

	@Override
	public AimsPortalUpdateArticle updateArticles(UUID requestId, ArticleParam articleParam) {
		log.debug("Request to updateArticles in AIMS Portal with RequestId: {} and Request: {}", requestId, articleParam);
		try {
			ResponseEntity<Object> response = aimsServiceProxy.updateAIMSArticleInfo(aimsKey, articleParam);
			if(!ObjectUtils.isEmpty(response) && response.getStatusCode().is2xxSuccessful()) {
				log.info("Calls to AIMS Portal Update Article with RequestId: {} is successful",requestId);
				AimsPortalUpdateArticle apua = AimsPortalUpdateArticle.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.TRUE)
						.errorMessage(null)
						.build();
				return apua;
			}else {
				if(!ObjectUtils.isEmpty(response)  && response.getStatusCode().is5xxServerError()) {
					log.error("Calls to AIMS Portal Update Article with RequestId: {} and AssignArticleLabel: {} is failed 503 with response {}",requestId, articleParam, "service unavailable");
					return AimsPortalUpdateArticle.builder()
							.statusCode(503)
							.success(Boolean.FALSE)
							.errorMessage(HttpStatus.SERVICE_UNAVAILABLE.name())
							.build(); 
				}
				log.error("Calls to AIMS Portal Update Article with RequestId: {} and ArticleParam: {} is failed with response {}",requestId, articleParam, response);
				//@TODO handle error scenerios properly after testing
				return AimsPortalUpdateArticle.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.FALSE)
						.errorMessage(ObjectUtils.isEmpty(response.getBody())? "" : response.getBody().toString())
						.build();
			}
		} catch(FeignServerException e  ) {
			log.error("Calls to AIMS Portal Update Article with RequestId: {} and AssignArticleLabel: {} is failed 503 with response {}",requestId, articleParam, e.getMessage());
			return AimsPortalUpdateArticle.builder()
					.statusCode(e.status())
					.success(Boolean.FALSE)
					.errorMessage((e.status() == 503)? HttpStatus.SERVICE_UNAVAILABLE.name() : e.getMessage())
					.build();
		}catch(FeignClientException e) {
			log.error("Calls to AIMS Portal Update Article with RequestId: {} and AssignArticleLabel: {} is failed with response {}",requestId, articleParam, e.getMessage());
			return AimsPortalUpdateArticle.builder()
					.statusCode(e.status())
					.success(Boolean.FALSE)
					.errorMessage((e.status() == 404)? HttpStatus.NOT_FOUND.name() : e.getMessage())
					.build();
		}


	}

	@Override
	public AimsPortalUpdateArticle deleteArticle(String articleId, String stationCode) {
		try {
			ResponseEntity<Object> response = aimsServiceProxy.deleteArticle(aimsKey, articleId,stationCode);
			if(!ObjectUtils.isEmpty(response) && response.getStatusCode().is2xxSuccessful()) {
				log.info("Calls to AIMS Portal delete Article with articleId: {} is successful",articleId);
				AimsPortalUpdateArticle apua = AimsPortalUpdateArticle.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.TRUE)
						.errorMessage(null)
						.build();
				return apua;
			}else {
				log.error("Calls to AIMS Portal delete Article with articleId {} is failed with response {}",articleId, response);
				//@TODO handle error scenerios properly after testing
				return AimsPortalUpdateArticle.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.FALSE)
						.errorMessage(ObjectUtils.isEmpty(response.getBody())? "" : response.getBody().toString())
						.build();
			}
		} catch(FeignClientException e) {
			log.error("Calls to AIMS Portal delete Article with articleId {} is failed with response {}",articleId, e.getMessage());
			return AimsPortalUpdateArticle.builder()
					.statusCode(e.status())
					.success(Boolean.FALSE)
					.errorMessage((e.status() == 404)? HttpStatus.NOT_FOUND.name() : e.getMessage())
					.build();
		}


	}
}
