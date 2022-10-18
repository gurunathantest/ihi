package com.ihi.hedera.preDefined;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.FileCreateTransaction;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.ihi.hedera.dto.MessageDto;

public class NFTService {

	public static void main(String[] args)
			throws InterruptedException, TimeoutException, PrecheckStatusException, ReceiptStatusException, IOException {
		// TODO Auto-generated method stub

		NFTService nftService = new NFTService();
		// FileServiceUtil fileServiceUtil = new FileServiceUtil();
		
//		File resource = new ClassPathResource("tokenTemplate.json").getFile();
//
//		byte[] fileContent = nftService.getTokenTemplate(resource);
		Client hederaClient = nftService.createHederaClient();
//		TransactionReceipt transactionReceipt = nftService.fileCreate(resource, fileContent, hederaClient);
//		var fileId = transactionReceipt.fileId.toString();
//		System.out.println("File ID...." + fileId);
//		TokenServiceUtil tokenServiceUtil = new TokenServiceUtil();
//
//		var operatorId = "0.0.26060726";
//		System.out.println("operatorId {}" + operatorId);
//		var operatorKey = "302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf";
//		System.out.println("operatorKey {}" + operatorKey);
//		
//		TokenId tokenId = tokenServiceUtil.createToken(fileId,operatorId,operatorKey);
//
//		System.out.println("Token ID...." + tokenId);
//
//		var accountId = nftService.getAccountId();
//		System.out.println("Account ID....." + accountId);
//		TopicId topicId = nftService.generateTopicId(hederaClient);
//		System.out.println("Topic Id...." + topicId);
//		MutableDateTime endDate = new MutableDateTime();
//		endDate.addDays(10);
//		NftReportDto propertyAddressDto = NftReportDto.builder().typeOfProperty("House").postalCode("B3 2EW").building("27 Colmore Row")
//				.town("Birmingham").country("England").build();
//		MessageDto messageDto = MessageDto.builder().tokenId(tokenId).accountId(accountId).endDate(endDate)
//				.reservePrice(1000L).ownerName("John Smith").coOwnerName("Susan Udwala").propertyAddress(propertyAddressDto).build();
//		TransactionReceipt transaction = nftService.publishMessageToHedera(topicId, messageDto);
//		System.out.println("Transaction......" + transaction);
		TopicId topicIdNew = TopicId.fromString("0.0.26061547");
		nftService.subscribeToTopic(topicIdNew,hederaClient);
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
			return transactionReceipt;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return bFile;
	}

	public Client createHederaClient() throws InterruptedException {
		var networkName = "testnet";
		var mirrorNetwork = "hcs.testnet.mirrornode.hedera.com:5600";
		System.out.println("mirrorNetwork....." + mirrorNetwork);
		Client client;

		// noinspection EnhancedSwitchMigration
		switch (networkName) {
		case "mainnet":
			client = Client.forMainnet();

			System.out.println("Create Hedera client for Mainnet");

			System.out.println("Using {} to connect to the hedera mirror network" + mirrorNetwork);
			client.setMirrorNetwork(List.of(mirrorNetwork));
			break;
		case "testnet":
			System.out.println("Create Hedera client for Testnet");

			client = Client.forTestnet();
			client.setMirrorNetwork(List.of(mirrorNetwork));
			System.out.println("Using {} to connect to the hedera mirror network" + mirrorNetwork);
			System.out.println("The hedera mirror network" + client.getMirrorNetwork());
			System.out.println("The network" + client.getNetwork());

			break;
		default:
			throw new IllegalStateException("unknown hedera network name: " + networkName);
		}

		var operatorId = "0.0.26060726";
		System.out.println("operatorId {}" + operatorId);
		var operatorKey = "302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf";
		System.out.println("operatorKey {}" + operatorKey);

		if (operatorId != null && operatorKey != null) {
			client.setOperator(AccountId.fromString(operatorId), PrivateKey.fromString(operatorKey));
		}

		System.out.println("The OperatorPublicKey {}" + client.getOperatorPublicKey());

		System.out.println("The OperatorAccountId {}" + client.getOperatorAccountId());
		System.out.println("HederaClinet....." + client);
		return client;
	}

	public AccountId getAccountId() {
		var operatorId = "0.0.26060726";
		var accountId = AccountId.fromString(operatorId);
		return accountId;
	}

	/*
	 * Create topic
	 */
	public TopicId generateTopicId(Client hederaClient)
			throws ReceiptStatusException, PrecheckStatusException, InterruptedException, TimeoutException {

		var topicId = Objects
				.requireNonNull(new TopicCreateTransaction().execute(hederaClient).getReceipt(hederaClient).topicId);
		System.out.println("New TopicID...." + topicId);
		return topicId;
	}

	public TransactionReceipt publishMessageToHedera(TopicId topicId, MessageDto messageDto)
			throws TimeoutException, PrecheckStatusException, ReceiptStatusException, InterruptedException {
		TransactionReceipt transactionReceipt = null;
		try {
			Client hederaClinet = createHederaClient();
			var operatorId = Objects.requireNonNull(hederaClinet.getOperatorAccountId());
			var transactionId = TransactionId.generate(operatorId);

			// Create topic
			if (Objects.isNull(topicId)) {
				topicId = generateTopicId(hederaClinet);
			}
			System.out.println("Topic ID......" + topicId);

			subscribeToTopic(topicId, hederaClinet);

			// Subscribe to a topic
			transactionReceipt = new TopicMessageSubmitTransaction().setMaxChunks(1).setTopicId(topicId)
					.setTransactionId(transactionId).setMessage(messageDto.toString())
					.execute(hederaClinet, Duration.ofMinutes(5)).getReceipt(hederaClinet, Duration.ofMinutes(5));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transactionReceipt;

	}

	/*
	 * Subscribe to a topic
	 */
	public void subscribeToTopic(TopicId topicId, Client client) {
		try {
			System.out.println("###subscribeToTopic###");
			new TopicMessageQuery().setTopicId(topicId).subscribe(client, resp -> {
				String messageAsString = new String(resp.contents, StandardCharsets.UTF_8);

				System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	public byte[] getTokenTemplate(File resource) throws IOException {
		byte[] content = Files.readAllBytes(resource.toPath());
		String text = new String(Files.readAllBytes(resource.toPath()));
		return content;
	}
}
