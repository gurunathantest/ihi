package com.ihi.hts.utils;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.ihi.hts.service.ExampleHelper;
import com.ihi.hts.service.TokenHelperService;

@Service
public class NFTTokenUtil {
	
	@Autowired
	TokenHelperService tokenHelper;

	public TokenId createToken(String fileId,String operatorId,String operatorKey) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		  final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull(operatorId));
		  final PrivateKey OPERATOR_KEY_FROM = PrivateKey.fromString(Objects.requireNonNull(operatorKey));
		
		  Client client = tokenHelper.setOperator();
			TokenCreateTransaction transaction = new TokenCreateTransaction().setTokenName("Associate Token Test 3")
					.setTokenSymbol("$"+fileId).setTreasuryAccountId(OPERATOR_ID_FROM).setInitialSupply(1)
					.setMaxTransactionFee(new Hbar(100));

			// Build the unsigned transaction, sign with admin private key of the token,
			// sign with the token treasury private key, submit the transaction to a Hedera
			// network
			TransactionResponse txResponse = transaction.freezeWith(client).sign(ExampleHelper.getOperatorKey(tokenHelper.getOperatorPrivateKey())).sign(OPERATOR_KEY_FROM)
					.execute(client);

			// Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);

			// Get the token ID from the receipt
			TokenId tokenId = receipt.tokenId;

		System.out.println("The new token ID is " + tokenId);
		return tokenId;
	}
}
