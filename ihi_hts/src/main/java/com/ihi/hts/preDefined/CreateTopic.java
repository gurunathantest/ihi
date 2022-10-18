package com.ihi.hts.preDefined;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;

public class CreateTopic {
	private static final Logger logger = LoggerFactory.getLogger(CreateTopic.class);
	
	private static final AccountId ADMIN_OPERATOR_ID = AccountId.fromString(Objects.requireNonNull("0.0.26060726"));
	private static final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(
			"302e020100300506032b657004220420cc84d7a2119f7c32b3303460845a57ddf9aded9009f53b5af4e5fd92fd87b8cf"));
    
	
    public static void main(String[] args) throws Exception {
          
		Client client = Client.forTestnet();
		client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
    	
        TopicCreateTransaction transaction = new TopicCreateTransaction();

      //Sign with the client operator private key and submit the transaction to a Hedera network
      TransactionResponse txResponse = transaction.execute(client);

      //Request the receipt of the transaction
      TransactionReceipt receipt = txResponse.getReceipt(client);

      //Get the topic ID
      TopicId newTopicId = receipt.topicId;

      System.out.println("The new topic ID is " + newTopicId);
    }
}
