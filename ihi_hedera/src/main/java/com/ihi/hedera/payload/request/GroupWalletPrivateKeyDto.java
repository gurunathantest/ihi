package com.ihi.hedera.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupWalletPrivateKeyDto {
	private String privateKey;
	private String publicKey;
	private String walleltId;
	
}
