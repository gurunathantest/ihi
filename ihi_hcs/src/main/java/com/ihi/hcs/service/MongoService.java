package com.ihi.hcs.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaHtsInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.ServiceType;
import com.ihi.hcs.payload.request.LogRequest;
import com.ihi.hcs.payload.response.LogResponse;


@Component
public interface MongoService {
	
	public String getExceptionToString(Exception exception);
	
	public void saveLogInformation(LogRequest logRequest);
	
	public List<LogResponse> fetchLogInformations(ServiceType serviceType);
	
	public void saveSubscribe(HederaSubscribeInfo request);
 
	public void saveHederaHtsInfo(HederaHtsInfo hederaHtsInfoRequest);

	public void saveHcsSubscribe(HcsSubscribeInfo hcsSubscribeInfo);
}
