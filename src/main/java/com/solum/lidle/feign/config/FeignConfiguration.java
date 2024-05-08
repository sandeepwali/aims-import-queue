package com.solum.lidle.feign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solum.lidl.config.CustomErrorDecoder;

import feign.Logger;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfiguration {

	@Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
	
	
	@Bean
	public ErrorDecoder errorDecoder() {
	   return new CustomErrorDecoder();
	}
}
