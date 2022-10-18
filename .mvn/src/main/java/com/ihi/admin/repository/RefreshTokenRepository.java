package com.ihi.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ihi.admin.model.RefreshToken;
import com.ihi.admin.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User byId);
	
	Optional<RefreshToken> findByUserId(String userId);

}
