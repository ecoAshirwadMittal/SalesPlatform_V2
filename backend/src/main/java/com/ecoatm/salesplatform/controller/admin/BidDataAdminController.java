package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.BidDataAdminListResponse;
import com.ecoatm.salesplatform.service.admin.BidDataAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * P8 Lane 3A — admin Bid Data view + remove.
 *
 * <p>Class-level {@code @PreAuthorize} grants Administrator + SalesOps in
 * line with the cross-cutting note in the P8 plan. The corresponding
 * SecurityConfig matcher widens {@code /api/v1/admin/bid-data/**} to both
 * roles (the catch-all {@code /api/v1/admin/**} default is Administrator-only).
 */
@RestController
@RequestMapping("/api/v1/admin/bid-data")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class BidDataAdminController {

    private final BidDataAdminService service;

    public BidDataAdminController(BidDataAdminService service) {
        this.service = service;
    }

    @GetMapping
    public BidDataAdminListResponse list(
            @RequestParam(required = false) Long bidRoundId,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(name = "submittedBidAmountGt", required = false, defaultValue = "0")
            double submittedBidAmountGt) {
        // QA POM passes `submittedBidAmountGt=0` to mean "only rows with a
        // submitted bid > 0". Treat any positive value as the same boolean
        // toggle to keep the URL contract loose.
        return service.list(bidRoundId, buyerCodeId, submittedBidAmountGt > 0);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.softDelete(id, userId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Pulled from the security context. Mirrors the helper used in
     * {@code ReserveBidController} (referenced by the P8 plan): JWT filter
     * stores {@code userId} as the principal directly; under
     * {@code @WithMockUser} the principal is a String so we fall back to 0L.
     */
    private static long userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return 0L;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) {
            return l;
        }
        return 0L;
    }
}
