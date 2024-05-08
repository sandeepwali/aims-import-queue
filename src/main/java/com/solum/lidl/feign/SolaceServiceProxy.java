package com.solum.lidl.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.solum.lidl.entity.SolaceApiResponse;
import com.solum.lidle.feign.config.FeignSolaceConfiguration;

@FeignClient(name = "messageCount", url = "${solace.message.count.url}", configuration = FeignSolaceConfiguration.class)
public interface SolaceServiceProxy {
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<SolaceApiResponse> checkNumberOfMessagesInQueue();
	
}