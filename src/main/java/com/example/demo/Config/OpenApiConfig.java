package com.example.demo.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI likelambaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Likelamba API")
                        .description("API de gestion des tontines et micro-crédits rotatifs")
                        .version("v1.0"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/auth/**", "/api/paiements/callback")
                .build();
    }

    @Bean
    public GroupedOpenApi authenticatedApi() {
        return GroupedOpenApi.builder()
                .group("authenticated")
                .pathsToMatch("/api/organisations/**", "/api/tontines/**", "/api/credits/**", "/api/penalites/**")
                .build();
    }
}
