package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.partialcredit.CreateDraftRequest;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestSummary;
import com.ecoatm.salesplatform.dto.partialcredit.SetLinesRequest;
import com.ecoatm.salesplatform.dto.partialcredit.UpdateDraftRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestValidationException;
import com.ecoatm.salesplatform.service.partialcredit.ValidationIssue;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Buyer-facing REST surface for the Sprint-2 wizard. Five verbs:
 *
 * <ul>
 *   <li>{@code POST /draft} — create a {@link CreditRequest} in DRAFT,
 *       enriched from Snowflake when available</li>
 *   <li>{@code PATCH /{id}} — wizard step-to-step state save (reasons +
 *       damage Q/A)</li>
 *   <li>{@code POST /{id}/submit} — run validator, denormalise manifest
 *       fields onto each line entity, flip status to PENDING_APPROVAL</li>
 *   <li>{@code GET /{id}} — detail view</li>
 *   <li>{@code GET / ?buyerCodeId=&status=} — buyer's own request list</li>
 * </ul>
 *
 * <p><b>Role gating:</b> Class-level {@code @PreAuthorize} accepts the
 * future {@code PartialCredit_Buyer} role and the existing {@code Bidder}
 * role so today's buyer accounts can hit the surface before SPKB-3659 wires
 * the new role mapping. Administrators are admitted for diagnostic /
 * support flows. Per-buyer-code scoping happens in
 * {@link CreditRequestService} — class-level role gating is necessary but
 * not sufficient.
 */
@RestController
@RequestMapping("/api/v1/buyer/partial-credit")
@PreAuthorize("hasAnyRole('PartialCredit_Buyer','Bidder','Administrator')")
public class BuyerPartialCreditController {

    private final CreditRequestService service;

    public BuyerPartialCreditController(CreditRequestService service) {
        this.service = service;
    }

    @PostMapping("/draft")
    public ResponseEntity<CreditRequestDetail> createDraft(
            @RequestBody CreateDraftRequest body, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.createDraft(body.orderNumber(), body.buyerCodeId(), userId, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetail(cr));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CreditRequestDetail> update(
            @PathVariable Long id,
            @RequestBody UpdateDraftRequest body,
            Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.update(
                id, userId, admin,
                body.hasMissingDevice(), body.hasWrongDevice(), body.hasEncumberedDevice(),
                body.shipmentDamaged());
        return ResponseEntity.ok(toDetail(cr));
    }

    @PostMapping("/{id}/missing-lines")
    public ResponseEntity<CreditRequestDetail> setMissingLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        service.replaceMissingLines(id, principalUserId(auth), isAdmin(auth),
                body.barcodes() == null ? List.of() : body.barcodes());
        return ResponseEntity.ok(toDetail(service.getById(id, principalUserId(auth), isAdmin(auth))));
    }

    @PostMapping("/{id}/wrong-lines")
    public ResponseEntity<CreditRequestDetail> setWrongLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        service.replaceWrongLines(id, principalUserId(auth), isAdmin(auth),
                body.wrongLines() == null ? List.of() : body.wrongLines());
        return ResponseEntity.ok(toDetail(service.getById(id, principalUserId(auth), isAdmin(auth))));
    }

    @PostMapping("/{id}/encumbered-lines")
    public ResponseEntity<CreditRequestDetail> setEncumberedLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        service.replaceEncumberedLines(id, principalUserId(auth), isAdmin(auth),
                body.barcodes() == null ? List.of() : body.barcodes());
        return ResponseEntity.ok(toDetail(service.getById(id, principalUserId(auth), isAdmin(auth))));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<CreditRequestDetail> submit(@PathVariable Long id, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.submit(id, userId, admin);
        return ResponseEntity.ok(toDetail(cr));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditRequestDetail> getById(@PathVariable Long id, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.getById(id, userId, admin);
        return ResponseEntity.ok(toDetail(cr));
    }

    @GetMapping
    public ResponseEntity<List<CreditRequestSummary>> list(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) SystemStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        Pageable pageable = PageRequest.of(page, Math.min(size, 200));
        Page<CreditRequest> rows = service.listForBuyerCode(buyerCodeId, userId, admin, pageable);
        // Status filter is applied in-memory for now — the typical buyer
        // landing has at most a few dozen rows. If list sizes grow we
        // push the filter into the repo via a dedicated finder.
        List<CreditRequestSummary> body = rows.stream()
                .filter(cr -> status == null || matchesStatus(cr, status))
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(body);
    }

    // ─── exception handlers ────────────────────────────────────────────

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

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> onForbidden(SecurityException ex) {
        // Don't leak buyer_code_id collisions across tenants in the body.
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "FORBIDDEN"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> onConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "INVALID_STATE",
                "message", ex.getMessage() == null ? "Invalid state transition" : ex.getMessage()));
    }

    // ─── helpers ───────────────────────────────────────────────────────

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got " + principal.getClass());
    }

    private static boolean isAdmin(Authentication auth) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_Administrator".equals(a.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private CreditRequestDetail toDetail(CreditRequest cr) {
        CreditRequestStatus statusRow = service.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        return CreditRequestDetail.from(
                cr,
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                service.getMissingLines(cr.getId()),
                service.getWrongLines(cr.getId()),
                service.getEncumberedLines(cr.getId()));
    }

    private CreditRequestSummary toSummary(CreditRequest cr) {
        CreditRequestStatus statusRow = service.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        return CreditRequestSummary.from(cr, statusRow.getSystemStatus(), statusRow.getExternalStatusText());
    }

    private boolean matchesStatus(CreditRequest cr, SystemStatus target) {
        return service.findStatusRow(cr.getStatusId())
                .map(row -> row.getSystemStatus() == target)
                .orElse(false);
    }
}
