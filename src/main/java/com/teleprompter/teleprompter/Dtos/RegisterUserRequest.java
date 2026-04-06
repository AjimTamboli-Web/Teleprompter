package com.teleprompter.teleprompter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// creating DTO for user registration from user entity

@Getter @Setter
public class RegisterUserRequest {

	
	@NotBlank
	@Size(max = 100)
	private String name;
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String phone;
	
	@NotBlank
	@Size(min = 8,max = 72)
	private String password;
	
	
}
