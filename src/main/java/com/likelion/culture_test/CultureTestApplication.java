package com.likelion.culture_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class CultureTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CultureTestApplication.class, args);
	}

}
