package com.ihi.admin.mongo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "hedera_subscribe_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HederaSubscribeInfo {

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;

	private String data;

	private DateTime createdTime;

	private String memo;

	private String clientId;
	
	private String topicId;
	
	private long consensusTimeStampSeconds;
	
	private int consensusTimeStampNanos;
	
	private String status;

}
