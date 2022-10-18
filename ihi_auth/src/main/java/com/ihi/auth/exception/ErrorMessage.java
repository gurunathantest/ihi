package com.ihi.auth.exception;


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

	public ErrorMessage(HttpStatus status, Throwable ex, DateTime date, String message) {
		this();
		this.httpStatus = status;
		this.message = message;
		this.description = ex.getLocalizedMessage();
		this.timestamp = date;
		this.status = status.value();
	}

	public ErrorMessage(HttpStatus badRequest, DateTime date, String message, String description) {
		// TODO Auto-generated constructor stub
		this.httpStatus = badRequest;
		this.timestamp = date;
		this.message = message;
		this.description = description;
		this.status = badRequest.value();
	}
}
