package com.ihi.hedera.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NFTCreationRequest {

	@NotNull
	private String tokenName;
	
	@NotNull
	private String tokenSymbol;
	
	private String clientId;
	
	private String userId;
	
	private int numberOfSupply;
	
	private String userAccountId;
	
	private String userPrivateKey;
}
