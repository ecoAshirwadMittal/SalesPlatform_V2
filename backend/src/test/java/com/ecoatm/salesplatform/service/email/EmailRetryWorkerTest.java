package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.SmtpConfig;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link EmailRetryWorker}. Mocks every collaborator
 * ({@link EmailLogRepository}, {@link SmtpConfigService}, {@link EmailService})
 * and injects a {@link Clock#fixed} so "now" is deterministic. This suite
 * covers the worker's own orchestration — which finder gets called with
 * which arguments, that {@code retry_count} is bumped and saved BEFORE
 * {@code resend} is called (so the escalating backoff in
 * {@link EmailService#attemptSend} reads the incremented value), and that
 * the stale-PENDING rescue only touches the rows the finder actually
 * returns. The finders' real SQL filtering is IT-covered separately by
 * {@code EmailRepositoryIT}.
 */
@ExtendWith(MockitoExtension.class)
class EmailRetryWorkerTest {

    private static final Instant NOW = Instant.parse("2026-07-11T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final int STALE_PENDING_MINUTES = 5;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Mock private EmailLogRepository emailLogRepository;
    @Mock private SmtpConfigService smtpConfigService;
    @Mock private EmailService emailService;

    private EmailRetryWorker worker;

    @BeforeEach
    void setUp() {
        worker = new EmailRetryWorker(
                emailLogRepository, smtpConfigService, emailService, FIXED_CLOCK, STALE_PENDING_MINUTES);

        SmtpConfig smtpConfig = new SmtpConfig();
        smtpConfig.setMaxRetryAttempts(MAX_RETRY_ATTEMPTS);
        when(smtpConfigService.get()).thenReturn(smtpConfig);

        // Deliberately NOT stubbing the two finders here: retryPending()
        // unconditionally queries both on every run, and Mockito's default
        // answer for an unstubbed List-returning method is already an empty
        // list (ReturnsEmptyValues) — exactly "nothing due." Tests that need
        // a non-empty result stub the finder themselves; stubbing an empty
        // List here too would make that per-test stub shadow this one and
        // trip MockitoExtension's UnnecessaryStubbingException.
    }

    @Test
    @DisplayName("rescues stale PENDING rows then retries eligible FAILED rows, bumping retry_count before resend")
    void rescuesStalePending_thenRetriesFailed() {
        EmailLog stalePending = pendingLog(1L, NOW.minus(Duration.ofMinutes(10)));
        when(emailLogRepository.findByStatusAndCreatedDateBefore(eq(EmailStatus.PENDING), any()))
                .thenReturn(List.of(stalePending));

        EmailLog failedRow = failedLog(2L, 1, NOW.minus(Duration.ofMinutes(1)));
        when(emailLogRepository.findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        eq(EmailStatus.FAILED), eq(NOW), eq(MAX_RETRY_ATTEMPTS)))
                .thenReturn(List.of(failedRow));

        worker.retryPending();

        // (a) stale PENDING rescued: flipped to FAILED w/ next_attempt_at=now, persisted via saveAll
        assertThat(stalePending.getStatus()).isEqualTo(EmailStatus.FAILED);
        assertThat(stalePending.getNextAttemptAt()).isEqualTo(NOW);
        assertThat(stalePending.getErrorMessage()).contains("stale PENDING");
        verify(emailLogRepository).saveAll(List.of(stalePending));

        // (b) FAILED row due for retry: retry_count incremented + saved BEFORE resend is called
        assertThat(failedRow.getRetryCount()).isEqualTo(2);
        verify(emailService).resend(failedRow.getId());
        InOrder inOrder = inOrder(emailLogRepository, emailService);
        inOrder.verify(emailLogRepository).save(failedRow);
        inOrder.verify(emailService).resend(failedRow.getId());
    }

    @Test
    @DisplayName("stale-PENDING cutoff is now minus the configured stale-pending-min window")
    void staleRescue_usesConfiguredCutoff() {
        worker.retryPending();

        ArgumentCaptor<Instant> cutoffCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(emailLogRepository)
                .findByStatusAndCreatedDateBefore(eq(EmailStatus.PENDING), cutoffCaptor.capture());
        assertThat(cutoffCaptor.getValue()).isEqualTo(NOW.minus(Duration.ofMinutes(STALE_PENDING_MINUTES)));
        verify(emailLogRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("skipsWhenRetryCountAtMax — a row at the retry cap is excluded by the finder, resend never called")
    void skipsWhenRetryCountAtMax() {
        // Default setUp stub already returns no rows for the retryCountLessThan(max)
        // finder — mirroring what the real query does when the only FAILED row is
        // already at retry_count == max (proven at the SQL level by
        // EmailRepositoryIT#retryFinder_excludesLogsAtRetryCap).
        worker.retryPending();

        verify(emailService, never()).resend(any());
        verify(emailLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("skipsFutureNextAttempt — a row not yet due is excluded by the finder, resend never called")
    void skipsFutureNextAttempt() {
        // Default setUp stub already returns no rows for the retry finder —
        // mirroring what the real query does when next_attempt_at is still in
        // the future (proven at the SQL level by
        // EmailRepositoryIT#retryFinder_excludesFutureNextAttempt).
        worker.retryPending();

        verify(emailService, never()).resend(any());
        verify(emailLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("increments and persists retry_count before calling resend, for every eligible row")
    void incrementsRetryCountBeforeCallingResend() {
        EmailLog failedRow = failedLog(5L, 0, NOW.minus(Duration.ofSeconds(30)));
        when(emailLogRepository.findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        eq(EmailStatus.FAILED), eq(NOW), eq(MAX_RETRY_ATTEMPTS)))
                .thenReturn(List.of(failedRow));

        worker.retryPending();

        assertThat(failedRow.getRetryCount()).isEqualTo(1);
        InOrder inOrder = inOrder(emailLogRepository, emailService);
        inOrder.verify(emailLogRepository).save(failedRow);
        inOrder.verify(emailService).resend(5L);
    }

    // ── helpers ──────────────────────────────────────────────────────────

    private static EmailLog pendingLog(Long id, Instant createdDate) {
        EmailLog log = new EmailLog();
        log.setId(id);
        log.setStatus(EmailStatus.PENDING);
        log.setCreatedDate(createdDate);
        log.setRetryCount(0);
        return log;
    }

    private static EmailLog failedLog(Long id, int retryCount, Instant nextAttemptAt) {
        EmailLog log = new EmailLog();
        log.setId(id);
        log.setStatus(EmailStatus.FAILED);
        log.setRetryCount(retryCount);
        log.setNextAttemptAt(nextAttemptAt);
        return log;
    }
}
