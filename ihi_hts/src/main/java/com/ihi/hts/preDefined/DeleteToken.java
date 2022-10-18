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
import com.hedera.hashgraph.sdk.TokenDeleteTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
	/*
	 * THIS IS EXAMPLE TEST CLASS USED TO DeleteToken
	 */
public class DeleteToken {
	private static final Logger logger = LoggerFactory.getLogger(DeleteToken.class);
	
	private static final AccountId OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.55806"));
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull("302e020100300506032b657004220420f598d94c2b2b9956c511288ce4245d327005ce3e362e70b5544fcb29e45fbd1a"));
    
    public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		
		Client client = Client.forTestnet();
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);
        TokenDeleteTransaction transaction = new TokenDeleteTransaction()
        	     .setTokenId(new TokenId(0,0,209992));

        	//Freeze the unsigned transaction, sign with the admin private key of the account, submit the transaction to a Hedera network
        	TransactionResponse txResponse = transaction.freezeWith(client).sign(OPERATOR_KEY).execute(client);

        	//Request the receipt of the transaction
        	TransactionReceipt receipt = txResponse.getReceipt(client);

        	//Obtain the transaction consensus status
        	Status transactionStatus = receipt.status;

        	logger.info("The transaction consensus status is " +transactionStatus);
	}
}
