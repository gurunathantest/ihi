package com.ihi.hedera.payload.request;

import com.hedera.hashgraph.sdk.PrivateKey;

import lombok.Data;

@Data
public class NFTTokenRequest {
	private PrivateKey nftToken;
	private String tokenName;
	private String tokenSymbol;

	private String clientId;

	private String userId;
}
