package com.ecoatm.salesplatform.service.email;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Single-leader scheduled worker that makes {@code email.log} the source of
 * truth for delivery. Each tick does two independent jobs:
 *
 * <ol>
 *   <li><b>Stale-PENDING rescue.</b> A {@code PENDING} row with no
 *       resolution after {@code email.retry.stale-pending-min} minutes means
 *       the app crashed (or was killed) between the log insert and the send
 *       attempt in {@link EmailService#sendTemplated}. These rows are
 *       flipped to {@code FAILED} with {@code next_attempt_at=now} so they
 *       fall straight into the retry job below instead of sitting orphaned
 *       forever.</li>
 *   <li><b>Escalating retry.</b> Re-drives every {@code FAILED} row whose
 *       {@code next_attempt_at} has elapsed and {@code retry_count} is still
 *       under {@code smtp_config.max_retry_attempts} through
 *       {@link EmailService#resend}. This worker bumps and <em>persists</em>
 *       {@code retry_count} before calling {@code resend} — {@code resend}
 *       reloads the row by id, so it sees the incremented count and
 *       {@link EmailService#attemptSend} computes the next backoff off of
 *       it, escalating on repeated failures. {@code resend} itself never
 *       touches {@code retry_count} (stays count-neutral), which is what
 *       lets T9's admin-forced resend reuse it without inheriting the
 *       auto-retry budget — it just resets {@code retry_count} to 0 first
 *       when it wants a full cycle again. Once {@code retry_count} reaches
 *       the cap the finder no longer selects the row and it stays
 *       {@code FAILED} — terminal.</li>
 * </ol>
 *
 * <p>Not {@code @Transactional}: the stale-PENDING bulk write and each
 * retried row are persisted independently
 * ({@link EmailLogRepository#saveAll}/{@link EmailLogRepository#save} and
 * {@link EmailService#resend} each own their own transaction), and a single
 * row's retry failure is caught and logged rather than allowed to abort the
 * rest of the batch — so one bad row can never roll back, or block, what
 * this tick already did for every other row.
 */
@Component
public class EmailRetryWorker {

    private static final Logger log = LoggerFactory.getLogger(EmailRetryWorker.class);
    private static final String STALE_PENDING_MESSAGE = "rescued: stale PENDING";

    private final EmailLogRepository emailLogRepository;
    private final SmtpConfigService smtpConfigService;
    private final EmailService emailService;
    private final Clock clock;
    private final int stalePendingMinutes;

    public EmailRetryWorker(
            EmailLogRepository emailLogRepository,
            SmtpConfigService smtpConfigService,
            EmailService emailService,
            Clock clock,
            @Value("${email.retry.stale-pending-min:5}") int stalePendingMinutes) {
        this.emailLogRepository = emailLogRepository;
        this.smtpConfigService = smtpConfigService;
        this.emailService = emailService;
        this.clock = clock;
        this.stalePendingMinutes = stalePendingMinutes;
    }

    @Scheduled(fixedDelayString = "${email.retry.fixed-delay-ms:120000}")
    @SchedulerLock(name = "emailRetry", lockAtLeastFor = "PT10S", lockAtMostFor = "PT110S")
    public void retryPending() {
        Instant now = clock.instant();
        rescueStalePending(now);
        retryFailedRows(now);
    }

    /**
     * Flips every {@code PENDING} row older than {@code stalePendingMinutes}
     * to {@code FAILED} with {@code next_attempt_at=now} in a single bulk
     * write, so it is picked up by {@link #retryFailedRows} on this tick (it
     * runs second) or the next.
     */
    private void rescueStalePending(Instant now) {
        Instant cutoff = now.minus(Duration.ofMinutes(stalePendingMinutes));
        List<EmailLog> stalePending =
                emailLogRepository.findByStatusAndCreatedDateBefore(EmailStatus.PENDING, cutoff);
        if (stalePending.isEmpty()) {
            return;
        }
        for (EmailLog emailLog : stalePending) {
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(STALE_PENDING_MESSAGE);
            emailLog.setNextAttemptAt(now);
        }
        emailLogRepository.saveAll(stalePending);
        log.info("Rescued {} stale PENDING email.log row(s) older than {} minute(s)",
                stalePending.size(), stalePendingMinutes);
    }

    /**
     * Re-drives every {@code FAILED} row due for another attempt. Bumps and
     * saves {@code retry_count} first so {@link EmailService#resend}'s
     * reload — and the escalating backoff it computes on failure — sees the
     * incremented value. Each row is handled independently: one row's
     * exception is logged and does not stop the rest of the batch.
     */
    private void retryFailedRows(Instant now) {
        int maxRetryAttempts = smtpConfigService.get().getMaxRetryAttempts();
        List<EmailLog> retryable = emailLogRepository
                .findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
                        EmailStatus.FAILED, now, maxRetryAttempts);
        for (EmailLog emailLog : retryable) {
            Long id = emailLog.getId();
            try {
                emailLog.setRetryCount(emailLog.getRetryCount() + 1);
                emailLogRepository.save(emailLog);
                emailService.resend(id);
            } catch (Exception ex) {
                log.error("Email retry failed for email.log id={}", id, ex);
            }
        }
    }
}
