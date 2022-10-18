package com.ihi.hcs.payload.request;

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
public class HcsMessageSubmit {

	private Object message;
	private String memo;
	private String clientId;
	private String userId;
}
