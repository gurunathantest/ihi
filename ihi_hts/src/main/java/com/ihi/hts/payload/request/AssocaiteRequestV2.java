package com.ihi.hts.payload.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssocaiteRequestV2 {
	
	@NotNull
	private String adminOperatorId;
	
	@NotNull
	private String adminOperatorKey;

	private String tokenId;
	
	private String operatorId;
	
	private String clientId;
	
	private String userId;
	
	private List<GroupWalletPrivateKeyDto> responseWallet; 
}
