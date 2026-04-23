package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.PasswordResetToken;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.PasswordResetTokenRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private PasswordResetService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(userRepository, tokenRepository, passwordEncoder);

        testUser = new User();
        testUser.setId(42L);
        testUser.setName("buyer@example.com");
        testUser.setPassword("$2a$10$oldhashedpassword");
        testUser.setActive(true);
        testUser.setBlocked(false);
    }

    // --- requestReset ---

    @Test
    @DisplayName("requestReset_knownEmail_persistsTokenAndLogsPayload")
    void requestReset_knownEmail_persistsTokenAndLogsPayload() {
        when(userRepository.findByNameIgnoreCase("buyer@example.com"))
                .thenReturn(Optional.of(testUser));

        service.requestReset("buyer@example.com");

        // Old tokens purged before new one issued
        verify(tokenRepository).deleteActiveTokensForUser(42L);

        // A token row is saved
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(captor.capture());

        PasswordResetToken saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(42L);
        assertThat(saved.getTokenHash()).hasSize(64);          // SHA-256 hex = 64 chars
        assertThat(saved.getExpiresAt()).isAfter(Instant.now());
        assertThat(saved.getConsumedAt()).isNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("requestReset_unknownEmail_silentlyReturns_enumerationResistant")
    void requestReset_unknownEmail_silentlyReturns_enumerationResistant() {
        when(userRepository.findByNameIgnoreCase("ghost@example.com"))
                .thenReturn(Optional.empty());

        // Must not throw and must not save anything
        service.requestReset("ghost@example.com");

        verify(tokenRepository, never()).save(any());
        verify(tokenRepository, never()).deleteActiveTokensForUser(any());
    }

    @Test
    @DisplayName("requestReset_trimsEmailBeforeLookup")
    void requestReset_trimsEmailBeforeLookup() {
        when(userRepository.findByNameIgnoreCase("buyer@example.com"))
                .thenReturn(Optional.of(testUser));

        service.requestReset("  buyer@example.com  ");

        verify(userRepository).findByNameIgnoreCase("buyer@example.com");
    }

    // --- confirmReset ---

    @Test
    @DisplayName("confirmReset_validToken_updatesPasswordAndConsumesToken")
    void confirmReset_validToken_updatesPasswordAndConsumesToken() {
        // Generate a real token + compute its hash the same way the service does
        String rawToken = "test-raw-token-that-is-long-enough-to-be-realistic";
        String tokenHash = computeSha256Hex(rawToken);

        PasswordResetToken tokenRow = new PasswordResetToken();
        tokenRow.setId(1L);
        tokenRow.setUserId(42L);
        tokenRow.setTokenHash(tokenHash);
        tokenRow.setExpiresAt(Instant.now().plusSeconds(1800));
        tokenRow.setCreatedAt(Instant.now());

        when(tokenRepository.findValidByHash(eq(tokenHash), any(Instant.class)))
                .thenReturn(Optional.of(tokenRow));
        when(userRepository.findById(42L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("$2a$10$newhash");

        service.confirmReset(rawToken, "NewPassword1!");

        // Password was updated
        assertThat(testUser.getPassword()).isEqualTo("$2a$10$newhash");
        verify(userRepository).save(testUser);

        // Token marked consumed
        assertThat(tokenRow.getConsumedAt()).isNotNull();
        verify(tokenRepository).save(tokenRow);
    }

    @Test
    @DisplayName("confirmReset_invalidToken_throwsIllegalArgumentException")
    void confirmReset_invalidToken_throwsIllegalArgumentException() {
        when(tokenRepository.findValidByHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmReset("bogus-token", "NewPassword1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or expired token");

        // Password encoder must never be called when the token is rejected
        verify(passwordEncoder, never()).encode(any());
    }

    // --- helper ---

    private static String computeSha256Hex(String input) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
