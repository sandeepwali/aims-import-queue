package com.solum.lidl.config;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "rmaEntityManagerFactory", transactionManagerRef = "rmaTransactionManager", basePackages = {
		"com.solum.lidl.respository.stats" })
public class RmaDbConfig {

	@Bean(name = "rmaDataSource")
	@ConfigurationProperties(prefix = "spring.label.datasource")
	public DataSource dataSource() {
		//secondaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean(name = "rmaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean rmaEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("rmaDataSource") DataSource dataSource) {
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		return builder.dataSource(dataSource).properties(properties)
				.packages("com.solum.lidl.entity.stats").persistenceUnit("Statistics").build();
	}

	@Bean(name = "rmaTransactionManager")
	public PlatformTransactionManager rmaTransactionManager(
			@Qualifier("rmaEntityManagerFactory") EntityManagerFactory rmaEntityManagerFactory) {
		return new JpaTransactionManager(rmaEntityManagerFactory);
	}
}

