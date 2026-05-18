package com.teleprompter.teleprompter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.RegisterUserRequest;
import com.teleprompter.teleprompter.dtos.UserMapper;
import com.teleprompter.teleprompter.dtos.UserResponse;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.repository.UserRepository;

@Service
public class UserService {

		private final UserRepository userRepo;
		private final UserMapper userMapper;
		private final PasswordEncoder passwordEncoder;
	
//		constructor injection for all dependencies
	public UserService(UserRepository repo, UserMapper mapper, PasswordEncoder passwordEncoder) {
		this.userMapper = mapper;
		this.userRepo = repo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public UserResponse registerUser(RegisterUserRequest request) {
		
		  // 1. Uniqueness pre-checks 
		if(userRepo.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email is already registered " + request.getEmail());
		}
		
		if(userRepo.existsByPhone(request.getPhone())) {
			throw new RuntimeException("Phone number is alredy registered " + request.getPhone());
		}
		
		// 2. Structurally map DTO to Entity
		User user = userMapper.toEntity(request);
		
		 // 3. Security Intervention: Hash the raw password before it reaches the DB layer
		String hashedPassword = passwordEncoder.encode(request.getPassword());
		user.setPasswordHash(hashedPassword);
		
		// 4. Save to the persistent store
		User userSaved = userRepo.save(user);
		
		
		 // 5. Shape the persistent entity back into an external API response
		return userMapper.toResponse(userSaved);
	}
	
	
	
}
