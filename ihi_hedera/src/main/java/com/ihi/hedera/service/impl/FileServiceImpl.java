package com.ihi.hedera.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.FileCreateTransaction;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.ihi.hedera.constant.HederaHtsInfoConstnat;
import com.ihi.hedera.exception.HederaException;
import com.ihi.hedera.utils.AsynchronousUtils;
import com.ihi.hedera.utils.HCSTransactionService;

@Service
public class FileServiceImpl {

	@Autowired
	HCSTransactionService hcsTransactionService;

	@Autowired
	static MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	static Environment env;

	@Autowired
	static AsynchronousUtils asynchronousUtils;

	public static void main(String[] args) throws InterruptedException {
		FileServiceImpl fileServiceImpl = new FileServiceImpl();
		// TODO Auto-generated method stub
		File file = new File("D:/Document/NFT_Documnet/tokeninfo.txt");

		byte[] fileContent = readContentIntoByteArray(file);
		Client hederaClient = fileServiceImpl.createHederaClient();
		TransactionReceipt transactionReceipt = fileServiceImpl.fileCreate(file, fileContent, hederaClient);
	}

	public TransactionReceipt fileCreate(File fileData, byte[] fileContent, Client hederaClient)
			throws InterruptedException {
		// Publish file to Hedera File Service storage
		var privateKey = PrivateKey.fromString(
				"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf");
		// var client = Client.forTestnet();
		var fileId = "";
		var fileChunk = 4000;
		var largeFile = fileData.getTotalSpace() > fileChunk;
		var startIndex = 0;
		try {
			var keys = privateKey;
			var fileCreateTransaction = new FileCreateTransaction();

			if (largeFile) {
				// if we have a large file (> 4000 bytes), create the file with keys
				// then run file append
				// then remove keys
				fileCreateTransaction.setContents(fileContent);
				fileCreateTransaction.setKeys(keys);
			} else {
				fileCreateTransaction.setContents(fileContent);
			}
			var response = fileCreateTransaction.execute(hederaClient);
			var transactionReceipt = response.getReceipt(hederaClient);

			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.FILE, HederaHtsInfoConstnat.FILE_CREATE, "");
			return transactionReceipt;
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(
					env.getProperty("failed.create.file") + "," + e.getMessage(), "/api/nft/file/create",
					"fileServiceImpl method", "", "");
			throw new HederaException(e.getMessage());
		}
	}

	private static byte[] readContentIntoByteArray(File file) {
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
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed.read.file") + "," + e.getMessage(),
					"/api/nft/create/file", "readContentIntoByteArray method in fileserviceImpl", "", "");
			throw new HederaException(e.getMessage());
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

		var operatorId = " 0.0.26060726";
		var operatorKey = "302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf";

		if (operatorId != null && operatorKey != null) {
			client.setOperator(AccountId.fromString(operatorId), PrivateKey.fromString(operatorKey));
		}

		return client;
	}
}
