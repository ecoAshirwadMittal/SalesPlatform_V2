package com.ecoatm.salesplatform.listener.rma;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends the buyer-facing RMA approval email once an RMA review commits as
 * {@link RmaReviewOutcome#APPROVED}. This is the event-driven port of the
 * legacy {@code SUB_SendEmail_RMAApproved} (the {@code PWSRMAApprovalEmail}
 * template), decoupled off the admin request thread so the notification can
 * never roll the review back or block the UI response.
 *
 * <p>Follows the same {@code AFTER_COMMIT} + {@code @Async} shape as
 * {@code ReviewCompletedEmailListener} (partial credit): it fires only after
 * the review-completion transaction has durably committed, and runs on the
 * shared {@link AsyncConfig#EMAIL_EXECUTOR} pool. Rendering, recipient-list
 * plumbing beyond this class, the {@code email.log} write, and delivery all
 * live in {@link EmailService#sendTemplated} against the shared
 * {@code email.template} store (the V93 {@code RMA_Approved} seed).
 *
 * <p><b>Transaction shape (the readOnly-tx gotcha).</b> This method is
 * {@code @Transactional(REQUIRES_NEW)} — deliberately NOT {@code readOnly}.
 * {@link EmailService#sendTemplated} is itself {@code @Transactional} and
 * WRITES {@code email.log}, so it joins this method's transaction; a
 * {@code readOnly=true} transaction here would make that INSERT fail. It stays
 * {@code REQUIRES_NEW} (its own transaction on the async thread, isolated from
 * the already-committed review) — same gotcha documented on the partial-credit
 * listener.
 *
 * <p><b>No local enable flag.</b> Unlike the partial-credit listener, this one
 * carries no {@code *.enabled} gate: dev/test already route through
 * {@code LoggingEmailSender} (logs, never sends), and the template's own
 * {@code enabled} column gates production delivery — a disabled
 * {@code RMA_Approved} row makes {@code sendTemplated} throw
 * {@link IllegalStateException}, which is swallowed here like any other
 * failure. Nothing to redeploy to turn it off.
 */
@Component
public class RmaApprovedEmailListener {

    private static final Logger log = LoggerFactory.getLogger(RmaApprovedEmailListener.class);

    /** {@code email.template.template_key} seeded by V93. */
    static final String TEMPLATE_KEY = "RMA_Approved";

    /** {@code email.log.source_module} tag for every send this listener triggers. */
    private static final String SOURCE_MODULE = "RMA";

    /** {@code rma_item.status} value for an approved line (the raw enum the
     *  review writes — {@code status_display} is the human label). Only these
     *  lines appear in the approved-items summary, mirroring the legacy
     *  {@code Filter on ENUM_RMAItemStatus.Approve}. */
    private static final String ITEM_STATUS_APPROVE = "Approve";

    private final RmaRepository rmaRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final EmailService emailService;

    public RmaApprovedEmailListener(
            RmaRepository rmaRepository,
            EcoATMDirectUserRepository directUserRepository,
            BuyerCodeLookupService buyerCodeLookup,
            EmailService emailService) {
        this.rmaRepository = rmaRepository;
        this.directUserRepository = directUserRepository;
        this.buyerCodeLookup = buyerCodeLookup;
        this.emailService = emailService;
    }

    /**
     * React to an APPROVED {@link RmaReviewCompletedEvent} by dispatching the
     * {@code RMA_Approved} template through {@link EmailService#sendTemplated}.
     * Runs on {@link AsyncConfig#EMAIL_EXECUTOR} after the review commits.
     *
     * <p>All exceptions are caught and logged — a failure here must never
     * affect the already-final review nor surface to the admin user.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRmaReviewCompleted(RmaReviewCompletedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error(
                    "RMA approval email delivery failed for rmaId={}: {}",
                    event.rmaId(),
                    ex.getMessage(),
                    ex);
        }
    }

    private void handle(RmaReviewCompletedEvent event) {
        if (event.outcome() != RmaReviewOutcome.APPROVED) {
            log.debug("RMA approval email skipped: non-approved outcome={} for rmaId={}",
                    event.outcome(), event.rmaId());
            return;
        }

        Long rmaId = event.rmaId();
        if (rmaId == null) {
            log.warn("RmaReviewCompletedEvent published with null rmaId — skipping approval email");
            return;
        }

        Rma rma = rmaRepository.findById(rmaId).orElse(null);
        if (rma == null) {
            log.warn("RMA approval email skipped: no Rma for id={}", rmaId);
            return;
        }

        List<String> recipients = resolveRecipientEmails(rma.getBuyerCodeId());
        if (recipients.isEmpty()) {
            log.warn(
                    "RMA approval email skipped: no active recipients for buyerCodeId={} (rmaId={})",
                    rma.getBuyerCodeId(),
                    rmaId);
            return;
        }

        String buyerCode = resolveBuyerCode(rma.getBuyerCodeId());

        // Recipients MUST travel via SendOverrides.to: the V93-seeded RMA_Approved
        // template has to_default=null, so a null overrides would make
        // sendTemplated throw "no recipients". cc/bcc are explicitly null so the
        // template's own defaults apply.
        EmailLog sent = emailService.sendTemplated(
                TEMPLATE_KEY,
                variablesFor(rma, buyerCode),
                new EmailService.SendOverrides(recipients, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, rmaId));

        log.info(
                "RMA approval email dispatched: rmaId={} rmaNumber={} recipients={} status={}",
                rmaId,
                rma.getNumber(),
                recipients.size(),
                sent.getStatus());
    }

    /**
     * Builds the variable map for the {@code RMA_Approved} template. Keeps the
     * rendering layer free of currency/null-format concerns:
     * {@code approvedTotalDisplay} carries the literal dollar sign so the V93
     * seed could be written without colliding with Flyway's {@code ${...}}
     * placeholder syntax (same trick the V90 partial-credit seed used).
     */
    static Map<String, Object> variablesFor(Rma rma, String buyerCode) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("rmaNumber", rma.getNumber() != null ? rma.getNumber() : String.valueOf(rma.getId()));
        vars.put("buyerCode", buyerCode != null ? buyerCode : "");
        vars.put("approvedQty", rma.getApprovedQty() != null ? rma.getApprovedQty() : 0);
        vars.put("approvedSkus", rma.getApprovedSkus() != null ? rma.getApprovedSkus() : 0);
        BigDecimal approvedTotal =
                rma.getApprovedSalesTotal() != null ? rma.getApprovedSalesTotal() : BigDecimal.ZERO;
        vars.put("approvedTotalDisplay", formatMoney(approvedTotal));
        vars.put("approvedItemsSummary", approvedItemsSummary(rma));
        return vars;
    }

    /**
     * One line per APPROVED item — {@code "<imei> — <returnReason>"}, joined by
     * newlines. Mirrors {@code SUB_SendEmail_RMAApproved}, which listed only the
     * approved lines. Empty when the RMA has no approved items (defensive — an
     * APPROVED outcome normally has at least one).
     */
    private static String approvedItemsSummary(Rma rma) {
        return rma.getItems().stream()
                .filter(item -> ITEM_STATUS_APPROVE.equals(item.getStatus()))
                .map(RmaApprovedEmailListener::describeItem)
                .collect(Collectors.joining("\n"));
    }

    private static String describeItem(RmaItem item) {
        String imei = item.getImei() != null ? item.getImei() : "";
        String reason = item.getReturnReason() != null ? item.getReturnReason() : "";
        return reason.isEmpty() ? imei : imei + " — " + reason;
    }

    /**
     * Formats {@code amount} as {@code $#,##0.00} in a US locale — the app-wide
     * currency convention. A fresh {@link DecimalFormat} per call because it is
     * not thread-safe and this runs on a shared async pool.
     */
    private static String formatMoney(BigDecimal amount) {
        DecimalFormat fmt = new DecimalFormat("$#,##0.00", new DecimalFormatSymbols(Locale.US));
        return fmt.format(amount);
    }

    /** Buyer-code string for the {@code buyerCode} var; {@code null} when the
     *  RMA has no buyer code or the lookup misses (rendered as empty). */
    private String resolveBuyerCode(Long buyerCodeId) {
        return buyerCodeId == null ? null : buyerCodeLookup.findCodeById(buyerCodeId);
    }

    /**
     * Pulls active EcoATM Direct user emails for the buyer code — the same
     * resolver path PWS notifications and the partial-credit listener use, so
     * the recipient list stays consistent across surfaces. Emails are never
     * logged (only the count), matching the partial-credit listener.
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
