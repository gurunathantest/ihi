package com.ihi.hts.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenInfoResponse {
	
	 private String userId;
	 private String tokenId;
	 private String createdTime;
	
}
