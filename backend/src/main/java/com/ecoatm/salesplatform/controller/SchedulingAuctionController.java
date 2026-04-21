package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.SchedulingAuctionListPageResponse;
import com.ecoatm.salesplatform.service.auctions.AuctionListService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only surface backing the admin Scheduling Auctions grid
 * (QA parity with {@code Mx_Admin.Pages.Scheduling_Auctions}).
 *
 * <p>Writes to {@code auctions.scheduling_auctions} remain on
 * {@link com.ecoatm.salesplatform.controller.AuctionController#saveSchedule}
 * and {@code unschedule}; this controller does not expose a write path.
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling-auctions")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class SchedulingAuctionController {

    private final AuctionListService auctionListService;

    public SchedulingAuctionController(AuctionListService auctionListService) {
        this.auctionListService = auctionListService;
    }

    @GetMapping
    public SchedulingAuctionListPageResponse list(
            @RequestParam(required = false) Long auctionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String weekDisplay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return auctionListService.searchSchedulingAuctions(
                auctionId, status, weekDisplay, page, pageSize);
    }
}
