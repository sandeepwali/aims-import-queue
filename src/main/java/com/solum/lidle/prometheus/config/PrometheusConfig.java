package com.solum.lidle.prometheus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class PrometheusConfig {

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
	    return new TimedAspect(registry);
	}
}
