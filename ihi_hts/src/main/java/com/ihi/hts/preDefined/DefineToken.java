package com.ihi.hts.preDefined;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;

/*
 * THIS IS EXAMPLE TEST CLASS USED TO DefineToken
 */
public class DefineToken {
	private static final Logger logger = LoggerFactory.getLogger(DefineToken.class);

	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
	private static final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull("0.0.34049829"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
	private static final PrivateKey OPERATOR_KEY_FROM = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420c07dafe3cf48424050de95e61238c899e818c745e8ec861e0744320c053b0f03"));

	public static void main(String[] args)
			throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

		Client client = Client.forTestnet();
		client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
		TokenCreateTransaction transaction = new TokenCreateTransaction().setTokenName("testing ggcoe1")
				.setTokenSymbol("GGCUSD").setTreasuryAccountId(OPERATOR_ID_FROM).setInitialSupply(5000)
				.setMaxTransactionFee(new Hbar(100)).setDecimals(2).setAdminKey(ADMIN_OPERATOR_KEY.getPublicKey())
				.setKycKey(OPERATOR_KEY_FROM.getPublicKey()).setFreezeKey(OPERATOR_KEY_FROM.getPublicKey()).setWipeKey(OPERATOR_KEY_FROM.getPublicKey())
				.setSupplyKey(OPERATOR_KEY_FROM.getPublicKey());

		// Build the unsigned transaction, sign with admin private key of the token,
		// sign with the token treasury private key, submit the transaction to a Hedera
		// network
		TransactionResponse txResponse = transaction.freezeWith(client).sign(ADMIN_OPERATOR_KEY).sign(OPERATOR_KEY_FROM)
				.execute(client);

		// Request the receipt of the transaction
		TransactionReceipt receipt = txResponse.getReceipt(client);

		// Get the token ID from the receipt
		TokenId tokenId = receipt.tokenId;

		// The new token ID is 0.0.179740
		logger.info("The new token ID is " + tokenId);
	}
}
