package com.ihi.hcs.mongo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ihi.hcs.payload.request.HcsStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "hcs_subscribe_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HcsSubscribeInfo {

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
	
	private String apiName;
	
	private String userId;
	
	private HcsStatus status;
	
	private String serviceType;
	
	private String role;
	
	private String logType;
}
