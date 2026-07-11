package com.ecoatm.salesplatform.listener.partialcredit;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailMessage;
import com.ecoatm.salesplatform.service.email.EmailSender;
import com.ecoatm.salesplatform.service.partialcredit.EmailAuditService;
import com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService;
import com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService.RenderedEmail;
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
 * <p><b>Sprint 4 / Chunk 2:</b> subject + body now come from
 * {@code partial_credit.email_templates} via {@link EmailTemplateService}.
 * Every send attempt records a row in {@code partial_credit.email_audit}
 * via {@link EmailAuditService} so the "did the buyer get the email?"
 * question is answerable without mining stdout. The listener is still
 * gated by {@code partial-credit.review-completed-email.enabled} so it
 * can be flipped off without redeploy.
 */
@Component
public class ReviewCompletedEmailListener {

    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedEmailListener.class);

    static final String TEMPLATE_APPROVED = "ReviewCompleted_Approved";
    static final String TEMPLATE_DECLINED = "ReviewCompleted_Declined";

    private final CreditRequestRepository creditRequestRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final EmailSender emailSender;
    private final EmailTemplateService emailTemplateService;
    private final EmailAuditService emailAuditService;
    private final boolean enabled;

    public ReviewCompletedEmailListener(
            CreditRequestRepository creditRequestRepository,
            EcoATMDirectUserRepository directUserRepository,
            EmailSender emailSender,
            EmailTemplateService emailTemplateService,
            EmailAuditService emailAuditService,
            @Value("${partial-credit.review-completed-email.enabled:false}") boolean enabled) {
        this.creditRequestRepository = creditRequestRepository;
        this.directUserRepository = directUserRepository;
        this.emailSender = emailSender;
        this.emailTemplateService = emailTemplateService;
        this.emailAuditService = emailAuditService;
        this.enabled = enabled;
    }

    /**
     * Reload the credit request, resolve buyer recipients, render the
     * outcome-specific template, and dispatch the rendered email. Runs
     * on the {@link AsyncConfig#EMAIL_EXECUTOR} pool so the admin
     * completion call returns immediately.
     *
     * <p>All exceptions are caught and logged — a failure here must
     * never affect the originating completion transaction or surface to
     * the admin user (the review is already final once the event fires).
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
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
        RenderedEmail rendered = emailTemplateService.render(templateKey, variablesFor(cr, event.outcome()));

        EmailMessage message = EmailMessage.of(
                recipients,
                List.of(),
                rendered.subject(),
                rendered.bodyHtml(),
                rendered.bodyText());
        try {
            emailSender.send(message);
            recordAudit(templateKey, recipients, requestId, true, null);
            log.info(
                    "ReviewCompleted email dispatched: creditRequestId={} requestNumber={} outcome={} recipients={}",
                    requestId,
                    cr.getRequestNumber(),
                    event.outcome(),
                    recipients.size());
        } catch (RuntimeException sendFailure) {
            recordAudit(templateKey, recipients, requestId, false, sendFailure.getMessage());
            throw sendFailure;
        }
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

    private void recordAudit(
            String templateKey, List<String> recipients, Long requestId, boolean success, String errorMessage) {
        try {
            emailAuditService.recordBatch(templateKey, recipients, requestId, success, errorMessage);
        } catch (RuntimeException auditFailure) {
            log.error(
                    "Failed to record email_audit rows for creditRequestId={} templateKey={}: {}",
                    requestId,
                    templateKey,
                    auditFailure.getMessage(),
                    auditFailure);
        }
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
