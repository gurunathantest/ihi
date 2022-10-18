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
public class NFTMintRequest {
	
	@NotNull
	private String tokenId;
	
	@NotNull
	private String metaData;
	
	private String clientId;
	
	private String userId;
	
	private String userPrivateKey;
	

}
