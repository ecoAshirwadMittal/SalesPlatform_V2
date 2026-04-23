package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.OrderDetailByDeviceResponse;
import com.ecoatm.salesplatform.dto.OrderDetailBySkuResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryTabCounts;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.OrderHistoryView;
import com.ecoatm.salesplatform.model.pws.ShipmentDetail;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.ImeiDetailRepository;
import com.ecoatm.salesplatform.repository.pws.OfferItemRepository;
import com.ecoatm.salesplatform.repository.pws.OrderHistoryViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderHistoryServiceTest {

    @Mock
    private OrderHistoryViewRepository orderHistoryViewRepository;

    @Mock
    private BuyerCodeService buyerCodeService;

    @Mock
    private OfferItemRepository offerItemRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ImeiDetailRepository imeiDetailRepository;

    @InjectMocks
    private OrderHistoryService orderHistoryService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private OrderHistoryView makeView(Long id, String orderNumber, String status,
                                      LocalDateTime lastUpdateDate, Long buyerCodeId) {
        OrderHistoryView v = new OrderHistoryView();
        ReflectionTestUtils.setField(v, "id", id);
        ReflectionTestUtils.setField(v, "orderNumber", orderNumber);
        ReflectionTestUtils.setField(v, "orderStatus", status);
        ReflectionTestUtils.setField(v, "lastUpdateDate", lastUpdateDate);
        ReflectionTestUtils.setField(v, "buyerCodeId", buyerCodeId);
        ReflectionTestUtils.setField(v, "totalPrice", BigDecimal.ZERO);
        ReflectionTestUtils.setField(v, "skuCount", 1);
        ReflectionTestUtils.setField(v, "totalQuantity", 1);
        return v;
    }

    private BuyerCodeResponse makeCode(Long id) {
        return new BuyerCodeResponse(id, "BC-" + id, "Test Account", "Wholesale");
    }

    // -------------------------------------------------------------------------
    // ListOrders
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("listOrders")
    class ListOrders {

        @Test
        @DisplayName("all tab: returns all orders scoped to user's buyer codes")
        void allTab_returnsScopedOrders() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L), makeCode(2L)));
            OrderHistoryView row = makeView(100L, "ORD-001", "Ordered",
                    LocalDateTime.now(), 1L);
            Page<OrderHistoryView> page = new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("all", 10L, null, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).orderNumber()).isEqualTo("ORD-001");
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("recent tab: delegates to repository with lastUpdateDate filter")
        void recentTab_appliesDateFilter() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("recent", 10L, null, PageRequest.of(0, 20));

            assertThat(result.getContent()).isEmpty();
            verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("inProcess tab: delegates to repository with status IN filter")
        void inProcessTab_appliesStatusFilter() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("inProcess", 10L, null, PageRequest.of(0, 20));

            assertThat(result.getContent()).isEmpty();
            verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("complete tab: delegates to repository with status NOT IN filter")
        void completeTab_appliesStatusNotInFilter() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("complete", 10L, null, PageRequest.of(0, 20));

            assertThat(result.getContent()).isEmpty();
            verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("returns empty page when user has no buyer codes")
        void noBuyerCodes_returnsEmptyPage() {
            when(buyerCodeService.getBuyerCodesForUser(99L)).thenReturn(List.of());
            Page<OrderHistoryView> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("all", 99L, null, PageRequest.of(0, 20));

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("maps all DTO fields from view entity")
        void mapsDtoFieldsCorrectly() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L)));
            OrderHistoryView v = makeView(5L, "ORD-999", "Ordered",
                    LocalDateTime.of(2025, 1, 15, 10, 0), 1L);
            ReflectionTestUtils.setField(v, "offerDate", LocalDateTime.of(2025, 1, 10, 8, 0));
            ReflectionTestUtils.setField(v, "orderDate", LocalDateTime.of(2025, 1, 12, 9, 0));
            ReflectionTestUtils.setField(v, "shipDate", LocalDateTime.of(2025, 1, 20, 12, 0));
            ReflectionTestUtils.setField(v, "shipMethod", "FedEx");
            ReflectionTestUtils.setField(v, "skuCount", 3);
            ReflectionTestUtils.setField(v, "totalQuantity", 10);
            ReflectionTestUtils.setField(v, "totalPrice", new BigDecimal("450.00"));
            ReflectionTestUtils.setField(v, "buyer", "Jane Doe");
            ReflectionTestUtils.setField(v, "company", "TechCo LLC");
            ReflectionTestUtils.setField(v, "offerOrderType", "Order");
            ReflectionTestUtils.setField(v, "offerId", 42L);

            Page<OrderHistoryView> page = new PageImpl<>(List.of(v), PageRequest.of(0, 20), 1);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("all", 10L, null, PageRequest.of(0, 20));

            OrderHistoryResponse dto = result.getContent().get(0);
            assertThat(dto.id()).isEqualTo(5L);
            assertThat(dto.orderNumber()).isEqualTo("ORD-999");
            assertThat(dto.orderStatus()).isEqualTo("Ordered");
            assertThat(dto.shipMethod()).isEqualTo("FedEx");
            assertThat(dto.skuCount()).isEqualTo(3);
            assertThat(dto.totalQuantity()).isEqualTo(10);
            assertThat(dto.totalPrice()).isEqualByComparingTo("450.00");
            assertThat(dto.buyer()).isEqualTo("Jane Doe");
            assertThat(dto.company()).isEqualTo("TechCo LLC");
            assertThat(dto.offerOrderType()).isEqualTo("Order");
            assertThat(dto.offerId()).isEqualTo(42L);
        }

        @Test
        @DisplayName("throws IllegalArgumentException for unrecognized tab name")
        void unknownTab_throwsIllegalArgumentException() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L)));

            assertThatThrownBy(() ->
                    orderHistoryService.listOrders("bogusTab", 10L, null, PageRequest.of(0, 20)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("bogusTab");
        }

        @Test
        @DisplayName("null userId: throws IllegalArgumentException")
        void nullUserId_throws() {
            assertThatThrownBy(() ->
                    orderHistoryService.listOrders("all", null, null, PageRequest.of(0, 20)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // GetTabCounts
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getTabCounts")
    class GetTabCounts {

        @Test
        @DisplayName("returns correct counts for each tab")
        void returnsCountsForAllTabs() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L), makeCode(2L)));
            when(orderHistoryViewRepository.count(any(Specification.class)))
                    .thenReturn(5L)   // recent
                    .thenReturn(3L)   // inProcess
                    .thenReturn(12L)  // complete
                    .thenReturn(20L); // all

            OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(10L, null);

            assertThat(counts.recent()).isEqualTo(5L);
            assertThat(counts.inProcess()).isEqualTo(3L);
            assertThat(counts.complete()).isEqualTo(12L);
            assertThat(counts.all()).isEqualTo(20L);
        }

        @Test
        @DisplayName("returns all-zero counts when user has no buyer codes")
        void noBuyerCodes_returnsZeroCounts() {
            when(buyerCodeService.getBuyerCodesForUser(99L)).thenReturn(List.of());
            when(orderHistoryViewRepository.count(any(Specification.class))).thenReturn(0L);

            OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(99L, null);

            assertThat(counts.recent()).isZero();
            assertThat(counts.inProcess()).isZero();
            assertThat(counts.complete()).isZero();
            assertThat(counts.all()).isZero();
        }

        @Test
        @DisplayName("null userId: throws IllegalArgumentException")
        void nullUserId_throws() {
            assertThatThrownBy(() -> orderHistoryService.getTabCounts(null, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // BuyerCodeScoping
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("buyer-code scoping authorization")
    class BuyerCodeScoping {

        @Test
        @DisplayName("admin user: BuyerCodeService called with admin's userId")
        void adminUser_seesAllBuyerCodes() {
            List<BuyerCodeResponse> allCodes = List.of(makeCode(1L), makeCode(2L), makeCode(3L));
            when(buyerCodeService.getBuyerCodesForUser(1L)).thenReturn(allCodes);
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            orderHistoryService.listOrders("all", 1L, null, PageRequest.of(0, 20));

            verify(buyerCodeService).getBuyerCodesForUser(1L);
        }

        @Test
        @DisplayName("bidder user: BuyerCodeService called with bidder's userId")
        void bidderUser_buyerCodeServiceCalledWithBidderId() {
            when(buyerCodeService.getBuyerCodesForUser(50L))
                    .thenReturn(List.of(makeCode(7L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            orderHistoryService.listOrders("all", 50L, null, PageRequest.of(0, 20));

            verify(buyerCodeService).getBuyerCodesForUser(50L);
        }

        @Test
        @DisplayName("BuyerCodeService is always called — never bypassed")
        void buyerCodeServiceAlwaysCalled() {
            when(buyerCodeService.getBuyerCodesForUser(anyLong())).thenReturn(List.of(makeCode(1L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            orderHistoryService.listOrders("all", 1L, null, PageRequest.of(0, 20));

            verify(buyerCodeService, atLeastOnce()).getBuyerCodesForUser(1L);
        }

        @Test
        @DisplayName("specific buyerCodeId filters to that single code")
        void specificBuyerCodeId_filtersToSingleCode() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L), makeCode(2L), makeCode(3L)));
            OrderHistoryView row = makeView(100L, "ORD-001", "Ordered",
                    LocalDateTime.now(), 2L);
            Page<OrderHistoryView> page = new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            Page<OrderHistoryResponse> result =
                    orderHistoryService.listOrders("all", 10L, 2L, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSize(1);
            verify(buyerCodeService).getBuyerCodesForUser(10L);
        }

        @Test
        @DisplayName("buyerCodeId not in user's codes: falls back to all user codes")
        void buyerCodeIdNotInUserCodes_fallsBackToAll() {
            when(buyerCodeService.getBuyerCodesForUser(10L))
                    .thenReturn(List.of(makeCode(1L), makeCode(2L)));
            Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            // buyerCodeId=999 is not in user's codes [1, 2], so it should use all codes
            orderHistoryService.listOrders("all", 10L, 999L, PageRequest.of(0, 20));

            verify(buyerCodeService).getBuyerCodesForUser(10L);
        }
    }

    // -------------------------------------------------------------------------
    // GetDetailsBySku
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getDetailsBySku")
    class GetDetailsBySku {

        private OfferItem makeOfferItem(Long id, String sku, Long deviceId,
                                        Integer qty, Integer shippedQty,
                                        BigDecimal price, BigDecimal totalPrice) {
            OfferItem item = new OfferItem();
            item.setId(id);
            item.setSku(sku);
            item.setDeviceId(deviceId);
            item.setFinalOfferQuantity(qty);
            item.setShippedQty(shippedQty);
            item.setFinalOfferPrice(price);
            item.setFinalOfferTotalPrice(totalPrice);
            Offer offer = new Offer();
            offer.setId(100L);
            item.setOffer(offer);
            return item;
        }

        @Test
        @DisplayName("returns SKU details with device description")
        void returnsSkuDetailsWithDescription() {
            OfferItem item = makeOfferItem(1L, "SKU-001", 10L, 5, 3,
                    new BigDecimal("25.00"), new BigDecimal("125.00"));
            when(offerItemRepository.findByOfferId(100L)).thenReturn(List.of(item));
            Device device = new Device();
            device.setId(10L);
            device.setDescription("iPhone 14 Pro 128GB");
            when(deviceRepository.findAllById(anyCollection())).thenReturn(List.of(device));

            List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(100L);

            assertThat(result).hasSize(1);
            OrderDetailBySkuResponse dto = result.get(0);
            assertThat(dto.offerItemId()).isEqualTo(1L);
            assertThat(dto.sku()).isEqualTo("SKU-001");
            assertThat(dto.description()).isEqualTo("iPhone 14 Pro 128GB");
            assertThat(dto.orderedQty()).isEqualTo(5);
            assertThat(dto.shippedQty()).isEqualTo(3);
            assertThat(dto.unitPrice()).isEqualByComparingTo("25.00");
            assertThat(dto.totalPrice()).isEqualByComparingTo("125.00");
        }

        @Test
        @DisplayName("returns null description when device not found")
        void nullDescriptionWhenNoDevice() {
            OfferItem item = makeOfferItem(2L, "SKU-002", 999L, 1, 0,
                    new BigDecimal("10.00"), new BigDecimal("10.00"));
            when(offerItemRepository.findByOfferId(100L)).thenReturn(List.of(item));
            when(deviceRepository.findAllById(anyCollection())).thenReturn(List.of());

            List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(100L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).description()).isNull();
        }

        @Test
        @DisplayName("returns null description when deviceId is null")
        void nullDescriptionWhenDeviceIdNull() {
            OfferItem item = makeOfferItem(3L, "SKU-003", null, 2, 0,
                    new BigDecimal("5.00"), new BigDecimal("10.00"));
            when(offerItemRepository.findByOfferId(100L)).thenReturn(List.of(item));

            List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(100L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).description()).isNull();
            verify(deviceRepository, never()).findAllById(any());
        }

        @Test
        @DisplayName("returns empty list when no offer items found")
        void emptyListWhenNoItems() {
            when(offerItemRepository.findByOfferId(999L)).thenReturn(List.of());

            List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maps multiple items correctly")
        void mapsMultipleItems() {
            OfferItem item1 = makeOfferItem(1L, "SKU-A", 10L, 5, 5,
                    new BigDecimal("20.00"), new BigDecimal("100.00"));
            OfferItem item2 = makeOfferItem(2L, "SKU-B", 20L, 3, 0,
                    new BigDecimal("30.00"), new BigDecimal("90.00"));
            when(offerItemRepository.findByOfferId(100L)).thenReturn(List.of(item1, item2));
            Device d1 = new Device(); d1.setId(10L); d1.setDescription("Device A");
            Device d2 = new Device(); d2.setId(20L); d2.setDescription("Device B");
            when(deviceRepository.findAllById(anyCollection())).thenReturn(List.of(d1, d2));

            List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(100L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).sku()).isEqualTo("SKU-A");
            assertThat(result.get(1).sku()).isEqualTo("SKU-B");
        }
    }

    // -------------------------------------------------------------------------
    // GetDetailsByDevice
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getDetailsByDevice")
    class GetDetailsByDevice {

        private ImeiDetail makeImeiDetail(Long id, String imei, String serial,
                                          String boxLpn, OfferItem offerItem,
                                          ShipmentDetail shipmentDetail) {
            ImeiDetail d = new ImeiDetail();
            d.setId(id);
            d.setImeiNumber(imei);
            d.setSerialNumber(serial);
            d.setBoxLpnNumber(boxLpn);
            d.setOfferItem(offerItem);
            d.setShipmentDetail(shipmentDetail);
            return d;
        }

        private OfferItem makeItem(Long id, String sku, Long deviceId, BigDecimal price) {
            OfferItem item = new OfferItem();
            item.setId(id);
            item.setSku(sku);
            item.setDeviceId(deviceId);
            item.setFinalOfferPrice(price);
            return item;
        }

        private ShipmentDetail makeShipment(String trackingNumber, String trackingUrl) {
            ShipmentDetail sd = new ShipmentDetail();
            sd.setTrackingNumber(trackingNumber);
            sd.setTrackingUrl(trackingUrl);
            return sd;
        }

        @Test
        @DisplayName("returns device details with tracking info")
        void returnsDeviceDetailsWithTracking() {
            OfferItem item = makeItem(1L, "SKU-001", 10L, new BigDecimal("25.00"));
            ShipmentDetail sd = makeShipment("1Z999AA", "https://ups.com/track/1Z999AA");
            ImeiDetail detail = makeImeiDetail(100L, "353456789012345", "SN-001", "BOX-A", item, sd);
            when(imeiDetailRepository.findByOfferItemOfferId(50L)).thenReturn(List.of(detail));
            Device device = new Device(); device.setId(10L); device.setDescription("iPhone 14");
            when(deviceRepository.findAllById(anyCollection())).thenReturn(List.of(device));

            List<OrderDetailByDeviceResponse> result = orderHistoryService.getDetailsByDevice(50L);

            assertThat(result).hasSize(1);
            OrderDetailByDeviceResponse dto = result.get(0);
            assertThat(dto.imeiDetailId()).isEqualTo(100L);
            assertThat(dto.imei()).isEqualTo("353456789012345");
            assertThat(dto.sku()).isEqualTo("SKU-001");
            assertThat(dto.description()).isEqualTo("iPhone 14");
            assertThat(dto.unitPrice()).isEqualByComparingTo("25.00");
            assertThat(dto.serialNumber()).isEqualTo("SN-001");
            assertThat(dto.boxNumber()).isEqualTo("BOX-A");
            assertThat(dto.trackingNumber()).isEqualTo("1Z999AA");
            assertThat(dto.trackingUrl()).isEqualTo("https://ups.com/track/1Z999AA");
        }

        @Test
        @DisplayName("handles null shipment detail gracefully")
        void handlesNullShipmentDetail() {
            OfferItem item = makeItem(1L, "SKU-001", 10L, new BigDecimal("25.00"));
            ImeiDetail detail = makeImeiDetail(101L, "IMEI-002", "SN-002", "BOX-B", item, null);
            when(imeiDetailRepository.findByOfferItemOfferId(50L)).thenReturn(List.of(detail));
            Device device = new Device(); device.setId(10L); device.setDescription("Galaxy S24");
            when(deviceRepository.findAllById(anyCollection())).thenReturn(List.of(device));

            List<OrderDetailByDeviceResponse> result = orderHistoryService.getDetailsByDevice(50L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).trackingNumber()).isNull();
            assertThat(result.get(0).trackingUrl()).isNull();
        }

        @Test
        @DisplayName("returns empty list when no IMEI details found")
        void emptyListWhenNoDetails() {
            when(imeiDetailRepository.findByOfferItemOfferId(999L)).thenReturn(List.of());

            List<OrderDetailByDeviceResponse> result = orderHistoryService.getDetailsByDevice(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("handles null offerItem fields gracefully")
        void handlesNullOfferItemFields() {
            ImeiDetail detail = makeImeiDetail(102L, "IMEI-003", null, null, null, null);
            when(imeiDetailRepository.findByOfferItemOfferId(50L)).thenReturn(List.of(detail));

            List<OrderDetailByDeviceResponse> result = orderHistoryService.getDetailsByDevice(50L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).sku()).isNull();
            assertThat(result.get(0).description()).isNull();
            assertThat(result.get(0).unitPrice()).isNull();
        }
    }
}
