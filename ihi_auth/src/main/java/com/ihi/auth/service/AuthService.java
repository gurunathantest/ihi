package com.ihi.auth.service;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ihi.auth.payload.request.LoginRequest;
import com.ihi.auth.payload.request.SignupRequest;

@Service
public interface AuthService {

	ResponseEntity<?> authenticationService(LoginRequest loginRequest);

	ResponseEntity<?> singUpService(SignupRequest signUpRequest);

	ResponseEntity<?> refreshToken(@Valid String accessToken);

}
