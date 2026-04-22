package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidSubmissionResult;
import com.ecoatm.salesplatform.dto.BidderDashboardResponse;
import com.ecoatm.salesplatform.dto.SaveBidRequest;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataCreationService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataSubmissionService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidRateLimiter;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardLandingResult;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bidder-facing REST surface ported from Mendix
 * {@code ACT_OpenBidderDashboard} + {@code ACT_CreateBidData}. Three
 * verbs:
 *
 * <ul>
 *   <li>{@code GET /dashboard?buyerCodeId=…} — landing-route + grid load.
 *       Mendix-parity: routes to GRID, DOWNLOAD (Round 2 inventory),
 *       ALL_ROUNDS_DONE, or ERROR_AUCTION_NOT_FOUND. The GRID branch also
 *       lazily materializes {@code auctions.bid_data} rows for the
 *       selected (buyer_code, bid_round) pair.</li>
 *   <li>{@code PUT /bid-data/{id}} — single-row save with per-(user, round)
 *       rate-limit (60/min). Returns 429 when the bucket is exhausted.</li>
 *   <li>{@code POST /bid-rounds/{id}/submit} — re-callable submit; copies
 *       {@code bid_*} → {@code submitted_*} and prior {@code submitted_*}
 *       → {@code last_valid_*} for every row of the given round + buyer
 *       code.</li>
 * </ul>
 *
 * <p>Class-level {@code @PreAuthorize} grants Bidder + Administrator. The
 * Administrator bypass mirrors the existing admin-write pattern in
 * {@link AuctionController}: an Administrator can act on behalf of any
 * bidder for diagnostic / recovery purposes. The
 * {@link com.ecoatm.salesplatform.security.SecurityConfig} matcher at
 * {@code /api/v1/bidder/**} must agree with this annotation, otherwise
 * the filter chain would short-circuit before method security runs.
 *
 * <p><b>Principal shape:</b> {@code auth.getPrincipal()} is a {@code Long}
 * (user id) and {@code auth.getCredentials()} is the email
 * ({@code String}) — see
 * {@link com.ecoatm.salesplatform.security.JwtAuthenticationFilter}.
 */
@RestController
@RequestMapping("/api/v1/bidder")
@PreAuthorize("hasAnyRole('Bidder','Administrator')")
public class BidderDashboardController {

    private final BidderDashboardService dashboardService;
    private final BidDataCreationService creationService;
    private final BidDataSubmissionService submissionService;
    private final BidRateLimiter rateLimiter;

    public BidderDashboardController(BidderDashboardService dashboardService,
                                      BidDataCreationService creationService,
                                      BidDataSubmissionService submissionService,
                                      BidRateLimiter rateLimiter) {
        this.dashboardService = dashboardService;
        this.creationService = creationService;
        this.submissionService = submissionService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<BidderDashboardResponse> dashboard(
            @RequestParam long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        BidderDashboardLandingResult landing = dashboardService.landingRoute(userId, buyerCodeId);
        return switch (landing) {
            case BidderDashboardLandingResult.Grid g -> {
                creationService.ensureRowsExist(buyerCodeId, g.bidRoundId(), userId);
                yield ResponseEntity.ok(dashboardService.loadGrid(g.bidRoundId(), buyerCodeId));
            }
            case BidderDashboardLandingResult.Download d -> ResponseEntity.ok(
                    new BidderDashboardResponse("DOWNLOAD", null, null, List.of(), null, null));
            case BidderDashboardLandingResult.AllDone __ -> ResponseEntity.ok(
                    new BidderDashboardResponse("ALL_ROUNDS_DONE", null, null, List.of(), null, null));
            case BidderDashboardLandingResult.Error e -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BidderDashboardResponse("ERROR_AUCTION_NOT_FOUND", null, null, List.of(), null, null));
        };
    }

    @PutMapping("/bid-data/{id}")
    public ResponseEntity<BidDataRow> save(@PathVariable long id,
                                            @RequestBody SaveBidRequest req,
                                            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        // Resolve the bid round id from the row so the rate limiter can scope
        // its bucket to (user, round) without trusting a client-supplied value.
        long bidRoundId = submissionService.resolveBidRoundId(id);
        if (!rateLimiter.tryAcquire(userId, bidRoundId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(submissionService.save(userId, id, req));
    }

    /**
     * Re-callable submit endpoint. The {@code buyerCodeId} query param is
     * required because a bid round contains rows for every qualified buyer
     * code; without scoping the UPDATE by {@code buyer_code_id} a submit by
     * buyer A would silently flip rows for buyer B (see
     * {@link BidDataSubmissionService#submit}).
     */
    @PostMapping("/bid-rounds/{id}/submit")
    public ResponseEntity<BidSubmissionResult> submit(@PathVariable long id,
                                                       @RequestParam long buyerCodeId,
                                                       Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        String username = (String) auth.getCredentials();
        return ResponseEntity.ok(submissionService.submit(userId, username, id, buyerCodeId));
    }
}
