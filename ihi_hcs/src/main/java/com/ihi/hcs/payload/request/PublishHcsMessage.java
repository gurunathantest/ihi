package com.ihi.hcs.payload.request;

import io.swagger.annotations.ApiParam;
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
public class PublishHcsMessage {

	private Object message;
	private String memo;
	private String clientId;
	private String apiName;
	private String createdDate;
	private String userId;
	private String topicId;
	private HcsStatus status;
	private String serviceType;
	private String role;
	
}
