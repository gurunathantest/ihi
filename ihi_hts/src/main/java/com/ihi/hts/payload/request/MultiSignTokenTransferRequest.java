package com.ihi.hts.payload.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiSignTokenTransferRequest {
	
	private long amount;
	
	private String recieverId;
	
	private String senderId;
	
	private String userId;
	
	private String clientId;
	
	private String tokenId;
	
	private List<GroupWalletPrivateKeyDto> responseWallet; 

}
