package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.dto.email.SmtpConfigUpdate;
import com.ecoatm.salesplatform.model.email.SmtpConfig;
import com.ecoatm.salesplatform.repository.email.SmtpConfigRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link SmtpConfigService} — the cached accessor for the
 * singleton {@code email.smtp_config} row (id=1, seeded by V92).
 */
@ExtendWith(MockitoExtension.class)
class SmtpConfigServiceTest {

    @Mock private SmtpConfigRepository repository;

    private SmtpConfigService service;

    @BeforeEach
    void setUp() {
        service = new SmtpConfigService(repository);
    }

    @Test
    @DisplayName("get — loads id=1 once and caches; second call does not hit the repository")
    void get_loadsOnceAndCaches() {
        SmtpConfig config = newConfig("smtp.example.com", "noreply@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(config));

        SmtpConfig first = service.get();
        SmtpConfig second = service.get();

        assertThat(first).isSameAs(config);
        assertThat(second).isSameAs(config);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("invalidate — clears the cache so the next get() reloads from the repository")
    void invalidate_forcesReload() {
        SmtpConfig original = newConfig("smtp.example.com", "noreply@example.com");
        SmtpConfig updated = newConfig("smtp2.example.com", "updated@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(original), Optional.of(updated));

        SmtpConfig before = service.get();
        service.invalidate();
        SmtpConfig after = service.get();

        assertThat(before).isSameAs(original);
        assertThat(after).isSameAs(updated);
        verify(repository, times(2)).findById(1L);
    }

    @Test
    @DisplayName("resolvedFromAddress — returns the cached row's from_address")
    void resolvedFromAddress_returnsFromAddress() {
        SmtpConfig config = newConfig("smtp.example.com", "noreply@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(config));

        assertThat(service.resolvedFromAddress()).isEqualTo("noreply@example.com");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("get — throws IllegalStateException when the id=1 singleton row is missing")
    void get_throwsWhenSingletonRowMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("smtp_config");
    }

    // -------------------------------------------------------------------
    // update (Task 7 — admin PUT /smtp)
    // -------------------------------------------------------------------

    @Test
    @DisplayName("update — patches provided fields, stamps audit columns, saves, and invalidates the cache")
    void update_patchesFieldsAndStampsAudit_thenInvalidatesCache() {
        SmtpConfig existing = newConfig("smtp.old.com", "old@example.com");
        existing.setServerPort(25);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(SmtpConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        // Prime the cache so we can later prove invalidate() forces a reload.
        SmtpConfig cachedBefore = service.get();
        assertThat(cachedBefore).isSameAs(existing);

        SmtpConfigUpdate patch = new SmtpConfigUpdate(
                "smtp.new.com", 587, "SMTP", "new@example.com", "New Name",
                "reply@example.com", false, true, true, 5, 15000);

        SmtpConfig result = service.update(patch, 42L);

        assertThat(result.getServerHost()).isEqualTo("smtp.new.com");
        assertThat(result.getServerPort()).isEqualTo(587);
        assertThat(result.getProtocol()).isEqualTo("SMTP");
        assertThat(result.getFromAddress()).isEqualTo("new@example.com");
        assertThat(result.getFromDisplayName()).isEqualTo("New Name");
        assertThat(result.getReplyTo()).isEqualTo("reply@example.com");
        assertThat(result.getUseSsl()).isFalse();
        assertThat(result.getUseTls()).isTrue();
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getMaxRetryAttempts()).isEqualTo(5);
        assertThat(result.getTimeoutMs()).isEqualTo(15000);
        assertThat(result.getChangedById()).isEqualTo(42L);
        assertThat(result.getChangedDate()).isNotNull();
        verify(repository).save(existing);

        // invalidate() must force the next get() to reload from the repository
        // rather than reuse the pre-update snapshot cached above.
        SmtpConfig afterUpdateGet = service.get();
        assertThat(afterUpdateGet).isSameAs(result);
        verify(repository, times(3)).findById(1L); // prime + inside update() + post-update reload
    }

    @Test
    @DisplayName("update — null patch fields leave the existing column values unchanged")
    void update_leavesNullFieldsUnchanged() {
        SmtpConfig existing = newConfig("smtp.old.com", "old@example.com");
        existing.setServerPort(25);
        existing.setEnabled(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(SmtpConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        SmtpConfigUpdate allNullPatch = new SmtpConfigUpdate(
                null, null, null, null, null, null, null, null, null, null, null);

        SmtpConfig result = service.update(allNullPatch, 7L);

        assertThat(result.getServerHost()).isEqualTo("smtp.old.com");
        assertThat(result.getServerPort()).isEqualTo(25);
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getChangedById()).isEqualTo(7L);
        assertThat(result.getChangedDate()).isNotNull();
    }

    @Test
    @DisplayName("update — throws IllegalStateException when the id=1 singleton row is missing")
    void update_throwsWhenSingletonRowMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        SmtpConfigUpdate patch = new SmtpConfigUpdate(
                "smtp.new.com", null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.update(patch, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("smtp_config");
    }

    private static SmtpConfig newConfig(String host, String fromAddress) {
        SmtpConfig config = new SmtpConfig();
        config.setId(1L);
        config.setServerHost(host);
        config.setFromAddress(fromAddress);
        return config;
    }
}
