package com.ihi.hedera;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = { "com.ihi.hedera", "com.ihi.*" })
@PropertySources({ @PropertySource("classpath:env.properties"), @PropertySource("classpath:message.properties") })
@EnableAsync
public class IhiHederaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IhiHederaApplication.class, args);
	}

	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Ihi-hedera");
		executor.setCorePoolSize(cores);
		executor.setThreadPriority(10);
		executor.setQueueCapacity(25);
	    executor.initialize();
		return executor;
	}

}
