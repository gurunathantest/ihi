package com.ihi.hcs.mongo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection= "hedera_hts_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HederaHtsInfo {

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;
	
	private String clientId;
	
	private String service;
	
	private String operation;
	
	private double fees;
	
	@Builder.Default
	private DateTime createdTime= new DateTime();
	
	private String userId;
	
	private String logType;
	
	
}
