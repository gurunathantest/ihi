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
public class NFTAssociateRequest {
	
	@NotNull
	private String senderAccountId;	
	@NotNull
	private String userPrivateKey;
	
	@NotNull
	private String tokenId;
 
	private String clientId;

	private String userId;
}
