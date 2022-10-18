package com.ihi.admin.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.ihi.admin.mongo.model.LogLevel;
import com.ihi.admin.mongo.model.ServiceType;
import com.ihi.admin.mongo.model.SeverityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogRequest {

	@NotBlank
	private SeverityType severity;

	@NotBlank
	private LogLevel logLevel;

	@NotBlank
	private String logInfo;

	@NotNull
	private DateTime createdTime;

	private ServiceType type;
	
	private Object payLoad;
	
	private String apiName;
	
	private String userId;
	
	private int statusCode;
	
}
