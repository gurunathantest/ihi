package com.ihi.hcs.exception;


import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {

	private int status;
	private DateTime timestamp;
	private String message;
	private String description;
	private HttpStatus httpStatus;

	public ErrorMessage() {

	}

	public ErrorMessage(HttpStatus httpStatus, Throwable ex, DateTime date, String message,int status) {
		this();
		this.httpStatus = httpStatus;
		this.message = message;
		this.description = ex.getLocalizedMessage();
		this.timestamp = date;
		this.status = status;
	}

	public ErrorMessage(HttpStatus badRequest, DateTime date, String message, String description,int status) {
		// TODO Auto-generated constructor stub
		this.httpStatus = badRequest;
		this.timestamp = date;
		this.message = message;
		this.description = description;
		this.status = status;
	}
}
