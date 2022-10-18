package com.ihi.hcs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ihi.hcs.model.RefreshToken;
import com.ihi.hcs.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User byId);

}
