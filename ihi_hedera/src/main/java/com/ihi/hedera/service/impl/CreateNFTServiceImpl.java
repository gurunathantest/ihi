package com.ihi.hedera.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.FileContentsQuery;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenNftInfo;
import com.hedera.hashgraph.sdk.TokenNftInfoQuery;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.ihi.hedera.constant.ApiConstant;
import com.ihi.hedera.constant.HederaHtsInfoConstnat;
import com.ihi.hedera.exception.HederaException;
import com.ihi.hedera.payload.request.CreateFileRequest;
import com.ihi.hedera.payload.request.FileCreationRequest;
import com.ihi.hedera.payload.request.NFTAssociateRequest;
import com.ihi.hedera.payload.request.NFTCreationRequest;
import com.ihi.hedera.payload.request.NFTMintRequest;
import com.ihi.hedera.payload.request.NFTTransferRequest;
import com.ihi.hedera.payload.request.UserNftCreationRequest;
import com.ihi.hedera.payload.response.MessageResponse;
import com.ihi.hedera.service.CreateNFTService;
import com.ihi.hedera.service.ExampleHelper;
import com.ihi.hedera.utils.AsynchronousUtils;
import com.ihi.hedera.utils.FileServiceUtil;

@Service
public class CreateNFTServiceImpl implements CreateNFTService {

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
	public ResponseEntity<?> createFile(FileCreationRequest fileCreationRequest) throws InterruptedException {
		client = setOperator();
		String fileId = "";
		try {
			FileServiceUtil fileServiceUtil = new FileServiceUtil();
			fileId = fileServiceUtil.fileCreate(fileCreationRequest.getFileContent(), client,
					env.getProperty("H721_OEPRATOR_KEY"));

		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.create.nft") + "," + e.getMessage(), ApiConstant.CREATE_FILE,
					fileCreationRequest, fileCreationRequest.getUserId(), fileCreationRequest.getClientId());
			throw new HederaException(e.getMessage());
		}

		asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.FILE, HederaHtsInfoConstnat.FILE_CREATE,
				fileCreationRequest.getClientId());
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("file.created.success")).response(fileId).httpStatus(HttpStatus.OK).build());
	}

	@Override
	public ResponseEntity<?> fileCreate(CreateFileRequest createFileRequest) {
		client = setOperator();
		String fileId = "";
		try {
			FileServiceUtil fileServiceUtil = new FileServiceUtil();
			fileId = fileServiceUtil.createFile(createFileRequest.getFileContent(), client,
					env.getProperty("H721_OEPRATOR_KEY"));

		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.create.nft") + "," + e.getMessage(), ApiConstant.V2_CREATE_FILE,
					createFileRequest, createFileRequest.getUserId(), createFileRequest.getClientId());
			throw new HederaException(e.getMessage());
		}

		asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.FILE, HederaHtsInfoConstnat.FILE_CREATE,
				createFileRequest.getClientId());
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("file.created.success")).response(fileId).httpStatus(HttpStatus.OK).build());
	}

	private Client setOperator() {

		if (client != null)
			return client;

		String operatorId = env.getProperty("H721_OPERATOR_ID");
		String operatorKey = env.getProperty("H721_OEPRATOR_KEY");
		if (network.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
			/*
			 * try { Map network = new HashMap<String, AccountId>();
			 * network.put("0.testnet.hedera.com:50211", new AccountId(3));
			 * network.put("34.94.106.61:50211", new AccountId(3));
			 * network.put("50.18.132.211:50211", new AccountId(3));
			 * network.put("138.91.142.219:50211", new AccountId(3));
			 * 
			 * network.put("1.testnet.hedera.com:50211", new AccountId(4));
			 * network.put("35.237.119.55:50211", new AccountId(4));
			 * network.put("3.212.6.13:50211", new AccountId(4));
			 * network.put("52.168.76.241:50211", new AccountId(4));
			 * 
			 * network.put("2.testnet.hedera.com:50211", new AccountId(5));
			 * network.put("35.245.27.193:50211", new AccountId(5));
			 * network.put("52.20.18.86:50211", new AccountId(5));
			 * network.put("40.79.83.124:50211", new AccountId(5));
			 * 
			 * network.put("3.testnet.hedera.com:50211", new AccountId(6));
			 * network.put("34.83.112.116:50211", new AccountId(6));
			 * network.put("54.70.192.33:50211", new AccountId(6));
			 * network.put("52.183.45.65:50211", new AccountId(6));
			 * 
			 * network.put("4.testnet.hedera.com:50211", new AccountId(7));
			 * network.put("34.94.160.4:50211", new AccountId(7));
			 * network.put("54.176.199.109:50211", new AccountId(7));
			 * network.put("13.64.181.136:50211", new AccountId(7));
			 * 
			 * network.put("5.testnet.hedera.com:50211", new AccountId(8));
			 * network.put("34.106.102.218:50211", new AccountId(8));
			 * network.put("35.155.49.147:50211", new AccountId(8));
			 * network.put("13.78.238.32:50211", new AccountId(8));
			 * 
			 * network.put("6.testnet.hedera.com:50211", new AccountId(9));
			 * network.put("34.133.197.230:50211", new AccountId(9));
			 * network.put("52.14.252.207:50211", new AccountId(9));
			 * network.put("52.165.17.231:50211", new AccountId(9));
			 * client.setNetwork(network); }catch(Exception e) {
			 * 
			 * }
			 */
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		}
		return client;
	}

	@Override
	public ResponseEntity<?> getFile(String fileId) {
		try {
			client = setOperator();
			FileContentsQuery query = new FileContentsQuery().setFileId(FileId.fromString(fileId));

			ByteString contents = query.execute(client);

			String contentsToUtf8 = contents.toStringUtf8();

			return ResponseEntity.ok(
					MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("file.get.success"))
							.response(contentsToUtf8).httpStatus(HttpStatus.OK).build());
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed.get.file") + "," + e.getMessage(),
					ApiConstant.GET_FILE, fileId, fileId, fileId);
			throw new HederaException(e.getMessage());

		}

	}

	@Override
	public ResponseEntity<?> createNFTToken(NFTCreationRequest nftCreationRequest) {

		try {

			client = setOperator();

			String operatorId = env.getProperty("H721_OPERATOR_ID");
			String operatorKey = env.getProperty("H721_OEPRATOR_KEY");

			TokenCreateTransaction nftCreate = new TokenCreateTransaction()
					.setTokenName(nftCreationRequest.getTokenName()).setTokenSymbol(nftCreationRequest.getTokenSymbol())
					.setTokenType(TokenType.NON_FUNGIBLE_UNIQUE).setDecimals(0).setInitialSupply(0)
					.setTreasuryAccountId(ExampleHelper.getOperatorId(nftCreationRequest.getUserAccountId())).setSupplyType(TokenSupplyType.FINITE)
					.setMaxSupply(nftCreationRequest.getNumberOfSupply()).setSupplyKey(ExampleHelper.getOperatorKey(nftCreationRequest.getUserPrivateKey()))
					//.setMaxTransactionFee(new Hbar(1))
					.freezeWith(client);

			// Sign the transaction with the treasury key
			TokenCreateTransaction nftCreateTxSign = nftCreate.sign(ExampleHelper.getOperatorKey(nftCreationRequest.getUserPrivateKey()));

			// Submit the transaction to a Hedera network
			TransactionResponse nftCreateSubmit = nftCreateTxSign.execute(client);

			// Get the transaction receipt
			TransactionReceipt nftCreateRx = nftCreateSubmit.getReceipt(client);

			// Get the token ID
			TokenId tokenId = nftCreateRx.tokenId;

			// Log the token ID
			System.out.println("Created NFT with token ID " + tokenId);
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("create.nft.success")).response(tokenId.toString())
					.httpStatus(HttpStatus.OK).build());

		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.create.nft.token") + "," + e.getMessage(), ApiConstant.CREATE_NFT_TOKEN,
					nftCreationRequest, nftCreationRequest.getUserId(), nftCreationRequest.getClientId());
			throw new HederaException(e.getMessage());

		}
	}

	@Override
	public ResponseEntity<?> mintNFTToken(NFTMintRequest nftMintRequest) {

		try {
			client = setOperator();

			String operatorKey = env.getProperty("H721_OEPRATOR_KEY");

			String CID = nftMintRequest.getMetaData();

			// Mint a new NFT
			TokenMintTransaction mintTx = new TokenMintTransaction()
					.setTokenId(TokenId.fromString(nftMintRequest.getTokenId())).addMetadata(CID.getBytes())
					.freezeWith(client);

			// Sign transaction with the supply key
			TokenMintTransaction mintTxSign = mintTx.sign(ExampleHelper.getOperatorKey(nftMintRequest.getUserPrivateKey()));

			// Submit the transaction to a Hedera network
			TransactionResponse mintTxSubmit = mintTxSign.execute(client);

			// Get the transaction receipt
			TransactionReceipt mintRx = mintTxSubmit.getReceipt(client);

			// Log the serial number
			System.out.println("Created NFT " + nftMintRequest.getTokenId() + "with serial: " + mintRx.serials);
			return ResponseEntity.ok(
					MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("mint.nft.success"))
							.response(mintRx.serials).httpStatus(HttpStatus.OK).build());
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.mini.nft.token") + "," + e.getMessage(), ApiConstant.MINT_NFT_TOKEN,
					nftMintRequest, nftMintRequest.getUserId(), nftMintRequest.getClientId());
			throw new HederaException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> associateNFTToken(NFTAssociateRequest nftAssociateRequest) {

		try {
			client = setOperator();

			TokenAssociateTransaction associateAliceTx = new TokenAssociateTransaction()
					.setAccountId(ExampleHelper.getOperatorId(nftAssociateRequest.getSenderAccountId()))
					.setTokenIds(Collections.singletonList(TokenId.fromString(nftAssociateRequest.getTokenId())))
					.freezeWith(client).sign(ExampleHelper.getOperatorKey(nftAssociateRequest.getUserPrivateKey()));

			// Submit the transaction to a Hedera network
			TransactionResponse associateAliceTxSubmit = associateAliceTx.execute(client);

			// Get the transaction receipt
			TransactionReceipt associateAliceRx = associateAliceTxSubmit.getReceipt(client);

			// Confirm the transaction was successful
			System.out.println("NFT association with Alice's account: " + associateAliceRx.status);
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("associate.nft.success")).response(associateAliceRx.status.toString())
					.httpStatus(HttpStatus.OK).build());
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.associate.nft.token") + "," + e.getMessage(),
					ApiConstant.ASSOCIATE_NFT_TOKEN, nftAssociateRequest, nftAssociateRequest.getUserId(),
					nftAssociateRequest.getClientId());
			throw new HederaException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> transferNFTToken(NFTTransferRequest nftTransferRequest) {
		try {
			client = setOperator();

			String operatorId = env.getProperty("H721_OPERATOR_ID");
			String operatorKey = env.getProperty("H721_OEPRATOR_KEY");

			AccountBalance balanceCheckTreasury = new AccountBalanceQuery()
					.setAccountId(ExampleHelper.getOperatorId(operatorId)).execute(client);
			System.out.println("Treasury balance: " + balanceCheckTreasury.tokens + "NFTs of ID "
					+ nftTransferRequest.getTokenId());

			// Check the balance before the transfer for Alice's account
			AccountBalance balanceCheckAlice = new AccountBalanceQuery()
					.setAccountId(ExampleHelper.getOperatorId(nftTransferRequest.getSenderAccountId())).execute(client);
			System.out.println(
					"Alice's balance: " + balanceCheckAlice.tokens + "NFTs of ID " + nftTransferRequest.getTokenId());

			// Transfer the NFT from treasury to Alice
			// Sign with the treasury key to authorize the transfer
			TransferTransaction tokenTransferTx = new TransferTransaction()
					.addNftTransfer(new NftId(TokenId.fromString(nftTransferRequest.getTokenId()), nftTransferRequest.getSerialNumber()),
							ExampleHelper.getOperatorId(nftTransferRequest.getSenderAccountId()),
							ExampleHelper.getOperatorId(nftTransferRequest.getReceiverAccountId()))
					.freezeWith(client).sign(ExampleHelper.getOperatorKey(nftTransferRequest.getUserPrivateKey()));

			TransactionResponse tokenTransferSubmit = tokenTransferTx.execute(client);
			TransactionReceipt tokenTransferRx = tokenTransferSubmit.getReceipt(client);

			System.out.println("NFT transfer from Treasury to Alice: " + tokenTransferRx.status);

			// Check the balance of the treasury account after the transfer
			AccountBalance balanceCheckTreasury2 = new AccountBalanceQuery()
					.setAccountId(ExampleHelper.getOperatorId(operatorId)).execute(client);
			System.out.println("Treasury balance: " + balanceCheckTreasury2.tokens + "NFTs of ID "
					+ nftTransferRequest.getTokenId());

			// Check the balance of Alice's account after the transfer
			AccountBalance balanceCheckAlice2 = new AccountBalanceQuery()
					.setAccountId(ExampleHelper.getOperatorId(nftTransferRequest.getSenderAccountId())).execute(client);
			System.out.println(
					"Alice's balance: " + balanceCheckAlice2.tokens + "NFTs of ID " + nftTransferRequest.getTokenId());

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("transfer.nft.success"))
					.response(balanceCheckAlice2.tokens.get(nftTransferRequest.getTokenId())).httpStatus(HttpStatus.OK)
					.build());

		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.transfer.nft.token") + "," + e.getMessage(), ApiConstant.TRANSFER_NFT_TOKEN,
					nftTransferRequest, nftTransferRequest.getUserId(), nftTransferRequest.getClientId());
			throw new HederaException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> createUserNft(UserNftCreationRequest userNftCreationRequest) {

		NFTCreationRequest nftCreationRequest = NFTCreationRequest.builder()
				.tokenName(userNftCreationRequest.getTokenName()).tokenSymbol(userNftCreationRequest.getTokenSymbol())
				.build();
		ResponseEntity<?> createNftResponse = createNFTToken(nftCreationRequest);

		if (createNftResponse.getStatusCode().is2xxSuccessful()) {

			MessageResponse messageResponse = (MessageResponse) createNftResponse.getBody();
			String tokenId = (String) messageResponse.getResponse();

			NFTMintRequest nftMintRequest = NFTMintRequest.builder().metaData(userNftCreationRequest.getMetaData())
					.tokenId(tokenId).build();
			ResponseEntity<?> mintNftResponse = mintNFTToken(nftMintRequest);

			if (mintNftResponse.getStatusCode().is2xxSuccessful()) {

				String accountId = userNftCreationRequest.getUserAccountId();
				String privateKey = userNftCreationRequest.getUserPrivateKey();

				NFTAssociateRequest nftAssociateRequest = NFTAssociateRequest.builder().tokenId(tokenId)
						.senderAccountId(accountId).userPrivateKey(privateKey).build();
				ResponseEntity<?> associateNftResponse = associateNFTToken(nftAssociateRequest);

				if (associateNftResponse.getStatusCode().is2xxSuccessful()) {

					NFTTransferRequest nftTransferRequest = NFTTransferRequest.builder().tokenId(tokenId)
							.senderAccountId(accountId).userPrivateKey(privateKey).build();
					ResponseEntity<?> transferNftResponse = transferNFTToken(nftTransferRequest);

					if (transferNftResponse.getStatusCode().is2xxSuccessful()) {
						return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
								.message(env.getProperty("create.nft.success")).response(tokenId)
								.httpStatus(HttpStatus.OK).build());
					} else {

						MessageResponse transferMessageResponse = (MessageResponse) transferNftResponse.getBody();
						return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
								.message(env.getProperty("transfer.nft.fails"))
								.response(transferMessageResponse.getResponse()).httpStatus(HttpStatus.OK).build());

					}

				} else {

					MessageResponse associateMessageResponse = (MessageResponse) associateNftResponse.getBody();
					return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
							.message(env.getProperty("associate.nft.fails"))
							.response(associateMessageResponse.getResponse()).httpStatus(HttpStatus.OK).build());
				}

			} else {

				MessageResponse mintMessageResponse = (MessageResponse) mintNftResponse.getBody();
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
						.message(env.getProperty("mint.nft.fails")).response(mintMessageResponse.getResponse())
						.httpStatus(HttpStatus.OK).build());

			}
		} else {

			MessageResponse messageResponse = (MessageResponse) createNftResponse.getBody();
			String tokenId = (String) messageResponse.getResponse();
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed.create.user.nft.token"),
					ApiConstant.CREATE_NFT_USER, userNftCreationRequest, userNftCreationRequest.getUserId(),
					userNftCreationRequest.getClientId());
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("create.nft.fails")).response(tokenId).httpStatus(HttpStatus.OK).build());

		}
	}

	@Override
	public ResponseEntity<?> getNft(String nftId,long serialNumber) {

		try {
			client = setOperator();

			List<TokenNftInfo> nftInfos = new TokenNftInfoQuery()
					.setNftId(new NftId(TokenId.fromString(nftId), serialNumber)).execute(client);

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("get.nft.success")).response(nftInfos).httpStatus(HttpStatus.OK).build());
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed.get.nft.token"),
					ApiConstant.GET_NFT_TOKEN, nftId, nftId, nftId);
			throw new HederaException(e.getMessage());
		}
	}

}
