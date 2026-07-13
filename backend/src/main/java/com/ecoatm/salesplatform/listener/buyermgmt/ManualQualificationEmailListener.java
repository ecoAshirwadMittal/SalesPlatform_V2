package com.ecoatm.salesplatform.listener.buyermgmt;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Emails the buyer when an admin manually qualifies (includes) their buyer code
 * on a <em>Started</em> auction round. This is the event-driven port of the
 * legacy {@code SUB_SendManualQualificationEmail}, which
 * {@code NF_OnIncludedChanged_New} calls only on the {@code Included=true} +
 * {@code RoundStatus=Started} branch of a manual override.
 *
 * <p>Follows the same {@code AFTER_COMMIT} + {@code @Async} shape as
 * {@code RmaApprovedEmailListener} and the partial-credit
 * {@code ReviewCompletedEmailListener}: it fires only after the override
 * transaction has durably committed, and runs on the shared
 * {@link AsyncConfig#EMAIL_EXECUTOR} pool so a notification can never roll the
 * override back or block the admin's PATCH response. Rendering, the
 * {@code email.log} write, and delivery all live in
 * {@link EmailService#sendTemplated} against the shared {@code email.template}
 * store (the V99 {@code ManualQualification} seed).
 *
 * <p><b>The condition lives here, not on the event.</b>
 * {@link QualificationOverriddenEvent} is published on <em>every</em>
 * successful override (carrying facts only). This listener applies the legacy
 * {@code NF_OnIncludedChanged_New} gate itself — the email is sent only when
 * {@code roundStatus == Started && included == true}. Any other combination
 * (a non-Started round, or an override that un-includes) is a silent no-op.
 *
 * <p><b>Transaction shape (the readOnly-tx gotcha).</b> This method is
 * {@code @Transactional(REQUIRES_NEW)} — deliberately NOT {@code readOnly}.
 * {@link EmailService#sendTemplated} is itself {@code @Transactional} and
 * WRITES {@code email.log}, so it joins this method's transaction; a
 * {@code readOnly=true} transaction here would make that INSERT fail. It stays
 * {@code REQUIRES_NEW} (its own transaction on the async thread, isolated from
 * the already-committed override) — same gotcha documented on the RMA and
 * partial-credit listeners.
 *
 * <p><b>No local enable flag.</b> Dev/test already route through
 * {@code LoggingEmailSender} (logs, never sends), and the template's own
 * {@code enabled} column gates production delivery — a disabled
 * {@code ManualQualification} row makes {@code sendTemplated} throw
 * {@link IllegalStateException}, which is swallowed here like any other failure.
 */
@Component
public class ManualQualificationEmailListener {

    private static final Logger log = LoggerFactory.getLogger(ManualQualificationEmailListener.class);

    /** {@code email.template.template_key} seeded by V99. */
    static final String TEMPLATE_KEY = "ManualQualification";

    /** {@code email.log.source_module} tag for every send this listener triggers. */
    private static final String SOURCE_MODULE = "QUALIFICATION";

    /** Renders {@code occurredAt} as {@code yyyy-MM-dd HH:mm 'UTC'} for the
     *  {@code qualifiedAtDisplay} var. {@link DateTimeFormatter} is immutable
     *  and thread-safe, so a shared constant is safe on the async pool. */
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'").withZone(ZoneOffset.UTC);

    private final EcoATMDirectUserRepository directUserRepository;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final EmailService emailService;

    public ManualQualificationEmailListener(
            EcoATMDirectUserRepository directUserRepository,
            BuyerCodeLookupService buyerCodeLookup,
            EmailService emailService) {
        this.directUserRepository = directUserRepository;
        this.buyerCodeLookup = buyerCodeLookup;
        this.emailService = emailService;
    }

    /**
     * React to a {@link QualificationOverriddenEvent} by dispatching the
     * {@code ManualQualification} template through
     * {@link EmailService#sendTemplated} — but only for the
     * {@code Started + included} branch. Runs on
     * {@link AsyncConfig#EMAIL_EXECUTOR} after the override commits.
     *
     * <p>All exceptions are caught and logged — a failure here must never
     * affect the already-committed override nor surface to the admin user.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onQualificationOverridden(QualificationOverriddenEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error(
                    "Manual-qualification email delivery failed for qualifiedBuyerCodeId={}: {}",
                    event.qualifiedBuyerCodeId(),
                    ex.getMessage(),
                    ex);
        }
    }

    private void handle(QualificationOverriddenEvent event) {
        // Legacy NF_OnIncludedChanged_New gate: SUB_SendManualQualificationEmail
        // is reached ONLY when the override sets included=true on a Started round.
        if (event.roundStatus() != SchedulingAuctionStatus.Started || !event.included()) {
            log.debug(
                    "Manual-qualification email skipped: roundStatus={} included={} (qbcId={})",
                    event.roundStatus(),
                    event.included(),
                    event.qualifiedBuyerCodeId());
            return;
        }

        List<String> recipients = resolveRecipientEmails(event.buyerCodeId());
        if (recipients.isEmpty()) {
            log.warn(
                    "Manual-qualification email skipped: no active recipients for buyerCodeId={} (qbcId={})",
                    event.buyerCodeId(),
                    event.qualifiedBuyerCodeId());
            return;
        }

        String buyerCode = resolveBuyerCode(event.buyerCodeId());

        // Recipients MUST travel via SendOverrides.to: the V99-seeded
        // ManualQualification template has to_default=null, so a null overrides
        // would make sendTemplated throw "no recipients". cc/bcc are explicitly
        // null so the template's own defaults apply.
        EmailLog sent = emailService.sendTemplated(
                TEMPLATE_KEY,
                variablesFor(event, buyerCode),
                new EmailService.SendOverrides(recipients, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, event.qualifiedBuyerCodeId()));

        log.info(
                "Manual-qualification email dispatched: qbcId={} buyerCodeId={} recipients={} status={}",
                event.qualifiedBuyerCodeId(),
                event.buyerCodeId(),
                recipients.size(),
                sent.getStatus());
    }

    /**
     * Builds the variable map for the {@code ManualQualification} template. The
     * event already carries every fact needed (no DB reload): the buyer code
     * string is resolved by the caller; the auction reference and timestamp come
     * straight off the event.
     */
    static Map<String, Object> variablesFor(QualificationOverriddenEvent event, String buyerCode) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("buyerCode", buyerCode != null ? buyerCode : "");
        vars.put(
                "schedulingAuctionId",
                event.schedulingAuctionId() != null ? event.schedulingAuctionId() : "");
        vars.put("qualifiedAtDisplay", formatTimestamp(event.occurredAt()));
        return vars;
    }

    private static String formatTimestamp(Instant instant) {
        return instant == null ? "" : TIMESTAMP_FORMAT.format(instant);
    }

    /** Buyer-code string for the {@code buyerCode} var; {@code null} when the
     *  event has no buyer code or the lookup misses (rendered as empty). */
    private String resolveBuyerCode(Long buyerCodeId) {
        return buyerCodeId == null ? null : buyerCodeLookup.findCodeById(buyerCodeId);
    }

    /**
     * Pulls active EcoATM Direct user emails for the buyer code — the same
     * resolver path PWS notifications and the RMA / partial-credit listeners
     * use, so the recipient list stays consistent across surfaces. Emails are
     * never logged (only the count), matching the sibling listeners.
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
