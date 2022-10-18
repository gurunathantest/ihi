package com.ihi.hcs.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class saveHederaHtsInfoRequest {

	private String service;
	private String operation;
	private String clientId;
	private String userId;
	private String logType;
}
