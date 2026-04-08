package com.ecoatm.salesplatform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET = "test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!";
    private static final long DEFAULT_EXP_MS = 3600000; // 1 hour
    private static final long REMEMBER_ME_EXP_MS = 7200000; // 2 hours

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, DEFAULT_EXP_MS, REMEMBER_ME_EXP_MS);
    }

    @Test
    void generateToken_containsCorrectClaims() {
        String token = jwtService.generateToken(42L, "test@example.com", List.of("ADMIN", "Bidder"), false);

        assertThat(token).isNotBlank();
        assertThat(jwtService.getUserId(token)).isEqualTo(42L);
        assertThat(jwtService.getEmail(token)).isEqualTo("test@example.com");
        assertThat(jwtService.getRoles(token)).containsExactly("ADMIN", "Bidder");
    }

    @Test
    void generateToken_defaultExpiration_isNotRememberMe() {
        String token = jwtService.generateToken(1L, "a@b.com", List.of(), false);
        Claims claims = jwtService.parseToken(token);

        long expiresInMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertThat(expiresInMs).isEqualTo(DEFAULT_EXP_MS);
    }

    @Test
    void generateToken_rememberMe_usesLongerExpiration() {
        String token = jwtService.generateToken(1L, "a@b.com", List.of(), true);
        Claims claims = jwtService.parseToken(token);

        long expiresInMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertThat(expiresInMs).isEqualTo(REMEMBER_ME_EXP_MS);
    }

    @Test
    void isValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken(1L, "a@b.com", List.of("USER"), false);
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void isValid_returnsFalseForTamperedToken() {
        String token = jwtService.generateToken(1L, "a@b.com", List.of("USER"), false);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void isValid_returnsFalseForExpiredToken() {
        // Create a service with 0ms expiration
        JwtService expiredService = new JwtService(TEST_SECRET, 0, 0);
        String token = expiredService.generateToken(1L, "a@b.com", List.of(), false);

        // Token expires immediately
        assertThat(expiredService.isValid(token)).isFalse();
    }

    @Test
    void isValid_returnsFalseForTokenSignedWithDifferentKey() {
        String differentSecret = "different-secret-key-must-also-be-32-bytes-long!!!";
        JwtService otherService = new JwtService(differentSecret, DEFAULT_EXP_MS, REMEMBER_ME_EXP_MS);
        String token = otherService.generateToken(1L, "a@b.com", List.of(), false);

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void isValid_returnsFalseForGarbageString() {
        assertThat(jwtService.isValid("not-a-jwt")).isFalse();
    }

    @Test
    void isValid_returnsFalseForDummyToken() {
        // The old dummy token pattern must be rejected
        assertThat(jwtService.isValid("dummy-jwt-token-42")).isFalse();
    }

    @Test
    void parseToken_extractsAllClaimsCorrectly() {
        String token = jwtService.generateToken(99L, "admin@ecoatm.com", List.of("ADMIN", "SalesOps"), false);
        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("admin@ecoatm.com");
        assertThat(claims.get("userId", Long.class)).isEqualTo(99L);
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }
}
