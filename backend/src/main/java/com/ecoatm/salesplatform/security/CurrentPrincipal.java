package com.ecoatm.salesplatform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves a stable display name for the currently authenticated principal.
 * JwtAuthenticationFilter populates UsernamePasswordAuthenticationToken with
 * principal=userId (Long) and credentials=email — the email is the most
 * user-friendly stamp for audit rows, so we prefer it when available.
 */
public final class CurrentPrincipal {

    private CurrentPrincipal() {}

    public static String displayName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "system";
        }
        Object credentials = auth.getCredentials();
        if (credentials instanceof String email && !email.isBlank()) {
            return email;
        }
        String name = auth.getName();
        return (name == null || name.isBlank()) ? "system" : name;
    }
}
