package com.ihi.hcs.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.hcs.config.IhiMongoHealthIndicator;
import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaHtsInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.LogLevel;
import com.ihi.hcs.mongo.model.ServiceType;
import com.ihi.hcs.mongo.model.SeverityType;
import com.ihi.hcs.payload.request.LogRequest;

@Service
public class MongoDBLoggerService {

	@Autowired
	public IhiMongoHealthIndicator mongoHealthIndicator;

	@Autowired
	public MongoService mongoService;

	public void createLogger(String message, ServiceType serviceType, Object payLoad, String apiName, String userId,
			int statusCode, String clientId, String topicId, String LogType) {
		if (mongoHealthIndicator.health().getStatus() == Status.UP) {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO)
					.logInfo(message).createdTime(new DateTime()).type(serviceType).payLoad(payLoad).apiName(apiName)
					.userId(userId).statusCode(statusCode).clientId(clientId).topicId(topicId).LogType(LogType)
					.build());
		} else {
			mongoService.saveLogInformation(
					LogRequest.builder().severity(SeverityType.Alert).logLevel(LogLevel.INFO).logInfo(message)
							.createdTime(new DateTime()).type(ServiceType.LOG).payLoad(payLoad).apiName(apiName)
							.userId(userId).statusCode(statusCode).clientId(clientId).LogType(LogType).build());
		}
	}

	public void createSubscribeLogger(HederaSubscribeInfo hederaSubscribeInfo) {

		mongoService.saveSubscribe(hederaSubscribeInfo);

	}

	public void savehederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest) {
		mongoService.saveHederaHtsInfo(hederaHtsInfoRequest);
	}

	public void createHcsSubscribeLogger(HcsSubscribeInfo hcsSubscribeInfo) {
		mongoService.saveHcsSubscribe(hcsSubscribeInfo);

	}
}
