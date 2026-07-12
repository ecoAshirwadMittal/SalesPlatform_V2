package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.dto.RmaDetailResponse;
import com.ecoatm.salesplatform.dto.RmaSubmitResponse;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link RmaService#submitRmaRequest} against a real
 * PostgreSQL database, exercising the gap 0.2 / {@code VAL_RMARequestFile}
 * OfferItem match: an uploaded IMEI must resolve to a shipped OfferItem owned
 * by the buyer code (via {@code imei_detail → offer_item → offer.buyer_code_id}),
 * populating {@code device_id} / SKU / sale price on the RMA line and rolling up
 * the header totals. Unmatched IMEIs reject the whole submission.
 */
@Transactional
class RmaSubmitOfferItemMatchIT extends PostgresIntegrationTest {

    private static final long BUYER_CODE_ID = 90001L;
    private static final long DEVICE_ID = 90001L;
    private static final long OFFER_ID = 90001L;
    private static final long OFFER_ITEM_ID = 90001L;
    private static final long ORDER_ID = 90001L;
    private static final String MATCHED_IMEI = "IT-RMA-IMEI-1";
    private static final String REASON = "Physically Damaged"; // seeded active by V33

    @Autowired private RmaService rmaService;
    @Autowired private RmaRepository rmaRepository;
    @Autowired private RmaItemRepository rmaItemRepository;
    @Autowired private EntityManager em;

    @BeforeEach
    void seed() {
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyers (id, company_name, status)
                VALUES (:id, 'RMA Buyer Corp', 'Active') ON CONFLICT (id) DO NOTHING
                """).setParameter("id", BUYER_CODE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status, soft_delete)
                VALUES (:id, 'IT-RMA-BC', 'Wholesale', 'Active', false) ON CONFLICT (id) DO NOTHING
                """).setParameter("id", BUYER_CODE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                VALUES (:id, :id) ON CONFLICT DO NOTHING
                """).setParameter("id", BUYER_CODE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO mdm.device (id, sku, is_active, available_qty, atp_qty, list_price, min_price)
                VALUES (:id, 'IT-RMA-SKU', true, 100, 100, 200.00, 100.00) ON CONFLICT (id) DO NOTHING
                """).setParameter("id", DEVICE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws.offer (id, offer_type, status, buyer_code_id, total_qty, total_price, submission_date, updated_date)
                VALUES (:id, 'BUYER', 'Ordered', :bc, 1, 175.00, NOW(), NOW()) ON CONFLICT (id) DO NOTHING
                """).setParameter("id", OFFER_ID).setParameter("bc", BUYER_CODE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws.offer_item (id, offer_id, sku, device_id, quantity, price, total_price, final_offer_price, item_status)
                VALUES (:id, :offer, 'IT-RMA-SKU', :device, 1, 175.00, 175.00, 175, 'Finalize') ON CONFLICT (id) DO NOTHING
                """).setParameter("id", OFFER_ITEM_ID).setParameter("offer", OFFER_ID)
                .setParameter("device", DEVICE_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws.imei_detail (id, imei_number, offer_item_id)
                VALUES (:id, :imei, :oi) ON CONFLICT (id) DO NOTHING
                """).setParameter("id", 90001L).setParameter("imei", MATCHED_IMEI)
                .setParameter("oi", OFFER_ITEM_ID).executeUpdate();

        em.createNativeQuery("""
                INSERT INTO pws."order" (id, offer_id, order_number, order_status, ship_date, created_date, updated_date)
                VALUES (:id, :offer, 'ORD-RMA-IT-1', 'Submitted', NOW(), NOW(), NOW()) ON CONFLICT (id) DO NOTHING
                """).setParameter("id", ORDER_ID).setParameter("offer", OFFER_ID).executeUpdate();

        em.flush();
        em.clear();
    }

    private InputStream csv(String body) {
        return new ByteArrayInputStream(
                ("IMEI/Serial,Return Reason\n" + body).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("matched IMEI is accepted; line carries deviceId/SKU/sale price; roll-ups non-zero")
    void matchedImei_accepted_populatesFields() {
        RmaSubmitResponse response = rmaService.submitRmaRequest(
                BUYER_CODE_ID, null, csv(MATCHED_IMEI + "," + REASON + "\n"));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getRmaId()).isNotNull();

        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(response.getRmaId());
        assertThat(items).hasSize(1);
        RmaItem item = items.get(0);
        assertThat(item.getDeviceId()).isEqualTo(DEVICE_ID);
        assertThat(item.getSalePrice()).isNotNull().isEqualByComparingTo("175");
        assertThat(item.getOrderNumber()).isEqualTo("ORD-RMA-IT-1");
        assertThat(item.getShipDate()).isNotNull();

        Rma rma = rmaRepository.findById(response.getRmaId()).orElseThrow();
        assertThat(rma.getRequestQty()).isEqualTo(1);
        assertThat(rma.getRequestSkus()).isEqualTo(1);
        assertThat(rma.getRequestSalesTotal()).isNotNull().isEqualByComparingTo("175");

        // SKU resolves from the matched device on the detail projection.
        RmaDetailResponse detail = rmaService.getRmaDetail(response.getRmaId());
        assertThat(detail.getItems()).hasSize(1);
        assertThat(detail.getItems().get(0).getSku()).isEqualTo("IT-RMA-SKU");
    }

    @Test
    @DisplayName("unknown IMEI is rejected; no RMA persisted")
    void unknownImei_rejected_nothingPersisted() {
        long rmaCountBefore = rmaRepository.count();

        RmaSubmitResponse response = rmaService.submitRmaRequest(
                BUYER_CODE_ID, null, csv("NOPE-999," + REASON + "\n"));

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrors())
                .anySatisfy(e -> assertThat(e).contains("NOPE-999").contains("does not match"));
        assertThat(rmaRepository.count()).isEqualTo(rmaCountBefore);
    }
}
