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
public class UserNftCreationRequest {

	@NotNull
	private String tokenName;

	@NotNull
	private String tokenSymbol;

	@NotNull
	private String metaData;

	@NotNull
	private String userAccountId;

	@NotNull
	private String userPrivateKey;

	private String userId;

	private String clientId;
}
