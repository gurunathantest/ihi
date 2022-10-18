package com.ihi.hedera.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ihi.hedera.model.Role;
import com.ihi.hedera.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Page<User> findAll(Pageable pageable);

	List<User> findAll();

	User getById(String id);

	Page<User> findById(String id, Pageable pageable);

	Boolean existsByPassword(String password);

	User findByEmail(String email);

	User findByusername(String username);

	List<User> findAllByRoles(Role role);

	@Query(value = "select * from users where first_name=:firstName or last_name=:lastName", nativeQuery = true)
	User findByFirstNameOrLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
	
	Boolean existsBymobileNum(String mobileNo);

}
