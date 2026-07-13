package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context, real-Postgres IT for buyer DRAFT deletion (gap 2.5). Unlike
 * the {@link BuyerPartialCreditControllerIT} {@code @WebMvcTest} slice (which
 * mocks the service), this drives the real controller →
 * {@code CreditRequestService.deleteDraft} → a real
 * {@code CreditRequestRepository.deleteById} against a Flyway'd Postgres, so it
 * proves the one thing the slice cannot: the row is <em>actually gone</em> and
 * the V89 {@code ON DELETE CASCADE} child rows go with it.
 *
 * <p>Ownership is exercised for real: the happy path calls as a user resolved
 * from an existing {@code user_buyers → buyer_code_buyers} link, so the
 * service's {@code ensureBuyerCodeOwnership} passes on genuine ownership rather
 * than the admin bypass. The foreign case calls as a user that owns nothing.
 *
 * <p>Auth is injected via the {@code authentication(...)} post-processor — the
 * same principal shape {@code JwtAuthenticationFilter} installs from a real JWT
 * (a {@code Long} user id + {@code ROLE_*} authority) — mirroring
 * {@link com.ecoatm.salesplatform.controller.admin.QualifiedBuyerCodeAdminControllerIT}.
 * The class is {@code @Transactional}: {@code deleteDraft} uses plain
 * {@code @Transactional} (propagation REQUIRED), so it joins the test
 * transaction and every fixture + delete rolls back after each test.
 */
@AutoConfigureMockMvc
@Transactional
class BuyerPartialCreditDeleteIT extends PostgresIntegrationTest {

    /** A user id that owns no buyer code — drives the foreign-caller 403. */
    private static final long FOREIGN_USER_ID = 987_654_321L;

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;

    /**
     * Shared, transaction-bound persistence context — the same one the service's
     * {@code deleteById} enqueues the remove into. {@code deleteById} does not
     * flush, and the service {@code @Transactional} joins this test's tx (so no
     * commit happens when it returns), so the DELETE stays pending in the
     * persistence context. An explicit {@link EntityManager#flush()} pushes it to
     * Postgres — which is also what fires the V89 {@code ON DELETE CASCADE} — so
     * the subsequent raw-JDBC counts observe the real post-delete DB state.
     */
    @PersistenceContext private EntityManager em;

    @Test
    @DisplayName("owner deletes a DRAFT -> 204, the row and its cascade children are gone")
    void deleteOwnerDraft_returns204_rowAndChildrenGone() throws Exception {
        long[] pair = anyOwnershipPair();
        long ownerUserId = pair[0];
        long buyerCodeId = pair[1];

        long crId = seedCreditRequest(buyerCodeId, "DRAFT");
        long lineId = seedMissingLine(crId);

        mvc.perform(delete("/api/v1/buyer/partial-credit/{id}", crId).with(bidder(ownerUserId)))
                .andExpect(status().isNoContent());

        // Force the pending JPA remove to Postgres so the DB-level cascade fires.
        em.flush();

        assertThat(countCreditRequests(crId)).isZero();
        // V89 ON DELETE CASCADE removed the child line with the parent.
        assertThat(countMissingLines(lineId)).isZero();
    }

    @Test
    @DisplayName("owner deletes a submitted request -> 409, the row survives")
    void deleteNonDraft_returns409_rowSurvives() throws Exception {
        long[] pair = anyOwnershipPair();
        long ownerUserId = pair[0];
        long buyerCodeId = pair[1];

        long crId = seedCreditRequest(buyerCodeId, "PENDING_APPROVAL");

        mvc.perform(delete("/api/v1/buyer/partial-credit/{id}", crId).with(bidder(ownerUserId)))
                .andExpect(status().isConflict());

        assertThat(countCreditRequests(crId)).isEqualTo(1);
    }

    @Test
    @DisplayName("a caller who does not own the buyer code -> 403, the row survives")
    void deleteForeignCaller_returns403_rowSurvives() throws Exception {
        long buyerCodeId = anyOwnershipPair()[1];
        long crId = seedCreditRequest(buyerCodeId, "DRAFT");

        mvc.perform(delete("/api/v1/buyer/partial-credit/{id}", crId).with(bidder(FOREIGN_USER_ID)))
                .andExpect(status().isForbidden());

        assertThat(countCreditRequests(crId)).isEqualTo(1);
    }

    @Test
    @DisplayName("no authentication -> 401")
    void deleteUnauthenticated_returns401() throws Exception {
        mvc.perform(delete("/api/v1/buyer/partial-credit/1"))
                .andExpect(status().isUnauthorized());
    }

    // ─── seeding + lookups ──────────────────────────────────────────────

    /**
     * Resolves an existing {@code (owner user id, owned buyer_code id)} pair
     * from the seeded {@code user_buyers → buyer_code_buyers} graph — the same
     * join the service's ownership guard evaluates — so the happy path exercises
     * genuine ownership without hand-seeding the whole chain.
     */
    private long[] anyOwnershipPair() {
        return jdbc.queryForObject(
                "SELECT ub.user_id, bcb.buyer_code_id "
                        + "FROM user_mgmt.user_buyers ub "
                        + "JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_id = ub.buyer_id "
                        + "LIMIT 1",
                (rs, n) -> new long[] {rs.getLong(1), rs.getLong(2)});
    }

    private long seedCreditRequest(long buyerCodeId, String systemStatus) {
        Long statusId = jdbc.queryForObject(
                "SELECT id FROM partial_credit.credit_request_statuses WHERE system_status = ?",
                Long.class, systemStatus);
        return jdbc.queryForObject(
                "INSERT INTO partial_credit.credit_requests "
                        + "(request_number, status_id, order_number, buyer_code_id) "
                        + "VALUES (?, ?, ?, ?) RETURNING id",
                Long.class,
                "PCR-DEL-" + System.nanoTime(), statusId, "SO-DEL-1", buyerCodeId);
    }

    private long seedMissingLine(long creditRequestId) {
        return jdbc.queryForObject(
                "INSERT INTO partial_credit.missing_device_lines "
                        + "(credit_request_id, barcode_submitted) VALUES (?, ?) RETURNING id",
                Long.class, creditRequestId, "BC-DEL-1");
    }

    private int countCreditRequests(long id) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM partial_credit.credit_requests WHERE id = ?",
                Integer.class, id);
    }

    private int countMissingLines(long id) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM partial_credit.missing_device_lines WHERE id = ?",
                Integer.class, id);
    }

    /** Auth shape matching what {@code JwtAuthenticationFilter} installs from a real JWT. */
    private static RequestPostProcessor bidder(long userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId,
                "buyer@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_Bidder"))));
    }
}
