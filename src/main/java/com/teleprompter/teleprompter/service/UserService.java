package com.teleprompter.teleprompter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.LoginRequest;
import com.teleprompter.teleprompter.dtos.LoginResponse;
import com.teleprompter.teleprompter.dtos.RegisterUserRequest;
import com.teleprompter.teleprompter.dtos.UserMapper;
import com.teleprompter.teleprompter.dtos.UserResponse;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.enums.UserRole;
import com.teleprompter.teleprompter.exception.DuplicateResourceException;
import com.teleprompter.teleprompter.exception.InvalidCredentialsException;
import com.teleprompter.teleprompter.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public UserService(UserRepository repo, UserMapper mapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userMapper = mapper;
		this.userRepo = repo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
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

		user.setRole(UserRole.USER);   // Explicitly enforce USER role on server (prevent mass assignment)

		String hashedPassword = passwordEncoder.encode(request.getPassword());
		user.setPasswordHash(hashedPassword);
		
		
		User userSaved = userRepo.save(user);

		return userMapper.toResponse(userSaved);
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {

		User user = userRepo.findByEmail(request.email()) // fail fast - no BCrypt call
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) { // fails slow - BCrypt runs
			throw new InvalidCredentialsException("Invalid email or password");
		}

		String token = jwtService.generateToken(user);
		
		UserResponse userResponse = userMapper.toResponse(user);
		
		return new LoginResponse(token, jwtService.getExpirationSeconds(),userResponse);

	}

}
