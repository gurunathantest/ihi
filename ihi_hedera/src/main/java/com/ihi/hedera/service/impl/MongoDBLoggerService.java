package com.ihi.hedera.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.hedera.config.IhiMongoHealthIndicator;
import com.ihi.hedera.mongo.model.HederaHtsInfo;
import com.ihi.hedera.mongo.model.LogLevel;
import com.ihi.hedera.mongo.model.ServiceType;
import com.ihi.hedera.mongo.model.SeverityType;
import com.ihi.hedera.payload.request.LogRequest;

@Service
public class MongoDBLoggerService {

	@Autowired
	public IhiMongoHealthIndicator mongoHealthIndicator;

	@Autowired
	public MongoService mongoService;

	public void createLogger(String message, ServiceType serviceType, Object payLoad, String apiName, int statusCode,
			String userId, String clientId) {
		if (mongoHealthIndicator.health().getStatus() == Status.UP) {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO)
					.logInfo(message).createdTime(new DateTime()).type(serviceType).payLoad(payLoad).apiName(apiName)
					.userId(userId).statusCode(statusCode).clientId(clientId).build());
		} else {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO)
					.logInfo(message).createdTime(new DateTime()).type(ServiceType.LOG).payLoad(payLoad)
					.apiName(apiName).userId(userId).statusCode(statusCode).clientId(clientId).build());
		}
	}

	public void savehederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest) {
		mongoService.saveHederaHtsInfo(hederaHtsInfoRequest);
	}
}
