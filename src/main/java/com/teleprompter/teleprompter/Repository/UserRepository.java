package com.teleprompter.teleprompter.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{

	Optional<User> findByEmail(String email);
	
	boolean existsByPhone(String phone);
	
}
