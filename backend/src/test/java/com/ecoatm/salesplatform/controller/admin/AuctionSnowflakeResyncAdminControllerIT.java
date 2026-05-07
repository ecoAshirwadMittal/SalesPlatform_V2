package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService.AuctionSnowflakeResyncResult;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService.RoundOutcome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller slice IT for {@link AuctionSnowflakeResyncAdminController}.
 *
 * <p>Mirrors the {@link RecalcAdminControllerIT} pattern: slice via
 * {@link WebMvcTest} with {@code @MockBean} service + the real
 * {@link SecurityConfig} imported. Covers RBAC + HTTP status code mappings.
 */
@WebMvcTest(AuctionSnowflakeResyncAdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AuctionSnowflakeResyncAdminControllerIT {

    @Autowired MockMvc mvc;

    @MockBean AuctionSnowflakeResyncService resyncService;

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("Administrator → 200 + serialized resync result")
    void administrator_returns_200_with_result() throws Exception {
        List<RoundOutcome> outcomes = List.of(
            new RoundOutcome(1, 2, true, true),
            new RoundOutcome(2, 3, true, true)
        );
        when(resyncService.resync(101L))
            .thenReturn(new AuctionSnowflakeResyncResult(101L, 601L, outcomes, 2, 0, 120L));

        mvc.perform(post("/api/v1/admin/auctions/101/resync-snowflake"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.auctionId").value(101))
            .andExpect(jsonPath("$.weekId").value(601))
            .andExpect(jsonPath("$.totalSuccesses").value(2))
            .andExpect(jsonPath("$.totalFailures").value(0))
            .andExpect(jsonPath("$.outcomes").isArray())
            .andExpect(jsonPath("$.outcomes[0].closedRound").value(1))
            .andExpect(jsonPath("$.outcomes[0].targetRound").value(2))
            .andExpect(jsonPath("$.outcomes[0].bidRankingPushed").value(true))
            .andExpect(jsonPath("$.outcomes[0].targetPricePushed").value(true));
    }

    @Test
    @WithMockUser(roles = "Bidder")
    @DisplayName("Bidder → 403")
    void bidder_returns_403() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/101/resync-snowflake"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated → 401")
    void unauthenticated_returns_401() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/101/resync-snowflake"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("Unknown auction id → 404")
    void unknown_auction_returns_404() throws Exception {
        doThrow(new EntityNotFoundException("Auction", 999L))
            .when(resyncService).resync(999L);

        mvc.perform(post("/api/v1/admin/auctions/999/resync-snowflake"))
            .andExpect(status().isNotFound());
    }
}
