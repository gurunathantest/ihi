package com.ihi.hcs.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ihi.hcs.model.HederaFees;
import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaHtsInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.ServiceType;
import com.ihi.hcs.payload.request.ExceptionLoggerRequest;
import com.ihi.hcs.payload.request.saveHederaHtsInfoRequest;
import com.ihi.hcs.repository.HederaFeesRepo;
import com.ihi.hcs.service.MongoDBLoggerService;

@Component
public class HcsActiveMQConsumer {

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private HederaFeesRepo hederaFeesRepo;

	@JmsListener(destination = "hcs_exception_message_mongodblogger", containerFactory = "jmsFactory")
	public void exceptionMessgeIntoMongoLoggerActiveMQ(ExceptionLoggerRequest loggerRequest) throws JmsException {
		mongoDBLoggerService.createLogger(loggerRequest.getMessage(), ServiceType.HCS, loggerRequest.getPayload(),
				loggerRequest.getUserId(), loggerRequest.getApi(), HttpStatus.BAD_REQUEST.value(),
				loggerRequest.getClientId(), loggerRequest.getTopicId(), loggerRequest.getLogType());

	}

	@JmsListener(destination = "hcs_saveHederaHtsInfo", containerFactory = "jmsFactory")
	public void saveHederaHtsInfo(saveHederaHtsInfoRequest infoRequest) throws JmsException {

		HederaFees fees = hederaFeesRepo.findByServiceAndOperations(infoRequest.getService(),
				infoRequest.getOperation());
		mongoDBLoggerService.savehederaHtsInfo(HederaHtsInfo.builder().clientId(infoRequest.getClientId())
				.service(infoRequest.getService()).operation(infoRequest.getOperation()).fees(fees.getPriceInUsd())
				.userId(infoRequest.getUserId()).logType(infoRequest.getLogType()).build());
	}

	@JmsListener(destination = "hcs_saveSubscribeMessage", containerFactory = "jmsFactory")
	public void saveSubscribeMessage(HederaSubscribeInfo hederaSubscribeInfo) throws JmsException {
		mongoDBLoggerService.createSubscribeLogger(hederaSubscribeInfo);
	}

	@JmsListener(destination = "hcs_saveHcsSubscribeMessage", containerFactory = "jmsFactory")
	public void saveHcsSubscribeMessage(HcsSubscribeInfo hcsSubscribeInfo) throws JmsException {
		mongoDBLoggerService.createHcsSubscribeLogger(hcsSubscribeInfo);
	}

}
