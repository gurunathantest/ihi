package com.ihi.auth.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.auth.mongo.model.AuthLogInfo;
import com.ihi.auth.mongo.model.DataLogInfo;
import com.ihi.auth.mongo.model.LogLevel;
import com.ihi.auth.mongo.model.ServiceType;
import com.ihi.auth.mongo.repository.AuthLogInfoRepository;
import com.ihi.auth.mongo.repository.DataLogInfoRepository;
import com.ihi.auth.payload.request.LogRequest;
import com.ihi.auth.payload.response.LogResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MongoServiceImpl implements MongoService {

	@Autowired
	private DataLogInfoRepository dataLogInfoRepository;

	@Autowired
	private AuthLogInfoRepository authLogInfoRepository;

	@Override
	public String getExceptionToString(Exception exception) {
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	@Async
	@Override
	public void saveLogInformation(LogRequest logRequest) {
		ServiceType type;
		type = logRequest.getType();
		switch (type) {
		case AUTH:
			authLogInfoRepository
					.save(AuthLogInfo.builder().severity(logRequest.getSeverity()).logLevel(logRequest.getLogLevel())
							.logInfo(logRequest.getLogInfo()).createdTime(logRequest.getCreatedTime())
							.payLoad(logRequest.getPayLoad()).apiName(logRequest.getApiName())
							.userId(logRequest.getUserId()).statusCode(logRequest.getStatusCode()).build());
			break;

		case DATA:
			dataLogInfoRepository
					.save(DataLogInfo.builder().severity(logRequest.getSeverity()).logLevel(logRequest.getLogLevel())
							.logInfo(logRequest.getLogInfo()).createdTime(logRequest.getCreatedTime())
							.payLoad(logRequest.getPayLoad()).apiName(logRequest.getApiName())
							.userId(logRequest.getUserId()).statusCode(logRequest.getStatusCode()).build());
			break;

		default:
			if (LogLevel.INFO == logRequest.getLogLevel()) {
				log.info(logRequest.getLogInfo());
			} else {
				log.error(logRequest.getLogInfo());
			}
			break;
		}

	}

	@Override
	public List<LogResponse> fetchLogInformations(ServiceType serviceType) {
		ServiceType type;
		type = serviceType;
		switch (type) {
		case AUTH:
			return authLogInfoRepository.findAll().stream()
					.sorted(Comparator.comparing(AuthLogInfo::getCreatedTime).reversed())
					.map(auth -> getAuthLogResponse(auth)).collect(Collectors.toList());
		case DATA:
			return dataLogInfoRepository.findAll().stream()
					.sorted(Comparator.comparing(DataLogInfo::getCreatedTime).reversed())
					.map(data -> getDataLogResponse(data)).collect(Collectors.toList());
		default:
			return null;
		}
	}

	private LogResponse getAuthLogResponse(AuthLogInfo authLogInfo) {
		return LogResponse.builder().id(authLogInfo.getId()).severity(authLogInfo.getSeverity())
				.logLevel(authLogInfo.getLogLevel()).logInfo(authLogInfo.getLogInfo())
				.createdTime(authLogInfo.getCreatedTime()).build();
	}

	private LogResponse getDataLogResponse(DataLogInfo dataLogInfo) {
		return LogResponse.builder().id(dataLogInfo.getId()).severity(dataLogInfo.getSeverity())
				.logLevel(dataLogInfo.getLogLevel()).logInfo(dataLogInfo.getLogInfo())
				.createdTime(dataLogInfo.getCreatedTime()).build();
	}

}
