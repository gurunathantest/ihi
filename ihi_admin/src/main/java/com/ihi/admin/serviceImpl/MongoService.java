package com.ihi.admin.serviceImpl;

import java.util.List;

import com.ihi.admin.mongo.model.ServiceType;
import com.ihi.admin.payload.request.LogRequest;
import com.ihi.admin.payload.response.LogResponse;



public interface MongoService {
	
	public String getExceptionToString(Exception exception);
	
	public void saveLogInformation(LogRequest logRequest);
	
	public List<LogResponse> fetchLogInformations(ServiceType serviceType);

}
