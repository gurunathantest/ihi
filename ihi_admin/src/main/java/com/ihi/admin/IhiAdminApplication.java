package com.ihi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;




@SpringBootApplication(scanBasePackages = {"com.ihi.admin","com.ihi.*"} )
@PropertySources({ @PropertySource("classpath:env.properties"),@PropertySource("classpath:message.properties") })
public class IhiAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(IhiAdminApplication.class, args);
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
	
}
