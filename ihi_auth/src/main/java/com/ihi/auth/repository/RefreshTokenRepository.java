package com.ihi.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ihi.auth.model.RefreshToken;
import com.ihi.auth.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User byId);
	
	Optional<RefreshToken> findByUserId(String userId);

}
