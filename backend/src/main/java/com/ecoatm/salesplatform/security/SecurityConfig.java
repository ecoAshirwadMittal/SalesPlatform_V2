package com.ecoatm.salesplatform.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/logout",
                        "/api/v1/auth/sso",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/reset-password"
                ).permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/v1/admin/inventory/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/auctions/round-filters/**").hasRole("Administrator")
                .requestMatchers(HttpMethod.POST, "/api/v1/admin/auctions/*/rounds/1/init").hasRole("Administrator")
                .requestMatchers("/api/v1/admin/auctions/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/scheduling-auctions/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/buyers/**").hasAnyRole("Administrator", "Compliance")
                .requestMatchers("/api/v1/admin/**").hasRole("Administrator")
                .requestMatchers("/api/v1/inventory/sync/**").hasRole("Administrator")
                .requestMatchers("/api/v1/bidder/**").hasAnyRole("Bidder", "Administrator")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // setAllowedOriginPatterns (not setAllowedOrigins) supports wildcards
        // like "https://*.ngrok-free.dev" — required when exposing the local
        // frontend through an ngrok tunnel for external testing.
        configuration.setAllowedOriginPatterns(List.copyOf(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-CSRF-TOKEN", "ngrok-skip-browser-warning"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
