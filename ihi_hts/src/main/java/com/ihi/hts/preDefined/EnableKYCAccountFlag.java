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
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
	/*
	 * THIS IS EXAMPLE TEST CLASS USED TO EnableKYCAccountFlag
	 */
public class EnableKYCAccountFlag {
	private static final Logger logger = LoggerFactory.getLogger(EnableKYCAccountFlag.class);
	
	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
	private static final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull("0.0.34049829"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
	private static final PrivateKey OPERATOR_KEY_FROM = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420c07dafe3cf48424050de95e61238c899e818c745e8ec861e0744320c053b0f03"));
	private static final String TOKEN_ID = "0.0.34054344";
	
	private static final AccountId ASSOCIATE_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.34049831"));
	private static final PrivateKey ASSOCIATE_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b65700422042099194f5343dbf70047d54c6d6bcb89845f85fc59cdf6052aa2dc85c0f37b3598"));
	
    public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		
		Client client = Client.forTestnet();
        client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
        //Enable KYC flag on account
        TokenGrantKycTransaction transaction = new TokenGrantKycTransaction()
            .setAccountId(ASSOCIATE_OPERATOR_ID)
            .setTokenId(TokenId.fromString(TOKEN_ID));

        //Freeze the unsigned transaction, sign with the kyc private key of the token, submit the transaction to a Hedera network
        TransactionResponse txResponse = transaction.freezeWith(client).sign(OPERATOR_KEY_FROM).execute(client);
            
        //Request the receipt of the transaction
        TransactionReceipt receipt = txResponse.getReceipt(client);
            
        //Obtain the transaction consensus status
        Status transactionStatus = receipt.status;

        logger.info("The transaction consensus status is " +transactionStatus);

   
	}
}
