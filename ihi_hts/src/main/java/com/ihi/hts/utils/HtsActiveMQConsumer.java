package com.ihi.hts.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ihi.hts.model.HederaFees;
import com.ihi.hts.mongo.model.HederaHtsInfo;
import com.ihi.hts.mongo.model.ServiceType;
import com.ihi.hts.payload.request.ExceptionLoggerRequest;
import com.ihi.hts.payload.request.saveHederaHtsInfoRequest;
import com.ihi.hts.repository.HederaFeesRepo;
import com.ihi.hts.service.impl.MongoDBLoggerService;

@Component
public class HtsActiveMQConsumer {

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private HederaFeesRepo hederaFeesRepo;

	@JmsListener(destination = "hts_exception_message_mongodblogger", containerFactory = "jmsFactory")
	public void exceptionMessgeIntoMongoLoggerActiveMQ(ExceptionLoggerRequest loggerRequest) throws JmsException {

		mongoDBLoggerService.createLogger(loggerRequest.getMessage(), ServiceType.HTS, loggerRequest.getPayload(),
				loggerRequest.getApi(), loggerRequest.getUserId(), HttpStatus.BAD_REQUEST.value(),
				loggerRequest.getClientId(), loggerRequest.getTransactionReceipt(), loggerRequest.getLogType());

	}

	@JmsListener(destination = "hts_saveHederaHtsInfo", containerFactory = "jmsFactory")
	public void saveHederaHtsInfo(saveHederaHtsInfoRequest infoRequest) throws JmsException {

		HederaFees fees = hederaFeesRepo.findByServiceAndOperations(infoRequest.getService(),
				infoRequest.getOperation());
		mongoDBLoggerService.savehederaHtsInfo(HederaHtsInfo.builder().clientId(infoRequest.getClientId())
				.service(infoRequest.getService()).operation(infoRequest.getOperation()).fees(fees.getPriceInUsd())
				.userId(infoRequest.getUserId()).logType(infoRequest.getLogType()).build());

	}

}
