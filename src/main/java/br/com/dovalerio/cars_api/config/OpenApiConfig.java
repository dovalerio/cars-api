package br.com.dovalerio.cars_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(securityComponents());
    }

    private Info apiInfo() {
        return new Info()
                .title("Cars API")
                .description("API REST para gerenciamento de veículos com controle de acesso e integração de câmbio")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Danilo")
                        .email("danilo@email.com"))
                .license(new License()
                        .name("MIT"));
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", bearerAuthScheme());
    }

    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}