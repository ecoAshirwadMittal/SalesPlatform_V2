package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AuctionDetailResponse;
import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterResponse;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.dto.RoundView;
import com.ecoatm.salesplatform.dto.ScheduleDefaultsResponse;
import com.ecoatm.salesplatform.exception.AuctionAlreadyStartedException;
import com.ecoatm.salesplatform.exception.AuctionHasBidsException;
import com.ecoatm.salesplatform.exception.DuplicateAuctionTitleException;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.exception.RoundValidationException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.AuctionListService;
import com.ecoatm.salesplatform.service.auctions.AuctionScheduleService;
import com.ecoatm.salesplatform.service.auctions.AuctionService;
import com.ecoatm.salesplatform.service.auctions.BidRoundSelectionFilterService;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationResult;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AuctionControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private AuctionService auctionService;
    @MockBean private AuctionScheduleService auctionScheduleService;
    @MockBean private BidRoundSelectionFilterService bidRoundSelectionFilterService;
    @MockBean private AuctionListService auctionListService;
    @MockBean private Round1InitializationService round1InitializationService;
    @MockBean private SchedulingAuctionRepository schedulingAuctionRepository;

    private static CreateAuctionResponse sampleCreateResponse() {
        return new CreateAuctionResponse(
                42L,
                "Auction 2026 / Wk17",
                "Unscheduled",
                100L,
                "2026 / Wk17");
    }

    private static AuctionDetailResponse sampleDetail(String status) {
        RoundView r1 = new RoundView(11L, 1, "Round 1",
                Instant.parse("2026-04-21T00:00:00Z"),
                Instant.parse("2026-04-24T15:00:00Z"),
                "Scheduled", true);
        RoundView r2 = new RoundView(12L, 2, "Round 2",
                Instant.parse("2026-04-24T21:00:00Z"),
                Instant.parse("2026-04-25T16:00:00Z"),
                "Scheduled", true);
        RoundView r3 = new RoundView(13L, 3, "Upsell Round",
                Instant.parse("2026-04-25T19:00:00Z"),
                Instant.parse("2026-04-26T20:00:00Z"),
                "Scheduled", true);
        return new AuctionDetailResponse(42L, "Auction 2026 / Wk17", status,
                100L, "2026 / Wk17", List.of(r1, r2, r3));
    }

    private static String validScheduleBody() {
        return """
                {
                  "round1Start": "2026-04-21T00:00:00Z",
                  "round1End": "2026-04-24T15:00:00Z",
                  "round2Start": "2026-04-24T21:00:00Z",
                  "round2End": "2026-04-25T16:00:00Z",
                  "round2Active": true,
                  "round3Start": "2026-04-25T19:00:00Z",
                  "round3End": "2026-04-26T20:00:00Z",
                  "round3Active": true
                }
                """;
    }

    // ---------- Create (existing) ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST / — Administrator gets 201 + Location header")
    void create_admin_returns201() throws Exception {
        when(auctionService.createAuction(any())).thenReturn(sampleCreateResponse());

        mvc.perform(post("/api/v1/admin/auctions")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"weekId\":100,\"customSuffix\":null}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/admin/auctions/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.auctionTitle").value("Auction 2026 / Wk17"))
                .andExpect(jsonPath("$.auctionStatus").value("Unscheduled"))
                .andExpect(jsonPath("$.weekId").value(100))
                .andExpect(jsonPath("$.weekDisplay").value("2026 / Wk17"))
                .andExpect(jsonPath("$.rounds").doesNotExist());
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("POST / — SalesOps gets 201 (matches page role gate)")
    void create_salesOps_returns201() throws Exception {
        when(auctionService.createAuction(any())).thenReturn(sampleCreateResponse());

        mvc.perform(post("/api/v1/admin/auctions")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"weekId\":100,\"customSuffix\":null}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("POST / — Bidder is denied (403)")
    void create_bidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"weekId\":100,\"customSuffix\":null}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST / — duplicate title surfaces as 409")
    void create_duplicateTitle_returns409() throws Exception {
        when(auctionService.createAuction(any()))
                .thenThrow(new DuplicateAuctionTitleException("Auction 2026 / Wk17"));

        mvc.perform(post("/api/v1/admin/auctions")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"weekId\":100,\"customSuffix\":null}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "An auction with this name already exists: Auction 2026 / Wk17"));
    }

    // ---------- GET /{id} ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /{id} — returns auction detail with rounds")
    void getAuction_returnsDetail() throws Exception {
        when(auctionScheduleService.getAuctionDetail(42L)).thenReturn(sampleDetail("Scheduled"));

        mvc.perform(get("/api/v1/admin/auctions/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.auctionStatus").value("Scheduled"))
                .andExpect(jsonPath("$.rounds.length()").value(3))
                .andExpect(jsonPath("$.rounds[2].name").value("Upsell Round"));
    }

    // ---------- GET /{id}/schedule-defaults ----------

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("GET /{id}/schedule-defaults — SalesOps can read")
    void getScheduleDefaults_returnsPayload() throws Exception {
        ScheduleDefaultsResponse defaults = new ScheduleDefaultsResponse(
                Instant.parse("2026-04-21T00:00:00Z"),
                Instant.parse("2026-04-24T15:00:00Z"),
                Instant.parse("2026-04-24T21:00:00Z"),
                Instant.parse("2026-04-25T16:00:00Z"),
                Instant.parse("2026-04-25T19:00:00Z"),
                Instant.parse("2026-04-26T20:00:00Z"),
                true, true, 360, 180);
        when(auctionScheduleService.loadScheduleDefaults(42L)).thenReturn(defaults);

        mvc.perform(get("/api/v1/admin/auctions/42/schedule-defaults"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.round2MinutesOffset").value(360))
                .andExpect(jsonPath("$.round3MinutesOffset").value(180))
                .andExpect(jsonPath("$.round2Active").value(true))
                .andExpect(jsonPath("$.round3Active").value(true));
    }

    // ---------- PUT /{id}/schedule ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /{id}/schedule — Administrator gets 200 + detail")
    void saveSchedule_admin_returns200() throws Exception {
        when(auctionScheduleService.saveSchedule(eq(42L), any(), anyString()))
                .thenReturn(sampleDetail("Scheduled"));

        mvc.perform(put("/api/v1/admin/auctions/42/schedule")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validScheduleBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auctionStatus").value("Scheduled"))
                .andExpect(jsonPath("$.rounds.length()").value(3));

        verify(auctionScheduleService).saveSchedule(eq(42L), any(), anyString());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /{id}/schedule — round validation error → 400")
    void saveSchedule_validationError_returns400() throws Exception {
        when(auctionScheduleService.saveSchedule(eq(42L), any(), anyString()))
                .thenThrow(new RoundValidationException(
                        List.of("Round 1 end date must be later than start date")));

        mvc.perform(put("/api/v1/admin/auctions/42/schedule")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validScheduleBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Round 1 end date must be later than start date"))
                .andExpect(jsonPath("$.details.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /{id}/schedule — bids exist → 409")
    void saveSchedule_withBids_returns409() throws Exception {
        when(auctionScheduleService.saveSchedule(eq(42L), any(), anyString()))
                .thenThrow(new AuctionHasBidsException());

        mvc.perform(put("/api/v1/admin/auctions/42/schedule")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validScheduleBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Bids have already been submitted; reschedule is not available."));
    }

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("PUT /{id}/schedule — Bidder is denied (403)")
    void saveSchedule_bidder_returns403() throws Exception {
        mvc.perform(put("/api/v1/admin/auctions/42/schedule")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validScheduleBody()))
                .andExpect(status().isForbidden());
    }

    // ---------- POST /{id}/unschedule ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST /{id}/unschedule — happy path returns 200 + Unscheduled status")
    void unschedule_admin_returns200() throws Exception {
        when(auctionScheduleService.unschedule(eq(42L), anyString()))
                .thenReturn(sampleDetail("Unscheduled"));

        mvc.perform(post("/api/v1/admin/auctions/42/unschedule").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auctionStatus").value("Unscheduled"));
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST /{id}/unschedule — Started round → 409")
    void unschedule_startedRound_returns409() throws Exception {
        when(auctionScheduleService.unschedule(eq(42L), anyString()))
                .thenThrow(new AuctionAlreadyStartedException(
                        "Auction has started. Unscheduling the auction is not available."));

        mvc.perform(post("/api/v1/admin/auctions/42/unschedule").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Auction has started. Unscheduling the auction is not available."));
    }

    // ---------- DELETE /{id} ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("DELETE /{id} — Administrator gets 204")
    void delete_admin_returns204() throws Exception {
        mvc.perform(delete("/api/v1/admin/auctions/42").with(csrf()))
                .andExpect(status().isNoContent());

        verify(auctionScheduleService).delete(42L);
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("DELETE /{id} — SalesOps is denied (403) — delete is Administrator-only")
    void delete_salesOps_returns403() throws Exception {
        mvc.perform(delete("/api/v1/admin/auctions/42").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("DELETE /{id} — Started round → 409")
    void delete_startedRound_returns409() throws Exception {
        doThrow(new AuctionAlreadyStartedException(
                "Auction has started. Deleting the auction is not available."))
                .when(auctionScheduleService).delete(42L);

        mvc.perform(delete("/api/v1/admin/auctions/42").with(csrf()))
                .andExpect(status().isConflict());
    }

    // ---------- Round filters (GET/PUT /round-filters/{round}) ----------

    private static BidRoundSelectionFilterResponse sampleRoundFilterResponse(int round) {
        return new BidRoundSelectionFilterResponse(
                99L,
                42L,
                round,
                new BigDecimal("85.0000"),
                new BigDecimal("150000.00"),
                new BigDecimal("5000.00"),
                "A",
                "B",
                "C",
                Boolean.FALSE,
                Boolean.TRUE,
                "Only_Qualified",
                "InventoryRound1QualifiedBids",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2026-04-20T00:00:00Z"));
    }

    private static String validRoundFilterBody() {
        return """
                {
                  "targetPercent": 90.5,
                  "targetValue": 200000.00,
                  "totalValueFloor": 7500.00,
                  "mergedGrade1": "AA",
                  "mergedGrade2": "BB",
                  "mergedGrade3": "CC",
                  "stbAllowAllBuyersOverride": true,
                  "stbIncludeAllInventory": false,
                  "regularBuyerQualification": "All_Buyers",
                  "regularBuyerInventoryOptions": "ShowAllInventory"
                }
                """;
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /round-filters/2 — Administrator gets 200")
    void getRoundFilter_admin_returns200() throws Exception {
        when(bidRoundSelectionFilterService.get(2)).thenReturn(sampleRoundFilterResponse(2));

        mvc.perform(get("/api/v1/admin/auctions/round-filters/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.round").value(2))
                .andExpect(jsonPath("$.regularBuyerQualification").value("Only_Qualified"))
                .andExpect(jsonPath("$.regularBuyerInventoryOptions")
                        .value("InventoryRound1QualifiedBids"));
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("GET /round-filters/3 — SalesOps can read")
    void getRoundFilter_salesOps_returns200() throws Exception {
        when(bidRoundSelectionFilterService.get(3)).thenReturn(sampleRoundFilterResponse(3));

        mvc.perform(get("/api/v1/admin/auctions/round-filters/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.round").value(3));
    }

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("GET /round-filters/2 — Bidder is denied (403)")
    void getRoundFilter_bidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/auctions/round-filters/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /round-filters/2 — Administrator gets 200 + updated payload")
    void updateRoundFilter_admin_returns200() throws Exception {
        when(bidRoundSelectionFilterService.update(eq(2), any()))
                .thenReturn(new BidRoundSelectionFilterResponse(
                        99L, 42L, 2,
                        new BigDecimal("90.5000"),
                        new BigDecimal("200000.00"),
                        new BigDecimal("7500.00"),
                        "AA", "BB", "CC",
                        Boolean.TRUE, Boolean.FALSE,
                        "All_Buyers", "ShowAllInventory",
                        Instant.parse("2024-01-01T00:00:00Z"),
                        Instant.parse("2026-04-20T12:00:00Z")));

        mvc.perform(put("/api/v1/admin/auctions/round-filters/2")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validRoundFilterBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regularBuyerQualification").value("All_Buyers"))
                .andExpect(jsonPath("$.stbAllowAllBuyersOverride").value(true))
                .andExpect(jsonPath("$.mergedGrade1").value("AA"));

        verify(bidRoundSelectionFilterService).update(eq(2), any());
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("PUT /round-filters/2 — SalesOps is denied (403) — write is Administrator-only")
    void updateRoundFilter_salesOps_returns403() throws Exception {
        mvc.perform(put("/api/v1/admin/auctions/round-filters/2")
                        .with(csrf())
                        .contentType("application/json")
                        .content(validRoundFilterBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /round-filters/1 — unknown round → 400")
    void getRoundFilter_invalidRound_returns400() throws Exception {
        when(bidRoundSelectionFilterService.get(1))
                .thenThrow(new IllegalArgumentException("round must be 2 or 3 (was 1)"));

        mvc.perform(get("/api/v1/admin/auctions/round-filters/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("round must be 2 or 3 (was 1)"));
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /round-filters/2 — missing row → 404")
    void getRoundFilter_missing_returns404() throws Exception {
        when(bidRoundSelectionFilterService.get(2))
                .thenThrow(new EntityNotFoundException("BidRoundSelectionFilter", 2));

        mvc.perform(get("/api/v1/admin/auctions/round-filters/2"))
                .andExpect(status().isNotFound());
    }

    // ---------- POST /{auctionId}/rounds/1/init ----------

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST /{id}/rounds/1/init — Administrator gets 200 + result")
    void r1InitEndpoint_administrator_returnsResult() throws Exception {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(301L);
        sa.setAuctionId(101L);
        sa.setRound(1);
        when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
                .thenReturn(Optional.of(sa));
        when(round1InitializationService.initialize(301L))
                .thenReturn(new Round1InitializationResult(301L, 101L, 42L, 3, 1, 579, 187));

        mvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qbcsCreated").value(579))
                .andExpect(jsonPath("$.clampedNonDw").value(3))
                .andExpect(jsonPath("$.clampedDw").value(1));
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("POST /{id}/rounds/1/init — SalesOps is denied (403)")
    void r1InitEndpoint_salesOps_forbidden() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("POST /{id}/rounds/1/init — Bidder is denied (403)")
    void r1InitEndpoint_bidder_forbidden() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST /{id}/rounds/1/init — missing Round 1 → 404")
    void r1InitEndpoint_missingRound1_returnsNotFound() throws Exception {
        when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
                .thenReturn(Optional.empty());

        mvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST /{id}/rounds/1/init — service throws → 500")
    void r1InitEndpoint_serviceThrows_propagatesAs500() throws Exception {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(301L);
        sa.setAuctionId(101L);
        sa.setRound(1);
        when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
                .thenReturn(Optional.of(sa));
        when(round1InitializationService.initialize(301L))
                .thenThrow(new RuntimeException("boom"));

        mvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init").with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
