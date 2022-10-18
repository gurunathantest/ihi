package com.ihi.auth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ihi.auth.mongo.model.ServiceType;
import com.ihi.auth.service.impl.MongoDBLoggerService;


@Component
public class AsynchronousUtils {
	
	@Autowired
	MongoDBLoggerService mongoDBLoggerService;
	
	
	@Async("threadPoolTaskExecutor")
	public void exceptionMessgeIntoMongoLogger(String message,String api,Object payload,String clientId) {
		mongoDBLoggerService.createLogger(message ,ServiceType.AUTH,payload,
				api,clientId, HttpStatus.BAD_REQUEST.value());
	}
}
