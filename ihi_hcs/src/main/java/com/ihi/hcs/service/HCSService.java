package com.ihi.hcs.service;

import javax.jms.JMSException;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.ihi.hcs.constant.ApiConstant;
import com.ihi.hcs.constant.HederaHtsInfoConstnat;
import com.ihi.hcs.exception.HcsException;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.LogType;
import com.ihi.hcs.mongo.repository.HcsSubscribeInfoRepo;
import com.ihi.hcs.mongo.repository.HederaSubscribeInfoRepo;
import com.ihi.hcs.payload.request.ExceptionLoggerRequest;
import com.ihi.hcs.payload.request.HcsMessageSubmit;
import com.ihi.hcs.payload.request.PublishHcsMessage;
import com.ihi.hcs.payload.request.saveHederaHtsInfoRequest;
import com.ihi.hcs.payload.response.MessageResponse;
import com.ihi.hcs.utilities.AsynchronousUtils;

@Service
public class HCSService {

	@Autowired
	Environment env;

	@Value("${H721_NETWORK}")
	private String network;

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private HederaSubscribeInfoRepo hederaSubscribeInfoRepo;

	@Autowired
	private HcsSubscribeInfoRepo hcsSubscribeInfoRepo;

	@Autowired
	private AsynchronousUtils asynchronousUtils;

	@Autowired
	private JmsTemplate jmsTemplate;

	private Client client = null;

	/*
	 * //create HCS Topic public String createTopic() throws HederaStatusException {
	 * 
	 * TransactionId tx = new ConsensusTopicCreateTransaction()
	 * .setAdminKey(EnvUtils.getOperatorKey().publicKey) .execute(client);
	 * ConsensusTopicId topicId = tx.getReceipt(client).getConsensusTopicId();
	 * 
	 * return topicId.toString(); }
	 * 
	 * //Delete a HCS Topic public boolean deleteTopic(String topicId) throws
	 * HederaStatusException { new ConsensusTopicDeleteTransaction()
	 * .setTopicId(ConsensusTopicId.fromString(topicId)) .execute(client)
	 * .getReceipt(client); //get reciept to confirm the deletion. return true; }
	 * 
	 * //get info on a hcs topic public ConsensusTopicInfo getTopicInfo(String
	 * topicId) throws HederaStatusException { long cost = new
	 * ConsensusTopicInfoQuery() .setTopicId(ConsensusTopicId.fromString(topicId))
	 * .getCost(client); ConsensusTopicInfo info = new ConsensusTopicInfoQuery()
	 * .setTopicId(ConsensusTopicId.fromString(topicId)) .setQueryPayment(cost +
	 * cost / 50) //add 2% to estimated cost .execute(client); return info; }
	 * 
	 *//**
		 * Subscribe to messages on the topic, printing out the received message and
		 * metadata as it is published by the Hedera mirror node.
		 *//*
			 * public boolean subscribeToTopic(String topicId) { new
			 * MirrorConsensusTopicQuery() .setTopicId(ConsensusTopicId.fromString(topicId))
			 * .setStartTime(Instant.ofEpochSecond(0))
			 * .subscribe(HederaClient.getMirrorClient(), message -> {
			 * mongoDBLoggerService.createLogger("subscribeToTopic 105", ServiceType.HCS
			 * ,"Received message from MirrorNode: " + new String(message.message,
			 * StandardCharsets.UTF_8) + "\n\t consensus timestamp: " +
			 * message.consensusTimestamp + "\n\t topic sequence number: " +
			 * message.sequenceNumber, "/api/hcs/topic/subscribe/message", new
			 * DateTime().toString("yyyy-MM-dd HH:mm:ss"), HttpStatus.OK.value()); }, // On
			 * gRPC error, print the stack trace Throwable::printStackTrace); return true; }
			 */

	// submit a message to a hcs topic
	public ResponseEntity<?> submitMessage(String topicId, HcsMessageSubmit hcsMessageSubmit) {
		client = setOperator();
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(env.getProperty("jasypt.secret.key"));
		try {
			JsonObject requestObj = new JsonObject();
			requestObj.addProperty("message", textEncryptor.encrypt(String.valueOf(hcsMessageSubmit.getMessage())));
			requestObj.addProperty("memo", hcsMessageSubmit.getMemo());
			requestObj.addProperty("clientId", hcsMessageSubmit.getClientId());
			new TopicMessageSubmitTransaction().setTopicId(TopicId.fromString(topicId))
					.setMessage(requestObj.toString()).setTransactionMemo(hcsMessageSubmit.getMemo()).execute(client);
		} catch (Exception e) {
			try {

				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.submit.message") + "," + e.getMessage(),
						ApiConstant.SUBMII_MESSAGE, hcsMessageSubmit.toString(),
						hcsMessageSubmit.getUserId(), hcsMessageSubmit.getClientId(), topicId,
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {

				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.submit.message") + "," + e.getMessage(),
						ApiConstant.SUBMII_MESSAGE, hcsMessageSubmit.toString(),
						hcsMessageSubmit.getUserId(), hcsMessageSubmit.getClientId(), topicId,
						LogType.ASYNC_THREAD.name());

			}

			throw new HcsException(env.getProperty("issue.submit.message") + "," + e.getMessage());
		}

		try {
			saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.CONSENSUS, HederaHtsInfoConstnat.CONSENSUS_SUBMIT_MESSAGE,
					hcsMessageSubmit.getClientId(), hcsMessageSubmit.getUserId(), LogType.ACTIVE_MQ.name());
		} catch (JmsException jms) {
			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.CONSENSUS,
					HederaHtsInfoConstnat.CONSENSUS_SUBMIT_MESSAGE, hcsMessageSubmit.getClientId(),
					hcsMessageSubmit.getUserId(), LogType.ASYNC_THREAD.name());
		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("submit.messages")).httpStatus(HttpStatus.OK).build());
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

	public ResponseEntity<?> getLedgerMessage(String clientId, Pageable pageable) {
		Page<HederaSubscribeInfo> ledgerMsg = hederaSubscribeInfoRepo.findByClientId(clientId, pageable);
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("ledger.message")).response(ledgerMsg).httpStatus(HttpStatus.OK).build());
	}

	public ResponseEntity<?> publishMessage(String topicId, PublishHcsMessage publishHcsMessage) {
		client = setOperator();
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(env.getProperty("jasypt.secret.key"));
		try {
			JsonObject requestObj = new JsonObject();
			requestObj.addProperty("message", textEncryptor.encrypt(String.valueOf(publishHcsMessage.getMessage())));
			requestObj.addProperty("memo", publishHcsMessage.getMemo());
			requestObj.addProperty("clientId", publishHcsMessage.getClientId());
			requestObj.addProperty("apiName", publishHcsMessage.getApiName());
			requestObj.addProperty("createdDate", publishHcsMessage.getCreatedDate());
			requestObj.addProperty("userId", publishHcsMessage.getUserId());
			requestObj.addProperty("topicId", publishHcsMessage.getTopicId());
			requestObj.addProperty("status", publishHcsMessage.getStatus().toString());
			requestObj.addProperty("serviceType", publishHcsMessage.getServiceType());
			requestObj.addProperty("role", publishHcsMessage.getRole());
			new TopicMessageSubmitTransaction().setTopicId(TopicId.fromString(topicId))
					.setMessage(requestObj.toString()).setTransactionMemo(publishHcsMessage.getMemo()).execute(client);
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.submit.message") + "," + e.getMessage(),
						ApiConstant.SUBMII_MESSAGE, publishHcsMessage.toString(),
						publishHcsMessage.getUserId(), publishHcsMessage.getClientId(), topicId,
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {

				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.submit.message") + "," + e.getMessage(),
						ApiConstant.SUBMII_MESSAGE, publishHcsMessage.toString(),
						publishHcsMessage.getUserId(), publishHcsMessage.getClientId(), topicId,
						LogType.ASYNC_THREAD.name());
			}
			throw new HcsException(e.getMessage());
		}

		try {
			saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.CONSENSUS, HederaHtsInfoConstnat.CONSENSUS_SUBMIT_MESSAGE,
					publishHcsMessage.getClientId(), publishHcsMessage.getUserId(), LogType.ACTIVE_MQ.name());
		} catch (JmsException jms) {
			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.CONSENSUS,
					HederaHtsInfoConstnat.CONSENSUS_SUBMIT_MESSAGE, publishHcsMessage.getClientId(),
					publishHcsMessage.getUserId(), LogType.ASYNC_THREAD.name());
		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("submit.messages")).httpStatus(HttpStatus.OK).build());
	}

	public ResponseEntity<?> getHcsMessage(String clientId, PageRequest pageable) {
		Page<HederaSubscribeInfo> ledgerMsg = hcsSubscribeInfoRepo.findByClientId(clientId, pageable);
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("ledger.message")).response(ledgerMsg).httpStatus(HttpStatus.OK).build());
	}

	public void exceptionMessgeIntoMongoLoggerActiveMQ(String message, String api, String payload, String userId,
			String clientId, String topicId, String logType) throws JmsException {
		ExceptionLoggerRequest loggerRequest = ExceptionLoggerRequest.builder().message(message).api(api).userId(userId)
				.clientId(clientId).payload(payload).topicId(topicId).LogType(logType).build();

		jmsTemplate.convertAndSend("hcs_exception_message_mongodblogger", loggerRequest);
	}

	public void saveHederaHtsInfoActiveMQ(String service, String operation, String clientId, String userId,
			String logType) throws JmsException {
		saveHederaHtsInfoRequest infoRequest = saveHederaHtsInfoRequest.builder().clientId(clientId)
				.operation(operation).service(service).userId(userId).logType(logType).build();
		jmsTemplate.convertAndSend("hcs_saveHederaHtsInfo", infoRequest);
	}
	

}
