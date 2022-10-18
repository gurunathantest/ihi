package com.ihi.hts.payload.request;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
@Data
@Builder
@ToString
public class BurnTokenRequest {

	@NotNull
	private String tokenId;
	
	@NotNull
	private String tokenOwnerPrivateKey;
	
	
	@NotNull
	private long amount;
	
	private String clientId;
	
	private String userId;
}
