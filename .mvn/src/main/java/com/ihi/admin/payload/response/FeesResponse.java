package com.ihi.admin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeesResponse {

	private String service;
	
	private String operation;
	
	private int times;
	
	private String price;
	
}
