package com.ihi.hts.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ihi.hts.model.HederaFees;
import com.ihi.hts.mongo.model.HederaHtsInfo;
import com.ihi.hts.mongo.model.ServiceType;
import com.ihi.hts.repository.HederaFeesRepo;
import com.ihi.hts.service.impl.MongoDBLoggerService;

@Component
public class AsynchronousUtils {

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private HederaFeesRepo hederaFeesRepo;

	@Async("threadPoolTaskExecutor")
	public void exceptionMessgeIntoMongoLogger(String message, String api, String payload, String userId,
			String clientId, String transactionReceipt, String logType) {
		mongoDBLoggerService.createLogger(message, ServiceType.HTS, payload, api, userId,
				HttpStatus.BAD_REQUEST.value(), clientId, transactionReceipt, logType);
	}

	@Async("threadPoolTaskExecutor")
	public void saveHederaHtsInfo(String service, String operation, String clientId, String userId, String logType) {

		HederaFees fees = hederaFeesRepo.findByServiceAndOperations(service, operation);
		mongoDBLoggerService.savehederaHtsInfo(HederaHtsInfo.builder().clientId(clientId).service(service)
				.operation(operation).fees(fees.getPriceInUsd()).userId(userId).logType(logType).build());
	}
}
