package com.ecoatm.salesplatform.service.email;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.EmailTemplate;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;
import com.ecoatm.salesplatform.repository.email.EmailTemplateRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The general send pipeline for the unified email module: load a template by
 * key, render it, resolve recipients/from, snapshot the attempt to
 * {@code email.log}, send, and mark the row {@code SENT}/{@code FAILED}.
 * Every future caller (Partial Credit's T11 migration, admin "send test",
 * future modules) is meant to go through {@link #sendTemplated} rather than
 * building an {@link EmailMessage} and calling {@link EmailSender} directly.
 *
 * <p><b>Synchronous by design.</b> Both {@link #sendTemplated} and
 * {@link #resend} block until the send attempt completes and return the
 * persisted {@link EmailLog} — neither is {@code @Async}. Running a send off
 * the caller's thread (e.g. only after a business transaction commits) is
 * the <em>caller's</em> responsibility, exactly as
 * {@code ReviewCompletedEmailListener} already does with its own
 * {@code @TransactionalEventListener(AFTER_COMMIT)} + {@code @Async} setup.
 * Keeping this class synchronous keeps its contract simple ("call it, get
 * the outcome back") and lets each caller choose its own commit/async
 * semantics instead of inheriting one baked into the service.
 */
@Service
public class EmailService {

    /** Caps {@code error_message} so an unusually large exception message
     *  (e.g. a nested stack-trace-style message) can't blow past a sane
     *  audit-row size. */
    private static final int MAX_ERROR_MESSAGE_LENGTH = 2000;

    /** Shift cap for {@link #backoff(int)}: bounds {@code 1L << retryCount}
     *  so a large {@code retryCount} can never overflow the shift.
     *  {@link #MAX_BACKOFF_MINUTES} is what actually limits the returned
     *  delay in practice — this just keeps the shift itself well-defined. */
    private static final int MAX_BACKOFF_SHIFT = 6;

    /** Longest backoff {@link #backoff(int)} will ever return. */
    private static final long MAX_BACKOFF_MINUTES = 60;

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogRepository emailLogRepository;
    private final SmtpConfigService smtpConfigService;
    private final TemplateRenderer templateRenderer;
    private final EmailSender emailSender;

    public EmailService(
            EmailTemplateRepository emailTemplateRepository,
            EmailLogRepository emailLogRepository,
            SmtpConfigService smtpConfigService,
            TemplateRenderer templateRenderer,
            EmailSender emailSender) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailLogRepository = emailLogRepository;
        this.smtpConfigService = smtpConfigService;
        this.templateRenderer = templateRenderer;
        this.emailSender = emailSender;
    }

    /**
     * Renders {@code templateKey} with {@code vars}, resolves its recipients
     * (per-field {@code overrides} beat the template's own defaults), writes
     * a {@code PENDING} {@link EmailLog} snapshot, sends, and updates that
     * same row to {@code SENT}/{@code FAILED}. A transport failure is
     * captured on the returned log — it is never rethrown, so the audit
     * trail is always truthful even if a caller ignores the return value.
     *
     * @throws EntityNotFoundException if no {@code email.template} row has
     *     this key
     * @throws IllegalStateException if the template is disabled
     * @throws IllegalArgumentException if recipient resolution yields no
     *     {@code to} address — thrown before anything is persisted or sent
     */
    @Transactional
    public EmailLog sendTemplated(
            String templateKey, Map<String, Object> vars, SendOverrides overrides, SourceRef source) {
        EmailTemplate template = emailTemplateRepository.findByTemplateKey(templateKey)
                .orElseThrow(() -> new EntityNotFoundException("Email template not found: " + templateKey));
        if (!Boolean.TRUE.equals(template.getEnabled())) {
            throw new IllegalStateException("Email template is disabled: " + templateKey);
        }

        Map<String, Object> resolvedVars = vars == null ? Map.of() : vars;
        String subject = templateRenderer.renderPlain(template.getSubject(), resolvedVars);
        String html = templateRenderer.render(template.getContentHtml(), resolvedVars);
        String plain = template.getContentPlain() == null
                ? null
                : templateRenderer.renderPlain(template.getContentPlain(), resolvedVars);

        List<String> to = overrideOrDefault(overrides == null ? null : overrides.to(), template.getToDefault());
        if (to.isEmpty()) {
            throw new IllegalArgumentException("Email has no recipients: " + templateKey);
        }
        List<String> cc = overrideOrDefault(overrides == null ? null : overrides.cc(), template.getCcDefault());
        List<String> bcc = overrideOrDefault(overrides == null ? null : overrides.bcc(), template.getBccDefault());
        String from = resolveFrom(template.getFromAddress());
        String replyTo = notBlank(template.getReplyTo()) ? template.getReplyTo().trim() : null;

        EmailLog emailLog = new EmailLog();
        emailLog.setTemplateKey(templateKey);
        emailLog.setFromAddress(from);
        emailLog.setToAddress(String.join(",", to));
        emailLog.setCc(csvOrNull(cc));
        emailLog.setBcc(csvOrNull(bcc));
        emailLog.setSubject(subject);
        emailLog.setContentHtml(html);
        emailLog.setStatus(EmailStatus.PENDING);
        emailLog.setRetryCount(0);
        emailLog.setSourceModule(source == null ? null : source.module());
        emailLog.setSourceId(source == null ? null : source.id());
        // The @Column default doesn't apply through a JPA insert that sends
        // an explicit null, so this must be set here rather than relying on
        // the entity field initializer alone.
        emailLog.setCreatedDate(Instant.now());
        emailLog = emailLogRepository.save(emailLog);

        // Build the message INSIDE attemptSend's try (via a supplier) so a
        // construction-time failure — e.g. a rendered subject/html that came
        // out blank, which EmailMessage rejects — is captured as a FAILED log
        // rather than escaping and rolling back the PENDING row already
        // inserted above (IDENTITY id → INSERT ran, but same @Transactional).
        emailLog = attemptSend(
                emailLog, () -> new EmailMessage(to, cc, bcc, from, replyTo, subject, html, plain));
        return emailLogRepository.save(emailLog);
    }

    /**
     * Rebuilds an {@link EmailMessage} from a previously persisted
     * {@link EmailLog}'s snapshot columns (not from the live template, which
     * may have since changed) and resends it. {@code email.log} has no
     * {@code reply_to}/{@code content_plain} columns, so the rebuilt message
     * always carries {@code replyTo=null}/{@code textBody=null} — a resend
     * is HTML-only with no reply-to override, matching what was actually
     * persisted the first time.
     *
     * <p>This is the admin-forced resend path (T9). It intentionally does
     * NOT bump {@code retry_count} — that re-queue bookkeeping belongs to
     * the auto-retry worker (T6), which calls this method rather than
     * duplicating the send logic.
     *
     * @throws EntityNotFoundException if no {@code email.log} row has this id
     */
    @Transactional
    public EmailLog resend(Long logId) {
        EmailLog emailLog = emailLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Email log not found: id=" + logId));

        // Same rationale as sendTemplated: build the message inside
        // attemptSend's try so a blank snapshot subject/html (which
        // EmailMessage rejects) becomes a FAILED status-update, not an escaped
        // throw. attemptSend returns the same instance it mutates, so a final
        // alias is safe for the supplier to close over.
        final EmailLog snapshot = emailLog;
        emailLog = attemptSend(emailLog, () -> new EmailMessage(
                split(snapshot.getToAddress()), split(snapshot.getCc()), split(snapshot.getBcc()),
                snapshot.getFromAddress(), null,
                snapshot.getSubject(), snapshot.getContentHtml(), null));
        return emailLogRepository.save(emailLog);
    }

    /**
     * Exponential backoff for the next retry attempt: {@code 2^retryCount}
     * minutes, shift-guarded so a large {@code retryCount} can't overflow,
     * then capped at {@link #MAX_BACKOFF_MINUTES}. {@code public static} —
     * shared with T6's {@code EmailRetryWorker}, which schedules successive
     * retries on the same schedule this class uses for a first failure.
     */
    public static Duration backoff(int retryCount) {
        // Guard the 1L << negative footgun: a negative shift wraps mod-64
        // (e.g. 1L << -1 == 1L << 63 → a wildly huge value). T6 will pass
        // column-derived counts, so clamp defensively at the floor.
        int safeRetryCount = Math.max(retryCount, 0);
        long minutes = Math.min(1L << Math.min(safeRetryCount, MAX_BACKOFF_SHIFT), MAX_BACKOFF_MINUTES);
        return Duration.ofMinutes(minutes);
    }

    /**
     * Builds the message (via {@code messageSupplier}) and sends it via
     * {@link #emailSender}, mutating {@code emailLog}'s status in place.
     * Message construction happens <em>inside</em> the try so that a
     * construction-time {@link IllegalArgumentException} (blank rendered
     * subject/html, empty recipient list) is treated identically to a
     * transport failure — recorded as a FAILED log, never rethrown — so
     * both {@link #sendTemplated} and {@link #resend} return a truthful
     * FAILED row instead of throwing and rolling back the PENDING snapshot.
     */
    private EmailLog attemptSend(EmailLog emailLog, Supplier<EmailMessage> messageSupplier) {
        try {
            emailSender.send(messageSupplier.get());
            emailLog.setStatus(EmailStatus.SENT);
            emailLog.setSentDate(Instant.now());
        } catch (Exception ex) {
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(truncate(ex.getMessage(), MAX_ERROR_MESSAGE_LENGTH));
            // Keyed off the row's OWN retry_count rather than a hardcoded 0:
            // on a fresh sendTemplated attempt retry_count is 0, so this is
            // still backoff(0) — unchanged. On a resend called by T6's
            // EmailRetryWorker, the worker has already incremented and saved
            // retry_count before calling resend(), so this reload sees the
            // incremented value and the delay escalates each retry. resend()
            // itself never touches retry_count (stays count-neutral) — see
            // EmailRetryWorker.
            emailLog.setNextAttemptAt(Instant.now().plus(backoff(emailLog.getRetryCount())));
        }
        return emailLog;
    }

    /** {@code override} wins whenever it is non-null — including an
     *  explicitly empty list, which means "send with none of this field."
     *  A null override falls through to the template's own comma-separated
     *  default column. */
    private static List<String> overrideOrDefault(List<String> override, String templateDefaultCsv) {
        return override != null ? override : split(templateDefaultCsv);
    }

    /** Blank is treated as absent so a stray empty/whitespace string in the
     *  DB can never reach {@link EmailMessage} as a non-null blank
     *  {@code from} — closes the T3 carry-forward at the resolution layer:
     *  the resolved value is always a real address or {@code null}, never
     *  blank. */
    private String resolveFrom(String templateFromAddress) {
        if (notBlank(templateFromAddress)) {
            return templateFromAddress.trim();
        }
        String smtpFrom = smtpConfigService.resolvedFromAddress();
        return notBlank(smtpFrom) ? smtpFrom.trim() : null;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static List<String> split(String csv) {
        return csv == null
                ? List.of()
                : Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
    }

    private static String csvOrNull(List<String> values) {
        return values.isEmpty() ? null : String.join(",", values);
    }

    private static String truncate(String message, int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength);
    }

    /** Per-field recipient overrides for {@link #sendTemplated}. Each field
     *  is independently nullable: {@code null} means "use the template's
     *  own default for this field"; a non-null (possibly empty) list is
     *  used as-is. */
    public record SendOverrides(List<String> to, List<String> cc, List<String> bcc) {}

    /** Free-text provenance tag written to {@code email.log.source_module}/
     *  {@code source_id} (e.g. {@code new SourceRef("PARTIAL_CREDIT", 7L)}).
     *  The whole argument and both fields are nullable — a system send with
     *  no traceable origin passes {@code null} for {@code source} entirely. */
    public record SourceRef(String module, Long id) {}
}
