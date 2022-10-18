package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransferTokenRequest {
	
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
	
	private String userId;
}
