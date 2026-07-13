package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.Order;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context IT for {@link BulkOfferStatusController} against real Flyway'd
 * Postgres — mirrors {@link SalesRepControllerIT} (extends
 * {@link PostgresIntegrationTest}, {@code @AutoConfigureMockMvc}, Long-principal
 * auth via the {@code authentication(...)} post-processor). {@code @Transactional}
 * rolls back seeded + mutated rows after each test.
 *
 * <p>Date-range cases use a far-future ({@code 2099}) window so the resolver hits
 * only the order seeded by the test — the shared dev DB's migrated orders all
 * carry historical {@code order_date}s, so 2099 is a collision-free namespace and
 * the counts stay deterministic.
 */
@AutoConfigureMockMvc
@Transactional
class BulkOfferStatusControllerIT extends PostgresIntegrationTest {

    private static final long ADMIN_USER_ID = 9001L;    // admin@test.com (Administrator)
    private static final long SALESOPS_USER_ID = 9003L; // salesops@test.com (SalesOps)
    private static final String URL = "/api/v1/admin/pws/bulk-status";

    @Autowired private MockMvc mvc;
    @Autowired private OfferRepository offerRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private JdbcTemplate jdbc;

    // ── authz matrix ────────────────────────────────────────────────────

    @Test
    @DisplayName("Unauthenticated → 401")
    void unauthenticatedReturns401() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(allPeriodBody(1L, "CANCELLED")))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Bidder → 403 (Administrator-only)")
    void bidderForbidden() throws Exception {
        mvc.perform(post(URL).with(bidder()).contentType(MediaType.APPLICATION_JSON)
                        .content(allPeriodBody(1L, "CANCELLED")))
           .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("SalesOps → 403 (this tool is narrower than the /admin/** norm)")
    void salesOpsForbidden() throws Exception {
        mvc.perform(post(URL).with(salesOps()).contentType(MediaType.APPLICATION_JSON)
                        .content(allPeriodBody(1L, "CANCELLED")))
           .andExpect(status().isForbidden());
    }

    // ── validation ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Administrator + invalid date range (end <= start) → 400")
    void badDateRangeReturns400() throws Exception {
        String body = """
                {"allPeriod":false,"startingDate":"2099-01-31","endingDate":"2099-01-01",
                 "fromOfferStatus":"SALES_REVIEW","toOrderStatus":"CANCELLED",
                 "notOrderStatusChange":false,"hasShipmentDetails":false}
                """;
        mvc.perform(post(URL).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Administrator + metadata-only (notOrderStatusChange) → 400 (unsupported — pws.order has no has_shipment_details column)")
    void metadataOnlyReturns400() throws Exception {
        // The metadata-only path cannot persist hasShipmentDetails on the modern
        // schema, so it is rejected outright rather than pretending success.
        String body = """
                {"allPeriod":false,"startingDate":"2099-01-01","endingDate":"2099-01-31",
                 "notOrderStatusChange":true,"hasShipmentDetails":true}
                """;
        mvc.perform(post(URL).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest());
    }

    // ── happy paths ─────────────────────────────────────────────────────

    @Test
    @DisplayName("allPeriod status change → 200, the selected order's offer flips + audit row written")
    void allPeriodHappyPath() throws Exception {
        long offerId = seedOffer("SALES_REVIEW");
        long orderId = seedOrder(offerId, LocalDateTime.of(2099, 6, 15, 12, 0));

        mvc.perform(post(URL).with(admin()).contentType(MediaType.APPLICATION_JSON)
                        .content(allPeriodBody(orderId, "CANCELLED")))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.matchedOrders").value(1))
           .andExpect(jsonPath("$.changedOffers").value(1))
           .andExpect(jsonPath("$.metadataOnly").value(false));

        assertThat(offerRepository.findById(offerId)).get()
                .extracting(Offer::getStatus).isEqualTo("CANCELLED");

        Integer auditRows = jdbc.queryForObject(
                "SELECT COUNT(*) FROM pws.admin_audit_log "
                        + "WHERE action='BULK_STATUS_CHANGE' AND actor='admin@test.com'",
                Integer.class);
        assertThat(auditRows).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("date-range status change → 200, only the in-range matching offer flips")
    void dateRangeHappyPath() throws Exception {
        long offerId = seedOffer("SALES_REVIEW");
        seedOrder(offerId, LocalDateTime.of(2099, 6, 15, 12, 0));

        String body = """
                {"allPeriod":false,"startingDate":"2099-06-01","endingDate":"2099-06-30",
                 "fromOfferStatus":"SALES_REVIEW","toOrderStatus":"CANCELLED",
                 "notOrderStatusChange":false,"hasShipmentDetails":false}
                """;
        mvc.perform(post(URL).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.matchedOrders").value(1))
           .andExpect(jsonPath("$.changedOffers").value(1));

        assertThat(offerRepository.findById(offerId)).get()
                .extracting(Offer::getStatus).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("date-range with a non-matching fromOfferStatus → 200 but 0 offers changed (the guard)")
    void dateRangeGuardBlocksNonMatching() throws Exception {
        long offerId = seedOffer("SALES_REVIEW");
        seedOrder(offerId, LocalDateTime.of(2099, 7, 15, 12, 0));

        String body = """
                {"allPeriod":false,"startingDate":"2099-07-01","endingDate":"2099-07-31",
                 "fromOfferStatus":"PENDING_ORDER","toOrderStatus":"CANCELLED",
                 "notOrderStatusChange":false,"hasShipmentDetails":false}
                """;
        mvc.perform(post(URL).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.matchedOrders").value(1))
           .andExpect(jsonPath("$.changedOffers").value(0));

        // The guard held — the offer is unchanged.
        assertThat(offerRepository.findById(offerId)).get()
                .extracting(Offer::getStatus).isEqualTo("SALES_REVIEW");
    }

    // ── seeding helpers ─────────────────────────────────────────────────

    private long seedOffer(String status) {
        Offer offer = new Offer();
        offer.setOfferType("BUYER");
        offer.setStatus(status);
        offer.setVisibleInHistory(true);
        return offerRepository.saveAndFlush(offer).getId();
    }

    private long seedOrder(long offerId, LocalDateTime orderDate) {
        Offer offer = offerRepository.findById(offerId).orElseThrow();
        Order order = new Order();
        order.setOffer(offer);
        order.setOrderDate(orderDate);
        order.setOrderNumber("BULK-IT-" + System.nanoTime());
        return orderRepository.saveAndFlush(order).getId();
    }

    // ── request bodies ──────────────────────────────────────────────────

    private static String allPeriodBody(long orderId, String toStatus) {
        return "{\"allPeriod\":true,\"toOrderStatus\":\"" + toStatus + "\","
                + "\"notOrderStatusChange\":false,\"hasShipmentDetails\":false,"
                + "\"orderIds\":[" + orderId + "]}";
    }

    // ── auth shapes (Long principal, matching JwtAuthenticationFilter) ──

    private static RequestPostProcessor admin() {
        return principal(ADMIN_USER_ID, "admin@test.com", "ROLE_Administrator");
    }

    private static RequestPostProcessor salesOps() {
        return principal(SALESOPS_USER_ID, "salesops@test.com", "ROLE_SalesOps");
    }

    private static RequestPostProcessor bidder() {
        return principal(9999L, "bidder@buyerco.com", "ROLE_Bidder");
    }

    private static RequestPostProcessor principal(long userId, String email, String role) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId, email, List.of(new SimpleGrantedAuthority(role))));
    }
}
