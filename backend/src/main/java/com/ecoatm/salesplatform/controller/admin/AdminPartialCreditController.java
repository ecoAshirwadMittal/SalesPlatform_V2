package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.partialcredit.AdminCreditRequestRow;
import com.ecoatm.salesplatform.dto.partialcredit.AdminLandingResponse;
import com.ecoatm.salesplatform.dto.partialcredit.AdminLineProjection;
import com.ecoatm.salesplatform.dto.partialcredit.CompleteReviewRequest;
import com.ecoatm.salesplatform.dto.partialcredit.CompleteReviewResponse;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail;
import com.ecoatm.salesplatform.dto.partialcredit.EncumberedFieldsRequest;
import com.ecoatm.salesplatform.dto.partialcredit.GlobalDecisionRequest;
import com.ecoatm.salesplatform.dto.partialcredit.GlobalDecisionResponse;
import com.ecoatm.salesplatform.dto.partialcredit.HeaderSummaryDto;
import com.ecoatm.salesplatform.dto.partialcredit.LineDecisionRequest;
import com.ecoatm.salesplatform.dto.partialcredit.LineDecisionResponse;
import com.ecoatm.salesplatform.dto.partialcredit.SectionDecisionRequest;
import com.ecoatm.salesplatform.dto.partialcredit.SectionDecisionResponse;
import com.ecoatm.salesplatform.dto.partialcredit.StatusConfigPatch;
import com.ecoatm.salesplatform.dto.partialcredit.StatusConfigRow;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.partialcredit.AccountingEmailService;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminListFilter;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.CompleteReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.EncumberedLineEntry;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.GlobalDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.OpenReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.SectionDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestValidationException;
import com.ecoatm.salesplatform.service.partialcredit.PartialCreditExcelExportService;
import com.ecoatm.salesplatform.service.partialcredit.StatusConfigService;
import com.ecoatm.salesplatform.service.partialcredit.TooManyRowsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin REST surface for the partial-credit review workflow (Sprint 3
 * chunk 4 — SPKB-3658, SPKB-3663, SPKB-3664).
 *
 * <p>Eight endpoints under {@code /api/v1/admin/partial-credit/**}.
 * Class-level {@code @PreAuthorize} accepts the future
 * {@code PartialCredit_*} role names plus today's
 * {@code SalesOps}/{@code Administrator} so the surface is reachable
 * before SPKB-3659 wires the new role mapping. Per-request scope
 * (admin-only, no buyer-code gate) is handled by
 * {@link AdminCreditRequestService} — controller just trusts the
 * authenticated principal.
 *
 * <p><b>Deviation from plan:</b> the original spec called for
 * {@code GET /{id}/review} to mutate state on first access. We split it
 * into:
 * <ul>
 *   <li>{@code POST /{id}/open-for-review} — explicit state mutation
 *       ({@code PENDING_APPROVAL -> UNDER_REVIEW})</li>
 *   <li>{@code GET /{id}} — read-only detail; no state change</li>
 * </ul>
 * Honest verbs win over clever GETs that surprise the caller.
 */
@RestController
@RequestMapping("/api/v1/admin/partial-credit")
// Sprint 4 §7.1 — drop the orphaned PartialCredit_* role names; admin
// surface uses the existing global roles. CoAdministrator + SalesRep
// were added per the Sprint 4 §7 final-state allowlist (SalesRep needs
// admin access for diagnostic / on-behalf flows).
// L-7 (security review 2026-07-10): the SalesRep grant here is INTENTIONAL and
// verified against the Sprint 4 §7 role matrix — SalesRep submits/reviews credit
// requests on behalf of buyers, so it must reach this admin surface. Per-request
// admin scope (no buyer-code gate) is enforced by AdminCreditRequestService, not
// the matcher. Not a finding to fix — documented here to close L-7.
@PreAuthorize("hasAnyRole('SalesOps','SalesRep','Administrator','CoAdministrator')")
public class AdminPartialCreditController {

    /**
     * Prefix for the accounting-email rate-limit bucket key. This endpoint is
     * a real outbound-email trigger, so it is rate-limited like the unified
     * email admin's {@code /send-test} / {@code /log/{id}/resend} — user-keyed
     * (the caller always has a verified JWT principal), not IP-keyed.
     */
    private static final String ACCOUNTING_EMAIL_RATE_LIMIT_PREFIX = "partial-credit-accounting-email:";

    private final AdminCreditRequestService adminService;
    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestStatusRepository statusRepository;
    private final MissingDeviceLineRepository missingDeviceLineRepository;
    private final WrongDeviceLineRepository wrongDeviceLineRepository;
    private final EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    private final CreditRequestPhotoRepository photoRepository;
    private final BuyerCodeRepository buyerCodeRepository;
    private final StatusConfigService statusConfigService;
    private final PartialCreditExcelExportService exportService;
    private final AccountingEmailService accountingEmailService;
    private final UploadRateLimiter uploadRateLimiter;

    public AdminPartialCreditController(
            AdminCreditRequestService adminService,
            CreditRequestRepository creditRequestRepository,
            CreditRequestStatusRepository statusRepository,
            MissingDeviceLineRepository missingDeviceLineRepository,
            WrongDeviceLineRepository wrongDeviceLineRepository,
            EncumberedDeviceLineRepository encumberedDeviceLineRepository,
            CreditRequestPhotoRepository photoRepository,
            BuyerCodeRepository buyerCodeRepository,
            StatusConfigService statusConfigService,
            PartialCreditExcelExportService exportService,
            AccountingEmailService accountingEmailService,
            UploadRateLimiter uploadRateLimiter) {
        this.adminService = adminService;
        this.creditRequestRepository = creditRequestRepository;
        this.statusRepository = statusRepository;
        this.missingDeviceLineRepository = missingDeviceLineRepository;
        this.wrongDeviceLineRepository = wrongDeviceLineRepository;
        this.encumberedDeviceLineRepository = encumberedDeviceLineRepository;
        this.photoRepository = photoRepository;
        this.buyerCodeRepository = buyerCodeRepository;
        this.statusConfigService = statusConfigService;
        this.exportService = exportService;
        this.accountingEmailService = accountingEmailService;
        this.uploadRateLimiter = uploadRateLimiter;
    }

    /**
     * Pre-resolves wrong-device-line → photo-count for the V91 DTO
     * builder. One query, grouped in memory (per-line cap is 5 so
     * total volume is tiny).
     */
    private java.util.Map<Long, Integer> wrongPhotoCounts(Long creditRequestId) {
        java.util.Map<Long, Integer> counts = new java.util.HashMap<>();
        for (var photo : photoRepository.findByCreditRequestIdOrderById(creditRequestId)) {
            Long lineId = photo.getWrongDeviceLineId();
            if (lineId != null) {
                counts.merge(lineId, 1, Integer::sum);
            }
        }
        return counts;
    }

    // -------------------------------------------------------------------
    // GET /  —  landing list + status counters
    // -------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<AdminLandingResponse> list(
            @RequestParam(required = false) SystemStatus status,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) LineKind reason,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        AdminListFilter filter = new AdminListFilter(
                status, buyerCodeId, orderNumber, reason, dateFrom, dateTo);
        // Cap the page size — guard against accidentally huge admin scrapes.
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200));

        Page<CreditRequest> rows = adminService.listForAdmin(filter, pageable);
        Map<Long, CreditRequestStatus> statusById = loadStatusesById();
        Map<Long, String> buyerCodeById = loadBuyerCodesById(rows.getContent());

        List<AdminCreditRequestRow> projected = rows.stream()
                .map(cr -> {
                    CreditRequestStatus row = statusById.get(cr.getStatusId());
                    if (row == null) {
                        // Fallback synthetic status row — should never happen
                        // because V89 seed guarantees all five rows; if it
                        // does we'd rather surface the row blank than 500.
                        row = new CreditRequestStatus();
                        row.setSystemStatus(SystemStatus.PENDING_APPROVAL);
                        row.setExternalStatusText("Unknown");
                        row.setColorHex("#888888");
                    }
                    return AdminCreditRequestRow.from(cr, row, buyerCodeById.get(cr.getBuyerCodeId()));
                })
                .toList();

        return ResponseEntity.ok(new AdminLandingResponse(
                projected, adminService.statusCounters(), rows.getTotalElements()));
    }

    // -------------------------------------------------------------------
    // GET /{id}  —  read-only detail
    // -------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<CreditRequestDetail> getById(@PathVariable Long id) {
        CreditRequest cr = creditRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + id));
        CreditRequestStatus statusRow = statusRepository.findById(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException(
                        "Status row missing for credit request " + cr.getId()));
        return ResponseEntity.ok(CreditRequestDetail.from(
                cr,
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                statusRow.getInternalStatusText(),
                statusRow.getColorHex(),
                missingDeviceLineRepository.findByCreditRequestIdOrderById(id),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(id),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(id),
                wrongPhotoCounts(id)));
    }

    // -------------------------------------------------------------------
    // POST /{id}/open-for-review  —  state transition
    // -------------------------------------------------------------------

    @PostMapping("/{id}/open-for-review")
    public ResponseEntity<CreditRequestDetail> openForReview(
            @PathVariable Long id, Authentication auth) {
        OpenReviewResult result = adminService.openForReview(id, principalUserId(auth));
        CreditRequestStatus statusRow = statusRepository.findById(result.creditRequest().getStatusId())
                .orElseThrow(() -> new IllegalStateException(
                        "Status row missing for credit request " + id));
        return ResponseEntity.ok(CreditRequestDetail.from(
                result.creditRequest(),
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                statusRow.getInternalStatusText(),
                statusRow.getColorHex(),
                result.missingLines(),
                result.wrongLines(),
                result.encumberedLines(),
                wrongPhotoCounts(id)));
    }

    // -------------------------------------------------------------------
    // POST /{id}/lines/{lineId}/decision  —  per-line accept/decline
    // -------------------------------------------------------------------

    @PostMapping("/{id}/lines/{lineId}/decision")
    public ResponseEntity<LineDecisionResponse> setLineDecision(
            @PathVariable Long id,
            @PathVariable Long lineId,
            @RequestBody LineDecisionRequest body,
            Authentication auth) {
        LineDecisionResult result = adminService.setLineDecision(
                id, lineId, body.kind(), body.decision(), principalUserId(auth));
        return ResponseEntity.ok(new LineDecisionResponse(
                AdminLineProjection.fromUntyped(result.updatedLine()),
                HeaderSummaryDto.from(result.summary())));
    }

    // -------------------------------------------------------------------
    // POST /{id}/sections/{kind}/decision  —  bulk per-section
    // -------------------------------------------------------------------

    @PostMapping("/{id}/sections/{kind}/decision")
    public ResponseEntity<SectionDecisionResponse> setSectionDecision(
            @PathVariable Long id,
            @PathVariable LineKind kind,
            @RequestBody SectionDecisionRequest body,
            Authentication auth) {
        SectionDecisionResult result = adminService.setSectionDecision(
                id, kind, body.decision(), principalUserId(auth));
        return ResponseEntity.ok(new SectionDecisionResponse(
                result.kind(), result.updatedCount(), HeaderSummaryDto.from(result.summary())));
    }

    // -------------------------------------------------------------------
    // POST /{id}/decision  —  global bulk
    // -------------------------------------------------------------------

    @PostMapping("/{id}/decision")
    public ResponseEntity<GlobalDecisionResponse> setGlobalDecision(
            @PathVariable Long id,
            @RequestBody GlobalDecisionRequest body,
            Authentication auth) {
        GlobalDecisionResult result = adminService.setGlobalDecision(
                id, body.decision(), principalUserId(auth));
        return ResponseEntity.ok(new GlobalDecisionResponse(
                result.missingUpdated(),
                result.wrongUpdated(),
                result.encumberedUpdated(),
                HeaderSummaryDto.from(result.summary())));
    }

    // -------------------------------------------------------------------
    // POST /{id}/lines/{lineId}/encumbered  —  Prolog Result + Actual Value
    // -------------------------------------------------------------------

    @PostMapping("/{id}/lines/{lineId}/encumbered")
    public ResponseEntity<LineDecisionResponse> setEncumberedFields(
            @PathVariable Long id,
            @PathVariable Long lineId,
            @RequestBody EncumberedFieldsRequest body,
            Authentication auth) {
        EncumberedLineEntry result = adminService.setEncumberedFields(
                id, lineId, body.prologResult(), body.actualValue(), principalUserId(auth));
        return ResponseEntity.ok(new LineDecisionResponse(
                AdminLineProjection.of(result.line()),
                HeaderSummaryDto.from(result.summary())));
    }

    // -------------------------------------------------------------------
    // POST /{id}/complete-review  —  terminal transition
    // -------------------------------------------------------------------

    @PostMapping("/{id}/complete-review")
    public ResponseEntity<CompleteReviewResponse> completeReview(
            @PathVariable Long id,
            @RequestBody CompleteReviewRequest body,
            Authentication auth) {
        CompleteReviewResult result = adminService.completeReview(
                id, body.outcome(), principalUserId(auth));
        CreditRequest cr = result.creditRequest();
        return ResponseEntity.ok(new CompleteReviewResponse(
                cr.getId(),
                cr.getRequestNumber(),
                result.outcome(),
                cr.getReviewCompletedOn(),
                HeaderSummaryDto.from(result.summary())));
    }

    // -------------------------------------------------------------------
    // Status configuration endpoints (Sprint 3 chunk 7 — SPKB-3664)
    // -------------------------------------------------------------------

    /**
     * Lists the 5 seeded status rows ordered by {@code sort_order}. Used by
     * the admin status-config grid and any surface that needs canonical
     * pill colors.
     */
    @GetMapping("/statuses")
    public List<StatusConfigRow> listStatuses() {
        return statusConfigService.listAll().stream()
                .map(StatusConfigRow::from)
                .toList();
    }

    /**
     * Patches a single status row's cosmetic fields. {@code system_status}
     * itself is immutable. Returns the updated row so the frontend can
     * replace optimistic UI state with the persisted snapshot.
     */
    @PatchMapping("/statuses/{id}")
    // L-7 (security review 2026-07-10): status-config edits are platform-wide
    // (pill colours/labels), so this one endpoint is tighter than the buyer-review
    // surface — Administrator/Co-Admin only, dropping SalesOps/SalesRep. 'Co-Admin'
    // is the real seeded role name (identity.user_roles; V2/V15) — hasAnyRole
    // prepends ROLE_, so this matches the ROLE_Co-Admin authority granted by
    // JwtAuthenticationFilter. (NB: the class-level @PreAuthorize's stale
    // 'CoAdministrator' spelling is a separate pre-existing issue, out of scope.)
    @PreAuthorize("hasAnyRole('Administrator','Co-Admin')")
    public StatusConfigRow updateStatus(
            @PathVariable Long id,
            @RequestBody StatusConfigPatch patch,
            Authentication auth) {
        Long actorUserId = principalUserId(auth);
        return StatusConfigRow.from(statusConfigService.update(id, patch, actorUserId));
    }

    // -------------------------------------------------------------------
    // GET /export.xlsx  —  filtered xlsx download (Sprint 4 chunk 7)
    // -------------------------------------------------------------------

    /**
     * Streams a two-sheet xlsx (Requests + Lines) for the filter set
     * the admin landing has already applied. Caps at
     * {@link PartialCreditExcelExportService#ROW_CAP} requests — over
     * that, the export service throws {@link TooManyRowsException}
     * and the caller gets a {@code 413 Payload Too Large} with a
     * {@code {error, limit, matched}} body so the UI can render a
     * "narrow your filters" toast inline.
     */
    @GetMapping("/export.xlsx")
    public ResponseEntity<byte[]> exportXlsx(
            @RequestParam(required = false) SystemStatus status,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) LineKind reason,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        AdminListFilter filter = new AdminListFilter(
                status, buyerCodeId, orderNumber, reason, dateFrom, dateTo);
        byte[] bytes = exportService.export(filter);
        String filename = "partial-credit-"
                + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    // -------------------------------------------------------------------
    // GET /{id}/export.xlsx  —  single-request xlsx packet (gap 2.5)
    // -------------------------------------------------------------------

    /**
     * Streams a two-sheet xlsx (Requests + Lines) scoped to ONE credit
     * request — the admin per-request review packet, porting legacy
     * {@code ACT_DownloadCreditRequest}. {@code 404 Not Found} when the
     * request doesn't exist (the export service owns that check).
     *
     * <p>Authz is the class-level {@code @PreAuthorize} plus the
     * {@code /api/v1/admin/partial-credit/**} SecurityConfig matcher —
     * same defense-in-depth as the sibling {@link #exportXlsx} bulk export
     * (no per-method tightening; this endpoint is not narrower than the
     * class default).
     *
     * <p>The download filename names the buyer's order (falling back to
     * the surrogate id) and is emitted via {@link ContentDisposition}
     * after allowlist-sanitising the order token, so a hostile order
     * number can never inject into the header (repo Security Rule — never
     * string-concatenate the Content-Disposition value).
     */
    @GetMapping("/{id}/export.xlsx")
    public ResponseEntity<byte[]> exportSingleXlsx(@PathVariable Long id) {
        byte[] bytes = exportService.exportSingle(id);
        // Re-read only for the filename token; the export above already
        // 404'd if the request was missing, so this is guaranteed present.
        CreditRequest cr = creditRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + id));
        String filename = "CR-" + filenameToken(cr) + ".xlsx";
        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .filename(filename)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(bytes);
    }

    /**
     * Filename token for the per-request packet: the buyer's order number
     * when present, else the surrogate id. Collapses anything outside a
     * conservative allowlist (letters, digits, dot, dash, underscore) to
     * {@code _} so a quote / CR-LF / path separator in an order number
     * cannot corrupt the download name before {@link ContentDisposition}
     * quotes it.
     */
    private static String filenameToken(CreditRequest cr) {
        String order = cr.getOrderNumber();
        if (order != null && !order.isBlank()) {
            String cleaned = order.trim().replaceAll("[^A-Za-z0-9._-]", "_");
            if (!cleaned.isBlank()) {
                return cleaned;
            }
        }
        return String.valueOf(cr.getId());
    }

    // -------------------------------------------------------------------
    // POST /{id}/send-accounting-email  —  manual accounting notification
    // -------------------------------------------------------------------

    /**
     * Manually emails the accounting distribution list the sales-approved
     * summary for one credit request — the modern port of legacy
     * {@code ACT_SendCreditRequestAccountingEmail} (template
     * {@code CreditRequestSalesApproved}), gap 2.5 Task 4. An explicit admin
     * action, NOT auto-fired on approval.
     *
     * <p>Synchronous: the admin gets the sent/failed outcome back
     * ({@code {success, logId, status}}, mirroring the unified email
     * {@code send-test}). {@link EmailService#sendTemplated} records the
     * transport outcome on the {@code email.log} row and does not rethrow a
     * transport failure, so a delivery failure still returns {@code 200} with
     * {@code success=false} / {@code status=FAILED}.
     *
     * <p><b>Authz:</b> the class-level {@code @PreAuthorize} + the
     * {@code /api/v1/admin/partial-credit/**} SecurityConfig matcher, tightened
     * here to {@code SalesOps}/{@code Administrator} at the method level
     * (defense-in-depth) since this is an outbound-email trigger, not a
     * read/review action.
     *
     * <p><b>Rate limit:</b> user-keyed via {@link UploadRateLimiter}, checked
     * FIRST (before loading anything) — same treatment as the email admin's
     * {@code send-test}/{@code resend}. Denied → {@code 429}.
     *
     * <p><b>Outcomes:</b> {@code 200} sent; {@code 409} when the request is not
     * APPROVED or when no recipients are configured (both via
     * {@code IllegalStateException} → the class 409 handler); {@code 404} when
     * the request is missing.
     */
    @PostMapping("/{id}/send-accounting-email")
    @PreAuthorize("hasAnyRole('SalesOps','Administrator')")
    public ResponseEntity<AccountingEmailResult> sendAccountingEmail(
            @PathVariable Long id, Authentication auth) {
        if (!uploadRateLimiter.tryAcquire(ACCOUNTING_EMAIL_RATE_LIMIT_PREFIX + principalUserId(auth))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        EmailLog sent = accountingEmailService.sendAccountingEmail(id);
        boolean success = sent.getStatus() == EmailStatus.SENT;
        return ResponseEntity.ok(
                new AccountingEmailResult(success, sent.getId(), sent.getStatus().name()));
    }

    /** {@code POST /{id}/send-accounting-email} response body. */
    public record AccountingEmailResult(boolean success, Long logId, String status) {
    }

    // -------------------------------------------------------------------
    // Exception handlers — mirror buyer-side controller's convention
    // -------------------------------------------------------------------

    @ExceptionHandler(CreditRequestValidationException.class)
    public ResponseEntity<Map<String, Object>> onValidationFailure(CreditRequestValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "VALIDATION_FAILED",
                "issues", ex.getIssues().stream()
                        .map(i -> Map.of("code", i.code(), "message", i.message()))
                        .toList()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> onNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage() == null ? "Resource not found" : ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> onInvalidArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "INVALID_ARGUMENT",
                "message", ex.getMessage() == null ? "Invalid argument" : ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> onConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "INVALID_STATE",
                "message", ex.getMessage() == null ? "Invalid state transition" : ex.getMessage()));
    }

    @ExceptionHandler(TooManyRowsException.class)
    public ResponseEntity<Map<String, Object>> onTooManyRows(TooManyRowsException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of(
                "error", "too_many_rows",
                "limit", ex.limit(),
                "matched", ex.matched()));
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got "
                + (principal == null ? "null" : principal.getClass()));
    }

    private Map<Long, CreditRequestStatus> loadStatusesById() {
        Map<Long, CreditRequestStatus> map = new HashMap<>();
        for (CreditRequestStatus row : statusRepository.findAll()) {
            map.put(row.getId(), row);
        }
        return map;
    }

    private Map<Long, String> loadBuyerCodesById(List<CreditRequest> rows) {
        Map<Long, String> map = new HashMap<>();
        for (CreditRequest cr : rows) {
            if (cr.getBuyerCodeId() != null && !map.containsKey(cr.getBuyerCodeId())) {
                buyerCodeRepository.findById(cr.getBuyerCodeId())
                        .map(BuyerCode::getCode)
                        .ifPresent(code -> map.put(cr.getBuyerCodeId(), code));
            }
        }
        return map;
    }
}
