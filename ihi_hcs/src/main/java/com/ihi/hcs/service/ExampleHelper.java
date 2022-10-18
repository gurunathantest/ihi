package com.ihi.hcs.service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;

public final class ExampleHelper {

	private ExampleHelper() {
	}

	public static AccountId getOperatorId(String operatorId) {
		return AccountId.fromString(operatorId);
	}

	public static PrivateKey getOperatorKey(String operatorKey) {
		return PrivateKey.fromString(operatorKey);
	}

}
