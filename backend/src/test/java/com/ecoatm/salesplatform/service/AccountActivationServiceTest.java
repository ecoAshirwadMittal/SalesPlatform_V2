package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.EcoATMDirectUser;
import com.ecoatm.salesplatform.model.PasswordResetToken;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.PasswordResetTokenRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AccountActivationService}, the emailed-link account
 * activation port of legacy {@code ACT_ActivateNewUser}.
 *
 * <p>Load-bearing branches: a valid token sets the BCrypt password + flips the
 * {@link EcoATMDirectUser} status to Active + consumes the token (single-use);
 * an invalid/expired/already-consumed token is a generic reject with no state
 * change (no account enumeration); a weak password is rejected against the
 * legacy policy before any DB work; and the target user is always derived from
 * the token, never a request-supplied id.
 */
@ExtendWith(MockitoExtension.class)
class AccountActivationServiceTest {

    private static final long USER_ID = 42L;
    private static final String RAW_TOKEN = "activation-raw-token-long-enough-to-be-realistic";
    private static final String STRONG_PASSWORD = "ValidPass1!";
    private static final String GENERIC_TOKEN_ERROR = "Invalid or expired activation link";

    @Mock private UserRepository userRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-07-12T10:15:30Z"), ZoneOffset.UTC);

    private AccountActivationService service;

    private User user;
    private EcoATMDirectUser directUser;

    @BeforeEach
    void setUp() {
        service = new AccountActivationService(
                userRepository, directUserRepository, tokenRepository, passwordEncoder, fixedClock);

        user = new User();
        user.setId(USER_ID);
        user.setName("newbuyer@example.com");
        user.setPassword(null); // provisioned, not yet activated

        directUser = new EcoATMDirectUser();
        directUser.setUserId(USER_ID);
        directUser.setUserStatus("Inactive");
        directUser.setOverallUserStatus("Inactive");
        directUser.setInactive(true);
        directUser.setActivationDate(null);
    }

    // --- happy path ---

    @Test
    @DisplayName("activate_validToken_setsPasswordFlipsStatusConsumesToken")
    void activate_validToken_setsPasswordFlipsStatusConsumesToken() {
        PasswordResetToken tokenRow = validTokenRow();
        when(tokenRepository.findValidByHash(eq(sha256Hex(RAW_TOKEN)), any(Instant.class)))
                .thenReturn(Optional.of(tokenRow));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(directUserRepository.findById(USER_ID)).thenReturn(Optional.of(directUser));
        when(passwordEncoder.encode(STRONG_PASSWORD)).thenReturn("$2a$10$activatedhash");

        service.activate(RAW_TOKEN, STRONG_PASSWORD);

        // BCrypt hash set on the identity user (where login validates it)
        assertThat(user.getPassword()).isEqualTo("$2a$10$activatedhash");
        verify(userRepository).save(user);

        // EcoATMDirectUser business status flipped to Active + activation stamped
        assertThat(directUser.getUserStatus()).isEqualTo("Active");
        assertThat(directUser.getOverallUserStatus()).isEqualTo("Active");
        assertThat(directUser.isInactive()).isFalse();
        assertThat(directUser.getActivationDate()).isEqualTo(LocalDateTime.now(fixedClock));
        verify(directUserRepository).save(directUser);

        // Token consumed (single-use)
        assertThat(tokenRow.getConsumedAt()).isEqualTo(Instant.now(fixedClock));
        verify(tokenRepository).save(tokenRow);
    }

    @Test
    @DisplayName("activate_derivesTargetUserFromToken_notARequestField")
    void activate_derivesTargetUserFromToken_notARequestField() {
        PasswordResetToken tokenRow = validTokenRow();
        when(tokenRepository.findValidByHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(tokenRow));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(directUserRepository.findById(USER_ID)).thenReturn(Optional.of(directUser));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");

        service.activate(RAW_TOKEN, STRONG_PASSWORD);

        // The user loaded is exactly token.userId — no other id path exists
        verify(userRepository).findById(USER_ID);
        verify(directUserRepository).findById(USER_ID);
    }

    // --- token rejection (generic, enumeration-resistant) ---

    @Test
    @DisplayName("activate_invalidOrExpiredOrConsumedToken_genericReject_noStateChange")
    void activate_invalidToken_genericReject_noStateChange() {
        // findValidByHash already filters out expired + consumed tokens, so all three
        // failure modes collapse to an empty Optional here.
        when(tokenRepository.findValidByHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate("bogus-token", STRONG_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(GENERIC_TOKEN_ERROR);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(directUserRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("activate_userMissingForToken_genericReject_noEnumeration")
    void activate_userMissingForToken_genericReject() {
        when(tokenRepository.findValidByHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(validTokenRow()));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate(RAW_TOKEN, STRONG_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(GENERIC_TOKEN_ERROR);

        verify(userRepository, never()).save(any());
        verify(directUserRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }

    // --- weak password (policy enforced before any DB work) ---

    @Test
    @DisplayName("activate_weakPassword_tooShort_rejectedBeforeTokenLookup")
    void activate_weakPassword_tooShort_rejectedBeforeTokenLookup() {
        assertThatThrownBy(() -> service.activate(RAW_TOKEN, "Ab1!")) // 4 chars
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");

        // Password policy is checked first — no DB work, no state change
        verify(tokenRepository, never()).findValidByHash(anyString(), any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("activate_weakPassword_noUppercase_rejected")
    void activate_weakPassword_noUppercase_rejected() {
        assertThatThrownBy(() -> service.activate(RAW_TOKEN, "lowercase1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");

        verify(tokenRepository, never()).findValidByHash(anyString(), any());
    }

    @Test
    @DisplayName("activate_weakPassword_noSpecialChar_rejected")
    void activate_weakPassword_noSpecialChar_rejected() {
        assertThatThrownBy(() -> service.activate(RAW_TOKEN, "NoSpecial123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");

        verify(tokenRepository, never()).findValidByHash(anyString(), any());
    }

    // --- helpers ---

    private PasswordResetToken validTokenRow() {
        PasswordResetToken row = new PasswordResetToken();
        row.setId(1L);
        row.setUserId(USER_ID);
        row.setTokenHash(sha256Hex(RAW_TOKEN));
        row.setExpiresAt(Instant.now(fixedClock).plusSeconds(3600));
        row.setCreatedAt(Instant.now(fixedClock));
        return row;
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
