package com.teleprompter.teleprompter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.LoginRequest;
import com.teleprompter.teleprompter.dtos.RegisterUserRequest;
import com.teleprompter.teleprompter.dtos.UserMapper;
import com.teleprompter.teleprompter.dtos.UserResponse;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.exception.DuplicateResourceException;
import com.teleprompter.teleprompter.exception.InvalidCredentialsException;
import com.teleprompter.teleprompter.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository repo, UserMapper mapper, PasswordEncoder passwordEncoder) {
		this.userMapper = mapper;
		this.userRepo = repo;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse registerUser(RegisterUserRequest request) {

		if (userRepo.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("User", "email", request.getEmail());
		}

		if (userRepo.existsByPhone(request.getPhone())) {
			throw new DuplicateResourceException("User", "phone", request.getPhone());
		}

		User user = userMapper.toEntity(request);

		String hashedPassword = passwordEncoder.encode(request.getPassword());
		user.setPasswordHash(hashedPassword);

		User userSaved = userRepo.save(user);

		return userMapper.toResponse(userSaved);
	}

	@Transactional(readOnly = true)
	public UserResponse login(LoginRequest request) {

		User user = userRepo.findByEmail(request.email()) // fail fast - no BCrypt call
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) { // fails slow - BCrypt runs
			throw new InvalidCredentialsException("Invalid email or password");
		}

		return userMapper.toResponse(user);

	}

}
