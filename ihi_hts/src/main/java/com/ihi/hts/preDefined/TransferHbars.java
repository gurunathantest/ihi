package com.ihi.hts.preDefined;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;

public class TransferHbars {

	public static void main(String[] args) throws  TimeoutException, PrecheckStatusException, ReceiptStatusException {
		transferHbar();
		accountBalance("0.0.26060726");

//		transferToken("0.0.26268057");
//		accountBalanceInTokens("0.0.26268057");

	}
	public static void transferHbar() throws TimeoutException, PrecheckStatusException, ReceiptStatusException  {
		AccountId adminId = AccountId.fromString("0.0.4389");
		PrivateKey adminPKey = PrivateKey.fromString(
				"302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab");

		// Create your Hedera testnet client
		Client client = Client.forTestnet();
		client.setOperator(adminId, adminPKey);
		
		AccountId centralBankId = AccountId.fromString("0.0.26060726");
		
		// Transfer hbar
		TransferTransaction transaction = new TransferTransaction()
				.addHbarTransfer(adminId, new Hbar(-5000))
				.addHbarTransfer(centralBankId, new Hbar(5000));

		// Submit the transaction to a Hedera network
		TransactionResponse txResponse = transaction.execute(client);

		// Request the receipt of the transaction
		TransactionReceipt receipt = txResponse.getReceipt(client);

		// Get the transaction consensus status
		Status transactionStatus = receipt.status;
		System.out.println("The transaction consensus status is " + transactionStatus);
	}
	public static void accountBalance(String accountId) throws TimeoutException, PrecheckStatusException {
		AccountId adminId = AccountId.fromString("0.0.4389");
		PrivateKey adminPKey = PrivateKey.fromString("302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab");

		// Create your Hedera testnet client
		Client client = Client.forTestnet();
		client.setOperator(adminId, adminPKey);
		
		AccountId account=AccountId.fromString(accountId);
		//Check the account's balance
		AccountBalance accountBalanceNew = new AccountBalanceQuery()
		     .setAccountId(account)
		     .execute(client);

		System.out.println("The account balance is: " +accountBalanceNew.hbars);
	}
	
	public static void accountBalanceInTokens(String accountId) throws TimeoutException, PrecheckStatusException {
		AccountId adminId = AccountId.fromString("0.0.4389");
		PrivateKey adminPKey = PrivateKey.fromString("302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab");
		 Map tokenBalance = new HashMap();
		// Create your Hedera testnet client
		Client client = Client.forTestnet();
		client.setOperator(adminId, adminPKey);
		//user walletId
		AccountId account=AccountId.fromString(accountId);
		
		//Check the account's token balance
		//Create the query
		AccountBalanceQuery query = new AccountBalanceQuery()
		    .setAccountId(account);
		TokenId myTokenId = TokenId.fromString("0.0.26268054");
		//Sign with the operator private key and submit to a Hedera network
		AccountBalance Balance = query.execute(client);
		tokenBalance=Balance.token;
		long balanceToken = (long) tokenBalance.get(myTokenId);
		System.out.println("The token balance(s) for this account: "+balanceToken);
	}
	
	public static void transferToken(String toAccountId) throws  TimeoutException, PrecheckStatusException, ReceiptStatusException {
		AccountId adminId = AccountId.fromString("0.0.26060726");
		PrivateKey adminPKey = PrivateKey.fromString("302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf");
		
		Client client = Client.forTestnet();
		client.setOperator(adminId, adminPKey);
		
		TokenId myTokenId = TokenId.fromString("0.0.26268054");
		AccountId accountId = AccountId.fromString(toAccountId);
		//Create the transfer transaction
		TransferTransaction transaction = new TransferTransaction()
		     .addTokenTransfer(myTokenId, adminId, -10)
		     .addTokenTransfer(myTokenId, accountId, 10);

		//Sign with the client operator key and submit the transaction to a Hedera network
		TransactionResponse txResponse = transaction.execute(client);

		//Request the receipt of the transaction
		TransactionReceipt receipt = txResponse.getReceipt(client);

		//Get the transaction consensus status
		Status transactionStatus = receipt.status;

		System.out.println("The transaction consensus status is " +transactionStatus);

		//v2.0.1
	}
}