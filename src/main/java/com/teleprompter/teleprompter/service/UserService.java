package com.teleprompter.teleprompter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.RegisterUserRequest;
import com.teleprompter.teleprompter.dtos.UserMapper;
import com.teleprompter.teleprompter.dtos.UserResponse;
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
		
		
		
		
		
		
		
		return null;
	}
	
	
	
}
