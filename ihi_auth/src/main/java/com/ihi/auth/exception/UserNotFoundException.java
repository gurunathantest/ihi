package com.ihi.auth.exception;

public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 8307506986388742263L;
}
