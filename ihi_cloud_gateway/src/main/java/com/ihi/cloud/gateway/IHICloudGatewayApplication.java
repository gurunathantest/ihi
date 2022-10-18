package com.ihi.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.ihi.cloud.gateway","com.ihi.*"})
@PropertySources({ @PropertySource("classpath:application.yml"),@PropertySource("classpath:env.properties") })
public class IHICloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(IHICloudGatewayApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
