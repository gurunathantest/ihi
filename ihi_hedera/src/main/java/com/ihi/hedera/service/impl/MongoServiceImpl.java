package com.ihi.hedera.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ihi.hedera.mongo.model.DataLogInfo;
import com.ihi.hedera.mongo.model.HederaHtsInfo;
import com.ihi.hedera.mongo.model.HederaLogInfo;
import com.ihi.hedera.mongo.model.LogLevel;
import com.ihi.hedera.mongo.model.ServiceType;
import com.ihi.hedera.mongo.repository.DataLogInfoRepository;
import com.ihi.hedera.mongo.repository.HederaHtsInfoRepository;
import com.ihi.hedera.mongo.repository.HederaLogInfoRepository;
import com.ihi.hedera.payload.request.LogRequest;
import com.ihi.hedera.payload.response.LogResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MongoServiceImpl implements MongoService {

	@Autowired
	private DataLogInfoRepository dataLogInfoRepository;

	@Autowired
	private HederaLogInfoRepository hederaLogInfoRepository;

	@Autowired
	private HederaHtsInfoRepository hederaHtsInfoRepository;

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
		case HEDERA:
			hederaLogInfoRepository.save(HederaLogInfo.builder().severity(logRequest.getSeverity())
					.logLevel(logRequest.getLogLevel()).logInfo(logRequest.getLogInfo())
					.createdTime(logRequest.getCreatedTime()).payLoad(logRequest.getPayLoad())
					.apiName(logRequest.getApiName()).userId(logRequest.getUserId())
					.statusCode(logRequest.getStatusCode()).clientId(logRequest.getClientId()).build());
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
		case HEDERA:
			return hederaLogInfoRepository.findAll().stream()
					.sorted(Comparator.comparing(HederaLogInfo::getCreatedTime).reversed())
					.map(auth -> getAuthLogResponse(auth)).collect(Collectors.toList());
		default:
			return null;
		}
	}

	private LogResponse getAuthLogResponse(HederaLogInfo authLogInfo) {
		return LogResponse.builder().id(authLogInfo.getId()).severity(authLogInfo.getSeverity())
				.logLevel(authLogInfo.getLogLevel()).logInfo(authLogInfo.getLogInfo())
				.createdTime(authLogInfo.getCreatedTime()).build();
	}

	private LogResponse getDataLogResponse(DataLogInfo dataLogInfo) {
		return LogResponse.builder().id(dataLogInfo.getId()).severity(dataLogInfo.getSeverity())
				.logLevel(dataLogInfo.getLogLevel()).logInfo(dataLogInfo.getLogInfo())
				.createdTime(dataLogInfo.getCreatedTime()).build();
	}

	@Async
	@Override
	public void saveHederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest) {
		hederaHtsInfoRepository.save(hederaHtsInfoRequest);

	}

}
