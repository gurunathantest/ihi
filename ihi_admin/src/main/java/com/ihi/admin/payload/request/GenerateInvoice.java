package com.ihi.admin.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateInvoice {

	@NotNull
	private String clientId;
	
	@NotNull
	private int year;
	
	@NotNull
	private int month;
	
	@NotNull
	private int page;
	
	@NotNull
	private int size;
}
