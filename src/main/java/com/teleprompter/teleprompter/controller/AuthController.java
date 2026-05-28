package com.teleprompter.teleprompter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.dtos.LoginRequest;
import com.teleprompter.teleprompter.dtos.UserResponse;
import com.teleprompter.teleprompter.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request){
		
		UserResponse response = userService.login(request);
		
		return ResponseEntity.ok(response);
		
	}
	
}
