package com.teleprompter.teleprompter;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
	
  //User — id UUID PK, name VARCHAR(100), email VARCHAR(150) UNIQUE, 
  //	phone VARCHAR(15) UNIQUE, password_hash VARCHAR(255), 
  //	status VARCHAR(20) DEFAULT 'ACTIVE' (ACTIVE/SUSPENDED/BANNED/DELETED)
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(length = 100, nullable = false)
	private String name;
	
	@Column(length = 150, unique = true, nullable = false)
	private String email;
	
	@Column(length = 15, unique = true, nullable = false)
	private String phone;
	
	@Column(length = 255, nullable = false)
	private String passwordHash;
	
	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'ACTIVE'")
	private UserStatus status = UserStatus.ACTIVE;
	
	
}
