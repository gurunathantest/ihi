package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class CreateTokenRequest {
	
	private String tokenId;
	
	@NotNull
	private String tokenName;
	
	@NotNull
	private String symbol;
	
	@NotNull
	private String treasuryAccountId;
	
	@NotNull
	private String treasuryPrivateKey;
	
	@NotNull
	private int decimal;
	
	@NotNull
	private long initialSupply;
	
	private boolean adminKey;
	
	private boolean defaultFrozen;
	
	private boolean freezeKey;
	
	private boolean kycKey;
	
	private boolean supplyManagerKey;
	
	private boolean tokenRenewalAccount;
	
	private boolean wipeKey;
	
	private String clientId;
	
	private String userId;
}
