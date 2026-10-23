package com.teleprompter.teleprompter.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String jwtSecret; // jwt.secret is a Base64 String

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();

	}

	// JwtEncoder is a third-party (NimbusJwtEncoder) object that's why we build
	// using with bean annotation
	@Bean
	public JwtEncoder jwtEncoder() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtSecret); // We will extract the raw bytes by using this
		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256"); // and then create a `SecretKeySpec(...,
																			// "HmacSHA256")` to ensure we get a key of
																			// at least 256 bits (32 bytes).
		return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // CSRF security disable for stateless REST API
				.authorizeHttpRequests( // allowing all request for now (make public)

						auth -> auth.anyRequest().permitAll() // TODO: Replace with JWT authentication filter once
																// implemented
				);

		return http.build();
	}

}
