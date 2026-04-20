package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.exception.DuplicateAuctionTitleException;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.AuctionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private static CreateAuctionResponse sampleResponse() {
        return new CreateAuctionResponse(
                42L,
                "Auction 2026 / Wk17",
                "Unscheduled",
                100L,
                "2026 / Wk17",
                List.of(
                        new CreateAuctionResponse.Round(1000L, 1,
                                Instant.parse("2026-04-20T23:00:00Z"),
                                Instant.parse("2026-04-24T14:00:00Z"),
                                "Scheduled"),
                        new CreateAuctionResponse.Round(1001L, 2,
                                Instant.parse("2026-04-24T15:00:00Z"),
                                Instant.parse("2026-04-25T15:00:00Z"),
                                "Scheduled"),
                        new CreateAuctionResponse.Round(1002L, 3,
                                Instant.parse("2026-04-25T16:00:00Z"),
                                Instant.parse("2026-04-26T19:00:00Z"),
                                "Scheduled")));
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("POST / — Administrator gets 201 + Location header")
    void create_admin_returns201() throws Exception {
        when(auctionService.createAuction(any())).thenReturn(sampleResponse());

        mvc.perform(post("/api/v1/admin/auctions")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"weekId\":100,\"customSuffix\":null}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/admin/auctions/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.auctionTitle").value("Auction 2026 / Wk17"))
                .andExpect(jsonPath("$.rounds.length()").value(3));
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("POST / — SalesOps gets 201 (matches page role gate)")
    void create_salesOps_returns201() throws Exception {
        when(auctionService.createAuction(any())).thenReturn(sampleResponse());

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
}
