package com.ecoatm.salesplatform.listener.partialcredit;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailMessage;
import com.ecoatm.salesplatform.service.email.EmailSender;
import java.math.BigDecimal;
import java.util.List;
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
 * <p><b>Sprint 3 scope:</b> rendering uses the existing
 * {@link EmailSender} infrastructure ({@code LoggingEmailSender} in dev,
 * {@code SmtpEmailSender} in prod via {@code pws.email.enabled=true}).
 * Subject + body are hardcoded text per the §6 plan decision — Sprint 4
 * may introduce admin-editable email templates. The whole listener is
 * gated by {@code partial-credit.review-completed-email.enabled} so the
 * feature can be flipped off without redeploy if the dev-realistic copy
 * needs revision.
 */
@Component
public class ReviewCompletedEmailListener {

    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedEmailListener.class);

    private final CreditRequestRepository creditRequestRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final EmailSender emailSender;
    private final boolean enabled;

    public ReviewCompletedEmailListener(
            CreditRequestRepository creditRequestRepository,
            EcoATMDirectUserRepository directUserRepository,
            EmailSender emailSender,
            @Value("${partial-credit.review-completed-email.enabled:false}") boolean enabled) {
        this.creditRequestRepository = creditRequestRepository;
        this.directUserRepository = directUserRepository;
        this.emailSender = emailSender;
        this.enabled = enabled;
    }

    /**
     * Reload the credit request, resolve buyer recipients, and dispatch
     * the rendered email. Runs on the {@link AsyncConfig#EMAIL_EXECUTOR}
     * pool so the admin completion call returns immediately.
     *
     * <p>All exceptions are caught and logged — a failure here must never
     * affect the originating completion transaction or surface to the
     * admin user (the review is already final once the event fires).
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReviewCompleted(ReviewCompletedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            // Defensive — anything that escapes handle() (including
            // RuntimeException from a malformed CreditRequest row) must
            // not blow up the async worker.
            log.error(
                    "ReviewCompleted email delivery failed for creditRequestId={}: {}",
                    event.requestId(),
                    ex.getMessage(),
                    ex);
        }
    }

    private void handle(ReviewCompletedEvent event) {
        if (!enabled) {
            // Sprint 3 default — log the intent only. Flipping the flag
            // wires the real send through the same EmailSender pipeline
            // that PWS uses.
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

        EmailMessage message = new EmailMessage(
                recipients,
                List.of(),
                buildSubject(cr, event.outcome()),
                buildHtmlBody(cr, event.outcome()),
                buildTextBody(cr, event.outcome()));
        emailSender.send(message);
        log.info(
                "ReviewCompleted email dispatched: creditRequestId={} requestNumber={} outcome={} recipients={}",
                requestId,
                cr.getRequestNumber(),
                event.outcome(),
                recipients.size());
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

    static String buildSubject(CreditRequest cr, SystemStatus outcome) {
        String requestNumber = cr.getRequestNumber() != null ? cr.getRequestNumber() : String.valueOf(cr.getId());
        return outcome == SystemStatus.APPROVED
                ? "Your partial credit request " + requestNumber + " has been approved"
                : "Your partial credit request " + requestNumber + " has been declined";
    }

    static String buildHtmlBody(CreditRequest cr, SystemStatus outcome) {
        String requestNumber = cr.getRequestNumber() != null ? cr.getRequestNumber() : String.valueOf(cr.getId());
        String orderNumber = cr.getOrderNumber() != null ? cr.getOrderNumber() : "";
        BigDecimal approvedTotal = cr.getApprovedTotal() != null ? cr.getApprovedTotal() : BigDecimal.ZERO;
        String outcomeText = outcome == SystemStatus.APPROVED ? "approved" : "declined";

        StringBuilder html = new StringBuilder(512);
        html.append("<p>Hello,</p>");
        html.append("<p>Your partial credit request <strong>")
                .append(escapeHtml(requestNumber))
                .append("</strong> for order ")
                .append(escapeHtml(orderNumber))
                .append(" has been <strong>")
                .append(outcomeText)
                .append("</strong>.</p>");
        if (outcome == SystemStatus.APPROVED) {
            html.append("<p>Approved credit total: <strong>$")
                    .append(approvedTotal.toPlainString())
                    .append("</strong></p>");
        }
        html.append("<p>You can view the full review breakdown in your buyer portal.</p>");
        html.append("<p>Thank you,<br/>ecoATM Direct</p>");
        return html.toString();
    }

    static String buildTextBody(CreditRequest cr, SystemStatus outcome) {
        String requestNumber = cr.getRequestNumber() != null ? cr.getRequestNumber() : String.valueOf(cr.getId());
        String orderNumber = cr.getOrderNumber() != null ? cr.getOrderNumber() : "";
        BigDecimal approvedTotal = cr.getApprovedTotal() != null ? cr.getApprovedTotal() : BigDecimal.ZERO;
        String outcomeText = outcome == SystemStatus.APPROVED ? "approved" : "declined";

        StringBuilder text = new StringBuilder(256);
        text.append("Hello,\n\n");
        text.append("Your partial credit request ")
                .append(requestNumber)
                .append(" for order ")
                .append(orderNumber)
                .append(" has been ")
                .append(outcomeText)
                .append(".\n\n");
        if (outcome == SystemStatus.APPROVED) {
            text.append("Approved credit total: $").append(approvedTotal.toPlainString()).append("\n\n");
        }
        text.append("You can view the full review breakdown in your buyer portal.\n\n");
        text.append("Thank you,\necoATM Direct\n");
        return text.toString();
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
