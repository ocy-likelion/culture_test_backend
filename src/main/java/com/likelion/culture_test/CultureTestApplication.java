package com.likelion.culture_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EntityScan(basePackages = "com.likelion.culture_test.domain")  // 👈 이걸 추가
@EnableJpaRepositories(basePackages = "com.likelion.culture_test.domain")  // 👈 이것도 추가!
public class CultureTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CultureTestApplication.class, args);
	}

}
