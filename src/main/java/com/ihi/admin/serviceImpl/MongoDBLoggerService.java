package com.ihi.admin.serviceImpl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import com.ihi.admin.config.IhiMongoHealthIndicator;
import com.ihi.admin.mongo.model.LogLevel;
import com.ihi.admin.mongo.model.ServiceType;
import com.ihi.admin.mongo.model.SeverityType;
import com.ihi.admin.payload.request.LogRequest;


@Service
public class MongoDBLoggerService {

	@Autowired
	public  IhiMongoHealthIndicator mongoHealthIndicator;
	
	@Autowired
	public  MongoService mongoService;
	
	public  void createLogger(String message,ServiceType serviceType,Object payLoad,String apiName,String userId,int statusCode) {
		if (mongoHealthIndicator.health().getStatus() == Status.UP) {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert)
					.logLevel(LogLevel.INFO).logInfo(message)
					.createdTime(new DateTime()).type(serviceType)
					.payLoad(payLoad).apiName(apiName).userId(userId).statusCode(statusCode)
					.build());
		} else {
			mongoService.saveLogInformation(LogRequest.builder().severity(SeverityType.Alert)
					.logLevel(LogLevel.INFO).logInfo(message)
					.createdTime(new DateTime()).type(ServiceType.LOG)
					.payLoad(payLoad).apiName(apiName).userId(userId).statusCode(statusCode)
					.build());
		}
	}
}
