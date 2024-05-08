package com.solum.lidl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

//import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;

@Slf4j

@SpringBootApplication
@EnableFeignClients
//@EnablePrometheusEndpoint
@EnableScheduling
@EnableAsync

public class AimsImportQueueApplication {

	@Autowired
	BuildProperties buildProperties;


	public static void main(String[] args) {
		 SpringApplication.run(AimsImportQueueApplication.class, args);
	 
		
	}

 
	@PostConstruct
	private void logVersion() {
		
	  log.info(buildProperties.getName());
	  log.info(buildProperties.getVersion());
	  log.info(buildProperties.get("time"));
	  log.info(buildProperties.getGroup());
  }
}
