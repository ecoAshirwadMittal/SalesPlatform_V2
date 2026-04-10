package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.dto.OfferListItem;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.OfferSummary;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for {@link OfferReviewService} against a real PostgreSQL database.
 * <p>
 * Validates that:
 * - Native SQL queries for order numbers execute without errors
 * - BuyerCodeLookupService queries work in the context of offer listing
 * - Status filtering, summary aggregation, and detail loading all work end-to-end
 * - Lazy-loaded offer items are accessible within the request scope
 */
@Transactional
class OfferReviewServiceIT extends PostgresIntegrationTest {

    @Autowired
    private OfferReviewService offerReviewService;

    @Autowired
    private EntityManager em;

    private Long offerId1; // Sales_Review offer with items
    private Long offerId2; // Ordered offer with order number

    @BeforeEach
    void seedTestData() {
        // Sales rep + buyer + buyer code chain
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.sales_representatives (id, first_name, last_name, active)
                VALUES (80001, 'Sales', 'Person', true)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyers (id, company_name, status)
                VALUES (80001, 'Test Buyer Corp', 'Active')
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_sales_reps (buyer_id, sales_rep_id)
                VALUES (80001, 80001)
                ON CONFLICT DO NOTHING
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status, soft_delete)
                VALUES (80001, 'IT-BC-001', 'Wholesale', 'Active', false)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                VALUES (80001, 80001)
                ON CONFLICT DO NOTHING
                """).executeUpdate();

        // MDM device for offer items
        em.createNativeQuery("""
                INSERT INTO mdm.device (id, sku, is_active, available_qty, list_price, min_price)
                VALUES (80001, 'IT-SKU-001', true, 100, 50.00, 30.00)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        // Offer 1: Sales_Review with items
        em.createNativeQuery("""
                INSERT INTO pws.offer (id, offer_type, status, buyer_code_id, total_qty, total_price, submission_date, updated_date)
                VALUES (80001, 'BUYER', 'Sales_Review', 80001, 10, 500.00, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET status = 'Sales_Review'
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws.offer_item (id, offer_id, sku, device_id, quantity, price, total_price, item_status)
                VALUES (80001, 80001, 'IT-SKU-001', 80001, 10, 50.00, 500.00, null)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        // Offer 2: Ordered with an order
        em.createNativeQuery("""
                INSERT INTO pws.offer (id, offer_type, status, buyer_code_id, total_qty, total_price, submission_date, updated_date)
                VALUES (80002, 'BUYER', 'Ordered', 80001, 5, 250.00, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET status = 'Ordered'
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws.offer_item (id, offer_id, sku, device_id, quantity, price, total_price, item_status)
                VALUES (80002, 80002, 'IT-SKU-001', 80001, 5, 50.00, 250.00, 'Accept')
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws."order" (id, offer_id, order_number, order_status, created_date, updated_date)
                VALUES (80001, 80002, 'ORD-IT-001', 'Submitted', NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET order_number = 'ORD-IT-001'
                """).executeUpdate();

        offerId1 = 80001L;
        offerId2 = 80002L;
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("getStatusSummaries")
    class StatusSummaries {

        @Test
        @DisplayName("returns summaries for all statuses including Total")
        void returnsSummaries() {
            List<OfferSummary> summaries = offerReviewService.getStatusSummaries();
            assertThat(summaries).isNotEmpty();
            assertThat(summaries).extracting(OfferSummary::getStatus)
                    .contains("Sales_Review", "Ordered", "Total");
        }

        @Test
        @DisplayName("Total count equals sum of individual statuses")
        void totalEqualsSum() {
            List<OfferSummary> summaries = offerReviewService.getStatusSummaries();
            OfferSummary total = summaries.stream()
                    .filter(s -> "Total".equals(s.getStatus())).findFirst().orElseThrow();
            long sum = summaries.stream()
                    .filter(s -> !"Total".equals(s.getStatus()))
                    .mapToLong(OfferSummary::getOfferCount).sum();
            assertThat(total.getOfferCount()).isEqualTo(sum);
        }
    }

    @Nested
    @DisplayName("listOffers")
    class ListOffers {

        @Test
        @DisplayName("Total returns all non-Draft offers with buyer info populated")
        void totalStatus() {
            List<OfferListItem> offers = offerReviewService.listOffers("Total");
            assertThat(offers).isNotEmpty();

            // Find our test offer
            OfferListItem testOffer = offers.stream()
                    .filter(o -> o.getOfferId().equals(offerId1))
                    .findFirst().orElse(null);
            assertThat(testOffer).isNotNull();
            assertThat(testOffer.getBuyerCode()).isEqualTo("IT-BC-001");
            assertThat(testOffer.getBuyerName()).isEqualTo("Test Buyer Corp");
            assertThat(testOffer.getSalesRepName()).isEqualTo("Sales Person");
        }

        @Test
        @DisplayName("filters by specific status")
        void filterByStatus() {
            List<OfferListItem> offers = offerReviewService.listOffers("Sales_Review");
            assertThat(offers.stream().anyMatch(o -> o.getOfferId().equals(offerId1))).isTrue();
            assertThat(offers.stream().noneMatch(o -> o.getOfferId().equals(offerId2))).isTrue();
        }

        @Test
        @DisplayName("populates order number for Ordered offers")
        void orderNumberPopulated() {
            List<OfferListItem> offers = offerReviewService.listOffers("Ordered");
            OfferListItem ordered = offers.stream()
                    .filter(o -> o.getOfferId().equals(offerId2))
                    .findFirst().orElse(null);
            assertThat(ordered).isNotNull();
            assertThat(ordered.getOrderNumber()).isEqualTo("ORD-IT-001");
        }

        @Test
        @DisplayName("counts active SKUs correctly")
        void skuCountCorrect() {
            List<OfferListItem> offers = offerReviewService.listOffers("Sales_Review");
            OfferListItem testOffer = offers.stream()
                    .filter(o -> o.getOfferId().equals(offerId1))
                    .findFirst().orElseThrow();
            assertThat(testOffer.getTotalSkus()).isEqualTo(1);
        }

        @Test
        @DisplayName("null status treated as Total")
        void nullStatusIsTotal() {
            List<OfferListItem> offersNull = offerReviewService.listOffers(null);
            List<OfferListItem> offersTotal = offerReviewService.listOffers("Total");
            assertThat(offersNull).hasSameSizeAs(offersTotal);
        }
    }

    @Nested
    @DisplayName("getOfferDetail")
    class GetOfferDetail {

        @Test
        @DisplayName("returns offer with items and device info")
        void returnsDetail() {
            OfferResponse detail = offerReviewService.getOfferDetail(offerId1);
            assertThat(detail).isNotNull();
            assertThat(detail.getItems()).hasSize(1);
            assertThat(detail.getItems().get(0).getSku()).isEqualTo("IT-SKU-001");
        }

        @Test
        @DisplayName("throws for non-existent offer")
        void throwsForNonExistent() {
            assertThatThrownBy(() -> offerReviewService.getOfferDetail(999999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not found");
        }
    }
}
