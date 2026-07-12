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
                // Sales-representative write CRUD (gap 2.4). DEDICATED namespace,
                // intentionally NOT under /api/v1/admin/buyers/** above (which admits
                // Compliance) — sales-rep writes are Administrator + SalesOps only.
                // Explicit matcher (defense-in-depth) precedes the /api/v1/admin/**
                // catch-all; mirrored by @PreAuthorize on SalesRepController.
                .requestMatchers("/api/v1/admin/sales-representatives/**")
                    .hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/reserve-bids/**").hasRole("Administrator")
                // Unified email management (Task 7) — SMTP config admin surface.
                // Same effective role as the "/api/v1/admin/**" catch-all below, but
                // Security Rules mandate an explicit matcher (defense-in-depth) for
                // every new admin namespace rather than relying on the catch-all alone.
                .requestMatchers("/api/v1/admin/email/**").hasRole("Administrator")
                // P8 admin surfaces: SalesOps + Administrator per master plan;
                // matchers must precede the catch-all admin rule.
                .requestMatchers("/api/v1/admin/round-criteria/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/round3-reports/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/bid-data/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/admin/qualified-buyer-codes/**").hasAnyRole("Administrator", "SalesOps")
                // Sub-project 4B PO admin surface
                .requestMatchers("/api/v1/admin/purchase-orders/**").hasAnyRole("Administrator", "SalesOps")
                // L-9 (security review 2026-07-10): the Sub-project 4C recalc
                // (re-rank / recalculate-target-price) and Sub-project 5
                // (reassign-r2-buyers) matchers were removed here as DEAD CODE.
                // They target /api/v1/admin/auctions/scheduling-auctions/** —
                // already matched by the broader "/api/v1/admin/auctions/**" rule
                // above, which grants the SAME hasAnyRole("Administrator",
                // "SalesOps"). Spring Security applies the first matching rule, so
                // these later duplicates never fired. Authz for those URLs is
                // unchanged (still Administrator + SalesOps). NB: the R3
                // preprocess-r3 / reinit-r3 matchers stay above the broader rule
                // because they NARROW to Administrator-only — those are not
                // redundant and must not be removed.
                // Partial Credit Requests — admin review surface (Sprint 3).
                // Class-level @PreAuthorize on AdminPartialCreditController
                // narrows further to PartialCredit_SalesOps/PartialCredit_Admin
                // when SPKB-3659 wires the new role mapping; today's SalesOps
                // and Administrator accounts are admitted directly.
                // Matcher precedes the broader /api/v1/admin/** rule so SalesOps
                // is not blocked by the Administrator-only catch-all.
                // L-7 (security review 2026-07-10): status-config edits are
                // platform-wide (pill colours/labels) — tighter than the rest of
                // the partial-credit admin surface. This narrower matcher must
                // precede the broader partial-credit rule below (first-match-wins)
                // so it actually takes effect; it admits Co-Admin (blocked by the
                // broader rule today, whose role list has no Co-Admin) and drops
                // SalesOps/SalesRep for this one PATCH. 'Co-Admin' is the real
                // seeded role (identity.user_roles; V2/V15) — hasAnyRole prepends
                // ROLE_, matching the ROLE_Co-Admin authority from
                // JwtAuthenticationFilter. Mirrored by @PreAuthorize on
                // AdminPartialCreditController#updateStatus (defense-in-depth).
                .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/partial-credit/statuses/**")
                    .hasAnyRole("Administrator", "Co-Admin")
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
                // Internal PWS + inventory surfaces that previously fell through to
                // anyRequest().authenticated() — no financial/catalog write or sales
                // review should be reachable by a plain authenticated buyer
                // (security review 2026-07-10, CR-3 / H-1 / H-2 / H-4). The
                // /inventory/sync/** and /admin/inventory/** matchers above are more
                // specific and are evaluated first, so they keep their own roles.
                .requestMatchers("/api/v1/pws/offer-review/**")
                    .hasAnyRole("Administrator", "SalesOps", "SalesRep")
                .requestMatchers("/api/v1/pws/pricing/**").hasAnyRole("Administrator", "SalesOps")
                .requestMatchers("/api/v1/inventory/**").hasAnyRole("Administrator", "SalesOps")
                // Buyer-facing cart/offer/counter-offer surfaces — Bidder (own
                // buyer codes only, enforced at the service layer) + Administrator
                // (security review 2026-07-10, CR-3).
                .requestMatchers("/api/v1/pws/offers/**").hasAnyRole("Bidder", "Administrator")
                .requestMatchers("/api/v1/pws/counter-offers/**").hasAnyRole("Bidder", "Administrator")
                .requestMatchers("/api/v1/pws/orders/**").hasAnyRole("Bidder", "Administrator")
                // RMA Oracle resubmit — internal review recovery action only (no
                // Bidder). Narrows the broader /api/v1/pws/rma/** rule below and
                // MUST precede it (first-match-wins), or a Bidder would be admitted
                // by the broad rule. Mirrored by method-level @PreAuthorize on
                // RmaController#resubmitOracle (defense-in-depth). RMA #3 Task B0.
                .requestMatchers(HttpMethod.POST, "/api/v1/pws/rma/*/resubmit-oracle")
                    .hasAnyRole("Administrator", "SalesOps", "SalesRep")
                // RMA surface — buyer submit/view-own + internal review. The
                // review/mutation actions are further narrowed to internal roles by
                // method-level @PreAuthorize on the controller (CR-3/C6).
                .requestMatchers("/api/v1/pws/rma/**")
                    .hasAnyRole("Bidder", "SalesRep", "SalesOps", "Administrator")
                // Internal user-management (role assignment / PII) — Administrator
                // only. Without this an authenticated Bidder could self-grant
                // Administrator via /api/v1/users/direct-users. See review (CR-1).
                .requestMatchers("/api/v1/users/**").hasRole("Administrator")
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
