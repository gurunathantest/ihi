package com.ihi.hts.payload.request;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HederaFeesRequest {
	
	private String id;
	
	private String service;
	
	private String operations;
	
	private double priceInUsd;
	
	private DateTime updateDate;
	
	private String userId;
	
	private String clientId;

}
