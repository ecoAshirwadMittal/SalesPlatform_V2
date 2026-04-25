package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodePageResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeRow;
import com.ecoatm.salesplatform.service.buyermgmt.QualifiedBuyerCodeAdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin surface for Qualified Buyer Codes. Exposes list and manual-qualify
 * operations. The PATCH sets {@code qualification_type = 'Manual'} to let
 * admins override automatic R1 qualification decisions.
 */
@RestController
@RequestMapping("/api/v1/admin/qualified-buyer-codes")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class QualifiedBuyerCodeAdminController {

    private final QualifiedBuyerCodeAdminService qbcAdminService;

    public QualifiedBuyerCodeAdminController(QualifiedBuyerCodeAdminService qbcAdminService) {
        this.qbcAdminService = qbcAdminService;
    }

    @GetMapping
    public QualifiedBuyerCodePageResponse list(
            @RequestParam(required = false) Long schedulingAuctionId,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return qbcAdminService.list(schedulingAuctionId, buyerCodeId, page, pageSize);
    }

    /**
     * Manually qualify a QBC row — sets qualification_type to 'Manual'.
     * No request body is needed; the action is always the same.
     */
    @PatchMapping("/{id}/qualify")
    public QualifiedBuyerCodeRow qualify(@PathVariable long id) {
        return qbcAdminService.qualify(id);
    }
}
