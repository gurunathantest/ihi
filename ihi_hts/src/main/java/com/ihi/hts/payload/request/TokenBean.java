package com.ihi.hts.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
	 * TokenBean class used to receive inputs and pass to service 
	 * receive inputs values with the help of getters and setters
	 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenBean {
	private long amount;
	private String operatorId;
	private String operatorKey;
	private String tokenId;
	private String fromSenderId;
	private String fromSenderKey;
	private String toAccountId;
	private String toAccountKey;
	private String freezekey;
	private String clientId;
	private String adminOperatorId;
	private String adminOperatorKey;
	private String userId;
	
}

