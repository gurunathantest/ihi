package com.ihi.auth.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.ihi.auth.model.UserStatus;

import lombok.Data;

@Data
public class SignupRequest {

	@NotNull
	private Set<String> role;

	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	private String firstName;

	private String lastName;

	private String mobileNum;
	
	private UserStatus status;

}
