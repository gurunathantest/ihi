package com.ihi.hts.preDefined;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;

/*
 * THIS IS EXAMPLE TEST CLASS USED TO GetTokenInfo
 */
public class GetTokenInfo {
	private static final Logger logger = LoggerFactory.getLogger(GetTokenInfo.class);

	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.4389"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab"));
	

	public static void main(String[] args)
			throws TimeoutException, HederaReceiptStatusException, PrecheckStatusException {

		Client client = Client.forTestnet();
		client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
		TokenInfoQuery query = new TokenInfoQuery().setTokenId(new TokenId(0, 0, 179740));

		// Sign with the client operator private key, submit the query to the network
		// and get the token supply
		TokenInfo tokenSupply = query.execute(client);

		TokenInfoQuery query1 = new TokenInfoQuery().setTokenId(new TokenId(0, 0, 179305));

		// Sign with the client operator private key, submit the query to the network
		// and get the token supply
		long tokenSupply1 = query1.execute(client).totalSupply;
		logger.info("The token info is " + tokenSupply);
		logger.info("The token info is " + tokenSupply1);
	}
}
