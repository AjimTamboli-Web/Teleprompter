package com.teleprompter.teleprompter.dtos;

public record LoginResponse(
		String accessToken,
		String tokenType,
		long expiresInSeconds,
		UserResponse user) {

	public LoginResponse(String accessToken, long expiresInSeconds, UserResponse user) {
		this(accessToken,"Bearer",expiresInSeconds, user);
	}
	
}
