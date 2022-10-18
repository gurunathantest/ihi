package com.ihi.admin.payload.response;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.ihi.admin.mongo.model.LogLevel;
import com.ihi.admin.mongo.model.SeverityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogResponse {
	
	@NotBlank
	private String id;
	
	@NotBlank
	private SeverityType severity;

	@NotBlank
	private LogLevel logLevel;

	@NotBlank
	private String logInfo;

	@NotNull
	private DateTime createdTime;
	
}
