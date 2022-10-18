package com.ihi.hts.payload.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransferResponse {

	private BigDecimal nodeFee;
	private BigDecimal netWorkFee;
	private BigDecimal transactionFee;
	private String message;
	private double balanceRemain;
}
