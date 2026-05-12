package com.ecoatm.salesplatform.controller.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.BuyerCodeOption;
import com.ecoatm.salesplatform.dto.partialcredit.BuyerUserOption;
import com.ecoatm.salesplatform.dto.partialcredit.CreateOnBehalfRequest;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.OnBehalfSubmissionService;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Sales-rep on-behalf submission surface (Sprint 4 chunk 6 — SPKB-3659).
 * Three endpoints — pick buyer code, pick buyer user, create the
 * stamped draft — all gated to {@code SalesRep} or {@code Administrator}.
 *
 * <p>The created draft hands the rep into the standard buyer wizard via
 * the existing {@code /api/v1/buyer/partial-credit/{id}/*} endpoints;
 * those endpoints now accept the {@code SalesRep} role too (Chunk 6
 * widens the buyer-controller allowlist).
 */
@RestController
@RequestMapping("/api/v1/salesrep/partial-credit")
@PreAuthorize("hasAnyRole('SalesRep', 'Administrator')")
public class OnBehalfPartialCreditController {

    private final OnBehalfSubmissionService onBehalfService;
    private final CreditRequestService creditRequestService;

    public OnBehalfPartialCreditController(
            OnBehalfSubmissionService onBehalfService,
            CreditRequestService creditRequestService) {
        this.onBehalfService = onBehalfService;
        this.creditRequestService = creditRequestService;
    }

    @GetMapping("/buyer-codes")
    public ResponseEntity<List<BuyerCodeOption>> listEligibleBuyerCodes(Authentication auth) {
        return ResponseEntity.ok(onBehalfService.listEligibleBuyerCodes(principalUserId(auth)));
    }

    @GetMapping("/buyer-codes/{codeId}/users")
    public ResponseEntity<List<BuyerUserOption>> listBuyersForCode(
            @PathVariable Long codeId, Authentication auth) {
        return ResponseEntity.ok(onBehalfService.listBuyersForCode(principalUserId(auth), codeId));
    }

    @PostMapping("/drafts")
    public ResponseEntity<CreditRequestDetail> createDraftOnBehalf(
            @RequestBody CreateOnBehalfRequest body, Authentication auth) {
        Long salesRepUserId = principalUserId(auth);
        CreditRequest draft = onBehalfService.createDraftOnBehalf(
                salesRepUserId,
                body.orderNumber(),
                body.buyerCodeId(),
                body.onBehalfOfUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetail(draft));
    }

    // ── exception handlers ────────────────────────────────────────────

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> onForbidden(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "FORBIDDEN",
                "message", ex.getMessage() == null ? "Not allowed" : ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> onBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "INVALID_ARGUMENT",
                "message", ex.getMessage() == null ? "Invalid argument" : ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> onNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage() == null ? "Resource not found" : ex.getMessage()));
    }

    // ── helpers ───────────────────────────────────────────────────────

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got "
                + (principal == null ? "null" : principal.getClass()));
    }

    private CreditRequestDetail toDetail(CreditRequest cr) {
        CreditRequestStatus statusRow = creditRequestService.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        return CreditRequestDetail.from(
                cr,
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                creditRequestService.getMissingLines(cr.getId()),
                creditRequestService.getWrongLines(cr.getId()),
                creditRequestService.getEncumberedLines(cr.getId()));
    }
}
