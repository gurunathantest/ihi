package com.ihi.hedera.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ihi.hedera.model.HederaFees;
import com.ihi.hedera.mongo.model.HederaHtsInfo;
import com.ihi.hedera.mongo.model.ServiceType;
import com.ihi.hedera.repository.HederaFeesRepo;
import com.ihi.hedera.service.impl.MongoDBLoggerService;

@Component
public class AsynchronousUtils {
	
	@Autowired
	MongoDBLoggerService mongoDBLoggerService;
	
	@Autowired
	private HederaFeesRepo hederaFeesRepo;
	
	
	@Async("threadPoolTaskExecutor")
	public void exceptionMessgeIntoMongoLogger(String message,String api,Object payload,String userId,String clientId) {
		mongoDBLoggerService.createLogger(message ,ServiceType.HEDERA,payload,
				api,HttpStatus.BAD_REQUEST.value(),userId,clientId);
	}
	
	
	@Async("threadPoolTaskExecutor")
	public void saveHederaHtsInfo(String service,String operation,String clientId) {
		
		HederaFees fees = hederaFeesRepo.findByServiceAndOperations(service,operation);
		mongoDBLoggerService.savehederaHtsInfo(HederaHtsInfo.builder().clientId(clientId)
				.service(service).operation(operation)
				.fees(fees.getPriceInUsd()).build());
	}
}
