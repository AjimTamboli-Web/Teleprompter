package com.teleprompter.teleprompter.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.teleprompter.teleprompter.entity.User;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final long expirationMinutes;
	
	public JwtService(JwtEncoder jwtEncoder, 
			  @Value("${jwt.expiration-minutes}") long expirationMinutes) {
		this.jwtEncoder = jwtEncoder;
		this.expirationMinutes = expirationMinutes;
	}
	
	public String generateToken(User user) {
		Instant now = Instant.now();
		Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
		
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();     // A header is created
		
// A payload is created, which includes 'sub'(the user's UUID), 'iat'(the current time), 'exp'(current time + 60 minutes), and 'role'(user/admin).		
		JwtClaimsSet claims = JwtClaimsSet.builder() 
				.subject(user.getId().toString())           // Converted the UUID to a string
				.issuedAt(now)
				.expiresAt(expiresAt)
				.claim("role", user.getRole().name())
				.build();
	
	 JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims); // It bundles the header and payload into a packet.
	 return jwtEncoder.encode(parameters).getTokenValue(); // It signs using a secret key and provides the final, captured string.
	}
	
	public long getExpirationSeconds() {
		return expirationMinutes * 60;
	}
	
	
}
