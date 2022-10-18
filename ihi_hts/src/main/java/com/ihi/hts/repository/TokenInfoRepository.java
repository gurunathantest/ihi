package com.ihi.hts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hts.model.TokenInfo;
import com.ihi.hts.model.User;

@Repository
public interface TokenInfoRepository  extends JpaRepository<TokenInfo, String> {
	
	Optional<TokenInfo> findByUser(User user);

}
