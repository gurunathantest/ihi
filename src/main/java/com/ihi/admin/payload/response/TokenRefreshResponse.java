package com.ihi.admin.payload.response;

import lombok.Data;

@Data
public class TokenRefreshResponse {
	private String accessToken;
	private String refreshToken;
	private String tokenType = "Bearer";
	private int status;

	public TokenRefreshResponse(String accessToken, String refreshToken,int status) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.status = status;
	}
}
