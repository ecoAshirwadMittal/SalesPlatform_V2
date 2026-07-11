package com.ecoatm.salesplatform.repository.email;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.EmailTemplate;
import com.ecoatm.salesplatform.model.email.SmtpConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence round-trip coverage for the three Task-1 (V92) email-schema
 * entities. Confirms the JPA mappings line up with the real Postgres DDL —
 * column names/types, the {@code smtp_config} singleton (no
 * {@code @GeneratedValue}), the {@code IDENTITY} strategy for
 * template/log — plus the two derived finders that later tasks depend on:
 * T6's retry-due lookup and T9's paged status listing.
 */
@Transactional
class EmailRepositoryIT extends PostgresIntegrationTest {

    @Autowired SmtpConfigRepository smtpConfigRepository;
    @Autowired EmailTemplateRepository emailTemplateRepository;
    @Autowired EmailLogRepository emailLogRepository;

    // ── smtp_config singleton ───────────────────────────────────────────

    @Test
    @DisplayName("seeded smtp_config singleton (id=1) loads via findById")
    void smtpConfigSingleton_loadsById1() {
        Optional<SmtpConfig> found = smtpConfigRepository.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }

    // ── template ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("persisted template is readable by findByTemplateKey")
    void findByTemplateKey_returnsPersistedTemplate() {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey("EmailRepositoryIT_Template");
        template.setTemplateName("IT Test Template");
        template.setSubject("Test Subject");
        template.setContentHtml("<p>Hello</p>");
        emailTemplateRepository.save(template);

        Optional<EmailTemplate> found =
                emailTemplateRepository.findByTemplateKey("EmailRepositoryIT_Template");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getSubject()).isEqualTo("Test Subject");
    }

    @Test
    @DisplayName("findByTemplateKey returns empty for an unknown key")
    void findByTemplateKey_unknownKey_returnsEmpty() {
        assertThat(emailTemplateRepository.findByTemplateKey("Does_Not_Exist_Template")).isEmpty();
    }

    // ── log — retry finder (T6) ─────────────────────────────────────────

    @Test
    @DisplayName("retry finder returns a PENDING log due for retry")
    void retryFinder_returnsPendingLogDueForRetry() {
        EmailLog log = newLog(EmailStatus.PENDING, Instant.now().minusSeconds(60), 0);
        emailLogRepository.save(log);

        List<EmailLog> due = emailLogRepository
                .findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        EmailStatus.PENDING, Instant.now(), 3);

        assertThat(due).extracting(EmailLog::getId).contains(log.getId());
    }

    @Test
    @DisplayName("retry finder returns a FAILED log due for retry")
    void retryFinder_returnsFailedLogDueForRetry() {
        EmailLog log = newLog(EmailStatus.FAILED, Instant.now().minusSeconds(60), 1);
        emailLogRepository.save(log);

        List<EmailLog> due = emailLogRepository
                .findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        EmailStatus.FAILED, Instant.now(), 3);

        assertThat(due).extracting(EmailLog::getId).contains(log.getId());
    }

    @Test
    @DisplayName("retry finder excludes logs whose next_attempt_at is still in the future")
    void retryFinder_excludesFutureNextAttempt() {
        EmailLog notYetDue = newLog(EmailStatus.PENDING, Instant.now().plusSeconds(3600), 0);
        emailLogRepository.save(notYetDue);

        List<EmailLog> due = emailLogRepository
                .findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        EmailStatus.PENDING, Instant.now(), 3);

        assertThat(due).extracting(EmailLog::getId).doesNotContain(notYetDue.getId());
    }

    @Test
    @DisplayName("retry finder excludes logs at or above the retry cap")
    void retryFinder_excludesLogsAtRetryCap() {
        EmailLog atCap = newLog(EmailStatus.FAILED, Instant.now().minusSeconds(60), 3);
        emailLogRepository.save(atCap);

        List<EmailLog> due = emailLogRepository
                .findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        EmailStatus.FAILED, Instant.now(), 3);

        assertThat(due).extracting(EmailLog::getId).doesNotContain(atCap.getId());
    }

    // ── log — stale-PENDING rescue finder (T6) ──────────────────────────

    @Test
    @DisplayName("stale-PENDING finder returns only the PENDING row older than the cutoff, not a recent one")
    void stalePendingFinder_returnsOnlyRowsOlderThanCutoff() {
        Instant cutoff = Instant.now().minusSeconds(300);

        EmailLog oldPending = newLog(EmailStatus.PENDING, null, 0);
        oldPending.setCreatedDate(cutoff.minusSeconds(60));
        emailLogRepository.save(oldPending);

        EmailLog recentPending = newLog(EmailStatus.PENDING, null, 0);
        recentPending.setCreatedDate(Instant.now());
        emailLogRepository.save(recentPending);

        List<EmailLog> stale =
                emailLogRepository.findByStatusAndCreatedDateBefore(EmailStatus.PENDING, cutoff);

        assertThat(stale).extracting(EmailLog::getId).contains(oldPending.getId());
        assertThat(stale).extracting(EmailLog::getId).doesNotContain(recentPending.getId());
    }

    // ── log — paged status listing (T9) ─────────────────────────────────

    @Test
    @DisplayName("findByStatus returns a page of logs matching the status")
    void findByStatus_returnsPagedLogs() {
        EmailLog sent = newLog(EmailStatus.SENT, null, 0);
        sent.setSentDate(Instant.now());
        emailLogRepository.save(sent);

        Page<EmailLog> page = emailLogRepository.findByStatus(EmailStatus.SENT, PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(EmailLog::getId).contains(sent.getId());
    }

    private EmailLog newLog(EmailStatus status, Instant nextAttemptAt, int retryCount) {
        EmailLog log = new EmailLog();
        log.setToAddress("test@example.com");
        log.setSubject("Retry test");
        log.setStatus(status);
        log.setNextAttemptAt(nextAttemptAt);
        log.setRetryCount(retryCount);
        return log;
    }
}
