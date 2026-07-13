package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manual admin action: email an accounting distribution list the
 * "sales-approved" summary for a partial-credit request — the modern port of
 * the legacy {@code ACT_SendCreditRequestAccountingEmail} (Mendix template
 * {@code CreditRequestSalesApproved}), gap 2.5 Task 4.
 *
 * <p>Unlike the buyer submission-confirmation ({@code
 * CreditRequestSubmittedEmailListener}) and the review-completed email, this is
 * <b>not</b> event-driven — it fires only when an admin explicitly clicks
 * "Send accounting email" on the review page
 * ({@code POST /api/v1/admin/partial-credit/{id}/send-accounting-email}). It is
 * therefore synchronous: the admin gets the sent/failed outcome back.
 *
 * <p><b>Locked decisions (user, 2026-07-12):</b>
 * <ul>
 *   <li><b>Config-only recipients.</b> The accounting address is not in any
 *       migrated Mendix source, so it comes from
 *       {@code partial-credit.accounting-email.recipients} (comma-separated)
 *       with <b>no shipped default</b>. Unset/empty → fail safe: throw
 *       {@link AccountingRecipientsNotConfiguredException} (409), never a
 *       hard-coded fallback and never a silent no-op.</li>
 *   <li><b>APPROVED required.</b> The template is the <em>sales-approved</em>
 *       summary; a non-approved request → {@link CreditRequestNotApprovedException}
 *       (409).</li>
 * </ul>
 *
 * <p>Delivery, rendering, and the {@code email.log} write all live in
 * {@link EmailService#sendTemplated}; this service loads the request, guards
 * state + config, builds the variable map, and dispatches once to the whole
 * configured recipient list via a {@code SendOverrides.to} (the V102 template
 * has {@code to_default = NULL}).
 */
@Service
public class AccountingEmailService {

    private static final Logger log = LoggerFactory.getLogger(AccountingEmailService.class);

    /** Unified {@code email.template} key seeded by V102. */
    static final String TEMPLATE_KEY = "CreditRequestSalesApproved";

    /** {@code email.log.source_module} tag for every send this service triggers. */
    private static final String SOURCE_MODULE = "PARTIAL_CREDIT";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestStatusRepository statusRepository;
    private final BuyerCodeLookupService buyerCodeLookupService;
    private final WeekRepository weekRepository;
    private final EmailService emailService;

    /**
     * Configured accounting recipients — comma-separated, <b>no default</b>
     * (empty when unset). Bound as {@code List<String>} so Spring splits the
     * comma-separated property for us; each entry is re-trimmed and blanks are
     * dropped in {@link #configuredRecipients()} so a stray {@code "a, ,b"}
     * can never yield a blank {@code to} address.
     */
    private final List<String> recipients;

    public AccountingEmailService(
            CreditRequestRepository creditRequestRepository,
            CreditRequestStatusRepository statusRepository,
            BuyerCodeLookupService buyerCodeLookupService,
            WeekRepository weekRepository,
            EmailService emailService,
            @Value("${partial-credit.accounting-email.recipients:}") List<String> recipients) {
        this.creditRequestRepository = creditRequestRepository;
        this.statusRepository = statusRepository;
        this.buyerCodeLookupService = buyerCodeLookupService;
        this.weekRepository = weekRepository;
        this.emailService = emailService;
        this.recipients = recipients;
    }

    /**
     * Sends the accounting-notification email for one credit request and
     * returns the persisted {@link EmailLog} (the transport outcome —
     * {@code SENT} or {@code FAILED} — is captured on the row; a transport
     * failure is NOT rethrown, mirroring {@link EmailService#sendTemplated}).
     *
     * <p>{@code @Transactional} and deliberately <b>not</b> {@code readOnly}:
     * {@code sendTemplated} joins this transaction and INSERTs an
     * {@code email.log} row — a {@code readOnly} tx would fail that write.
     *
     * @throws EntityNotFoundException if no credit request has {@code requestId}
     *     (→ 404)
     * @throws CreditRequestNotApprovedException if the request is not APPROVED
     *     (→ 409)
     * @throws AccountingRecipientsNotConfiguredException if no recipients are
     *     configured (→ 409)
     */
    @Transactional
    public EmailLog sendAccountingEmail(Long requestId) {
        CreditRequest cr = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + requestId));

        SystemStatus status = systemStatusOf(cr);
        if (status != SystemStatus.APPROVED) {
            throw new CreditRequestNotApprovedException(
                    "Credit request " + requestId + " is not APPROVED (status=" + status
                            + "); the accounting email can only be sent for an approved request.");
        }

        List<String> configured = configuredRecipients();
        if (configured.isEmpty()) {
            throw new AccountingRecipientsNotConfiguredException(
                    "Accounting email recipients are not configured "
                            + "(set partial-credit.accounting-email.recipients).");
        }

        EmailLog sent = emailService.sendTemplated(
                TEMPLATE_KEY,
                buildVariables(cr),
                new EmailService.SendOverrides(configured, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, requestId));

        // Business identifiers only — never the recipient addresses (PII).
        log.info(
                "CreditRequestSalesApproved accounting email dispatched: creditRequestId={} requestNumber={} recipients={} status={}",
                requestId,
                cr.getRequestNumber(),
                configured.size(),
                sent.getStatus());
        return sent;
    }

    /**
     * Builds the {@code {{var}}} map for the CreditRequestSalesApproved template.
     * Every value is non-null (blank fallbacks) so a missing datum renders empty
     * rather than the string {@code "null"}.
     *
     * <p>Var mapping (legacy {@code CreditRequestSalesApproved}):
     * <ul>
     *   <li>{@code requestNumber} = {@code 'CR' + orderNumber} (legacy
     *       {@code RequestNumber}; matches the submit-email convention)</li>
     *   <li>{@code weekNumber} = {@code 'W' + <calendar week>} of the order's
     *       created date (legacy {@code 'W' + AuctionWeek}); empty when the
     *       week can't be resolved</li>
     *   <li>{@code buyerName} = the order party/company name</li>
     *   <li>{@code buyerCode} = the buyer code string</li>
     *   <li>{@code requestReasons} = comma-joined flagged reasons</li>
     *   <li>{@code totalDevicesApproved} = {@code approvedQty}</li>
     *   <li>{@code totalAmountApproved} = {@code '$' + approvedTotal}</li>
     * </ul>
     */
    Map<String, Object> buildVariables(CreditRequest cr) {
        // LinkedHashMap for a stable, readable order in logs/tests.
        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("requestNumber", "CR" + orEmpty(cr.getOrderNumber()));
        vars.put("weekNumber", weekLabel(cr));
        vars.put("buyerName", buyerName(cr));
        vars.put("buyerCode", orEmpty(buyerCodeLookupService.findCodeById(cr.getBuyerCodeId())));
        vars.put("requestReasons", buildRequestReasons(cr));
        vars.put("totalDevicesApproved",
                String.valueOf(cr.getApprovedQty() != null ? cr.getApprovedQty() : 0));
        vars.put("totalAmountApproved", "$" + money(cr.getApprovedTotal()));
        return vars;
    }

    /** Trimmed, blank-free copy of the configured recipient list. */
    private List<String> configuredRecipients() {
        if (recipients == null) {
            return List.of();
        }
        List<String> cleaned = new ArrayList<>();
        for (String r : recipients) {
            if (r != null && !r.isBlank()) {
                cleaned.add(r.trim());
            }
        }
        return cleaned;
    }

    private SystemStatus systemStatusOf(CreditRequest cr) {
        return statusRepository.findById(cr.getStatusId())
                .map(CreditRequestStatus::getSystemStatus)
                .orElseThrow(() -> new IllegalStateException(
                        "Status row missing for credit request " + cr.getId()));
    }

    /**
     * {@code 'W' + <calendar week number>} of the week containing the order's
     * created date, or {@code ""} when there is no order date or no week row
     * covers it. The exact legacy {@code AuctionWeek} source is not present in
     * {@code migration_context/}; the order's calendar week is the faithful,
     * human-readable stand-in (same "Wk" convention the bidder dashboard uses).
     */
    private String weekLabel(CreditRequest cr) {
        if (cr.getOrderCreatedDate() == null) {
            return "";
        }
        return weekRepository.findByDate(cr.getOrderCreatedDate())
                .map(Week::getWeekNumber)
                .map(n -> "W" + n)
                .orElse("");
    }

    /** Order party (company) name, falling back to the contact name, else "". */
    private static String buyerName(CreditRequest cr) {
        if (cr.getPartyName() != null && !cr.getPartyName().isBlank()) {
            return cr.getPartyName();
        }
        return orEmpty(cr.getBuyerName());
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

    /** 2-decimal plain string (no grouping) — the "$" prefix is added by the caller. */
    private static String money(BigDecimal value) {
        BigDecimal amount = value != null ? value : BigDecimal.ZERO;
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String orEmpty(String value) {
        return value != null ? value : "";
    }
}
