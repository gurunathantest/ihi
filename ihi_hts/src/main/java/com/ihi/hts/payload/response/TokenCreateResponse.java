package com.ihi.hts.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenCreateResponse {
	private String tokenId;
	private String tokenName;
	private String symbol;
	private String treasuryAccountId;
	private String treasuryPrivateKey;
	private int decimal;
	private long initialSupply;
	private String adminKey;
	private String defaultFrozen;
	private String freezeKey;
	private String kycKey;
	private String supplyManagerKey;
	private String tokenRenewalAccount;
	private String wipeKey;
	private long tokenInitValue;
}
