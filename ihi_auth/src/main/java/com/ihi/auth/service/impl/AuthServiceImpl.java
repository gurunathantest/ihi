package com.ihi.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihi.auth.constant.ApiConstant;
import com.ihi.auth.exception.MobileNumberExistException;
import com.ihi.auth.exception.RoleNotFoundException;
import com.ihi.auth.exception.UserInfoException;
import com.ihi.auth.exception.UserNameExistsException;
import com.ihi.auth.exception.UserNotFoundException;
import com.ihi.auth.model.ERole;
import com.ihi.auth.model.RefreshToken;
import com.ihi.auth.model.Role;
import com.ihi.auth.model.User;
import com.ihi.auth.model.UserStatus;
import com.ihi.auth.mongo.model.ServiceType;
import com.ihi.auth.payload.request.LoginRequest;
import com.ihi.auth.payload.request.SignupRequest;
import com.ihi.auth.payload.response.JwtResponse;
import com.ihi.auth.payload.response.MessageResponse;
import com.ihi.auth.payload.response.TokenRefreshResponse;
import com.ihi.auth.repository.RoleRepository;
import com.ihi.auth.repository.UserRepository;
import com.ihi.auth.security.jwt.JwtUtils;
import com.ihi.auth.service.AuthService;
import com.ihi.auth.utils.AsynchronousUtils;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RestTemplateService restTemplateService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private Environment env;

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	AsynchronousUtils asynchronousUtils;

	@Override
	public ResponseEntity<?> authenticationService(LoginRequest loginRequest) {
		if (!userRepository.existsByUsername(loginRequest.getUsername())) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("user.not.found"), ApiConstant.SIGN_IN, "",
					loginRequest.getUsername());
			throw new UserNotFoundException(env.getProperty("user.not.found"));
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(authentication);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList()); // creating refresh token

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), roles, refreshToken.getToken()));
	}

	@Transactional
	@Override
	public ResponseEntity<?> singUpService(SignupRequest signUpRequest) {
		// Create new user's account
		User user = User.builder().mobileNum(signUpRequest.getMobileNum()).email(signUpRequest.getEmail())
				.password(encoder.encode(signUpRequest.getPassword())).firstName(signUpRequest.getFirstName())
				.lastName(signUpRequest.getLastName()).username(signUpRequest.getUsername()).status(UserStatus.ACTIVE)
				.build();

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("email.already.exist"),
					ApiConstant.SIGN_UP, signUpRequest, signUpRequest.getEmail());
			throw new UserNameExistsException(env.getProperty("email.already.exist"));
		}

		if (userRepository.existsBymobileNum(signUpRequest.getMobileNum())) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("mobileno.already.exist"),
					ApiConstant.SIGN_UP, signUpRequest, "");
			throw new MobileNumberExistException(env.getProperty("mobileno.already.exist"));
		}

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();
		user.setRoles(getRole(strRoles, roles));
		try {
			user = userRepository.save(user);
		} catch (Exception e) {
			asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("user.info.error") + "," + e.getMessage(),
					ApiConstant.SIGN_UP, user, user.getId());
			throw new UserInfoException(e.getMessage());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("registered.success")).response(user).httpStatus(HttpStatus.OK).build());
	}

	private Set<Role> getRole(Set<String> strRoles, Set<Role> roles) {
		if (Objects.isNull(strRoles)) {
			if (!roleRepository.existsByName(ERole.ROLE_USER)) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("role.not.found"), "getRole", "", "");
				mongoDBLoggerService.createLogger(env.getProperty("role.not.found"), ServiceType.AUTH, null, "getRole",
						null, HttpStatus.BAD_REQUEST.value());
				throw new RoleNotFoundException(env.getProperty("role.not.found"));
			}
			Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "ROLE_SUPER_ADMIN":
					Role adminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN).get();
					roles.add(adminRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
					roles.add(userRole);
				}
			});
		}
		return roles;
	}

	@Override
	public ResponseEntity<?> refreshToken(@Valid String accessToken) {
		return refreshTokenService.findByToken(accessToken)
				// .map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, accessToken, HttpStatus.OK.value()));
				}).get();

	}
}
