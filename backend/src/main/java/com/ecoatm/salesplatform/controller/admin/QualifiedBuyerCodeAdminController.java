package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminListResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminRow;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeUpdateRequest;
import com.ecoatm.salesplatform.service.admin.QualifiedBuyerCodeAdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * P8 Lane 3B — admin Qualified Buyer Codes view + manual qualify/unqualify.
 *
 * <p>Class-level {@code @PreAuthorize} grants Administrator + SalesOps in
 * line with the cross-cutting note in the P8 plan; the SecurityConfig
 * matcher widens {@code /api/v1/admin/qualified-buyer-codes/**} to both
 * roles.
 */
@RestController
@RequestMapping("/api/v1/admin/qualified-buyer-codes")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class QualifiedBuyerCodeAdminController {

    private final QualifiedBuyerCodeAdminService service;

    public QualifiedBuyerCodeAdminController(QualifiedBuyerCodeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public QualifiedBuyerCodeAdminListResponse list(
            @RequestParam long schedulingAuctionId) {
        return service.list(schedulingAuctionId);
    }

    @PatchMapping("/{id}")
    public QualifiedBuyerCodeAdminRow updateIncluded(
            @PathVariable long id,
            @RequestBody @Valid QualifiedBuyerCodeUpdateRequest body) {
        return service.updateIncluded(id, body.included(), userId());
    }

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
