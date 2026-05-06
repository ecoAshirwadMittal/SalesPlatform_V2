package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService;
import com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller slice IT for {@link RecalcAdminController}.
 *
 * <p>Mirrors the {@code PurchaseOrderControllerIT} pattern (slice via
 * {@link WebMvcTest} with {@code @MockBean} services + the real
 * {@link SecurityConfig} imported) rather than the full-stack
 * {@code @SpringBootTest} scaffold in the task plan: the
 * {@code @Transactional(REQUIRES_NEW)} in the recalc services commits
 * independently, which would cause PK violations when {@code @Sql}
 * re-INSERTs across tests. Service-level behaviors (status flips,
 * event publishing, error wrapping) are exercised by
 * {@code BidRankingServiceTest} and {@code TargetPriceRecalcServiceTest};
 * this IT covers only RBAC + HTTP status code mappings.
 */
@WebMvcTest(RecalcAdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class RecalcAdminControllerIT {

    @Autowired MockMvc mvc;

    @MockBean BidRankingService rankingService;
    @MockBean TargetPriceRecalcService targetPriceService;
    @MockBean SchedulingAuctionRepository saRepo;

    @Test
    @WithMockUser(roles = "Administrator")
    void re_rank_admin_returns_200_with_success_body() throws Exception {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(999001L);
        sa.setRound(1);
        when(saRepo.findById(999001L)).thenReturn(Optional.of(sa));

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999001/re-rank"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedulingAuctionId").value(999001))
            .andExpect(jsonPath("$.closedRound").value(1))
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.startedAt").exists())
            .andExpect(jsonPath("$.finishedAt").exists());
    }

    @Test
    @WithMockUser(roles = "SalesOps")
    void re_rank_salesops_returns_200() throws Exception {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(999001L);
        sa.setRound(1);
        when(saRepo.findById(999001L)).thenReturn(Optional.of(sa));

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999001/re-rank"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    void re_rank_non_admin_returns_403() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999001/re-rank"))
            .andExpect(status().isForbidden());
    }

    @Test
    void re_rank_unauthenticated_returns_401() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999001/re-rank"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void re_rank_unknown_id_returns_404() throws Exception {
        doThrow(new EntityNotFoundException("scheduling_auction not found: id=999"))
            .when(rankingService).recalculate(999L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999/re-rank"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void re_rank_round_3_returns_400() throws Exception {
        // Service throws IllegalArgumentException for round 3.
        // GlobalExceptionHandler maps IllegalArgumentException → 400 (not 422).
        doThrow(new IllegalArgumentException(
                "RANKING only valid for closed round 1 or 2; was 3"))
            .when(rankingService).recalculate(999003L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999003/re-rank"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void re_rank_returns_409_when_already_running() throws Exception {
        doThrow(new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.RANKING, 999001L))
            .when(rankingService).recalculate(999001L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999001/re-rank"))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void recalculate_target_price_admin_returns_200() throws Exception {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(999001L);
        sa.setRound(1);
        when(saRepo.findById(999001L)).thenReturn(Optional.of(sa));

        mvc.perform(post(
                "/api/v1/admin/auctions/scheduling-auctions/999001/recalculate-target-price"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void recalculate_target_price_returns_409_when_already_running() throws Exception {
        doThrow(new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.TARGET_PRICE, 999001L))
            .when(targetPriceService).recalculate(999001L);

        mvc.perform(post(
                "/api/v1/admin/auctions/scheduling-auctions/999001/recalculate-target-price"))
            .andExpect(status().isConflict());
    }
}
