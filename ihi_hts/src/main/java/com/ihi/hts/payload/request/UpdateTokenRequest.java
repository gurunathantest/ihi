package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTokenRequest {

	@NotNull
	private long shard;
	@NotNull
	private long realm;
	@NotNull
	private long num;
	@NotNull
	private String tokenName;
	@NotNull
	private String symbol;
	@NotNull
	private String adminPrivateKey;
	
	private String clientId;
	
	private String userId;
}
