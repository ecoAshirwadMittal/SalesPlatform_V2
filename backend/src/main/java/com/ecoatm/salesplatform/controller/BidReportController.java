package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BidReportPageResponse;
import com.ecoatm.salesplatform.service.auctions.BidReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-facing read-only surface for bid reports. Currently exposes the
 * Round 3 bid report (ported from Mendix ACT_BidDataDoc_ExportExcel R3 logic).
 */
@RestController
@RequestMapping("/api/v1/admin/bid-reports")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class BidReportController {

    private final BidReportService bidReportService;

    public BidReportController(BidReportService bidReportService) {
        this.bidReportService = bidReportService;
    }

    /**
     * Paginated R3 bid report. Optional {@code auctionId} scopes the result
     * to a single auction; omitting it returns all Round 3 bid data across
     * all auctions (large — callers should always supply a filter in practice).
     */
    @GetMapping("/r3")
    public BidReportPageResponse getR3Report(
            @RequestParam(required = false) Long auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return bidReportService.getR3Report(auctionId, page, pageSize);
    }
}
