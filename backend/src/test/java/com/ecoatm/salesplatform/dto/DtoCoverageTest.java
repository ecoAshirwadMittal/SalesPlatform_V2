package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises getter/setter and factory method coverage for DTOs
 * that are otherwise only partially covered through service tests.
 */
class DtoCoverageTest {

    // ── OracleResponse ──────────────────────────────────────────────────

    @Test
    void oracleResponse_simulateSuccess() {
        OracleResponse r = OracleResponse.simulateSuccess();
        assertThat(r.getReturnCode()).isEqualTo("00");
        assertThat(r.getReturnMessage()).isNotNull();
        assertThat(r.getOrderNumber()).isNotNull();
        assertThat(r.getOrderId()).isNotNull();
        assertThat(r.getHttpCode()).isEqualTo(200);
        assertThat(r.getJsonResponse()).isNotNull();
    }

    @Test
    void oracleResponse_gettersSetters() {
        OracleResponse r = new OracleResponse();
        r.setReturnCode("01");
        r.setReturnMessage("Error");
        r.setOrderNumber("ORD-1");
        r.setOrderId("ID-1");
        r.setHttpCode(500);
        r.setJsonResponse("{\"err\":true}");

        assertThat(r.getReturnCode()).isEqualTo("01");
        assertThat(r.getReturnMessage()).isEqualTo("Error");
        assertThat(r.getOrderNumber()).isEqualTo("ORD-1");
        assertThat(r.getOrderId()).isEqualTo("ID-1");
        assertThat(r.getHttpCode()).isEqualTo(500);
        assertThat(r.getJsonResponse()).isEqualTo("{\"err\":true}");
    }

    // ── CaseLotResponse ─────────────────────────────────────────────────

    @Test
    void caseLotResponse_fromEntity_withDevice() {
        Device device = new Device();
        device.setId(10L);
        device.setSku("SKU-CL");
        device.setListPrice(BigDecimal.valueOf(100));

        CaseLot cl = new CaseLot();
        cl.setId(1L);
        cl.setCaseLotId("CL-001");
        cl.setCaseLotSize(10);
        cl.setCaseLotAtpQty(5);
        cl.setCaseLotPrice(BigDecimal.valueOf(80));
        cl.setDevice(device);

        CaseLotResponse r = CaseLotResponse.fromEntity(cl);

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getCaseLotId()).isEqualTo("CL-001");
        assertThat(r.getCaseLotSize()).isEqualTo(10);
        assertThat(r.getCaseLotAtpQty()).isEqualTo(5);
        assertThat(r.getCaseLotPrice()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(r.getDeviceId()).isEqualTo(10L);
        assertThat(r.getSku()).isEqualTo("SKU-CL");
        assertThat(r.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
        // Null lookups since device has no model/carrier/etc
        assertThat(r.getModelName()).isNull();
        assertThat(r.getCarrierName()).isNull();
        assertThat(r.getCapacityName()).isNull();
        assertThat(r.getColorName()).isNull();
        assertThat(r.getGradeName()).isNull();
    }

    @Test
    void caseLotResponse_fromEntity_withoutDevice() {
        CaseLot cl = new CaseLot();
        cl.setId(2L);
        cl.setCaseLotId("CL-002");
        cl.setCaseLotSize(5);
        cl.setDevice(null);

        CaseLotResponse r = CaseLotResponse.fromEntity(cl);

        assertThat(r.getId()).isEqualTo(2L);
        assertThat(r.getDeviceId()).isNull();
        assertThat(r.getSku()).isNull();
    }

    // ── OfferItemResponse ───────────────────────────────────────────────

    @Test
    void offerItemResponse_fromEntity_withDevice() {
        OfferItem item = new OfferItem();
        item.setId(1L);
        item.setSku("SKU-1");
        item.setDeviceId(10L);
        item.setQuantity(5);
        item.setPrice(BigDecimal.valueOf(25));
        item.setTotalPrice(BigDecimal.valueOf(125));
        item.setItemStatus("Accept");
        item.setOfferDrawerStatus("Ordered");
        item.setBuyerCounterStatus("Accept");
        item.setCounterQty(3);
        item.setCounterPrice(BigDecimal.valueOf(20));
        item.setCounterTotal(BigDecimal.valueOf(60));
        item.setFinalOfferPrice(BigDecimal.valueOf(22));
        item.setFinalOfferQuantity(4);
        item.setFinalOfferTotalPrice(BigDecimal.valueOf(88));
        item.setCounterCasePriceTotal(BigDecimal.valueOf(200));

        Device device = new Device();
        device.setId(10L);
        device.setListPrice(BigDecimal.valueOf(30));
        device.setMinPrice(BigDecimal.valueOf(15));
        device.setAvailableQty(50);
        device.setItemType("UNIT");

        OfferItemResponse r = OfferItemResponse.fromEntity(item, device);

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getSku()).isEqualTo("SKU-1");
        assertThat(r.getDeviceId()).isEqualTo(10L);
        assertThat(r.getQuantity()).isEqualTo(5);
        assertThat(r.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(25));
        assertThat(r.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(125));
        assertThat(r.getItemStatus()).isEqualTo("Accept");
        assertThat(r.getOfferDrawerStatus()).isEqualTo("Ordered");
        assertThat(r.getBuyerCounterStatus()).isEqualTo("Accept");
        assertThat(r.getCounterQty()).isEqualTo(3);
        assertThat(r.getCounterPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(r.getCounterTotal()).isEqualByComparingTo(BigDecimal.valueOf(60));
        assertThat(r.getFinalOfferPrice()).isEqualByComparingTo(BigDecimal.valueOf(22));
        assertThat(r.getFinalOfferQuantity()).isEqualTo(4);
        assertThat(r.getFinalOfferTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(88));
        assertThat(r.getCounterCasePriceTotal()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(r.getListPrice()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(r.getMinPrice()).isEqualByComparingTo(BigDecimal.valueOf(15));
        assertThat(r.getAvailableQty()).isEqualTo(50);
        assertThat(r.getItemType()).isEqualTo("UNIT");
        assertThat(r.getCaseLotSize()).isNull();
    }

    @Test
    void offerItemResponse_fromEntity_withNullDevice() {
        OfferItem item = new OfferItem();
        item.setId(2L);
        item.setSku("SKU-2");

        OfferItemResponse r = OfferItemResponse.fromEntity(item, null);

        assertThat(r.getId()).isEqualTo(2L);
        assertThat(r.getListPrice()).isNull();
        assertThat(r.getCategoryName()).isNull();
    }

    @Test
    void offerItemResponse_fromEntityFull_withCaseLot() {
        OfferItem item = new OfferItem();
        item.setId(3L);
        item.setSku("SKU-CL");

        Device device = new Device();
        device.setId(10L);

        CaseLot caseLot = new CaseLot();
        caseLot.setCaseLotSize(10);

        OfferItemResponse r = OfferItemResponse.fromEntityFull(item, device, caseLot);

        assertThat(r.getCaseLotSize()).isEqualTo(10);
        assertThat(r.getItemType()).isNull();
    }

    // ── OfferListItem ───────────────────────────────────────────────────

    @Test
    void offerListItem_gettersSetters() {
        OfferListItem item = new OfferListItem();
        item.setOfferId(1L);
        item.setOfferNumber("OFF-001");
        item.setStatus("Sales_Review");
        item.setOrderNumber("ORD-001");
        item.setBuyerName("Buyer Corp");
        item.setBuyerCode("BC001");
        item.setSalesRepName("John Doe");
        item.setTotalSkus(5);
        item.setTotalQty(50);
        item.setTotalPrice(BigDecimal.valueOf(500));
        LocalDateTime now = LocalDateTime.now();
        item.setSubmissionDate(now);
        item.setUpdatedDate(now);

        assertThat(item.getOfferId()).isEqualTo(1L);
        assertThat(item.getOfferNumber()).isEqualTo("OFF-001");
        assertThat(item.getStatus()).isEqualTo("Sales_Review");
        assertThat(item.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(item.getBuyerName()).isEqualTo("Buyer Corp");
        assertThat(item.getBuyerCode()).isEqualTo("BC001");
        assertThat(item.getSalesRepName()).isEqualTo("John Doe");
        assertThat(item.getTotalSkus()).isEqualTo(5);
        assertThat(item.getTotalQty()).isEqualTo(50);
        assertThat(item.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(item.getSubmissionDate()).isEqualTo(now);
        assertThat(item.getUpdatedDate()).isEqualTo(now);
    }

    // ── DeviceRequest ───────────────────────────────────────────────────

    @Test
    void deviceRequest_gettersSetters() {
        DeviceRequest req = new DeviceRequest();
        req.setSku("SKU-001");
        req.setDeviceCode("DC-001");
        req.setDescription("Test Device");
        req.setListPrice(BigDecimal.valueOf(100));
        req.setMinPrice(BigDecimal.valueOf(50));
        req.setFutureListPrice(BigDecimal.valueOf(110));
        req.setFutureMinPrice(BigDecimal.valueOf(55));
        req.setAvailableQty(200);
        req.setReservedQty(10);
        req.setAtpQty(190);
        req.setWeight(BigDecimal.valueOf(0.5));
        req.setItemType("UNIT");
        req.setBrandId(1L);
        req.setBrandName("Apple");
        req.setCategoryId(2L);
        req.setCategoryName("Phone");
        req.setModelId(3L);
        req.setModelName("iPhone 15");
        req.setConditionId(4L);
        req.setConditionName("Good");
        req.setCapacityId(5L);
        req.setCapacityName("128GB");
        req.setCarrierId(6L);
        req.setCarrierName("Verizon");
        req.setColorId(7L);
        req.setColorName("Black");
        req.setGradeId(8L);
        req.setGradeName("A");

        assertThat(req.getSku()).isEqualTo("SKU-001");
        assertThat(req.getDeviceCode()).isEqualTo("DC-001");
        assertThat(req.getDescription()).isEqualTo("Test Device");
        assertThat(req.getListPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(req.getMinPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(req.getFutureListPrice()).isEqualByComparingTo(BigDecimal.valueOf(110));
        assertThat(req.getFutureMinPrice()).isEqualByComparingTo(BigDecimal.valueOf(55));
        assertThat(req.getAvailableQty()).isEqualTo(200);
        assertThat(req.getReservedQty()).isEqualTo(10);
        assertThat(req.getAtpQty()).isEqualTo(190);
        assertThat(req.getWeight()).isEqualByComparingTo(BigDecimal.valueOf(0.5));
        assertThat(req.getItemType()).isEqualTo("UNIT");
        assertThat(req.getBrandId()).isEqualTo(1L);
        assertThat(req.getBrandName()).isEqualTo("Apple");
        assertThat(req.getCategoryId()).isEqualTo(2L);
        assertThat(req.getCategoryName()).isEqualTo("Phone");
        assertThat(req.getModelId()).isEqualTo(3L);
        assertThat(req.getModelName()).isEqualTo("iPhone 15");
        assertThat(req.getConditionId()).isEqualTo(4L);
        assertThat(req.getConditionName()).isEqualTo("Good");
        assertThat(req.getCapacityId()).isEqualTo(5L);
        assertThat(req.getCapacityName()).isEqualTo("128GB");
        assertThat(req.getCarrierId()).isEqualTo(6L);
        assertThat(req.getCarrierName()).isEqualTo("Verizon");
        assertThat(req.getColorId()).isEqualTo(7L);
        assertThat(req.getColorName()).isEqualTo("Black");
        assertThat(req.getGradeId()).isEqualTo(8L);
        assertThat(req.getGradeName()).isEqualTo("A");
    }

    // ── DeposcoInventoryDto ─────────────────────────────────────────────

    @Test
    void deposcoInventoryDto_inventoryPage() {
        DeposcoInventoryDto.InventoryPage page = new DeposcoInventoryDto.InventoryPage();
        page.setPageNumber(1);
        page.setTotalPages(5);
        page.setItems(List.of());

        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getItems()).isEmpty();
    }

    @Test
    void deposcoInventoryDto_facilityInventory() {
        DeposcoInventoryDto.FacilityInventory fi = new DeposcoInventoryDto.FacilityInventory();
        fi.setFacility("WH-1");
        fi.setTotal(BigDecimal.valueOf(100));
        fi.setAvailableToPromise(BigDecimal.valueOf(80));
        fi.setUnallocated(BigDecimal.valueOf(70));
        fi.setAllocated(BigDecimal.valueOf(30));

        assertThat(fi.getFacility()).isEqualTo("WH-1");
        assertThat(fi.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fi.getAvailableToPromise()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(fi.getUnallocated()).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(fi.getAllocated()).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    void deposcoInventoryDto_itemInventory() {
        DeposcoInventoryDto.ItemInventory ii = new DeposcoInventoryDto.ItemInventory();
        ii.setItemNumber("SKU-001");
        ii.setPageNo(1);
        ii.setFacilities(List.of());

        assertThat(ii.getItemNumber()).isEqualTo("SKU-001");
        assertThat(ii.getPageNo()).isEqualTo(1);
        assertThat(ii.getFacilities()).isEmpty();
    }
}
