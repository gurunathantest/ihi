package com.ihi.admin.serviceImpl;

import java.io.PrintWriter;

import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.admin.mongo.model.AdminLogInfo;
import com.ihi.admin.mongo.model.DataLogInfo;
import com.ihi.admin.mongo.model.LogLevel;
import com.ihi.admin.mongo.model.ServiceType;
import com.ihi.admin.mongo.repository.AdminLogInfoRepository;
import com.ihi.admin.mongo.repository.DataLogInfoRepository;
import com.ihi.admin.payload.request.LogRequest;
import com.ihi.admin.payload.response.LogResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MongoServiceImpl implements MongoService {

	@Autowired
	private DataLogInfoRepository dataLogInfoRepository;

	@Autowired
	private AdminLogInfoRepository adminLogInfoRepository;

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
			adminLogInfoRepository
					.save(AdminLogInfo.builder().severity(logRequest.getSeverity()).logLevel(logRequest.getLogLevel())
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
			return adminLogInfoRepository.findAll().stream()
					.sorted(Comparator.comparing(AdminLogInfo::getCreatedTime).reversed())
					.map(auth -> getAuthLogResponse(auth)).collect(Collectors.toList());
		case DATA:
			return dataLogInfoRepository.findAll().stream()
					.sorted(Comparator.comparing(DataLogInfo::getCreatedTime).reversed())
					.map(data -> getDataLogResponse(data)).collect(Collectors.toList());
		default:
			return null;
		}
	}

	private LogResponse getAuthLogResponse(AdminLogInfo adminLogInfo) {
		return LogResponse.builder().id(adminLogInfo.getId()).severity(adminLogInfo.getSeverity())
				.logLevel(adminLogInfo.getLogLevel()).logInfo(adminLogInfo.getLogInfo())
				.createdTime(adminLogInfo.getCreatedTime()).build();
	}

	private LogResponse getDataLogResponse(DataLogInfo dataLogInfo) {
		return LogResponse.builder().id(dataLogInfo.getId()).severity(dataLogInfo.getSeverity())
				.logLevel(dataLogInfo.getLogLevel()).logInfo(dataLogInfo.getLogInfo())
				.createdTime(dataLogInfo.getCreatedTime()).build();
	}

}
