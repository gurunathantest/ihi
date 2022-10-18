package com.ihi.hts.mongo.model;

import javax.persistence.GeneratedValue;

import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "hts_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HtsLogInfo {

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;

	private SeverityType severity;

	private LogLevel logLevel;

	private String logInfo;

	private DateTime createdTime;

	private Object payLoad;

	private String apiName;

	private int statusCode;

	private String userId;

	private String transactionRecipt;

	private String clientId;

	private String logType;

}
