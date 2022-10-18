package com.ihi.hts.payload.request;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
@Data
@Builder
@ToString
public class MintTokenRequest {
	
	@NotNull
	private long amount;
	
	@NotNull
	private String tokenId;
	
	@NotNull
	private String tokenOwnerPrivateKey;
	

	private String clientId;
	
	private String userId;
}
