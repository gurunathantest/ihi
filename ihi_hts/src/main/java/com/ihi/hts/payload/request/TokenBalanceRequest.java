package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TokenBalanceRequest {

	@NotNull
	private String operatorId;

	@NotNull
	private String tokenId;
	
	private String clientId;
	
	private String userId;
	
}
