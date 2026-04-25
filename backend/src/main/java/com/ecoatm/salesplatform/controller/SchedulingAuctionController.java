package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.RoundTransitionResponse;
import com.ecoatm.salesplatform.dto.SchedulingAuctionListPageResponse;
import com.ecoatm.salesplatform.service.auctions.AdminRoundTransitionService;
import com.ecoatm.salesplatform.service.auctions.AuctionListService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin surface for the Scheduling Auctions grid and manual round
 * transitions (QA parity with {@code Mx_Admin.Pages.Scheduling_Auctions}).
 *
 * <p>Manual start/close endpoints let admins force-transition a round
 * regardless of its wall-clock time, enabling recovery when the cron is
 * delayed or a round needs to be advanced early. Time-based guards are
 * intentionally omitted on these admin paths — see
 * {@link AdminRoundTransitionService}.
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling-auctions")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class SchedulingAuctionController {

    private final AuctionListService auctionListService;
    private final AdminRoundTransitionService adminRoundTransitionService;

    public SchedulingAuctionController(AuctionListService auctionListService,
                                       AdminRoundTransitionService adminRoundTransitionService) {
        this.auctionListService = auctionListService;
        this.adminRoundTransitionService = adminRoundTransitionService;
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

    /**
     * Force-start a scheduling auction round (Scheduled → Started).
     * Does not enforce wall-clock constraints — admin override.
     */
    @PostMapping("/{id}/start")
    public RoundTransitionResponse startRound(@PathVariable Long id) {
        return adminRoundTransitionService.startRound(id, currentActor());
    }

    /**
     * Force-close a scheduling auction round (Started → Closed).
     * Does not enforce wall-clock constraints — admin override.
     */
    @PostMapping("/{id}/close")
    public RoundTransitionResponse closeRound(@PathVariable Long id) {
        return adminRoundTransitionService.closeRound(id, currentActor());
    }

    private static String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "system";
        }
        return auth.getName();
    }
}
