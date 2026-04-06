package com.teleprompter.teleprompter.dtos;


import lombok.Getter;
import lombok.Setter;

// creating DTO for user registration from user entity

@Getter @Setter
public class RegisterUserRequest {


	private String name;
	
	private String email;
	
	private String phone;
	
	private Long passwordHash;
	
	
}
