package com.ihi.hedera.dto;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

	private String fileId;
	private TokenId tokenId;
	private AccountId accountId;
	private String topicId;
	
}
