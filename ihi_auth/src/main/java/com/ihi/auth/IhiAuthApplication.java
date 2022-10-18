package com.ihi.auth;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = {"com.ihi.auth","com.ihi.*"} )
@PropertySources({ @PropertySource("classpath:env.properties"),@PropertySource("classpath:message.properties") })
@EnableAsync
public class IhiAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(IhiAuthApplication.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}
	
	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Ihi-auth");
		executor.setCorePoolSize(cores);
		executor.setThreadPriority(10);
		executor.setQueueCapacity(25);
	    executor.initialize();
		return executor;
	}
}
