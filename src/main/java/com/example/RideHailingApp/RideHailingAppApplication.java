package com.example.RideHailingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RideHailingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideHailingAppApplication.class, args);
	}

}
