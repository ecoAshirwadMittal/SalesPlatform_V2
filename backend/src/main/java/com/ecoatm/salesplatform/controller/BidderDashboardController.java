package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidImportResult;
import com.ecoatm.salesplatform.dto.BidSubmissionResult;
import com.ecoatm.salesplatform.dto.BidderDashboardResponse;
import com.ecoatm.salesplatform.dto.CarryoverResult;
import com.ecoatm.salesplatform.dto.SaveBidRequest;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.biddata.BidCarryoverService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataCreationService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataSubmissionService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidExportService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidImportService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidRateLimiter;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardLandingResult;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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
    private final BidCarryoverService carryoverService;
    private final BidExportService exportService;
    private final BidImportService importService;
    private final BidRateLimiter rateLimiter;
    private final BidRoundRepository bidRoundRepo;
    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;

    public BidderDashboardController(BidderDashboardService dashboardService,
                                      BidDataCreationService creationService,
                                      BidDataSubmissionService submissionService,
                                      BidCarryoverService carryoverService,
                                      BidExportService exportService,
                                      BidImportService importService,
                                      BidRateLimiter rateLimiter,
                                      BidRoundRepository bidRoundRepo,
                                      SchedulingAuctionRepository saRepo,
                                      AuctionRepository auctionRepo) {
        this.dashboardService = dashboardService;
        this.creationService = creationService;
        this.submissionService = submissionService;
        this.carryoverService = carryoverService;
        this.exportService = exportService;
        this.importService = importService;
        this.rateLimiter = rateLimiter;
        this.bidRoundRepo = bidRoundRepo;
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
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

    /**
     * Carryover endpoint — copies the previous week's submitted bid values into
     * the current round for the given buyer code. Idempotent; calling it twice
     * produces identical state.
     *
     * <p>{@code buyerCodeId} is required for the same ownership-scoping reason
     * as the submit endpoint: a bid round spans all qualified buyer codes, so
     * an unscoped write would touch every buyer's rows.
     *
     * <p>Rate limiting is NOT applied here (Phase 9 scope) because carryover is
     * a coarse bulk operation — at most one call per page load makes sense, and
     * the advisory lock inside the service already serialises concurrent calls.
     * TODO(Phase 9 follow-up): evaluate whether a per-(user, round) rate limit
     * is warranted once real-world usage is observed.
     */
    @PostMapping("/bid-rounds/{id}/carryover")
    public ResponseEntity<CarryoverResult> carryover(@PathVariable long id,
                                                      @RequestParam long buyerCodeId,
                                                      Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(carryoverService.carryover(userId, id, buyerCodeId));
    }

    /**
     * Export the current bid slice as an xlsx file.
     *
     * <p>{@code GET /api/v1/bidder/bid-rounds/{id}/export?buyerCodeId=…}
     *
     * <p>Streams a {@code Content-Disposition: attachment} xlsx whose filename
     * follows the pattern
     * {@code Bids_{auctionTitle}_R{round}_{buyerCode}.xlsx}.
     * Column order is identical to the grid (Product Id / Brand / Model /
     * Model Name / Grade / Carrier / Added / Avail. Qty / Target Price /
     * Price / Qty. Cap / Id).
     *
     * <p>Uses {@link BidExportService} which internally uses
     * {@code SXSSFWorkbook} (streaming writer) so even a 500-row export stays
     * well under 5 MB.
     */
    @GetMapping("/bid-rounds/{id}/export")
    public void export(@PathVariable long id,
                       @RequestParam long buyerCodeId,
                       Authentication auth,
                       HttpServletResponse response) throws IOException {
        long userId = (Long) auth.getPrincipal();

        // Build a human-readable filename: Bids_{title}_R{round}_{buyerCode}.xlsx
        String filename = resolveExportFilename(id, buyerCodeId);
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename
                + "\"; filename*=UTF-8''" + encoded);

        exportService.export(id, buyerCodeId, response.getOutputStream());
    }

    /**
     * DOWNLOAD-mode helper: stream the xlsx for the most recently closed
     * Round 1 this buyer participated in. Used by the bidder dashboard's
     * {@code DOWNLOAD} mode, where the response lacks an explicit
     * {@code bidRoundId} (auction/bidRound slots are null). Returns
     * {@code 404 Not Found} when no closed R1 bid_round exists for this
     * buyer.
     *
     * <p>{@code GET /api/v1/bidder/download-round-1?buyerCodeId=…}
     */
    @GetMapping("/download-round-1")
    public void downloadRound1(@RequestParam long buyerCodeId,
                               Authentication auth,
                               HttpServletResponse response) throws IOException {
        long userId = (Long) auth.getPrincipal();
        Optional<Long> bidRoundIdOpt = dashboardService.findDownloadableRound1BidRoundId(userId, buyerCodeId);
        if (bidRoundIdOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No closed Round 1 available for download");
            return;
        }
        long bidRoundId = bidRoundIdOpt.get();

        String filename = resolveExportFilename(bidRoundId, buyerCodeId);
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename
                + "\"; filename*=UTF-8''" + encoded);

        exportService.export(bidRoundId, buyerCodeId, response.getOutputStream());
    }

    /**
     * Import a bid xlsx file uploaded by the bidder.
     *
     * <p>{@code POST /api/v1/bidder/bid-rounds/{id}/import?buyerCodeId=…}
     *
     * <p>Accepts {@code multipart/form-data} with a single {@code file} part.
     * Validates the file, then applies all updates in one transaction. Returns
     * {@code 200} with a {@link BidImportResult} on success; returns
     * {@code 200} with a non-empty {@code errors} array if validation fails
     * (no rows are written on validation failure).
     *
     * <p>One import consumes one token from the per-{@code (user, round)}
     * rate-limiter bucket (same 60/min cap as individual cell saves).
     */
    @PostMapping(value = "/bid-rounds/{id}/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BidImportResult> importBids(
            @PathVariable long id,
            @RequestParam long buyerCodeId,
            @RequestParam MultipartFile file,
            Authentication auth) throws IOException {

        long userId = (Long) auth.getPrincipal();

        // Rate-limit import using the same (user, round) bucket as saves.
        if (!rateLimiter.tryAcquire(userId, id)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        BidImportResult result = importService.importBids(userId, id, buyerCodeId, file);
        return ResponseEntity.ok(result);
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    /**
     * Resolves the export filename by loading the bid round → scheduling auction
     * → auction + buyer code. Gracefully falls back to a generic name if any
     * lookup fails so the export is never blocked by a missing FK.
     */
    private String resolveExportFilename(long bidRoundId, long buyerCodeId) {
        try {
            BidRound br = bidRoundRepo.findById(bidRoundId).orElse(null);
            if (br == null) return "Bids.xlsx";

            SchedulingAuction sa = saRepo.findById(br.getSchedulingAuctionId()).orElse(null);
            if (sa == null) return "Bids.xlsx";

            Auction auction = auctionRepo.findById(sa.getAuctionId()).orElse(null);
            String title = auction != null ? sanitizeFilename(auction.getAuctionTitle()) : "Auction";
            int round = sa.getRound();

            // Buyer code text — fall back to the id if lookup fails
            String buyerCode = "BC" + buyerCodeId;

            return "Bids_" + title + "_R" + round + "_" + buyerCode + ".xlsx";
        } catch (Exception e) {
            return "Bids.xlsx";
        }
    }

    /** Strip characters that are illegal in most filesystems. */
    private static String sanitizeFilename(String raw) {
        if (raw == null) return "Auction";
        return raw.replaceAll("[/\\\\:*?\"<>|]", "_").trim();
    }
}
