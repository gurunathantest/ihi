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

public class FileCreationRequest {

	@NotNull
	private byte[] fileContent;

	@NotNull
	private String operatorId;

	@NotNull
	private String operatorKey;

	private String clientId;

	private String userId;

}
