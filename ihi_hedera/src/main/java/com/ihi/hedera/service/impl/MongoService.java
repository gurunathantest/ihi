package com.ihi.hedera.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ihi.hedera.mongo.model.HederaHtsInfo;
import com.ihi.hedera.mongo.model.ServiceType;
import com.ihi.hedera.payload.request.LogRequest;
import com.ihi.hedera.payload.response.LogResponse;


@Component
public interface MongoService {
	
	public String getExceptionToString(Exception exception);
	
	public void saveLogInformation(LogRequest logRequest);
	
	public List<LogResponse> fetchLogInformations(ServiceType serviceType);
	
	public void saveHederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest);

}
