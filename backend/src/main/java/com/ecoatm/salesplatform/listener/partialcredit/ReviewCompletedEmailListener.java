package com.ecoatm.salesplatform.listener.partialcredit;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges {@link ReviewCompletedEvent} (published by
 * {@code AdminCreditRequestService.completeReview}) to the buyer-facing
 * "Review Completed" notification email.
 *
 * <p>Follows the same {@code AFTER_COMMIT} + {@code @Async} pattern as
 * {@code PwsOfferEmailListener}: the email send must never roll back the
 * review-completion transaction, and the slow recipient-resolution +
 * SMTP round-trip must not block the admin UI's response. The listener
 * reloads the {@link CreditRequest} inside a fresh
 * {@code REQUIRES_NEW} transaction before crossing the async boundary so
 * the {@code @Async} executor thread has a self-contained, fully
 * initialised aggregate to render.
 *
 * <p><b>T11 (unified email migration):</b> rendering, recipient
 * resolution beyond this class, delivery, and audit logging all go
 * through {@link EmailService#sendTemplated} against the shared
 * {@code email.template} store (V92 copied the 3 PC template rows over) —
 * the module-local {@code EmailTemplateService}/{@code EmailSender}/
 * {@code EmailAuditService} path is retired. Every send now writes one
 * {@code email.log} row tagged {@code source_module="PARTIAL_CREDIT"};
 * the old {@code partial_credit.email_audit} table is frozen (design
 * decision D5) — its historical rows stay queryable but nothing new is
 * written there. The listener is still gated by
 * {@code partial-credit.review-completed-email.enabled} so it can be
 * flipped off without redeploy.
 *
 * <p><b>Transaction shape matters here.</b> This method is
 * {@code @Transactional(REQUIRES_NEW)} — deliberately NOT {@code readOnly},
 * unlike the pre-T11 version. {@link EmailService#sendTemplated} is itself
 * {@code @Transactional} ({@code REQUIRES}) and WRITES {@code email.log}, so
 * it joins this method's transaction; a {@code readOnly=true} transaction
 * here would make that INSERT fail. It stays {@code REQUIRES_NEW} (its own
 * transaction on the async thread, isolated from the already-committed
 * review-completion transaction) — only the {@code readOnly} flag changed.
 */
@Component
public class ReviewCompletedEmailListener {

    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedEmailListener.class);

    static final String TEMPLATE_APPROVED = "ReviewCompleted_Approved";
    static final String TEMPLATE_DECLINED = "ReviewCompleted_Declined";

    /** {@code email.log.source_module} tag for every send this listener triggers. */
    private static final String SOURCE_MODULE = "PARTIAL_CREDIT";

    private final CreditRequestRepository creditRequestRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final EmailService emailService;
    private final boolean enabled;

    public ReviewCompletedEmailListener(
            CreditRequestRepository creditRequestRepository,
            EcoATMDirectUserRepository directUserRepository,
            EmailService emailService,
            @Value("${partial-credit.review-completed-email.enabled:false}") boolean enabled) {
        this.creditRequestRepository = creditRequestRepository;
        this.directUserRepository = directUserRepository;
        this.emailService = emailService;
        this.enabled = enabled;
    }

    /**
     * Reload the credit request, resolve buyer recipients, and dispatch the
     * outcome-specific template through {@link EmailService#sendTemplated}.
     * Runs on the {@link AsyncConfig#EMAIL_EXECUTOR} pool so the admin
     * completion call returns immediately.
     *
     * <p>All exceptions are caught and logged — a failure here must
     * never affect the originating completion transaction or surface to
     * the admin user (the review is already final once the event fires).
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReviewCompleted(ReviewCompletedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error(
                    "ReviewCompleted email delivery failed for creditRequestId={}: {}",
                    event.requestId(),
                    ex.getMessage(),
                    ex);
        }
    }

    private void handle(ReviewCompletedEvent event) {
        if (!enabled) {
            log.info(
                    "[ReviewCompletedEmailListener] (disabled) would send {} email for creditRequestId={} reviewerUserId={}",
                    event.outcome(),
                    event.requestId(),
                    event.reviewerUserId());
            return;
        }

        Long requestId = event.requestId();
        if (requestId == null) {
            log.warn("ReviewCompletedEvent published with null requestId — skipping send");
            return;
        }

        CreditRequest cr = creditRequestRepository.findById(requestId).orElse(null);
        if (cr == null) {
            log.warn("ReviewCompleted email skipped: no CreditRequest for id={}", requestId);
            return;
        }

        List<String> recipients = resolveRecipientEmails(cr.getBuyerCodeId());
        if (recipients.isEmpty()) {
            log.warn(
                    "ReviewCompleted email skipped: no active recipients for buyerCodeId={} (creditRequestId={})",
                    cr.getBuyerCodeId(),
                    requestId);
            return;
        }

        String templateKey = event.outcome() == SystemStatus.APPROVED
                ? TEMPLATE_APPROVED
                : TEMPLATE_DECLINED;

        // Recipients MUST travel via SendOverrides.to: the PC templates
        // copied by V92 have to_default=null, so passing a null overrides
        // would make sendTemplated throw "no recipients" (see its javadoc).
        EmailLog sent = emailService.sendTemplated(
                templateKey,
                variablesFor(cr, event.outcome()),
                new EmailService.SendOverrides(recipients, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, requestId));

        log.info(
                "ReviewCompleted email dispatched: creditRequestId={} requestNumber={} outcome={} recipients={} status={}",
                requestId,
                cr.getRequestNumber(),
                event.outcome(),
                recipients.size(),
                sent.getStatus());
    }

    /**
     * Builds the variable map for the Approved/Declined templates. Keeps
     * the rendering layer free of currency/null-format concerns:
     * {@code approvedTotalDisplay} carries the literal dollar sign so
     * the V90 seed could be written without colliding with Flyway's own
     * placeholder syntax.
     */
    static Map<String, Object> variablesFor(CreditRequest cr, SystemStatus outcome) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("requestNumber", cr.getRequestNumber() != null ? cr.getRequestNumber() : String.valueOf(cr.getId()));
        vars.put("orderNumber", cr.getOrderNumber() != null ? cr.getOrderNumber() : "");
        if (outcome == SystemStatus.APPROVED) {
            BigDecimal approvedTotal = cr.getApprovedTotal() != null ? cr.getApprovedTotal() : BigDecimal.ZERO;
            vars.put("approvedTotalDisplay", "$" + approvedTotal.toPlainString());
        }
        return vars;
    }

    /**
     * Pulls active EcoATM Direct user emails for the buyer code — same
     * resolver path PWS notifications use, so the recipient list stays
     * consistent across the two surfaces.
     */
    private List<String> resolveRecipientEmails(Long buyerCodeId) {
        if (buyerCodeId == null) {
            return List.of();
        }
        return directUserRepository.findActiveEmailsByBuyerCodeId(buyerCodeId).stream()
                .map(row -> row.length > 0 ? (String) row[0] : null)
                .filter(email -> email != null && !email.isBlank())
                .toList();
    }
}
