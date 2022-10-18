package com.ihi.hts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ihi.hts.model.ERole;
import com.ihi.hts.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(ERole name);

	Boolean existsByName(ERole name);

}
