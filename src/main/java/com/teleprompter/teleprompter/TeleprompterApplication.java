package com.teleprompter.teleprompter;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class TeleprompterApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));// must run before context starts
		SpringApplication.run(TeleprompterApplication.class, args);
	}

}
