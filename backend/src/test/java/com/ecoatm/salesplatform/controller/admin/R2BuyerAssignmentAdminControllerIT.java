package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentResult;
import com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller slice IT for {@link R2BuyerAssignmentAdminController}.
 *
 * <p>Mirrors {@code RecalcAdminControllerIT} pattern (4C admin canonical) —
 * slice via {@link WebMvcTest} with {@code @MockBean} services + the real
 * {@link SecurityConfig} imported. Covers RBAC + HTTP status code mappings:
 * 200 happy path, 401 unauthenticated, 403 non-admin, 404 unknown id,
 * 409 already-running, 400 round != 2.
 */
@WebMvcTest(R2BuyerAssignmentAdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class R2BuyerAssignmentAdminControllerIT {

    @Autowired MockMvc mvc;

    @MockBean R2BuyerAssignmentService service;
    @MockBean SchedulingAuctionRepository saRepo;

    private SchedulingAuction sa(RecalcStatus status, String error) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(502L);
        sa.setR2InitStatus(status);
        sa.setR2InitError(error);
        sa.setR2InitStartedAt(Instant.parse("2026-05-06T10:00:00Z"));
        sa.setR2InitFinishedAt(Instant.parse("2026-05-06T10:00:01Z"));
        return sa;
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("admin -> 200 + R2BuyerAssignmentResponse on success")
    void admin_can_reassign() throws Exception {
        // qualifiedCount=5 (union), specialTreatmentCount=2,
        // notQualifiedCount=3, specialBidDataCount=30, durationMs=100
        when(service.recalculate(anyLong())).thenReturn(
            new R2BuyerAssignmentResult(5, 2, 3, 30, 100L, false));
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa(RecalcStatus.SUCCESS, null)));

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 502L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedulingAuctionId").value(502))
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.error").doesNotExist())
            // qualifiedCount in DTO = qualified-but-not-special = 5 - 2 = 3
            .andExpect(jsonPath("$.qualifiedCount").value(3))
            .andExpect(jsonPath("$.specialTreatmentCount").value(2))
            .andExpect(jsonPath("$.notQualifiedCount").value(3))
            .andExpect(jsonPath("$.specialBidDataCount").value(30))
            .andExpect(jsonPath("$.durationMs").value(100))
            .andExpect(jsonPath("$.startedAt").exists())
            .andExpect(jsonPath("$.finishedAt").exists());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    @DisplayName("non-admin -> 403")
    void non_admin_forbidden() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 502L))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("unauthenticated -> 401")
    void unauthenticated_returns_401() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 502L))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("EntityNotFoundException -> 404")
    void unknown_id_returns_404() throws Exception {
        doThrow(new EntityNotFoundException("scheduling_auction not found: id=999"))
            .when(service).recalculate(999L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("RecalcAlreadyRunningException -> 409")
    void already_running_returns_409() throws Exception {
        doThrow(new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R2_INIT, 502L))
            .when(service).recalculate(502L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 502L))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("IllegalArgumentException (round != 2) -> 400")
    void wrong_round_returns_400() throws Exception {
        // GlobalExceptionHandler maps IllegalArgumentException -> 400 (not 422).
        doThrow(new IllegalArgumentException(
                "R2 buyer assignment only valid for round 2; was 1"))
            .when(service).recalculate(503L);

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 503L))
            .andExpect(status().isBadRequest());
    }
}
