package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AssociateKycUserRequest {
	

	@NotNull
	private String operatorId;
	
	@NotNull
	private String operatorKey;
	
	@NotNull
	private String tokenId;
	
	
	@NotNull
	private String adminOperatorId;
	
	@NotNull
	private String adminOperatorKey;
	
	private String clientId;
	
	private String userId;

}
