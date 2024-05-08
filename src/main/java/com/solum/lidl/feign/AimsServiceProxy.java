package com.solum.lidl.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.solum.lidl.service.dto.ArticleParam;
import com.solum.lidle.feign.config.FeignConfiguration;

@FeignClient(name = "data", url = "${aims.url.node}", configuration = FeignConfiguration.class)
public interface AimsServiceProxy {

	@RequestMapping(method = RequestMethod.POST, value = "${aims.url.articles}")
	public ResponseEntity<Object> updateAIMSArticleInfo(@RequestHeader("api-key") String apiKey,
			ArticleParam articleParam);


	@RequestMapping(method = RequestMethod.DELETE, value = "${aims.url.articles}/article")
	public ResponseEntity<Object> deleteArticle(@RequestHeader("api-key") String apiKey,
			@RequestParam(value="articleId") String articleId,@RequestParam(value="stationCode") String stationCode);
}
