package com.ihi.servicemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class IHIServiceMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(IHIServiceMonitorApplication.class, args);
	}

}
