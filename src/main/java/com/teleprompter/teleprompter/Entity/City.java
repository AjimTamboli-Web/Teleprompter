package com.teleprompter.teleprompter.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "city", uniqueConstraints =  @UniqueConstraint(columnNames = {"city_name","state","country"}))
@Getter @Setter
public class City {

	
	// City — id BIGINT PK, city_name VARCHAR(100), state VARCHAR(100), 
	//    country VARCHAR(100) DEFAULT 'India', UNIQUE(city_name, state, country)
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(length = 100, nullable = false)
	private String cityName;
	
	@Column(length = 100, nullable = false)
	private String state;
	
	
	@Column(length = 100, nullable = false)
	@org.hibernate.annotations.ColumnDefault("'India'")
	private String country = "India";
	
	 
	
	
}
