package com.teleprompter.teleprompter.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{

	Optional<User> findByEmail(String email);
	
//	Spring Data JPA uses Query Derivation at startup. It parses existsBy as a structural prefix, tokenizes Email and
//	Phone against the entity's property names, and compiles them directly into SELECT 1 FROM users WHERE email = ? 
//	LIMIT 1 database operations.
//	 To check phone and email uniqueness using derived query methods.
	boolean existsByPhone(String phone);
	boolean existsByEmail(String email);
	
}
