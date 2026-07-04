package com.example.ecommerce.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI configuration for generated Swagger documentation.
 *
 * @author Pial Kanti Samadder
 */
@Configuration
public class OpenApiConfig {
    private static final String API_GROUP = "ecommerce-api";
    private static final String API_PATH_PATTERN = "/api/v1/**";
    private static final String BEARER_AUTH = "bearerAuth";

    /**
     * Provides global metadata displayed in OpenAPI and Swagger UI.
     *
     * @return configured OpenAPI metadata
     */
    @Bean
    public OpenAPI ecommerceOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Ecommerce Backend API")
                        .description("REST API documentation for ecommerce backend services.")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Pial Kanti Samadder")
                                .email("pialkanti2012@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }

    /**
     * Groups versioned ecommerce API endpoints for Swagger UI.
     *
     * @return grouped OpenAPI definition for versioned ecommerce endpoints
     */
    @Bean
    public GroupedOpenApi ecommerceApiGroup() {
        return GroupedOpenApi.builder()
                .group(API_GROUP)
                .pathsToMatch(API_PATH_PATTERN)
                .build();
    }
}
