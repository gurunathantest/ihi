package com.ihi.hts.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.hts.config.IhiMongoHealthIndicator;
import com.ihi.hts.mongo.model.HederaHtsInfo;
import com.ihi.hts.mongo.model.LogLevel;
import com.ihi.hts.mongo.model.ServiceType;
import com.ihi.hts.mongo.model.SeverityType;
import com.ihi.hts.payload.request.LogRequest;

@Service
public class MongoDBLoggerService {

	@Autowired
	public IhiMongoHealthIndicator mongoHealthIndicator;

	@Autowired
	public MongoService mongoService;

	public void createLogger(String message, ServiceType serviceType, Object payLoad, String apiName, String userId,
			int statusCode, String transactionRecipt, String clientId, String logType) {
		if (mongoHealthIndicator.health().getStatus() == Status.UP) {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO)
					.logInfo(message).createdTime(new DateTime()).type(serviceType).payLoad(payLoad).apiName(apiName)
					.userId(userId).statusCode(statusCode).clientId(clientId).transactionRecipt(transactionRecipt)
					.logType(logType).build());
		} else {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO)
					.logInfo(message).createdTime(new DateTime()).type(ServiceType.LOG).payLoad(payLoad)
					.apiName(apiName).userId(userId).statusCode(statusCode).clientId(clientId)
					.transactionRecipt(transactionRecipt).logType(logType).build());
		}
	}

	public void savehederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest) {
		mongoService.saveHederaHtsInfo(hederaHtsInfoRequest);
	}
}
