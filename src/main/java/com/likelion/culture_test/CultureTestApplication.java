package com.likelion.culture_test;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "Culture Test API",
				description = "인사담당자 성향 테스트 API 문서",
				version = "1.0.0",
				contact = @Contact(
						name = "Likelion",
						email = "garam8796@gmail.com"
				)
		),
		servers = {
				@Server(url = "http://localhost:8090", description = "개발 서버"),
				@Server(url = "https://api.heun0.site", description = "운영 서버")
		}
)
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
//EnableCaching
public class CultureTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CultureTestApplication.class, args);
	}

}
