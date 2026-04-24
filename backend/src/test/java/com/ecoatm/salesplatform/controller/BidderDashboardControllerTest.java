package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidDataTotals;
import com.ecoatm.salesplatform.dto.BidRoundSummary;
import com.ecoatm.salesplatform.dto.BidSubmissionResult;
import com.ecoatm.salesplatform.dto.BidderDashboardResponse;
import com.ecoatm.salesplatform.dto.RoundTimerState;
import com.ecoatm.salesplatform.dto.SchedulingAuctionSummary;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.biddata.BidCarryoverService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataCreationResult;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataCreationService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataSubmissionService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidExportService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidImportService;
import com.ecoatm.salesplatform.service.auctions.biddata.BidRateLimiter;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardLandingResult;
import com.ecoatm.salesplatform.service.auctions.biddata.BidderDashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc tests for {@link BidderDashboardController}. The principal in
 * production is the JWT user id ({@code Long}) — see
 * {@link JwtAuthenticationFilter} — and the credentials slot is the email
 * (String). Stock {@code @WithMockUser} sets the principal to the username
 * String, which would blow up the {@code (Long) auth.getPrincipal()} cast,
 * so each test injects an {@link UsernamePasswordAuthenticationToken} via
 * the {@code authentication(...)} request post-processor with the matching
 * shape.
 */
@WebMvcTest(BidderDashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class BidderDashboardControllerTest {

    private static final long USER_ID = 7L;
    private static final long BUYER_CODE_ID = 99L;
    private static final long BID_ROUND_ID = 100L;
    private static final long SA_ID = 7L;
    private static final long BID_DATA_ID = 500L;
    private static final String USERNAME = "bidder@buyerco.com";

    @Autowired private MockMvc mvc;
    @MockBean private BidderDashboardService dashboardService;
    @MockBean private BidDataCreationService creationService;
    @MockBean private BidDataSubmissionService submissionService;
    @MockBean private BidCarryoverService carryoverService;
    @MockBean private BidExportService exportService;
    @MockBean private BidImportService importService;
    @MockBean private BidRateLimiter rateLimiter;
    @MockBean private com.ecoatm.salesplatform.repository.auctions.BidRoundRepository bidRoundRepository;
    @MockBean private com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository schedulingAuctionRepository;
    @MockBean private com.ecoatm.salesplatform.repository.auctions.AuctionRepository auctionRepository;

    /** Auth shape that matches what {@link JwtAuthenticationFilter} installs. */
    private static RequestPostProcessor jwtBidder() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                USER_ID, USERNAME,
                List.of(new SimpleGrantedAuthority("ROLE_Bidder")));
        return authentication(auth);
    }

    private static RequestPostProcessor jwtSalesOps() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                USER_ID, USERNAME,
                List.of(new SimpleGrantedAuthority("ROLE_SalesOps")));
        return authentication(auth);
    }

    private static BidderDashboardResponse sampleGridResponse() {
        SchedulingAuctionSummary auction = new SchedulingAuctionSummary(
                SA_ID, 42L, "Auction 2026 / Wk17", 1, "Round 1", "Started");
        BidRoundSummary round = new BidRoundSummary(
                BID_ROUND_ID, SA_ID, 1, "Started",
                Instant.parse("2026-04-21T16:00:00Z"),
                Instant.parse("2026-04-25T07:00:00Z"),
                false, null);
        return new BidderDashboardResponse(
                "GRID",
                auction,
                round,
                List.of(),
                new BidDataTotals(0, BigDecimal.ZERO, BigDecimal.ZERO, 0),
                new RoundTimerState(Instant.now(), null, null, -1, -1, true));
    }

    // ---------------------------------------------------------------------
    // GET /dashboard
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /dashboard — Bidder gets 200 + GRID mode for an active round")
    void get_dashboard_200_forBidder() throws Exception {
        when(dashboardService.landingRoute(eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(new BidderDashboardLandingResult.Grid(BID_ROUND_ID, SA_ID, 1));
        when(creationService.ensureRowsExist(eq(BUYER_CODE_ID), eq(BID_ROUND_ID), eq(USER_ID)))
                .thenReturn(new BidDataCreationResult(0, true, 5L));
        when(dashboardService.loadGrid(eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(sampleGridResponse());

        mvc.perform(get("/api/v1/bidder/dashboard")
                        .with(jwtBidder())
                        .param("buyerCodeId", String.valueOf(BUYER_CODE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("GRID"))
                .andExpect(jsonPath("$.auction.id").value(SA_ID))
                .andExpect(jsonPath("$.bidRound.id").value(BID_ROUND_ID));

        verify(creationService).ensureRowsExist(BUYER_CODE_ID, BID_ROUND_ID, USER_ID);
        verify(dashboardService).loadGrid(BID_ROUND_ID, BUYER_CODE_ID);
    }

    @Test
    @DisplayName("GET /dashboard — SalesOps role gets 403 (not a Bidder/Administrator)")
    void get_dashboard_403_forSalesOps() throws Exception {
        mvc.perform(get("/api/v1/bidder/dashboard")
                        .with(jwtSalesOps())
                        .param("buyerCodeId", String.valueOf(BUYER_CODE_ID)))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // PUT /bid-data/{id}
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("PUT /bid-data/{id} — happy path returns 200 + saved row")
    void put_bidData_200_forOwnRow() throws Exception {
        when(submissionService.resolveBidRoundId(BID_DATA_ID)).thenReturn(BID_ROUND_ID);
        when(rateLimiter.tryAcquire(eq(USER_ID), eq(BID_ROUND_ID))).thenReturn(true);
        when(submissionService.save(eq(USER_ID), eq(BID_DATA_ID), any()))
                .thenReturn(new BidDataRow(
                        BID_DATA_ID, BID_ROUND_ID, "ECO-1",
                        null, null, null, null, null,   // Phase 6B: MDM fields null on save path
                        "ABC", "Wholesale",
                        5, new BigDecimal("10.00"), null, 10, null,
                        null, null, null, null, null, null));

        mvc.perform(put("/api/v1/bidder/bid-data/" + BID_DATA_ID)
                        .with(jwtBidder())
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"bidQuantity\":5,\"bidAmount\":10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BID_DATA_ID))
                .andExpect(jsonPath("$.bidQuantity").value(5));

        verify(submissionService).save(eq(USER_ID), eq(BID_DATA_ID), any());
    }

    // ---------------------------------------------------------------------
    // GET /download-round-1 — DOWNLOAD-mode support (Option B)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /download-round-1 — 200 + xlsx when closed R1 bid_round exists")
    void get_downloadRound1_200() throws Exception {
        when(dashboardService.findDownloadableRound1BidRoundId(USER_ID, BUYER_CODE_ID))
                .thenReturn(java.util.Optional.of(BID_ROUND_ID));
        // Stub the export service's filename-resolve path the controller calls
        // before writing the xlsx (the controller reads bidRound + sa + auction
        // to build the filename). Minimal fixture — real bytes not asserted here.
        when(bidRoundRepository.findById(BID_ROUND_ID))
                .thenReturn(java.util.Optional.of(makeBidRound()));
        when(schedulingAuctionRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(makeSchedulingAuction()));
        when(auctionRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(makeAuction()));

        mvc.perform(get("/api/v1/bidder/download-round-1")
                        .param("buyerCodeId", String.valueOf(BUYER_CODE_ID))
                        .with(jwtBidder()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")));

        verify(exportService).export(eq(BID_ROUND_ID), eq(BUYER_CODE_ID), any());
    }

    @Test
    @DisplayName("GET /download-round-1 — 404 when no closed R1 bid_round exists")
    void get_downloadRound1_404() throws Exception {
        when(dashboardService.findDownloadableRound1BidRoundId(USER_ID, BUYER_CODE_ID))
                .thenReturn(java.util.Optional.empty());

        mvc.perform(get("/api/v1/bidder/download-round-1")
                        .param("buyerCodeId", String.valueOf(BUYER_CODE_ID))
                        .with(jwtBidder()))
                .andExpect(status().isNotFound());

        verify(exportService, org.mockito.Mockito.never()).export(anyLong(), anyLong(), any());
    }

    private static com.ecoatm.salesplatform.model.auctions.BidRound makeBidRound() {
        com.ecoatm.salesplatform.model.auctions.BidRound br =
                new com.ecoatm.salesplatform.model.auctions.BidRound();
        org.springframework.test.util.ReflectionTestUtils.setField(br, "id", BID_ROUND_ID);
        br.setSchedulingAuctionId(301L);
        return br;
    }

    private static com.ecoatm.salesplatform.model.auctions.SchedulingAuction makeSchedulingAuction() {
        com.ecoatm.salesplatform.model.auctions.SchedulingAuction sa =
                new com.ecoatm.salesplatform.model.auctions.SchedulingAuction();
        sa.setId(301L);
        sa.setAuctionId(101L);
        sa.setRound(1);
        return sa;
    }

    private static com.ecoatm.salesplatform.model.auctions.Auction makeAuction() {
        com.ecoatm.salesplatform.model.auctions.Auction a =
                new com.ecoatm.salesplatform.model.auctions.Auction();
        a.setId(101L);
        a.setAuctionTitle("Auction 2026 / Wk17");
        return a;
    }

    @Test
    @DisplayName("PUT /bid-data/{id} — rate limiter denies → 429 Too Many Requests")
    void put_bidData_429_whenRateLimited() throws Exception {
        when(submissionService.resolveBidRoundId(BID_DATA_ID)).thenReturn(BID_ROUND_ID);
        when(rateLimiter.tryAcquire(eq(USER_ID), eq(BID_ROUND_ID))).thenReturn(false);

        mvc.perform(put("/api/v1/bidder/bid-data/" + BID_DATA_ID)
                        .with(jwtBidder())
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"bidQuantity\":5,\"bidAmount\":10.0}"))
                .andExpect(status().isTooManyRequests());

        verify(submissionService, org.mockito.Mockito.never()).save(anyLong(), anyLong(), any());
    }

    // ---------------------------------------------------------------------
    // POST /bid-rounds/{id}/submit
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("POST /bid-rounds/{id}/submit — happy path returns 200 + submission result")
    void post_submit_200() throws Exception {
        when(submissionService.submit(eq(USER_ID), eq(USERNAME), eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(new BidSubmissionResult(BID_ROUND_ID, 3,
                        Instant.parse("2026-04-21T17:00:00Z"), false));

        mvc.perform(post("/api/v1/bidder/bid-rounds/" + BID_ROUND_ID + "/submit")
                        .with(jwtBidder())
                        .with(csrf())
                        .param("buyerCodeId", String.valueOf(BUYER_CODE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bidRoundId").value(BID_ROUND_ID))
                .andExpect(jsonPath("$.rowCount").value(3))
                .andExpect(jsonPath("$.resubmit").value(false));

        verify(submissionService).submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID);
    }
}
