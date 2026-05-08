package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.R2QualifiedBuyersResponse;
import com.ecoatm.salesplatform.service.admin.R2QualifiedBuyersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only admin endpoint backing the R2 Qualified Buyer Codes grid
 * (gap H10). The QBC criteria editor already exists; this surfaces the
 * RESULT of running the R2 buyer-assignment service against those criteria
 * for a specific auction so SalesOps can audit the qualification outcome.
 *
 * <p>RBAC: Administrator + SalesOps (matches sibling read-only admin
 * endpoints on this surface).
 */
@RestController
@RequestMapping("/api/v1/admin/auctions")
public class R2QualifiedBuyersController {

    private final R2QualifiedBuyersService service;

    public R2QualifiedBuyersController(R2QualifiedBuyersService service) {
        this.service = service;
    }

    @GetMapping("/{auctionId}/r2-qualified-buyers")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public R2QualifiedBuyersResponse list(@PathVariable("auctionId") Long auctionId) {
        return service.findByAuction(auctionId);
    }
}
