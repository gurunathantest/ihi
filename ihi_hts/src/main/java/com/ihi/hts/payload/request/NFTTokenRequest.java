package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class NFTTokenRequest {
	
	private String clientId;
	
	@NotNull
	private String fileId;
	
	@NotNull
	private String operatorId;
	
	@NotNull
	private String operatorKey;
	
	private String userId;
}