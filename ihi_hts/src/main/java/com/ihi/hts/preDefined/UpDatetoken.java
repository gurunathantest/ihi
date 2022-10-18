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
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenUpdateTransaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
	/*
	 * THIS IS EXAMPLE TEST CLASS USED TO UpDatetoken
	 */
public class UpDatetoken {
	private static final Logger logger = LoggerFactory.getLogger(UpDatetoken.class);
	
	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
	public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		
		Client client = Client.forTestnet();
        client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
      //Create the transaction 
        TokenUpdateTransaction transaction = new TokenUpdateTransaction()
             .setTokenId(new TokenId(0,0,46822390))
             .setTokenName("BIT")
             .setTokenSymbol("B");

        //Freeze the unsigned transaction, sign with the admin private key of the token, submit the transaction to a Hedera network
        TransactionResponse txResponse = transaction.freezeWith(client).sign(ADMIN_OPERATOR_KEY).execute(client);

        //Request the receipt of the transaction
        TransactionReceipt receipt = txResponse.getReceipt(client);

        //Get the transaction consensus status
        Status transactionStatus = receipt.status;

        logger.info("The transaction consensus status is " +transactionStatus);
	}
}
