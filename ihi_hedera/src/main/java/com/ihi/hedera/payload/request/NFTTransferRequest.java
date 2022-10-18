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
public class NFTTransferRequest {
	
	@NotNull
	private String tokenId;
	
	@NotNull
	private String senderAccountId;
	
	@NotNull
	private String userPrivateKey;

	private String userId;
	
	private String clientId;
	
	private int serialNumber;
	
	private String receiverAccountId;
}
