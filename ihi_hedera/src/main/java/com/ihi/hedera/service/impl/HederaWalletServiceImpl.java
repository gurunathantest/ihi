package com.ihi.hedera.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.ihi.hedera.exception.HederaException;
import com.ihi.hedera.payload.request.GroupWalletPrivateKeyDto;
import com.ihi.hedera.payload.request.MultiSignWalletRequest;
import com.ihi.hedera.payload.request.WalletRequest;
import com.ihi.hedera.payload.response.GroupWalletCreateResponse;
import com.ihi.hedera.payload.response.MessageResponse;
import com.ihi.hedera.payload.response.WalletCreateResponse;
import com.ihi.hedera.service.ExampleHelper;
import com.ihi.hedera.service.HederaWalletService;
import com.ihi.hedera.utils.AsynchronousUtils;

@Service
public class HederaWalletServiceImpl implements HederaWalletService {

	@Autowired
	Environment env;

	@Value("${H721_NETWORK}")
	private String network;

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private AsynchronousUtils asynchronousUtils;

	private Client client = null;

	@Override
	public ResponseEntity<?> createWallet() {
		client = setOperator();
		PrivateKey newKey = null;
		AccountId newAccountId = null;
		try {
			// Old code
			// newKey = PrivateKey.generate();
			// New code
			newKey = PrivateKey.generateED25519();
			TransactionResponse txId = new AccountCreateTransaction().setKey(newKey.getPublicKey())
					.setInitialBalance(Hbar.fromTinybars(1000)).execute(client);
			TransactionReceipt receipt = txId.getReceipt(client);
			newAccountId = receipt.accountId;
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("wallet.created.failed") + "," + e.getMessage(), "/api/hedera/wallet/create", "",
					"v1", "v1");
			throw new HederaException(e.getMessage());
		}
		String account = newAccountId.shard + "." + newAccountId.realm + "." + newAccountId.num;
		WalletCreateResponse walletCreateResponse = new WalletCreateResponse();
		walletCreateResponse.setAccoundId(newAccountId.num);
		walletCreateResponse.setPrivatekey(newKey.toString());
		walletCreateResponse.setShard(newAccountId.shard);
		walletCreateResponse.setReal(newAccountId.realm);
		walletCreateResponse.setAccount(account);
		walletCreateResponse.setPublickey(newKey.getPublicKey().toString());

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("wallet.created.success")).response(walletCreateResponse)
				.httpStatus(HttpStatus.OK).build());
	}

	private Client setOperator() {
		if (client != null)
			return client;

		String operatorId = env.getProperty("H721_OPERATOR_ID");
		String operatorKey = env.getProperty("H721_OEPRATOR_KEY");
		if (network.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		}
		return client;
	}
	
	
	@Override
	public ResponseEntity<?> v2CreateWallet(WalletRequest request) {
		client = setOperator();
		PrivateKey newKey = null;
		AccountId newAccountId = null;
		try {
			// Old code
			// newKey = PrivateKey.generate();
			// New code
			newKey = PrivateKey.generateED25519();
			TransactionResponse txId = new AccountCreateTransaction().setKey(newKey.getPublicKey())
					.setInitialBalance(Hbar.fromTinybars(1000)).execute(client);
			TransactionReceipt receipt = txId.getReceipt(client);
			newAccountId = receipt.accountId;
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("wallet.created.failed") + "," + e.getMessage(), "/api/hedera/wallet/create", "",
					request.getUserId(), request.getClientId());
			throw new HederaException(e.getMessage());
		}
		String account = newAccountId.shard + "." + newAccountId.realm + "." + newAccountId.num;
		WalletCreateResponse walletCreateResponse = new WalletCreateResponse();
		walletCreateResponse.setAccoundId(newAccountId.num);
		walletCreateResponse.setPrivatekey(newKey.toString());
		walletCreateResponse.setShard(newAccountId.shard);
		walletCreateResponse.setReal(newAccountId.realm);
		walletCreateResponse.setAccount(account);
		walletCreateResponse.setPublickey(newKey.getPublicKey().toString());

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("wallet.created.success")).response(walletCreateResponse)
				.httpStatus(HttpStatus.OK).build());
	}
	
	@Override
	public ResponseEntity<?> multiSigncreateWallet(List<MultiSignWalletRequest> list) {
		client = setOperator();
		AccountId newAccountId = null;
		// create a multi-sig account
		KeyList keylist = new KeyList();

		List<GroupWalletPrivateKeyDto> privateKeyList1 = new ArrayList<GroupWalletPrivateKeyDto>();

		list.forEach(keyy -> {
			GroupWalletPrivateKeyDto groupRespoList = new GroupWalletPrivateKeyDto();
			PrivateKey userPrivateKey = PrivateKey.generateED25519();
			PublicKey userPublicKey = userPrivateKey.getPublicKey();
			groupRespoList.setPrivateKey(userPrivateKey.toString());
			groupRespoList.setPublicKey(userPublicKey.toString());
			privateKeyList1.add(groupRespoList);
			keylist.add(userPrivateKey);
		});

		@Var
		TransactionReceipt receipt = null;
		try {
			TransactionResponse createAccountTransaction = new AccountCreateTransaction().setInitialBalance(new Hbar(2))
					.setKey(keylist).execute(client);

			receipt = createAccountTransaction.getReceipt(client);
		} catch (TimeoutException | PrecheckStatusException | ReceiptStatusException e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("wallet.created.failed") + "," + e.getMessage(),
					"/api/hedera//multisignwallet/create", "", "", "");
		}

		newAccountId = receipt.accountId;
		System.out.println("account id = " + receipt.accountId);
		String account = newAccountId.shard + "." + newAccountId.realm + "." + newAccountId.num;
		GroupWalletCreateResponse walletCreateResponse = new GroupWalletCreateResponse();
		walletCreateResponse.setAccoundId(newAccountId.num);

		walletCreateResponse.setShard(newAccountId.shard);
		walletCreateResponse.setReal(newAccountId.realm);
		walletCreateResponse.setAccount(account);

		walletCreateResponse.setGroupWalletPrivateKeyDto(privateKeyList1);

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value()).message("success")
				.response(walletCreateResponse).httpStatus(HttpStatus.OK).build());
	}


}
