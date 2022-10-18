package com.ihi.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ihi.auth.payload.request.LoginRequest;
import com.ihi.auth.payload.request.SignupRequest;
import com.ihi.auth.service.AuthService;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthService authService;

	@CrossOrigin
	@PostMapping("/token/generate")
	public ResponseEntity<?> tokenGenerate(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.authenticationService(loginRequest);
	}

	@CrossOrigin
	@PostMapping("/account/create")
	public ResponseEntity<?> accountCreation(@Valid @RequestBody SignupRequest signUpRequest) {
		return authService.singUpService(signUpRequest);
	}

	@PostMapping("/token/refresh/{refreshToken}")
	public ResponseEntity<?> refreshtoken(@Valid @PathVariable(name = "refreshToken" , required = true) String refreshToken) {
		return authService.refreshToken(refreshToken);
	}

}
