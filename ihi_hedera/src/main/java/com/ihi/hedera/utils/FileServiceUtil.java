package com.ihi.hedera.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.FileContentsQuery;
import com.hedera.hashgraph.sdk.FileCreateTransaction;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenId;
import com.ihi.hedera.mongo.model.ServiceType;
import com.ihi.hedera.service.impl.MongoDBLoggerService;

@Service
public class FileServiceUtil {

	@Autowired
	HCSTransactionService hcsTransactionService;

	@Autowired
	static MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	static Environment env;

	public static void main(String[] args)
			throws InterruptedException, TimeoutException, PrecheckStatusException, ReceiptStatusException {
		FileServiceUtil fileServiceImpl = new FileServiceUtil();
		// TODO Auto-generated method stub
		File file = new File("D:/Document/NFT_Documnet/tokeninfo.txt");

		byte[] fileContent = readContentIntoByteArray(file);

		var operatorId = "0.0.26060726";
		System.out.println("operatorId {}" + operatorId);
		var operatorKey = "302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf";
		System.out.println("operatorKey {}" + operatorKey);

		Client hederaClient = fileServiceImpl.createHederaClient();
		String fileId = fileServiceImpl.fileCreate(fileContent, hederaClient, operatorKey);

		TokenServiceUtil tokenServiceUtil = new TokenServiceUtil();
		// TokenId tokenId=fileServiceImpl.createToken(hederaClient,fileId);
		TokenId tokenId = tokenServiceUtil.createToken(fileId, operatorId, operatorKey);
	}

	public String fileCreate(byte[] fileContent, Client hederaClient, String operatorKey) throws InterruptedException {

		var privateKey = PrivateKey.fromString(operatorKey);

		var fileId = "";
		try {
			var keys = privateKey;
			var fileCreateTransaction = new FileCreateTransaction();

			// if we have a large file (> 4000 bytes), create the file with keys
			// then run file append
			// then remove keys
			fileCreateTransaction.setContents(fileContent);
			fileCreateTransaction.setKeys(keys);

			var response = fileCreateTransaction.execute(hederaClient);
			var transactionReceipt = response.getReceipt(hederaClient);
			fileId = Objects.requireNonNull(transactionReceipt.fileId.toString());
			System.out.println("File ID...." + fileId);
			return fileId;

		} catch (Exception e) {
			// TODO: handle exception
			mongoDBLoggerService.createLogger(env.getProperty("failed.create.f") + "," + e.getMessage(),
					ServiceType.HEDERA,"fileCreate (method inside File service Util class)", "",
					HttpStatus.BAD_REQUEST.value(), null, null);
		}
		return null;
	}

	public static byte[] readContentIntoByteArray(File file) {
		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
			for (int i = 0; i < bFile.length; i++) {
				System.out.print((char) bFile[i]);
			}
		} catch (Exception e) {
			mongoDBLoggerService.createLogger(env.getProperty("failed.create.f") + "," + e.getMessage(),
					ServiceType.HEDERA, "readContentIntoByteArray (method inside File service Util class)", "",
					HttpStatus.BAD_REQUEST.value(), null, null);
		}
		return bFile;
	}

	public Client createHederaClient() throws InterruptedException {
		var networkName = "testnet";
		var mirrorNetwork = "hcs.testnet.mirrornode.hedera.com:5600";
		Client client;

		// noinspection EnhancedSwitchMigration
		switch (networkName) {
		case "mainnet":
			client = Client.forMainnet();
			client.setMirrorNetwork(List.of(mirrorNetwork));
			break;
		case "testnet":
			client = Client.forTestnet();
			client.setMirrorNetwork(List.of(mirrorNetwork));

			break;
		default:
			throw new IllegalStateException("unknown hedera network name: " + networkName);
		}

		// var operatorId = "0.0.4389";
		final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
		final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
				"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
		// var operatorKey =
		// "302e020100300506032b657004220420b56be9ea5c14be403fa44280fe85457ccbda9388e161b10ce1bfa072d1fcd2ab";
		if (ADMIN_OPERATOR_ID != null && ADMIN_OPERATOR_KEY != null) {
			client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
		}
		return client;
	}

	public String getFileContent(FileId fileId, Client client) throws TimeoutException, PrecheckStatusException {
		ByteString contents = new FileContentsQuery().setFileId(fileId).execute(client);

		return contents.toStringUtf8();
	}

	public String createFile(String fileContent, Client hederaClient, String operatorKey) {

		var privateKey = PrivateKey.fromString(operatorKey);

		var fileId = "";
		try {
			var fileCreateTransaction = new FileCreateTransaction();

			// if we have a large file (> 4000 bytes), create the file with keys
			// then run file append
			// then remove keys
			fileCreateTransaction.setContents(fileContent);
			fileCreateTransaction.setKeys(privateKey);

			var response = fileCreateTransaction.execute(hederaClient);
			var transactionReceipt = response.getReceipt(hederaClient);
			fileId = Objects.requireNonNull(transactionReceipt.fileId.toString());
			return fileId;

		} catch (Exception e) {
			// TODO: handle exception
			mongoDBLoggerService.createLogger(env.getProperty("failed.create.f") + "," + e.getMessage(),
					ServiceType.HEDERA, "fileCreate (method inside File service Util class)", "/api/nft/file/create",
					HttpStatus.BAD_REQUEST.value(), null, null);
			return e.getMessage();
		}

	}

}
