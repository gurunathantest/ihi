package com.ihi.hts.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ihi.hts.model.RefreshToken;
import com.ihi.hts.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User byId);

}
