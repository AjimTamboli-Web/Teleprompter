package com.teleprompter.teleprompter.dtos;

import java.util.UUID;

import com.teleprompter.teleprompter.enums.UserStatus;

import lombok.Getter;
import lombok.Setter;

// creating DTO for user response from user entity

@Getter  @Setter
public class UserResponse {

	
	private UUID id;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private UserStatus status;   // Jackson इसे खुद-ब-खुद JSON में "ACTIVE" या "BANNED" स्ट्रिंग बना देगा
	
	
}
