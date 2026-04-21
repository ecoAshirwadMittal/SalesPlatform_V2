package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AuctionDetailResponse;
import com.ecoatm.salesplatform.dto.AuctionListPageResponse;
import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterRequest;
import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterResponse;
import com.ecoatm.salesplatform.dto.CreateAuctionRequest;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.dto.ScheduleAuctionRequest;
import com.ecoatm.salesplatform.dto.ScheduleDefaultsResponse;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.AuctionListService;
import com.ecoatm.salesplatform.service.auctions.AuctionScheduleService;
import com.ecoatm.salesplatform.service.auctions.AuctionService;
import com.ecoatm.salesplatform.service.auctions.BidRoundSelectionFilterService;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationResult;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

/**
 * Admin-facing REST surface for the auction lifecycle. Covers the Create-,
 * Schedule-, Unschedule-, and Delete-auction flows ported from Mendix
 * {@code ACT_Create_Auction}, {@code ACT_SaveScheduleAuction},
 * {@code ACT_UnscheduleAuction}, and {@code ACT_Delete_AuctionFromAdmin}.
 *
 * <p>Class-level {@code @PreAuthorize} covers the common
 * Administrator/SalesOps grant; {@link #delete(Long)} narrows to
 * Administrator only (Mendix parity — delete was restricted to Admin in the
 * legacy UI). The explicit SecurityConfig rule at the filter chain still
 * applies first; method security runs after.
 */
@RestController
@RequestMapping("/api/v1/admin/auctions")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionScheduleService auctionScheduleService;
    private final BidRoundSelectionFilterService bidRoundSelectionFilterService;
    private final AuctionListService auctionListService;
    private final Round1InitializationService round1InitializationService;
    private final SchedulingAuctionRepository schedulingAuctionRepository;

    public AuctionController(AuctionService auctionService,
                             AuctionScheduleService auctionScheduleService,
                             BidRoundSelectionFilterService bidRoundSelectionFilterService,
                             AuctionListService auctionListService,
                             Round1InitializationService round1InitializationService,
                             SchedulingAuctionRepository schedulingAuctionRepository) {
        this.auctionService = auctionService;
        this.auctionScheduleService = auctionScheduleService;
        this.bidRoundSelectionFilterService = bidRoundSelectionFilterService;
        this.auctionListService = auctionListService;
        this.round1InitializationService = round1InitializationService;
        this.schedulingAuctionRepository = schedulingAuctionRepository;
    }

    @PostMapping
    public ResponseEntity<CreateAuctionResponse> create(@RequestBody CreateAuctionRequest req) {
        CreateAuctionResponse response = auctionService.createAuction(req);
        return ResponseEntity.created(URI.create("/api/v1/admin/auctions/" + response.id()))
                .body(response);
    }

    /**
     * List endpoint backing the Auctions tile. Returns a paginated, filterable
     * view of {@code auctions.auctions} joined to {@code mdm.week} for the
     * displayed week label. Ordered by {@code createdDate} descending so the
     * most recent auction lands first (QA parity).
     */
    @GetMapping
    public AuctionListPageResponse list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return auctionListService.searchAuctions(title, weekId, status, page, pageSize);
    }

    @GetMapping("/{id}")
    public AuctionDetailResponse getAuction(@PathVariable Long id) {
        return auctionScheduleService.getAuctionDetail(id);
    }

    @GetMapping("/{id}/schedule-defaults")
    public ScheduleDefaultsResponse getScheduleDefaults(@PathVariable Long id) {
        return auctionScheduleService.loadScheduleDefaults(id);
    }

    @PutMapping("/{id}/schedule")
    public AuctionDetailResponse saveSchedule(@PathVariable Long id,
                                              @RequestBody ScheduleAuctionRequest req) {
        return auctionScheduleService.saveSchedule(id, req, currentActor());
    }

    @PostMapping("/{id}/unschedule")
    public AuctionDetailResponse unschedule(@PathVariable Long id) {
        return auctionScheduleService.unschedule(id, currentActor());
    }

    /**
     * Delete an auction — Administrator only (Mendix parity, delete was
     * gated to Admin in the legacy UI even though the schedule flow also
     * permits SalesOps).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    // Round 2 / Round 3 Selection Rules (Phase D — port of Mendix
    // acc_RoundTwoCriteriaPage + PG_Round3Criteria DataView).
    // ------------------------------------------------------------------

    /**
     * Read the selection-rules row for a given round. Administrator and
     * SalesOps may both read; the URL rejects {@code round} values other
     * than 2 or 3 with a 400 from the service layer.
     */
    @GetMapping("/round-filters/{round}")
    public BidRoundSelectionFilterResponse getRoundFilter(@PathVariable int round) {
        return bidRoundSelectionFilterService.get(round);
    }

    /**
     * Update the selection-rules row for a given round. Administrator only
     * (Mendix parity — the criteria pages were gated to Administrator in
     * the legacy UI). SalesOps hits the filter-chain matcher and receives
     * 403 before {@code @PreAuthorize} runs; see SecurityConfig for the
     * explicit matcher.
     */
    @PutMapping("/round-filters/{round}")
    @PreAuthorize("hasRole('Administrator')")
    public BidRoundSelectionFilterResponse updateRoundFilter(
            @PathVariable int round,
            @RequestBody BidRoundSelectionFilterRequest req) {
        return bidRoundSelectionFilterService.update(round, req);
    }

    /**
     * Admin-only recovery endpoint for Round 1 initialization. Re-runs the
     * target-price floor clamp + QBC rewrite that
     * {@link com.ecoatm.salesplatform.service.auctions.r1init.R1InitListener}
     * would normally fire on the {@code Scheduled → Started} event. Useful
     * when the listener was disabled or failed on a prior tick.
     *
     * <p>Gated to {@code Administrator} both by an explicit SecurityConfig
     * matcher (filter chain) and {@code @PreAuthorize} (method security),
     * because the class-level grant also admits SalesOps.
     */
    @PostMapping("/{auctionId}/rounds/1/init")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<Round1InitializationResult> initializeRound1(@PathVariable Long auctionId) {
        SchedulingAuction sa = schedulingAuctionRepository
                .findByAuctionIdAndRound(auctionId, 1)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Round 1 scheduling auction not found for auctionId=" + auctionId));
        Round1InitializationResult result = round1InitializationService.initialize(sa.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * Resolves the authenticated principal name for audit columns. Falls
     * back to {@code "system"} when the test harness uses an anonymous
     * principal; real requests always have an authenticated name because
     * the filter chain rejects unauthenticated traffic on this path.
     */
    private static String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "system";
        }
        return auth.getName();
    }
}
