package com.ihi.hcs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ihi.hcs.model.ERole;
import com.ihi.hcs.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(ERole name);

	Boolean existsByName(ERole name);

}
