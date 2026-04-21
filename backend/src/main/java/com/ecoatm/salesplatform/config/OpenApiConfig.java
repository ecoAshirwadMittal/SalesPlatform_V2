package com.ecoatm.salesplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Matches the HttpOnly cookie name issued by AuthController (2026-04-13 ADR).
    private static final String COOKIE_SCHEME = "authCookie";
    // Retained as a fallback for server-to-server callers and Postman collections.
    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI salesplatformOpenApi() {
        SecurityScheme cookieScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("auth_token")
                .description("JWT issued as an HttpOnly SameSite=Strict cookie by POST /api/v1/auth/login.");

        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Bearer token fallback for server-to-server callers; same JWT as the cookie.");

        return new OpenAPI()
                .info(new Info()
                        .title("SalesPlatform API")
                        .version("v1")
                        .description("REST API for the modern SalesPlatform (Mendix port). "
                                + "All endpoints under /api/v1 require authentication unless documented as permitAll.")
                        .license(new License().name("Proprietary").url("https://ecoatm.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local dev"),
                        new Server().url("https://buy-qa.ecoatmdirect.com").description("QA")))
                .components(new Components()
                        .addSecuritySchemes(COOKIE_SCHEME, cookieScheme)
                        .addSecuritySchemes(BEARER_SCHEME, bearerScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList(COOKIE_SCHEME)
                        .addList(BEARER_SCHEME));
    }
}
