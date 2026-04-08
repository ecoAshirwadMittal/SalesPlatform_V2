package com.ecoatm.salesplatform.model;

import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.Order;
import com.ecoatm.salesplatform.model.pws.ShipmentDetail;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises getter/setter coverage for JPA entity classes
 * that are only partially covered through service/controller tests.
 */
class EntityCoverageTest {

    // ── Order ───────────────────────────────────────────────────────────

    @Test
    void order_gettersSetters() {
        Order o = new Order();
        o.setId(1L);
        o.setLegacyId(100L);
        o.setOrderNumber("ORD-001");
        o.setOrderLine("LINE-1");
        o.setOrderStatus("Ordered");
        o.setOracleStatus("Success");
        o.setShipMethod("Ground");
        o.setShippedTotalQty(10);
        o.setShippedTotalPrice(BigDecimal.valueOf(500));
        LocalDateTime now = LocalDateTime.now();
        o.setOrderDate(now);
        o.setShipDate(now);
        o.setOracleHttpCode(200);
        o.setIsSuccessful(true);
        o.setOracleJsonResponse("{\"ok\":true}");
        o.setJsonContent("{\"payload\":true}");
        o.setBuyerCodeId(50L);
        o.setCreatedDate(now);
        o.setUpdatedDate(now);
        o.setShipments(new ArrayList<>());

        Offer offer = new Offer();
        offer.setId(10L);
        o.setOffer(offer);

        assertThat(o.getId()).isEqualTo(1L);
        assertThat(o.getLegacyId()).isEqualTo(100L);
        assertThat(o.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(o.getOrderLine()).isEqualTo("LINE-1");
        assertThat(o.getOrderStatus()).isEqualTo("Ordered");
        assertThat(o.getOracleStatus()).isEqualTo("Success");
        assertThat(o.getShipMethod()).isEqualTo("Ground");
        assertThat(o.getShippedTotalQty()).isEqualTo(10);
        assertThat(o.getShippedTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(o.getOrderDate()).isEqualTo(now);
        assertThat(o.getShipDate()).isEqualTo(now);
        assertThat(o.getOracleHttpCode()).isEqualTo(200);
        assertThat(o.getIsSuccessful()).isTrue();
        assertThat(o.getOracleJsonResponse()).isEqualTo("{\"ok\":true}");
        assertThat(o.getJsonContent()).isEqualTo("{\"payload\":true}");
        assertThat(o.getBuyerCodeId()).isEqualTo(50L);
        assertThat(o.getCreatedDate()).isEqualTo(now);
        assertThat(o.getUpdatedDate()).isEqualTo(now);
        assertThat(o.getOffer()).isNotNull();
        assertThat(o.getShipments()).isEmpty();
    }

    // ── ShipmentDetail ──────────────────────────────────────────────────

    @Test
    void shipmentDetail_gettersSetters() {
        ShipmentDetail sd = new ShipmentDetail();
        sd.setId(1L);
        sd.setLegacyId(200L);
        sd.setTrackingNumber("TRACK-001");
        sd.setTrackingUrl("https://track.example.com/001");
        sd.setSkuCount(3);
        sd.setQuantity(15);
        LocalDateTime now = LocalDateTime.now();
        sd.setCreatedDate(now);
        sd.setUpdatedDate(now);

        Order order = new Order();
        order.setId(10L);
        sd.setOrder(order);

        assertThat(sd.getId()).isEqualTo(1L);
        assertThat(sd.getLegacyId()).isEqualTo(200L);
        assertThat(sd.getTrackingNumber()).isEqualTo("TRACK-001");
        assertThat(sd.getTrackingUrl()).isEqualTo("https://track.example.com/001");
        assertThat(sd.getSkuCount()).isEqualTo(3);
        assertThat(sd.getQuantity()).isEqualTo(15);
        assertThat(sd.getCreatedDate()).isEqualTo(now);
        assertThat(sd.getUpdatedDate()).isEqualTo(now);
        assertThat(sd.getOrder()).isNotNull();
    }

    // ── CaseLot ─────────────────────────────────────────────────────────

    @Test
    void caseLot_gettersSetters() {
        CaseLot cl = new CaseLot();
        cl.setId(1L);
        cl.setCaseLotId("CL-001");
        cl.setCaseLotSize(10);
        cl.setCaseLotPrice(BigDecimal.valueOf(80));
        cl.setCaseLotAvlQty(50);
        cl.setCaseLotReservedQty(5);
        cl.setCaseLotAtpQty(45);
        cl.setIsActive(true);
        cl.setCreatedBy("admin");
        cl.setUpdatedBy("admin");

        Device device = new Device();
        device.setId(10L);
        cl.setDevice(device);

        assertThat(cl.getId()).isEqualTo(1L);
        assertThat(cl.getCaseLotId()).isEqualTo("CL-001");
        assertThat(cl.getCaseLotSize()).isEqualTo(10);
        assertThat(cl.getCaseLotPrice()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(cl.getCaseLotAvlQty()).isEqualTo(50);
        assertThat(cl.getCaseLotReservedQty()).isEqualTo(5);
        assertThat(cl.getCaseLotAtpQty()).isEqualTo(45);
        assertThat(cl.getIsActive()).isTrue();
        assertThat(cl.getCreatedBy()).isEqualTo("admin");
        assertThat(cl.getUpdatedBy()).isEqualTo("admin");
        assertThat(cl.getDevice()).isNotNull();
    }

    // ── Offer (uncovered setters) ───────────────────────────────────────

    @Test
    void offer_remainingGettersSetters() {
        Offer o = new Offer();
        o.setId(1L);
        o.setLegacyId(300L);
        o.setOfferType("BUYER");
        o.setStatus("Draft");
        o.setOfferNumber("OFF-001");
        o.setBuyerCodeId(100L);
        o.setTotalQty(10);
        o.setTotalPrice(BigDecimal.valueOf(500));
        LocalDateTime now = LocalDateTime.now();
        o.setSubmissionDate(now);
        o.setFinalOfferSubmittedOn(now);
        o.setCreatedDate(now);
        o.setUpdatedDate(now);
        o.setItems(new ArrayList<>());

        assertThat(o.getId()).isEqualTo(1L);
        assertThat(o.getLegacyId()).isEqualTo(300L);
        assertThat(o.getOfferType()).isEqualTo("BUYER");
        assertThat(o.getStatus()).isEqualTo("Draft");
        assertThat(o.getOfferNumber()).isEqualTo("OFF-001");
        assertThat(o.getBuyerCodeId()).isEqualTo(100L);
        assertThat(o.getTotalQty()).isEqualTo(10);
        assertThat(o.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(o.getSubmissionDate()).isEqualTo(now);
        assertThat(o.getFinalOfferSubmittedOn()).isEqualTo(now);
        assertThat(o.getCreatedDate()).isEqualTo(now);
        assertThat(o.getUpdatedDate()).isEqualTo(now);
        assertThat(o.getItems()).isEmpty();
    }

    // ── MDM lookup entities ─────────────────────────────────────────────

    @Test
    void baseLookup_subclasses() {
        // Test each BaseLookup subclass that has 0% coverage
        testLookup(new Condition());
        testLookup(new Capacity());
        testLookup(new Carrier());
        testLookup(new Grade());
        testLookup(new Category());
        testLookup(new Model());
        testLookup(new Color());
    }

    private void testLookup(BaseLookup lookup) {
        lookup.setId(1L);
        lookup.setLegacyId(100L);
        lookup.setName("test-name");
        lookup.setDisplayName("Test Name");
        lookup.setIsEnabled(true);
        lookup.setSortRank(5);

        assertThat(lookup.getId()).isEqualTo(1L);
        assertThat(lookup.getLegacyId()).isEqualTo(100L);
        assertThat(lookup.getName()).isEqualTo("test-name");
        assertThat(lookup.getDisplayName()).isEqualTo("Test Name");
        assertThat(lookup.getIsEnabled()).isTrue();
        assertThat(lookup.getSortRank()).isEqualTo(5);
    }

    // ── Simple model classes ────────────────────────────────────────────

    @Test
    void ecoATMDirectUser_instantiation() {
        EcoATMDirectUser u = new EcoATMDirectUser();
        assertThat(u).isNotNull();
    }

    @Test
    void account_instantiation() {
        Account a = new Account();
        assertThat(a).isNotNull();
    }
}
