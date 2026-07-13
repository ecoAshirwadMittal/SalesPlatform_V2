package com.ecoatm.salesplatform.listener.partialcredit;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.partialcredit.CreditRequestSubmittedEvent;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.util.ArrayList;
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
 * Bridges {@link CreditRequestSubmittedEvent} (published by
 * {@code CreditRequestService.submit} on the DRAFT → PENDING_APPROVAL flip) to
 * the buyer-facing submission-confirmation email.
 *
 * <p>Clone of {@code ReviewCompletedEmailListener}: same {@code AFTER_COMMIT} +
 * {@code @Async(EMAIL_EXECUTOR)} + {@code @Transactional(REQUIRES_NEW)} shape —
 * and deliberately NOT {@code readOnly}. {@link EmailService#sendTemplated} is
 * itself {@code @Transactional} and WRITES an {@code email.log} row, so it joins
 * this method's transaction; a {@code readOnly=true} transaction here would make
 * that INSERT fail. It stays {@code REQUIRES_NEW} (its own transaction on the
 * async thread, isolated from the already-committed submit transaction).
 *
 * <p>Rendering, delivery, and the {@code email.log} write all live in
 * {@link EmailService}; this listener only reloads the request, resolves buyer
 * recipients, builds the variable map, and dispatches <b>once</b> to the whole
 * recipient list — dropping the legacy per-user {@code Name} personalization
 * (the legacy {@code SUB_SendCreditRequestSubmittedEmail} looped per buyer user;
 * the modern port sends a single email like the review listener, using the buyer
 * company name instead).
 *
 * <p>Gated by {@code partial-credit.submitted-email.enabled} (default true) so
 * it can be flipped off without a redeploy. All exceptions are swallowed — a
 * failed email must never surface to, or roll back, the already-committed submit
 * transaction.
 */
@Component
public class CreditRequestSubmittedEmailListener {

    private static final Logger log =
            LoggerFactory.getLogger(CreditRequestSubmittedEmailListener.class);

    /** Unified {@code email.template} key seeded by V101. */
    static final String TEMPLATE_KEY = "CreditRequestSubmitted";

    /** {@code email.log.source_module} tag for every send this listener triggers. */
    private static final String SOURCE_MODULE = "PARTIAL_CREDIT";

    private final CreditRequestRepository creditRequestRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final EmailService emailService;
    private final boolean enabled;

    public CreditRequestSubmittedEmailListener(
            CreditRequestRepository creditRequestRepository,
            EcoATMDirectUserRepository directUserRepository,
            EmailService emailService,
            @Value("${partial-credit.submitted-email.enabled:true}") boolean enabled) {
        this.creditRequestRepository = creditRequestRepository;
        this.directUserRepository = directUserRepository;
        this.emailService = emailService;
        this.enabled = enabled;
    }

    /**
     * Reload the credit request, resolve buyer recipients, and dispatch the
     * submission-confirmation template through {@link EmailService#sendTemplated}.
     * Runs on the {@link AsyncConfig#EMAIL_EXECUTOR} pool so the submit call
     * returns immediately.
     *
     * <p>All exceptions are caught and logged — a failure here must never affect
     * the originating submit transaction or surface to the buyer (the request is
     * already submitted once the event fires).
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreditRequestSubmitted(CreditRequestSubmittedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error(
                    "CreditRequestSubmitted email delivery failed for creditRequestId={}: {}",
                    event.requestId(),
                    ex.getMessage(),
                    ex);
        }
    }

    private void handle(CreditRequestSubmittedEvent event) {
        if (!enabled) {
            log.info(
                    "[CreditRequestSubmittedEmailListener] (disabled) would send submission email for creditRequestId={} submittedByUserId={}",
                    event.requestId(),
                    event.submittedByUserId());
            return;
        }

        Long requestId = event.requestId();
        if (requestId == null) {
            log.warn("CreditRequestSubmittedEvent published with null requestId — skipping send");
            return;
        }

        CreditRequest cr = creditRequestRepository.findById(requestId).orElse(null);
        if (cr == null) {
            log.warn("CreditRequestSubmitted email skipped: no CreditRequest for id={}", requestId);
            return;
        }

        List<String> recipients = resolveRecipientEmails(cr.getBuyerCodeId());
        if (recipients.isEmpty()) {
            log.warn(
                    "CreditRequestSubmitted email skipped: no active recipients for buyerCodeId={} (creditRequestId={})",
                    cr.getBuyerCodeId(),
                    requestId);
            return;
        }

        // Recipients MUST travel via SendOverrides.to: the V101 template has
        // to_default=null, so a null overrides would make sendTemplated throw
        // "no recipients". cc/bcc are explicitly null (template default).
        EmailLog sent = emailService.sendTemplated(
                TEMPLATE_KEY,
                variablesFor(cr, resolveBuyerCompanyName(cr.getBuyerCodeId())),
                new EmailService.SendOverrides(recipients, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, requestId));

        log.info(
                "CreditRequestSubmitted email dispatched: creditRequestId={} requestNumber={} recipients={} status={}",
                requestId,
                cr.getRequestNumber(),
                recipients.size(),
                sent.getStatus());
    }

    /**
     * Builds the variable map for the CreditRequestSubmitted template.
     *
     * <p>{@code requestNumber} follows the legacy {@code 'CR' + orderNumber}
     * convention (distinct from the internal PCR request number). {@code
     * buyerName} carries the buyer company (the modern port drops the legacy
     * per-user recipient personalization). {@code requestReasons} is the
     * comma-joined list of flagged reasons; {@code totalDevices} carries the
     * requested total (legacy {@code Requested_Total}).
     */
    static Map<String, Object> variablesFor(CreditRequest cr, String buyerName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("requestNumber", "CR" + (cr.getOrderNumber() != null ? cr.getOrderNumber() : ""));
        vars.put("buyerName", buyerName != null ? buyerName : "");
        vars.put("requestReasons", buildRequestReasons(cr));
        vars.put("totalDevices",
                cr.getRequestedTotal() != null ? cr.getRequestedTotal().toPlainString() : "0");
        return vars;
    }

    /** Comma-joined human-readable list of the reasons the buyer flagged. */
    private static String buildRequestReasons(CreditRequest cr) {
        List<String> reasons = new ArrayList<>();
        if (Boolean.TRUE.equals(cr.getHasMissingDevice())) {
            reasons.add("Missing");
        }
        if (Boolean.TRUE.equals(cr.getHasWrongDevice())) {
            reasons.add("Wrong");
        }
        if (Boolean.TRUE.equals(cr.getHasEncumberedDevice())) {
            reasons.add("Encumbered");
        }
        return String.join(", ", reasons);
    }

    /**
     * Pulls active EcoATM Direct user emails for the buyer code — same resolver
     * path PWS + review-completed notifications use, so the recipient list stays
     * consistent across the surfaces.
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

    /** First buyer company name for the buyer code, or {@code ""} when none. */
    private String resolveBuyerCompanyName(Long buyerCodeId) {
        if (buyerCodeId == null) {
            return "";
        }
        List<String> names = directUserRepository.findBuyerCompanyNameByBuyerCodeId(buyerCodeId);
        return names.isEmpty() ? "" : names.get(0);
    }
}
