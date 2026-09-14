package com.lingo_leap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LingoLeapApplication {

	public static void main(String[] args) {
		SpringApplication.run(LingoLeapApplication.class, args);
	}

}
