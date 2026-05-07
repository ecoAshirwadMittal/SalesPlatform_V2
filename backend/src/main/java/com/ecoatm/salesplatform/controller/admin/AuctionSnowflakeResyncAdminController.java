package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService.AuctionSnowflakeResyncResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin recovery endpoint: force a Snowflake re-push of bid ranks + target prices
 * for all closed rounds of an auction.
 *
 * <p>Ports Mendix {@code ACT_Auction_SendAllBidsToSnowflake_Admin}. Sits at
 * {@code /api/v1/admin/auctions/{auctionId}/resync-snowflake} — different URL
 * scope from the scheduling-auction-scoped endpoints in
 * {@link RecalcAdminController} ({@code /scheduling-auctions/*}), which is why
 * this lives in its own controller rather than being added there.
 *
 * <p>Already covered by the catch-all
 * {@code /api/v1/admin/auctions/**} → {@code hasAnyRole("Administrator", "SalesOps")}
 * matcher in {@code SecurityConfig}; {@code @PreAuthorize} adds an explicit
 * Administrator-only gate to match the Mendix role guard.
 */
@RestController
@RequestMapping("/api/v1/admin/auctions")
public class AuctionSnowflakeResyncAdminController {

    private final AuctionSnowflakeResyncService resyncService;

    public AuctionSnowflakeResyncAdminController(AuctionSnowflakeResyncService resyncService) {
        this.resyncService = resyncService;
    }

    /**
     * Triggers a full Snowflake re-push for the auction's closed rounds.
     *
     * <p>For each closed round R in {1, 2}, calls both
     * {@code BidRankingSnowflakeWriter.pushBidRankings(weekId, R+1)} and
     * {@code TargetPriceSnowflakeWriter.pushTargetPrices(weekId, R+1)}.
     * R3 close is skipped (no R4 target round). Per-round writer failures are
     * isolated — one failure does not abort subsequent rounds.
     *
     * @param auctionId the auction to resync
     * @return per-round outcomes, success/failure counts, and duration
     * @throws com.ecoatm.salesplatform.exception.EntityNotFoundException HTTP 404 when auction not found
     * @throws IllegalStateException HTTP 409 when the auction has no weekId
     */
    @PostMapping("/{auctionId}/resync-snowflake")
    @PreAuthorize("hasRole('Administrator')")
    public AuctionSnowflakeResyncResult resyncSnowflake(@PathVariable long auctionId) {
        return resyncService.resync(auctionId);
    }
}
