package com.ihi.hedera.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ihi.hedera.model.RefreshToken;
import com.ihi.hedera.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User byId);

}
