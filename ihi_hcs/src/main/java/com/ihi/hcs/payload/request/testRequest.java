package com.ihi.hcs.payload.request;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class testRequest {
	private String data;

	private String memo;

	private String clientId;

	private String topicId;

	private long consensusTimeStampSeconds;

	private int consensusTimeStampNanos;

	private String status;

	private String userId;

	private String logType;
}
