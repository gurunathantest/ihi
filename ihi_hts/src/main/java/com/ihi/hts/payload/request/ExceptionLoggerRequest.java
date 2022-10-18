package com.ihi.hts.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionLoggerRequest {
	private String message;
	private String api;
	private String payload;
	private String userId;
	private String clientId;
	private String transactionReceipt;
	private String logType;
}
