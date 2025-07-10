plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.likelion"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-actuator")


	// Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// JUnit
	testImplementation("org.junit.jupiter:junit-jupiter")

	// Swagger UI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	implementation ("com.h2database:h2")

	// JWT
	implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON 파싱용


	//Oauth2
	implementation("org.springframework.boot:spring-boot-starter-security")  // 기본 보안
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // 소셜 로그인

}

tasks.withType<Test> {
	useJUnitPlatform()
}
