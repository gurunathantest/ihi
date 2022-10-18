package com.ihi.hedera.exception;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.gson.Gson;
import com.ihi.hedera.payload.response.MessageResponse;

public class HederaExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	Environment env;

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponseEntity(new ErrorMessage(HttpStatus.BAD_REQUEST, new DateTime(),
				env.getProperty("bad.credentials"), "", HttpStatus.BAD_REQUEST.value()));
	}

	private ResponseEntity<Object> buildResponseEntity(ErrorMessage apiError) {
		return new ResponseEntity<>(apiError, apiError.getHttpStatus());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(env.getProperty("url.not.found")).httpStatus(HttpStatus.BAD_REQUEST).build());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(String.format("%s has invalid value %s", ex.getBindingResult().getFieldError().getField(),
						ex.getBindingResult().getFieldError().getRejectedValue()))
				.httpStatus(HttpStatus.BAD_REQUEST).build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handle(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(env.getProperty("issue.happend.ihi") + " ," + ex.getMessage())
				.httpStatus(HttpStatus.BAD_REQUEST).build());
	}

	@ExceptionHandler(value = HederaException.class)
	public ResponseEntity<Object> hederaException(HederaException exception) {
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(env.getProperty("issue.happend.ihi") + " ," + exception.getMessage())
				.httpStatus(HttpStatus.BAD_REQUEST).build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
		ex.printStackTrace();
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(env.getProperty("issue.happend.ihi") + " ," + ex.getMessage())
				.httpStatus(HttpStatus.BAD_REQUEST).build());
	}

}
