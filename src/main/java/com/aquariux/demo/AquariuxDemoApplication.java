package com.aquariux.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AquariuxDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AquariuxDemoApplication.class, args);
	}

}
