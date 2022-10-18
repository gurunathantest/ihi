package com.ihi.hedera.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileRequest {

	@NotNull
	private String fileContent;
	
	@NotNull
	private String clientId;
	
	@NotNull
	private String userId;
	
}
