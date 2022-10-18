package com.ihi.hedera.utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageQuery;

@Service
public class HCSTransactionService {

	@Autowired
	private Environment env;
	
	
	
	public HCSTransactionService()
	        throws ReceiptStatusException,  PrecheckStatusException, InterruptedException {}
	
	public static void main(String[] args) throws ReceiptStatusException, PrecheckStatusException, InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		
		//GenerateTopicId generateTopicId = new GenerateTopicId();
		
		//final  Client hederaClient = generateTopicId.createHederaClient();
		//generateTopicId.generateTopicId(hederaClient);
		//generateTopicId.publishMessageToHedera();
	}
	

	Client createHederaClient() throws InterruptedException {
        var networkName = "testnet";
        var mirrorNetwork ="hcs.testnet.mirrornode.hedera.com:5600";
        System.out.println("mirrorNetwork....." + mirrorNetwork);
        Client client;

        // noinspection EnhancedSwitchMigration
        switch (networkName) {
            case "mainnet":
                client = Client.forMainnet();

                System.out.println("Create Hedera client for Mainnet");
               
                System.out.println(
                    "Using {} to connect to the hedera mirror network" +
                    mirrorNetwork
                );
                client.setMirrorNetwork(List.of(mirrorNetwork));
                break;
            case "testnet":
                System.out.println("Create Hedera client for Testnet");

                client = Client.forTestnet();
                client.setMirrorNetwork(List.of(mirrorNetwork));
                System.out.println( "Using {} to connect to the hedera mirror network"+mirrorNetwork);
                System.out.println( "The hedera mirror network"+client.getMirrorNetwork());
                System.out.println( "The network"+client.getNetwork());

                break;
            default:
                throw new IllegalStateException(
                    "unknown hedera network name: " + networkName
                );
        }

        var operatorId = "0.0.26060726";
        System.out.println("operatorId {}"+ operatorId);
        var operatorKey = "302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf";
        System.out.println("operatorKey {}"+operatorKey);

        if (operatorId != null && operatorKey != null) {
            client.setOperator(
                AccountId.fromString(operatorId),
                PrivateKey.fromString(operatorKey)
            );
        }
       // logger.info("Topic ID ", client);

        System.out.println("The OperatorPublicKey {}"+client.getOperatorPublicKey());

        System.out.println("The OperatorAccountId {}"+ client.getOperatorAccountId());
        System.out.println("HederaClinet....."+client);
        return client;
    }
	
	/*
	 * Create topic
	 */
	public TopicId generateTopicId(Client hederaClient)throws ReceiptStatusException, PrecheckStatusException, InterruptedException, TimeoutException {
		
		var topicId = Objects.requireNonNull(
	            new TopicCreateTransaction()
	                .execute(hederaClient)
	                .getReceipt(hederaClient)
	                .topicId
	        );
		System.out.println("New TopicID...."+topicId);
		return topicId;
	}
	
//	public TransactionReceipt publishMessageToHedera(TopicId topicId,PropertyPassportInfo nftTokenInfo) throws TimeoutException, PrecheckStatusException, ReceiptStatusException, InterruptedException {
//		TransactionReceipt transactionReceipt=null;
//		try {
//			Client hederaClinet= createHederaClient();
//			var operatorId = Objects.requireNonNull(
//					hederaClinet.getOperatorAccountId()
//		        );
//			 var transactionId = TransactionId.generate(operatorId);
//			 
//			 // Create topic
//			 if(Objects.isNull(topicId)) {
//				  topicId= generateTopicId(hederaClinet);
//			 }
//			System.out.println("Topic ID......"+topicId);
//			
//			
//			subscribeToTopic(topicId,hederaClinet);
//			
//			//Subscribe to a topic
//			 transactionReceipt = new TopicMessageSubmitTransaction()
//	        .setMaxChunks(1)
//	        .setTopicId(topicId)
//	        .setTransactionId(transactionId)
//	        .setMessage(nftTokenInfo.toString())
//	        .execute(hederaClinet, Duration.ofMinutes(5))
//	        .getReceipt(hederaClinet, Duration.ofMinutes(5));
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return transactionReceipt;
//		
//		
//	}
	
	public void createWallet() throws TimeoutException, PrecheckStatusException, InterruptedException, ReceiptStatusException {
		Client hederaClinet= createHederaClient();
		var privateKey = PrivateKey.generate();
		var transactionResponse = new AccountCreateTransaction().setKey(privateKey.getPublicKey()).
		setInitialBalance(Hbar.fromTinybars(10)).execute(hederaClinet);
		var transactionReceipt = transactionResponse.getReceipt(hederaClinet);
		var accountId = transactionReceipt.accountId;
		String account= accountId.shard+"."+accountId.realm+""+accountId.num;
		
	}
	/*
	 * Subscribe to a topic
	 */
	public void subscribeToTopic(TopicId topicId,Client client) {
		try {
			new TopicMessageQuery()
		    .setTopicId(topicId)
		    .subscribe(client, resp -> {
		            String messageAsString = new String(resp.contents, StandardCharsets.UTF_8);

		            System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
		    });
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
