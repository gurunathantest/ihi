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
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;

/*
 * THIS IS EXAMPLE TEST CLASS USED TO TransferToken
 */
public class TransferToken {
	private static final Logger logger = LoggerFactory.getLogger(TransferToken.class);

	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
	private static final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull("0.0.45952279"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
	private static final PrivateKey OPERATOR_KEY_FROM = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b6570042204208d19d10ecc18b3c85f2df85781f6bc244fe6cc89717a226214e00a6e06fe031c"));
	private static final String TOKEN_ID = "0.0.45949596";
	
	private static final AccountId ASSOCIATE_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.45949632"));
	private static final PrivateKey ASSOCIATE_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b65700422042099194f5343dbf70047d54c6d6bcb89845f85fc59cdf6052aa2dc85c0f37b3598"));
	
	public static void main(String[] args)
			throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

		Client client = Client.forTestnet();
		client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
		// Create the transfer transaction
		TransferTransaction transaction = new TransferTransaction().setTransactionMemo("testing token")
				.addTokenTransfer(TokenId.fromString(TOKEN_ID),OPERATOR_ID_FROM,  -500)
				.addTokenTransfer(TokenId.fromString(TOKEN_ID),ASSOCIATE_OPERATOR_ID , 500).freezeWith(client);

		// Sign with the client operator key and submit the transaction to a Hedera
		
		TransactionResponse txResponse = transaction.sign(OPERATOR_KEY_FROM).execute(client);
			System.out.println(txResponse.getReceipt(client).status);
		
		logger.info("The transaction consensus status is " + txResponse.getRecord(client));

	}
}
