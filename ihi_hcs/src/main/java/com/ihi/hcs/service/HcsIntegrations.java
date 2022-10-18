package com.ihi.hcs.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.text.StrongTextEncryptor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessage;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;
import com.ihi.hcs.mongo.model.LogType;
import com.ihi.hcs.payload.request.HcsStatus;
import com.ihi.hcs.payload.request.testRequest;
import com.ihi.hcs.payload.response.MessageResponse;
import com.ihi.hcs.utilities.AsynchronousUtils;
import com.ihi.hcs.utilities.EnvUtils;

@Component
public class HcsIntegrations {

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	StrongTextEncryptor strongTextEncryptor;

	@Autowired
	private AsynchronousUtils asynchronousUtils;

	@Autowired
	private JmsTemplate jmsTemplate;

	private Client client = null;

	public HcsIntegrations() {

		client = setOperator();

		new TopicMessageQuery().setTopicId(TopicId.fromString(EnvUtils.topicId)).subscribe(client, this::switchMethod);
	}

	public void switchMethod(TopicMessage topicMessage) {

		String message = new String(topicMessage.contents, StandardCharsets.UTF_8);
		Map<String, String> map = new HashMap<>();
		Arrays.stream(message.substring(1, message.length() - 1).split(",")).forEach(s -> {
			String key = s.split(":")[0];
			String value = s.split(":")[1];
			map.put(key.substring(1, key.length() - 1), value.substring(1, value.length() - 1));
		});
		if (map.containsKey("userId")) {
			saveHcsMessage(topicMessage, map);
		} else {
			saveMessage(topicMessage, map);
		}
	}

	private void saveMessage(TopicMessage topicMessage, Map<String, String> map) {
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(EnvUtils.jasyptSecretKey);

		HederaSubscribeInfo hederaSubscribeInfo = HederaSubscribeInfo.builder().id(topicMessage.sequenceNumber + "")
				.clientId(map.get("clientId")).memo(map.get("memo")).data(textEncryptor.decrypt(map.get("message")))
				.createdTime(new DateTime()).topicId(EnvUtils.topicId)
				.consensusTimeStampSeconds(topicMessage.consensusTimestamp.getEpochSecond())
				.consensusTimeStampNanos(topicMessage.consensusTimestamp.getNano()).status("Success").build();
		// mongoDBLoggerService.createSubscribeLogger(hederaSubscribeInfo);

		try {
			hederaSubscribeInfo.setLogType(LogType.ACTIVE_MQ.name());
			saveSubscribeMessageActiveMQ(hederaSubscribeInfo);
		} catch (JmsException e) {
			hederaSubscribeInfo.setLogType(LogType.ASYNC_THREAD.name());
			asynchronousUtils.saveSubscribeMessage(hederaSubscribeInfo);

		}

	}

	private void saveHcsMessage(TopicMessage topicMessage, Map<String, String> map) {
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(EnvUtils.jasyptSecretKey);

		HcsSubscribeInfo hcsSubscribeInfo = HcsSubscribeInfo.builder().id(topicMessage.sequenceNumber + "")
				.clientId(map.get("clientId")).memo(map.get("memo")).data(textEncryptor.decrypt(map.get("message")))
				.createdTime(new DateTime()).topicId(EnvUtils.topicId)
				.consensusTimeStampSeconds(topicMessage.consensusTimestamp.getEpochSecond())
				.consensusTimeStampNanos(topicMessage.consensusTimestamp.getNano()).apiName(map.get("apiName"))
				.serviceType(map.get("serviceType")).userId(map.get("userId")).topicId(map.get("topicId"))
				.role(map.get("role")).build();

		switch (map.get("status")) {
		case "APPROVED":
			hcsSubscribeInfo.setStatus(HcsStatus.APPROVED);
			break;
		case "REJECTED":
			hcsSubscribeInfo.setStatus(HcsStatus.REJECTED);
			break;
		case "FAILIURE":
			hcsSubscribeInfo.setStatus(HcsStatus.FAILURE);
			break;
		default:
			hcsSubscribeInfo.setStatus(HcsStatus.SUCCESS);
		}

		try {
			hcsSubscribeInfo.setLogType(LogType.ACTIVE_MQ.name());
			saveHcsSubscribeMessageActiveMQ(hcsSubscribeInfo);
		} catch (JmsException e) {
			hcsSubscribeInfo.setLogType(LogType.ASYNC_THREAD.name());
			asynchronousUtils.saveHcsSubscribeMessage(hcsSubscribeInfo);
		}

	}

	public Client setOperator() {

		if (client != null)
			return client;
		if (EnvUtils.netWork.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
		} else if (EnvUtils.netWork.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
		}
		client.setOperator(ExampleHelper.getOperatorId(EnvUtils.operatorId),
				ExampleHelper.getOperatorKey(EnvUtils.operatorPrivateKey));

		return client;
	}

	public ResponseEntity<?> testSubscribeHedera(testRequest request) {
		HederaSubscribeInfo hederaSubscribeInfo = HederaSubscribeInfo.builder().clientId(request.getClientId())
				.memo(request.getMemo()).data(request.getData()).topicId(request.getTopicId())
				.consensusTimeStampSeconds(request.getConsensusTimeStampSeconds()).createdTime(new DateTime())
				.consensusTimeStampNanos(request.getConsensusTimeStampNanos()).status("Success").build();
		hederaSubscribeInfo.setLogType("ActiveMQ");
		// saveSubscribeMessageActiveMQ(hederaSubscribeInfo);
		asynchronousUtils.saveSubscribeMessage(hederaSubscribeInfo);
		return ResponseEntity.ok(MessageResponse.builder().httpStatus(HttpStatus.OK).status(HttpStatus.OK.value())
				.message("SUCCESS").response(null).build());
	}

	public void saveSubscribeMessageActiveMQ(HederaSubscribeInfo hederaSubscribeInfo) throws JmsException {
		jmsTemplate.convertAndSend("hcs_saveSubscribeMessage", hederaSubscribeInfo);
	}

	public void saveHcsSubscribeMessageActiveMQ(HcsSubscribeInfo hcsSubscribeInfo) throws JmsException {
		jmsTemplate.convertAndSend("hcs_saveHcsSubscribeMessage", hcsSubscribeInfo);
	}

}
