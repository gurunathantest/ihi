package com.ihi.hts;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = {"com.ihi.hts","com.ihi.*"} )
@PropertySources({ @PropertySource("classpath:env.properties"),@PropertySource("classpath:message.properties") })
@EnableAsync
@EnableScheduling
public class IhiHtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IhiHtsApplication.class, args);
	}
	
	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Ihi-hts");
		executor.setCorePoolSize(cores);
		executor.setThreadPriority(10);
		executor.setQueueCapacity(25);
	    executor.initialize();
		return executor;
	}

}
