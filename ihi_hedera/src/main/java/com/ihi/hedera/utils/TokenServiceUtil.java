package com.ihi.hedera.utils;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;

@Service
public class TokenServiceUtil {

	public TokenId createToken(String fileId,String operatorId,String operatorKey) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.4389"));
		  final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull("0.0.55398"));
		  final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
				"302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab"));
		  final PrivateKey OPERATOR_KEY_FROM = PrivateKey.fromString(Objects.requireNonNull(
				"302e020100300506032b65700422042004ccc8ee33fb3e16a8d174e16d1ca3bb65220b874b6b3164f5de1dbc6a6ca7e3"));
		
		  Client client = Client.forTestnet();
			client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
			TokenCreateTransaction transaction = new TokenCreateTransaction().setTokenName("Associate Token Test 3")
					.setTokenSymbol("$"+fileId).setTreasuryAccountId(OPERATOR_ID_FROM).setInitialSupply(1)
					.setMaxTransactionFee(new Hbar(100));

			// Build the unsigned transaction, sign with admin private key of the token,
			// sign with the token treasury private key, submit the transaction to a Hedera
			// network
			TransactionResponse txResponse = transaction.freezeWith(client).sign(ADMIN_OPERATOR_KEY).sign(OPERATOR_KEY_FROM)
					.execute(client);

			// Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);

			// Get the token ID from the receipt
			TokenId tokenId = receipt.tokenId;

		System.out.println("The new token ID is " + tokenId);
		return tokenId;
	}
	

}
