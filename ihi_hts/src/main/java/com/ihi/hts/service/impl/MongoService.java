package com.ihi.hts.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ihi.hts.mongo.model.HederaHtsInfo;
import com.ihi.hts.mongo.model.ServiceType;
import com.ihi.hts.payload.request.LogRequest;
import com.ihi.hts.payload.response.LogResponse;


@Component
public interface MongoService {
	
	public String getExceptionToString(Exception exception);
	
	public void saveLogInformation(LogRequest logRequest);
	
	public List<LogResponse> fetchLogInformations(ServiceType serviceType);
	
	public void saveHederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest);

}
