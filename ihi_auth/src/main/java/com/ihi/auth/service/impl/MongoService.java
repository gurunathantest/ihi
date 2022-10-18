package com.ihi.auth.service.impl;

import java.util.List;

import com.ihi.auth.mongo.model.ServiceType;
import com.ihi.auth.payload.request.LogRequest;
import com.ihi.auth.payload.response.LogResponse;


public interface MongoService {
	
	public String getExceptionToString(Exception exception);
	
	public void saveLogInformation(LogRequest logRequest);
	
	public List<LogResponse> fetchLogInformations(ServiceType serviceType);

}
