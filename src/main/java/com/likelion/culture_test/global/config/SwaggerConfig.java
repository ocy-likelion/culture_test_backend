package com.likelion.culture_test.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {

        // jwt 인증 방식 정의
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        OpenAPI openAPI = new OpenAPI()
                .info(new Info().title("기업 컬쳐핏 테스트 API").version("v1.0.0"))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));

        if ("prod".equals(activeProfile)) {
            Server prodServer = new Server();
            prodServer.setUrl("https://api.heun0.site");
            prodServer.setDescription("Production server");
            openAPI.servers(List.of(prodServer));
        }

        return openAPI;
    }

}
