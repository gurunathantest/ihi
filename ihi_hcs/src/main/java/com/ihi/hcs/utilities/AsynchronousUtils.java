package com.ihi.hcs.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ihi.hcs.model.HederaFees;
import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaHtsInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.ServiceType;
import com.ihi.hcs.repository.HederaFeesRepo;
import com.ihi.hcs.service.MongoDBLoggerService;

@Component
public class AsynchronousUtils {

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private HederaFeesRepo hederaFeesRepo;

	@Async("threadPoolTaskExecutor")
	public void saveHederaHtsInfo(String service, String operation, String clientId, String userId, String logType) {

		HederaFees fees = hederaFeesRepo.findByServiceAndOperations(service, operation);
		mongoDBLoggerService.savehederaHtsInfo(HederaHtsInfo.builder().clientId(clientId).service(service)
				.operation(operation).fees(fees.getPriceInUsd())
				.logType(logType)
				.build());
	}

	@Async("threadPoolTaskExecutor")
	public void exceptionMessgeIntoMongoLogger(String message, String api, String payload, String userId,
			String clientId, String topicId, String logType) {
		mongoDBLoggerService.createLogger(message, ServiceType.HCS, payload, api, userId,
				HttpStatus.BAD_REQUEST.value(), clientId, topicId, logType);
	}

	@Async("threadPoolTaskExecutor")
	public void saveSubscribeMessage(HederaSubscribeInfo hederaSubscribeInfo) {
		mongoDBLoggerService.createSubscribeLogger(hederaSubscribeInfo);
	}

	@Async("threadPoolTaskExecutor")
	public void saveHcsSubscribeMessage(HcsSubscribeInfo hcsSubscribeInfo) {
		mongoDBLoggerService.createHcsSubscribeLogger(hcsSubscribeInfo);
	}
}
