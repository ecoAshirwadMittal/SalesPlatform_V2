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
                // Sub-project 6 R3 lifecycle admin endpoints (Administrator only).
                // Must precede the broader /api/v1/admin/auctions/** matcher.
                .requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/preprocess-r3")
                    .hasRole("Administrator")
                .requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reinit-r3")
                    .hasRole("Administrator")
                .requestMatchers("/api/v1/admin/auctions/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/scheduling-auctions/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/buyers/**").hasAnyRole("Administrator", "Compliance")
                .requestMatchers("/api/v1/admin/reserve-bids/**").hasRole("Administrator")
                // P8 admin surfaces: SalesOps + Administrator per master plan;
                // matchers must precede the catch-all admin rule.
                .requestMatchers("/api/v1/admin/round-criteria/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/round3-reports/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/bid-data/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/qualified-buyer-codes/**").hasAnyRole("Administrator", "SalesOps")
                // Sub-project 4B PO admin surface
                .requestMatchers("/api/v1/admin/purchase-orders/**").hasAnyRole("Administrator", "SalesOps")
                // Sub-project 4C recalc admin endpoints
                .requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/re-rank",
                                 "/api/v1/admin/auctions/scheduling-auctions/*/recalculate-target-price")
                    .hasAnyRole("Administrator", "SalesOps")
                // Sub-project 5 R2 buyer assignment admin endpoint
                .requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reassign-r2-buyers")
                    .hasAnyRole("Administrator", "SalesOps")
                // Partial Credit Requests — admin review surface (Sprint 3).
                // Class-level @PreAuthorize on AdminPartialCreditController
                // narrows further to PartialCredit_SalesOps/PartialCredit_Admin
                // when SPKB-3659 wires the new role mapping; today's SalesOps
                // and Administrator accounts are admitted directly.
                // Matcher precedes the broader /api/v1/admin/** rule so SalesOps
                // is not blocked by the Administrator-only catch-all.
                .requestMatchers("/api/v1/admin/partial-credit/**")
                    .hasAnyRole("PartialCredit_SalesOps", "PartialCredit_Admin", "SalesOps", "Administrator")
                .requestMatchers("/api/v1/admin/**").hasRole("Administrator")
                .requestMatchers("/api/v1/inventory/sync/**").hasRole("Administrator")
                .requestMatchers("/api/v1/bidder/**").hasAnyRole("Bidder", "Administrator")
                // Partial Credit Requests — buyer-facing surface. Class-level
                // @PreAuthorize on the controller narrows further to the new
                // PartialCredit_Buyer role (when SPKB-3659 wires it) plus
                // Bidder/Administrator for today's accounts.
                .requestMatchers("/api/v1/buyer/partial-credit/**")
                    .hasAnyRole("PartialCredit_Buyer", "Bidder", "Administrator")
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
