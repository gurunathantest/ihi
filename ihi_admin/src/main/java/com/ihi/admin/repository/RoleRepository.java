package com.ihi.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ihi.admin.model.ERole;
import com.ihi.admin.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(ERole name);

	Boolean existsByName(ERole name);

}
