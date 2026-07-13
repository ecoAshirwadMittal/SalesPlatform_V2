package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.EcoATMDirectUser;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.PasswordResetTokenRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context integration test for the public {@code POST /api/v1/auth/activate}
 * endpoint against a real Flyway'd Postgres.
 *
 * <p>Proves the end-to-end contract the mocked slice ({@code AuthControllerTest})
 * cannot: a valid seeded activation token drives controller → service →
 * repositories → DB and flips the real {@code ecoatm_direct_users} row to Active
 * while setting the BCrypt password on {@code identity.users}; an invalid token
 * and a weak password each return {@code 400} with no state change; and the
 * endpoint is reachable with no authentication (public matcher).
 *
 * <p>Seeds via {@link JdbcTemplate} (explicit SQL in the test transaction) and
 * reads back through the JPA repositories so in-transaction mutations are
 * reflected. {@code @Transactional} rolls the fixtures back after each test.
 */
@AutoConfigureMockMvc
@Transactional
class AccountActivationControllerIT extends PostgresIntegrationTest {

    // High, fixed id unlikely to collide with the V17/V19 seeded user range.
    private static final long TEST_USER_ID = 9_000_042L;
    private static final String TEST_EMAIL = "activation-it-buyer@example.com";
    private static final String RAW_TOKEN = "activation-it-raw-token-1234567890abcdef";
    private static final String STRONG_PASSWORD = "ValidPass1!";

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UserRepository userRepository;
    @Autowired private EcoATMDirectUserRepository directUserRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedInactiveUserWithToken() {
        // Newly-provisioned user: no password yet, business status Inactive.
        jdbc.update("""
                INSERT INTO identity.users (id, name, password, active, blocked)
                VALUES (?, ?, NULL, true, false)
                """, TEST_USER_ID, TEST_EMAIL);

        // Pre-activation: user_status stays NULL (chk_edu_user_status permits only
        // Active | Disabled | NULL); overall_user_status carries the 'Inactive' state.
        jdbc.update("""
                INSERT INTO user_mgmt.ecoatm_direct_users
                    (user_id, first_name, last_name, user_status, inactive, overall_user_status, is_buyer_role)
                VALUES (?, 'Activation', 'Tester', NULL, true, 'Inactive', true)
                """, TEST_USER_ID);

        // Valid, unconsumed, future-dated activation token (reset-token machinery).
        jdbc.update("""
                INSERT INTO identity.password_reset_tokens (user_id, token_hash, expires_at)
                VALUES (?, ?, ?)
                """, TEST_USER_ID, sha256Hex(RAW_TOKEN),
                Timestamp.from(Instant.now().plusSeconds(3600)));
    }

    @Test
    @DisplayName("POST /activate valid token → 200 and the user row flips to Active")
    void activate_validToken_returns200_andUserFlipsToActive() throws Exception {
        mvc.perform(post("/api/v1/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(RAW_TOKEN, STRONG_PASSWORD)))
                .andExpect(status().isOk());

        EcoATMDirectUser directUser = directUserRepository.findById(TEST_USER_ID).orElseThrow();
        assertThat(directUser.getUserStatus()).isEqualTo("Active");
        assertThat(directUser.getOverallUserStatus()).isEqualTo("Active");
        assertThat(directUser.isInactive()).isFalse();
        assertThat(directUser.getActivationDate()).isNotNull();

        // BCrypt password set on identity.users (where login validates it).
        User user = userRepository.findById(TEST_USER_ID).orElseThrow();
        assertThat(user.getPassword()).isNotBlank();
        assertThat(passwordEncoder.matches(STRONG_PASSWORD, user.getPassword())).isTrue();

        // Token consumed — a second lookup finds no live token (single-use).
        Optional<?> stillValid = tokenRepository.findValidByHash(sha256Hex(RAW_TOKEN), Instant.now());
        assertThat(stillValid).isEmpty();
    }

    @Test
    @DisplayName("POST /activate invalid token → 400 and no state change")
    void activate_invalidToken_returns400_noStateChange() throws Exception {
        mvc.perform(post("/api/v1/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("this-token-does-not-exist", STRONG_PASSWORD)))
                .andExpect(status().isBadRequest());

        EcoATMDirectUser directUser = directUserRepository.findById(TEST_USER_ID).orElseThrow();
        assertThat(directUser.getUserStatus()).isNull();
        assertThat(directUser.getOverallUserStatus()).isEqualTo("Inactive");
        assertThat(directUser.isInactive()).isTrue();
        assertThat(directUser.getActivationDate()).isNull();
    }

    @Test
    @DisplayName("POST /activate weak password → 400 and no state change (token not consumed)")
    void activate_weakPassword_returns400_noStateChange() throws Exception {
        mvc.perform(post("/api/v1/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(RAW_TOKEN, "weak")))
                .andExpect(status().isBadRequest());

        EcoATMDirectUser directUser = directUserRepository.findById(TEST_USER_ID).orElseThrow();
        assertThat(directUser.getOverallUserStatus()).isEqualTo("Inactive");
        assertThat(directUser.getActivationDate()).isNull();

        // Token must still be live — a weak password must not burn the token.
        Optional<?> stillValid = tokenRepository.findValidByHash(sha256Hex(RAW_TOKEN), Instant.now());
        assertThat(stillValid).isPresent();
    }

    @Test
    @DisplayName("POST /activate is reachable without authentication (public matcher)")
    void activate_isPublic_reachableWithoutAuth() throws Exception {
        // No cookie / Authorization header. A bad token yields 400 (business
        // rejection) — crucially NOT 401, which would mean the matcher is missing.
        mvc.perform(post("/api/v1/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("bad-token", STRONG_PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    private static String body(String token, String password) {
        return "{\"token\":\"" + token + "\",\"password\":\"" + password + "\"}";
    }

    private static String sha256Hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
