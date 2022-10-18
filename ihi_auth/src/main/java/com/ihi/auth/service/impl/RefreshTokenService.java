package com.ihi.auth.service.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ihi.auth.model.RefreshToken;
import com.ihi.auth.repository.RefreshTokenRepository;
import com.ihi.auth.repository.UserRepository;

@Service
public class RefreshTokenService {

	@Value("${auth.token.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	Environment env;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(String userId) {
		RefreshToken refreshToken =null;
		Optional<RefreshToken> refreshOptional=refreshTokenRepository.findByUserId(userId);
		
		if (refreshOptional.isPresent()) {
			refreshToken = refreshOptional.get();
		} else {
			refreshToken = new RefreshToken();
			refreshToken.setUser(userRepository.getById(userId));
			refreshToken.setToken(UUID.randomUUID().toString());
		}
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(String userId) {
		return refreshTokenRepository.deleteByUser(userRepository.getById(userId));
	}
}
