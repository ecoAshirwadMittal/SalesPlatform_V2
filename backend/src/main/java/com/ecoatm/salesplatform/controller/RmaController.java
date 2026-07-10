package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.service.RmaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PWS RMA (return) surface — mixed buyer + sales.
 *
 * <p><b>Security (review 2026-07-10, CR-3/C6):</b> the review/mutation actions
 * (approve / decline / update-item / complete-review) are <b>internal-only</b> —
 * a buyer must never be able to finalize their own return. Reads are scoped: a
 * buyer may only see RMAs for buyer codes they own; the "all" list/summary view
 * is internal-only. {@code submitRma} derives userId from the JWT (not a request
 * param) and verifies buyer-code ownership. Previously the whole surface fell
 * through to bare authentication with client-supplied userId/buyerCodeId.
 */
@RestController
@RequestMapping("/api/v1/pws/rma")
@PreAuthorize("hasAnyRole('Bidder','SalesRep','SalesOps','Administrator')")
public class RmaController {

    private static final Set<String> INTERNAL_ROLES =
            Set.of("ROLE_Administrator", "ROLE_SalesOps", "ROLE_SalesRep");

    /** 10 MB upload ceiling, mirroring {@code PricingController.MAX_UPLOAD_SIZE}. */
    private static final long MAX_UPLOAD_SIZE = 10 * 1024 * 1024;

    private final RmaService rmaService;
    private final BuyerCodeService buyerCodeService;

    public RmaController(RmaService rmaService, BuyerCodeService buyerCodeService) {
        this.rmaService = rmaService;
        this.buyerCodeService = buyerCodeService;
    }

    /** List RMAs. Buyers see only their own buyer code; the all-view is internal. */
    @GetMapping
    public ResponseEntity<List<RmaResponse>> getRmas(
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(required = false) String status,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (buyerCodeId != null) {
            if (!buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(rmaService.getRmasByBuyerCode(buyerCodeId, status));
        }
        if (!hasInternalRole(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rmaService.getAllRmas(status));
    }

    /** Status summary cards. Buyers see only their own buyer code; all-view is internal. */
    @GetMapping("/summary")
    public ResponseEntity<List<RmaSummaryResponse>> getSummary(
            @RequestParam(required = false) Long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (buyerCodeId != null) {
            if (!buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(rmaService.getSummary(buyerCodeId));
        }
        if (!hasInternalRole(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rmaService.getAllSummary());
    }

    /** Get available return reasons for RMA submission. */
    @GetMapping("/reasons")
    public ResponseEntity<List<RmaReasonResponse>> getReasons() {
        return ResponseEntity.ok(rmaService.getReturnReasons());
    }

    /** Download CSV template for RMA submission. */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String csv = "IMEI/Serial,Return Reason\n";
        byte[] content = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RMA_Template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(content.length)
                .body(content);
    }

    /** Submit an RMA request with a CSV file upload. userId comes from the JWT. */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RmaSubmitResponse> submitRma(
            @RequestParam Long buyerCodeId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        long userId = (Long) auth.getPrincipal();
        if (!buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(RmaSubmitResponse.failure(List.of("No file uploaded.")));
        }
        // M-6 (security review 2026-07-10): size + content-type allowlist,
        // mirroring PricingController.uploadPricingCsv. A missing Content-Type
        // is a rejection, not a bypass.
        if (file.getSize() > MAX_UPLOAD_SIZE) {
            return ResponseEntity.badRequest()
                    .body(RmaSubmitResponse.failure(List.of("File exceeds maximum size of 10 MB.")));
        }
        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equals("text/csv")
                        && !contentType.equals("text/plain")
                        && !contentType.equals("application/vnd.ms-excel"))) {
            return ResponseEntity.badRequest()
                    .body(RmaSubmitResponse.failure(List.of("File must be a CSV (text/csv).")));
        }
        RmaSubmitResponse response = rmaService.submitRmaRequest(buyerCodeId, userId, file.getInputStream());
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /** Get RMA detail. Scoped to the RMA's owning buyer code. */
    @GetMapping("/{rmaId}")
    public ResponseEntity<RmaDetailResponse> getRmaDetail(
            @PathVariable Long rmaId, Authentication auth) {
        if (!ownsRma((Long) auth.getPrincipal(), rmaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rmaService.getRmaDetail(rmaId));
    }

    // --- Internal (sales) review actions — buyers must never reach these ---

    /** Approve all items in an RMA. */
    @PutMapping("/{rmaId}/items/approve-all")
    @PreAuthorize("hasAnyRole('Administrator','SalesOps','SalesRep')")
    public ResponseEntity<RmaDetailResponse> approveAll(@PathVariable Long rmaId) {
        return ResponseEntity.ok(rmaService.approveAllItems(rmaId));
    }

    /** Decline all items in an RMA. */
    @PutMapping("/{rmaId}/items/decline-all")
    @PreAuthorize("hasAnyRole('Administrator','SalesOps','SalesRep')")
    public ResponseEntity<RmaDetailResponse> declineAll(@PathVariable Long rmaId) {
        return ResponseEntity.ok(rmaService.declineAllItems(rmaId));
    }

    /** Update a single item's status (Approve/Decline). */
    @PutMapping("/items/{itemId}/status")
    @PreAuthorize("hasAnyRole('Administrator','SalesOps','SalesRep')")
    public ResponseEntity<RmaDetailResponse> updateItemStatus(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(rmaService.updateItemStatus(itemId, body.get("status")));
    }

    /** Complete review — finalize the RMA. Reviewer id comes from the JWT. */
    @PutMapping("/{rmaId}/complete-review")
    @PreAuthorize("hasAnyRole('Administrator','SalesOps','SalesRep')")
    public ResponseEntity<RmaDetailResponse> completeReview(
            @PathVariable Long rmaId, Authentication auth) {
        long reviewerId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(rmaService.completeReview(rmaId, reviewerId));
    }

    private boolean hasInternalRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(INTERNAL_ROLES::contains);
    }

    /**
     * The caller must own the buyer code the RMA belongs to. A non-existent RMA
     * passes here so the service returns its normal not-found response.
     */
    private boolean ownsRma(long userId, Long rmaId) {
        Long buyerCodeId = rmaService.getRmaBuyerCodeId(rmaId);
        return buyerCodeId == null
                || buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }
}
