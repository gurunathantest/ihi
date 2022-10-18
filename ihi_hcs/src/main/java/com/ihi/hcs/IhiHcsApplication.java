package com.ihi.hcs;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = {"com.ihi.hcs","com.ihi.*"} )
@PropertySources({ @PropertySource("classpath:env.properties"),@PropertySource("classpath:message.properties") })
@Configuration
@EnableAsync
public class IhiHcsApplication {

	@Value("${jasypt.secret.key}")
	public String jasyptKey;
	
	public static void main(String[] args) {
		SpringApplication.run(IhiHcsApplication.class, args);
	}
	
	@Bean
	public StrongTextEncryptor createJasypt() {
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
			textEncryptor.setPassword(jasyptKey);
			return textEncryptor;
	}
	
	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Ihi-hcs");
		executor.setCorePoolSize(cores);
		executor.setThreadPriority(10);
		executor.setQueueCapacity(25);
	    executor.initialize();
		return executor;
	}

	 
}
