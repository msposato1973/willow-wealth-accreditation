package com.willow.accreditation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WillowAccreditationApplication {
	public static void main(String[] args) {
		SpringApplication.run(WillowAccreditationApplication.class, args);
	}
}