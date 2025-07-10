package com.likelion.culture_test.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
          .allowedOrigins(
              "http://localhost:5173",
              "https://www.survey.heun0.site",
              "https://api.heun0.site"
          )
          .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true)
          .maxAge(3600);
    }


    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(fastApiBaseUrl)  // FastAPI 서버 주소로 바꾸세요
                .build();
    }
}
