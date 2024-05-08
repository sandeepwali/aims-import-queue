package com.solum.lidle.feign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.auth.BasicAuthRequestInterceptor;

@Configuration
public class FeignSolaceConfiguration {
	
	@Value("${spring.cloud.stream.binders.solace.environment.solace.java.clientUsername}")
	private String userName;
	
	@Value("${spring.cloud.stream.binders.solace.environment.solace.java.clientPassword}")
	private String password;
    
	@Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
         return new BasicAuthRequestInterceptor(userName, 
        		 password);
    }
}
