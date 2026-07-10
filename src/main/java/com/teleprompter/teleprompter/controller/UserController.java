package com.teleprompter.teleprompter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.dtos.RegisterUserRequest;
import com.teleprompter.teleprompter.dtos.UserResponse;
import com.teleprompter.teleprompter.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping
	public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
		
		UserResponse response = userService.registerUser(request);
		
		 // TODO: Add Location header
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
