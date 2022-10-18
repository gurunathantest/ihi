package com.ihi.hedera.payload.response;

import java.util.List;

import com.ihi.hedera.payload.request.GroupWalletPrivateKeyDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupWalletCreateResponse {	
	private String account;
	private long shard;
	private long real;
	private long accoundId;
	private List<GroupWalletPrivateKeyDto> groupWalletPrivateKeyDto;

}
