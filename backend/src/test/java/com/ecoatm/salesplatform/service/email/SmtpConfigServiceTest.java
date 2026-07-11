package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static SmtpConfig newConfig(String host, String fromAddress) {
        SmtpConfig config = new SmtpConfig();
        config.setId(1L);
        config.setServerHost(host);
        config.setFromAddress(fromAddress);
        return config;
    }
}
