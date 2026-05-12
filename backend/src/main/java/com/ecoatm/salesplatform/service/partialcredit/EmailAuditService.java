package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.EmailAudit;
import com.ecoatm.salesplatform.repository.partialcredit.EmailAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Persists one row per email send attempt for the partial-credit email
 * flows. The caller hands over (templateKey, recipientEmail,
 * creditRequestId, success?, errorMessage?) and the service writes a
 * fresh row.
 *
 * <p>Writes use {@code REQUIRES_NEW} because the originating event
 * listener runs on the async {@code email-} executor inside a read-only
 * transaction (it reloaded the {@code CreditRequest} for rendering).
 * Audit writes must succeed independently of that read-only context.
 */
@Service
public class EmailAuditService {

    private static final Logger log = LoggerFactory.getLogger(EmailAuditService.class);

    private final EmailAuditRepository repository;

    public EmailAuditService(EmailAuditRepository repository) {
        this.repository = repository;
    }

    /** Record one row per (template, recipient) pair for a send attempt.
     *  Defensive: failures inside the audit insert are caught and logged
     *  — losing the audit row is preferable to crashing the async worker
     *  and shadowing the real send result. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EmailAudit record(
            String templateKey,
            String recipientEmail,
            Long creditRequestId,
            boolean success,
            String errorMessage) {
        try {
            EmailAudit row = new EmailAudit();
            row.setTemplateKey(templateKey);
            row.setRecipientEmail(recipientEmail);
            row.setCreditRequestId(creditRequestId);
            row.setSentAt(Instant.now());
            row.setSuccess(success);
            row.setErrorMessage(errorMessage);
            return repository.save(row);
        } catch (RuntimeException ex) {
            log.error(
                    "Failed to write email_audit row for templateKey={} recipient={} requestId={}: {}",
                    templateKey,
                    recipientEmail,
                    creditRequestId,
                    ex.getMessage(),
                    ex);
            return null;
        }
    }

    /** Convenience: record the same outcome for a batch of recipients
     *  (typical send call dispatches one EmailMessage to multiple buyer
     *  users, but the audit table stores per-recipient rows). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordBatch(
            String templateKey,
            List<String> recipientEmails,
            Long creditRequestId,
            boolean success,
            String errorMessage) {
        for (String recipient : recipientEmails) {
            record(templateKey, recipient, creditRequestId, success, errorMessage);
        }
    }
}
