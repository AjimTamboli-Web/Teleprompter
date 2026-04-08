package com.teleprompter.teleprompter.dtos;

import org.springframework.stereotype.Component;

import com.teleprompter.teleprompter.entity.User;


@Component      // Spring-managed bean, allows loose coupling and easy mocking in unit tests
public class UserMapper {
	
	 /**
      * Converts a database User entity into a clean outbound UserResponse DTO.
      * Crucially excludes the 'passwordHash' field to prevent security leaks.
     */
	public UserResponse toResponse(User user) {
		// Null-guard to prevent NullPointerException
		if(user == null	) {
			return null;
		}
		
		
		UserResponse response = new UserResponse();
		
		// Mapping every non-sensitive field carefully using setters
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setPhone(user.getPhone());
		response.setStatus(user.getStatus()); // Handled perfectly via Jackson string serialization

		
		return response;
	}
	
}
