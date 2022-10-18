package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PowerTransitionTokenRequest {
	
	@NotNull
	private long amount;
	
	@NotNull
	private String tokenId;
	
	@NotNull
	private String fromSenderId;
	
	@NotNull
	private String fromSenderKey;
	
	@NotNull
	private String toAccountId;
	
	private String clientId;
	
	private String trade;
}
