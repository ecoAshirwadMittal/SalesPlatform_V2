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
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
@PreAuthorize("hasAnyRole('PartialCredit_SalesOps','PartialCredit_Admin',"
            + "'SalesOps','Administrator')")
public class AdminPartialCreditController {

    private final AdminCreditRequestService adminService;
    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestStatusRepository statusRepository;
    private final MissingDeviceLineRepository missingDeviceLineRepository;
    private final WrongDeviceLineRepository wrongDeviceLineRepository;
    private final EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    private final BuyerCodeRepository buyerCodeRepository;

    public AdminPartialCreditController(
            AdminCreditRequestService adminService,
            CreditRequestRepository creditRequestRepository,
            CreditRequestStatusRepository statusRepository,
            MissingDeviceLineRepository missingDeviceLineRepository,
            WrongDeviceLineRepository wrongDeviceLineRepository,
            EncumberedDeviceLineRepository encumberedDeviceLineRepository,
            BuyerCodeRepository buyerCodeRepository) {
        this.adminService = adminService;
        this.creditRequestRepository = creditRequestRepository;
        this.statusRepository = statusRepository;
        this.missingDeviceLineRepository = missingDeviceLineRepository;
        this.wrongDeviceLineRepository = wrongDeviceLineRepository;
        this.encumberedDeviceLineRepository = encumberedDeviceLineRepository;
        this.buyerCodeRepository = buyerCodeRepository;
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
                missingDeviceLineRepository.findByCreditRequestIdOrderById(id),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(id),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(id)));
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
                result.missingLines(),
                result.wrongLines(),
                result.encumberedLines()));
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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> onConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "INVALID_STATE",
                "message", ex.getMessage() == null ? "Invalid state transition" : ex.getMessage()));
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
