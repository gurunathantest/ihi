package com.ihi.hedera.payload.request;

import lombok.Data;

@Data
public class AccountBean {
	private long nodeAccountNum;
	private String privKey;
}
