package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context, real-Postgres controller IT for the QBC admin round-status
 * guard (gap-analysis 2.4 sub-feature 2). Extends {@link PostgresIntegrationTest}
 * (real Flyway'd Postgres, full Spring context) rather than slicing the web
 * layer, so the PATCH drives controller → {@code QualifiedBuyerCodeAdminService}
 * → the {@code SchedulingAuction} load → the real
 * {@code GlobalExceptionHandler} exception→status mapping.
 *
 * <p>Auth is injected via the
 * {@code SecurityMockMvcRequestPostProcessors.authentication(...)} post
 * processor (the same principal shape {@code JwtAuthenticationFilter} installs
 * from a real JWT — a {@code Long} user id + {@code ROLE_*} authority), mirroring
 * {@link AdminEmailControllerSmokeIT} / {@code BidderDashboardFullChainIT}.
 *
 * <p>The class is {@code @Transactional}: {@code updateIncluded} uses plain
 * {@code @Transactional} (propagation REQUIRED, not REQUIRES_NEW), so it joins
 * the test transaction and every fixture + write rolls back after each test.
 * There is no AFTER_COMMIT listener for {@code QualificationOverriddenEvent} yet
 * (Task 4), so the synchronous publish is a no-op here.
 */
@AutoConfigureMockMvc
@Transactional
class QualifiedBuyerCodeAdminControllerIT extends PostgresIntegrationTest {

    /** Matches the {@code Long} principal the controller's {@code userId()} expects. */
    private static final long ADMIN_USER_ID = 9L;

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;

    @Test
    @DisplayName("PATCH on an open (Started-round) QBC -> 200 + qualification_type forced to Manual")
    void patch_startedRound_returns200() throws Exception {
        long qbcId = seedQbc("Started");

        mvc.perform(patch("/api/v1/admin/qualified-buyer-codes/{id}", qbcId)
                        .with(admin())
                        .contentType("application/json")
                        .content("{\"included\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) qbcId))
                .andExpect(jsonPath("$.qualificationType").value("Manual"))
                .andExpect(jsonPath("$.included").value(false));
    }

    @Test
    @DisplayName("PATCH on a Closed-round QBC -> 409 (round frozen, override rejected)")
    void patch_closedRound_returns409() throws Exception {
        long qbcId = seedQbc("Closed");

        mvc.perform(patch("/api/v1/admin/qualified-buyer-codes/{id}", qbcId)
                        .with(admin())
                        .contentType("application/json")
                        .content("{\"included\":false}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Round cannot be modified if it is closed"));
    }

    /**
     * Seeds the minimal parent chain (auction → scheduling_auction →
     * qualified_buyer_codes) for one QBC row whose SchedulingAuction carries the
     * given {@code round_status}. Column sets mirror the proven
     * {@code BidDataScenario} inserts (defaults cover the NOT NULL columns not
     * listed here). Returns the new {@code qualified_buyer_codes.id}.
     */
    private long seedQbc(String roundStatus) {
        Long weekId = jdbc.queryForObject("SELECT id FROM mdm.week LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);

        Long auctionId = jdbc.queryForObject(
                "INSERT INTO auctions.auctions (auction_title, auction_status, week_id) "
                        + "VALUES (?, 'Scheduled', ?) RETURNING id",
                Long.class, "QBC guard IT " + System.nanoTime(), weekId);

        Long saId = jdbc.queryForObject(
                "INSERT INTO auctions.scheduling_auctions "
                        + "(auction_id, name, round, start_datetime, end_datetime, round_status, has_round) "
                        + "VALUES (?, 'Round 1', 1, ?, ?, ?, true) RETURNING id",
                Long.class,
                auctionId,
                Timestamp.from(Instant.parse("2026-04-21T16:00:00Z")),
                Timestamp.from(Instant.parse("2026-04-25T07:00:00Z")),
                roundStatus);

        return jdbc.queryForObject(
                "INSERT INTO buyer_mgmt.qualified_buyer_codes "
                        + "(qualification_type, included, is_special_treatment, "
                        + " scheduling_auction_id, buyer_code_id) "
                        + "VALUES ('Qualified', true, false, ?, ?) RETURNING id",
                Long.class, saId, buyerCodeId);
    }

    /** Auth shape matching what {@code JwtAuthenticationFilter} installs from a real JWT. */
    private static RequestPostProcessor admin() {
        return authentication(new UsernamePasswordAuthenticationToken(
                ADMIN_USER_ID,
                "admin@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_Administrator"))));
    }
}
