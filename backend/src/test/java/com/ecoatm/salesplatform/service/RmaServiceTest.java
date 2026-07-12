package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.RmaDetailResponse;
import com.ecoatm.salesplatform.dto.RmaResponse;
import com.ecoatm.salesplatform.dto.RmaSubmitResponse;
import com.ecoatm.salesplatform.dto.RmaSummaryResponse;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.mdm.Grade;
import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.model.pws.RmaReason;
import com.ecoatm.salesplatform.model.pws.RmaStatus;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.ImeiDetailRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaReasonRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.repository.pws.RmaStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RmaServiceTest {

    @Mock private RmaRepository rmaRepository;
    @Mock private RmaItemRepository rmaItemRepository;
    @Mock private RmaStatusRepository rmaStatusRepository;
    @Mock private RmaReasonRepository rmaReasonRepository;
    @Mock private DeviceRepository deviceRepository;
    @Mock private ImeiDetailRepository imeiDetailRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookup;
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

    private RmaService rmaService;

    @BeforeEach
    void setUp() {
        rmaService = new RmaService(rmaRepository, rmaItemRepository,
                rmaStatusRepository, rmaReasonRepository, deviceRepository,
                imeiDetailRepository, orderRepository, buyerCodeLookup, eventPublisher);
    }

    // --- Helpers ---

    private RmaStatus makeStatus(String systemStatus, String grouped) {
        RmaStatus s = new RmaStatus();
        s.setId(1L);
        s.setSystemStatus(systemStatus);
        s.setInternalStatusText(systemStatus);
        s.setExternalStatusText(systemStatus);
        s.setStatusGroupedTo(grouped);
        return s;
    }

    private Rma makeRma(Long id, String number, Long buyerCodeId, RmaStatus status) {
        Rma rma = new Rma();
        rma.setId(id);
        rma.setNumber(number);
        rma.setBuyerCodeId(buyerCodeId);
        rma.setRmaStatus(status);
        rma.setRequestSkus(2);
        rma.setRequestQty(5);
        rma.setRequestSalesTotal(new java.math.BigDecimal("500"));
        rma.setApprovedCount(0);
        rma.setDeclinedCount(0);
        rma.setCreatedDate(LocalDateTime.now());
        return rma;
    }

    private RmaItem makeItem(Long id, Rma rma, String imei, Integer price, String itemStatus) {
        RmaItem item = new RmaItem();
        item.setId(id);
        item.setRma(rma);
        item.setImei(imei);
        item.setSalePrice(price != null ? new java.math.BigDecimal(price) : null);
        item.setReturnReason("Defective");
        item.setStatus(itemStatus);
        item.setDeviceId(id * 10);
        item.setCreatedDate(LocalDateTime.now());
        return item;
    }

    private Device makeDevice(Long id, String sku, String description, String gradeName, String itemType) {
        Device device = new Device();
        device.setId(id);
        device.setSku(sku);
        device.setDescription(description);
        device.setItemType(itemType);
        if (gradeName != null) {
            Grade grade = new Grade();
            grade.setName(gradeName);
            device.setGrade(grade);
        }
        return device;
    }

    // --- getRmasByBuyerCode ---

    @Nested
    @DisplayName("getRmasByBuyerCode")
    class GetRmasByBuyerCode {

        @Test
        @DisplayName("returns all RMAs for buyer when no status filter")
        void noFilter_returnsAll() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma1 = makeRma(1L, "RMA-001", 100L, status);
            Rma rma2 = makeRma(2L, "RMA-002", 100L, status);
            when(rmaRepository.findByBuyerCodeIdOrderByCreatedDateDesc(100L))
                    .thenReturn(List.of(rma1, rma2));

            List<RmaResponse> result = rmaService.getRmasByBuyerCode(100L, null);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNumber()).isEqualTo("RMA-001");
            verify(rmaRepository).findByBuyerCodeIdOrderByCreatedDateDesc(100L);
        }

        @Test
        @DisplayName("filters by status group when filter provided")
        void withFilter_filtersCorrectly() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            when(rmaRepository.findByBuyerCodeIdAndStatusGroupedTo(100L, "Pending_Approval"))
                    .thenReturn(List.of(rma));

            List<RmaResponse> result = rmaService.getRmasByBuyerCode(100L, "Pending_Approval");

            assertThat(result).hasSize(1);
            verify(rmaRepository).findByBuyerCodeIdAndStatusGroupedTo(100L, "Pending_Approval");
        }

        @Test
        @DisplayName("treats 'Total' filter same as no filter")
        void totalFilter_returnsAll() {
            when(rmaRepository.findByBuyerCodeIdOrderByCreatedDateDesc(100L))
                    .thenReturn(List.of());

            rmaService.getRmasByBuyerCode(100L, "Total");

            verify(rmaRepository).findByBuyerCodeIdOrderByCreatedDateDesc(100L);
            verify(rmaRepository, never()).findByBuyerCodeIdAndStatusGroupedTo(anyLong(), anyString());
        }
    }

    // --- getAllRmas ---

    @Nested
    @DisplayName("getAllRmas")
    class GetAllRmas {

        @Test
        @DisplayName("returns all RMAs ordered by date when no filter")
        void noFilter_usesDbQuery() {
            when(rmaRepository.findAllOrderByCreatedDateDesc()).thenReturn(List.of());

            rmaService.getAllRmas(null);

            verify(rmaRepository).findAllOrderByCreatedDateDesc();
        }

        @Test
        @DisplayName("filters by status group via JPQL query")
        void withFilter_usesDbQuery() {
            when(rmaRepository.findByStatusGroupedTo("Open")).thenReturn(List.of());

            rmaService.getAllRmas("Open");

            verify(rmaRepository).findByStatusGroupedTo("Open");
        }
    }

    // --- getRmaDetail ---

    @Nested
    @DisplayName("getRmaDetail")
    class GetRmaDetail {

        @Test
        @DisplayName("returns detail with enriched items")
        void returnsDetailWithDeviceInfo() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item = makeItem(1L, rma, "123456789", 200, null);
            Device device = makeDevice(10L, "IP14-128-BLK", "iPhone 14 128GB Black", "A", "Phone");

            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));
            when(deviceRepository.findById(10L)).thenReturn(Optional.of(device));

            RmaDetailResponse detail = rmaService.getRmaDetail(1L);

            assertThat(detail.getRma().getNumber()).isEqualTo("RMA-001");
            assertThat(detail.getItems()).hasSize(1);
            assertThat(detail.getItems().get(0).getSku()).isEqualTo("IP14-128-BLK");
            assertThat(detail.getItems().get(0).getGrade()).isEqualTo("A");
            assertThat(detail.getItems().get(0).getDeviceDescription()).isEqualTo("iPhone 14 128GB Black");
        }

        @Test
        @DisplayName("throws when RMA not found")
        void notFound_throws() {
            when(rmaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rmaService.getRmaDetail(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("handles item with no device gracefully")
        void noDevice_noEnrichment() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item = makeItem(1L, rma, "123456789", 200, null);
            item.setDeviceId(null);

            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));

            RmaDetailResponse detail = rmaService.getRmaDetail(1L);

            assertThat(detail.getItems().get(0).getSku()).isNull();
            verify(deviceRepository, never()).findById(anyLong());
        }
    }

    // --- updateItemStatus ---

    @Nested
    @DisplayName("updateItemStatus")
    class UpdateItemStatus {

        @Test
        @DisplayName("rejects invalid status values")
        void invalidStatus_throws() {
            assertThatThrownBy(() -> rmaService.updateItemStatus(1L, "Invalid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid status");
        }

        @Test
        @DisplayName("rejects null status")
        void nullStatus_throws() {
            assertThatThrownBy(() -> rmaService.updateItemStatus(1L, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("approves item and recalculates")
        void approve_updatesAndRecalculates() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item = makeItem(1L, rma, "123456789", 200, null);

            when(rmaItemRepository.findById(1L)).thenReturn(Optional.of(item));
            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));

            rmaService.updateItemStatus(1L, "Approve");

            assertThat(item.getStatus()).isEqualTo("Approve");
            assertThat(item.getStatusDisplay()).isEqualTo("Approved");
            verify(rmaItemRepository).save(item);
            verify(rmaRepository, atLeastOnce()).save(any(Rma.class));
        }

        @Test
        @DisplayName("declines item and recalculates")
        void decline_updatesAndRecalculates() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item = makeItem(1L, rma, "123456789", 200, null);

            when(rmaItemRepository.findById(1L)).thenReturn(Optional.of(item));
            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));

            rmaService.updateItemStatus(1L, "Decline");

            assertThat(item.getStatus()).isEqualTo("Decline");
            assertThat(item.getStatusDisplay()).isEqualTo("Declined");
        }

        @Test
        @DisplayName("throws when item not found")
        void itemNotFound_throws() {
            when(rmaItemRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rmaService.updateItemStatus(999L, "Approve"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("999");
        }
    }

    // --- approveAllItems / declineAllItems ---

    @Nested
    @DisplayName("bulk actions")
    class BulkActions {

        @Test
        @DisplayName("approveAll sets all items to Approve")
        void approveAll_setsAllApprove() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item1 = makeItem(1L, rma, "111", 100, null);
            RmaItem item2 = makeItem(2L, rma, "222", 200, null);

            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L))
                    .thenReturn(List.of(item1, item2));
            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));

            rmaService.approveAllItems(1L);

            assertThat(item1.getStatus()).isEqualTo("Approve");
            assertThat(item2.getStatus()).isEqualTo("Approve");
            verify(rmaItemRepository).saveAll(List.of(item1, item2));
        }

        @Test
        @DisplayName("declineAll sets all items to Decline")
        void declineAll_setsAllDecline() {
            RmaStatus status = makeStatus("Submitted", "Pending_Approval");
            Rma rma = makeRma(1L, "RMA-001", 100L, status);
            RmaItem item1 = makeItem(1L, rma, "111", 100, null);
            RmaItem item2 = makeItem(2L, rma, "222", 200, null);

            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L))
                    .thenReturn(List.of(item1, item2));
            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));

            rmaService.declineAllItems(1L);

            assertThat(item1.getStatus()).isEqualTo("Decline");
            assertThat(item2.getStatus()).isEqualTo("Decline");
        }
    }

    // --- completeReview ---

    @Nested
    @DisplayName("completeReview")
    class CompleteReview {

        @Test
        @DisplayName("sets Approved status when all items approved")
        void allApproved_setsApproved() {
            RmaStatus submitted = makeStatus("Submitted", "Pending_Approval");
            RmaStatus approved = makeStatus("Approved", "Open");
            Rma rma = makeRma(1L, "RMA-001", 100L, submitted);
            RmaItem item = makeItem(1L, rma, "111", 100, "Approve");

            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));
            when(rmaStatusRepository.findBySystemStatus("Approved")).thenReturn(Optional.of(approved));

            rmaService.completeReview(1L, 5L);

            assertThat(rma.getReviewedByUserId()).isEqualTo(5L);
            assertThat(rma.getReviewCompletedOn()).isNotNull();
            assertThat(rma.getSystemStatus()).isEqualTo("Approved");
            assertThat(rma.getApprovalDate()).isNotNull();
            verify(rmaRepository, atLeastOnce()).save(rma);

            // Publishes an APPROVED RmaReviewCompletedEvent carrying the rmaId +
            // JWT-derived reviewer so the AFTER_COMMIT Oracle-create listener
            // (and later email/Snowflake listeners) can react.
            ArgumentCaptor<RmaReviewCompletedEvent> captor =
                    ArgumentCaptor.forClass(RmaReviewCompletedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());
            RmaReviewCompletedEvent event = captor.getValue();
            assertThat(event.rmaId()).isEqualTo(1L);
            assertThat(event.outcome()).isEqualTo(RmaReviewOutcome.APPROVED);
            assertThat(event.reviewedByUserId()).isEqualTo(5L);
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("sets Declined status when all items declined")
        void allDeclined_setsDeclined() {
            RmaStatus submitted = makeStatus("Submitted", "Pending_Approval");
            RmaStatus declined = makeStatus("Declined", "Declined");
            Rma rma = makeRma(1L, "RMA-001", 100L, submitted);
            RmaItem item = makeItem(1L, rma, "111", 100, "Decline");

            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(item));
            when(rmaStatusRepository.findBySystemStatus("Declined")).thenReturn(Optional.of(declined));

            rmaService.completeReview(1L, 5L);

            assertThat(rma.getSystemStatus()).isEqualTo("Declined");
            assertThat(rma.getApprovalDate()).isNull();

            // Declined reviews still publish — a DECLINED event so downstream
            // listeners (Snowflake sync in a later task) see it; the
            // Oracle-create listener filters it out.
            ArgumentCaptor<RmaReviewCompletedEvent> captor =
                    ArgumentCaptor.forClass(RmaReviewCompletedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().outcome()).isEqualTo(RmaReviewOutcome.DECLINED);
        }

        @Test
        @DisplayName("sets Approved status for mixed (partial approve)")
        void mixed_setsApproved() {
            RmaStatus submitted = makeStatus("Submitted", "Pending_Approval");
            RmaStatus approved = makeStatus("Approved", "Open");
            Rma rma = makeRma(1L, "RMA-001", 100L, submitted);
            RmaItem item1 = makeItem(1L, rma, "111", 100, "Approve");
            RmaItem item2 = makeItem(2L, rma, "222", 200, "Decline");

            when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
            when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L))
                    .thenReturn(List.of(item1, item2));
            when(rmaStatusRepository.findBySystemStatus("Approved")).thenReturn(Optional.of(approved));

            rmaService.completeReview(1L, 5L);

            assertThat(rma.getSystemStatus()).isEqualTo("Approved");
            assertThat(rma.getApprovedCount()).isEqualTo(1);
            assertThat(rma.getDeclinedCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("throws when RMA not found")
        void notFound_throws() {
            when(rmaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rmaService.completeReview(999L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("999");
        }
    }

    // --- getSummary / getAllSummary ---

    @Nested
    @DisplayName("summary")
    class Summary {

        @Test
        @DisplayName("getSummary includes Total card at index 0")
        void getSummary_includesTotal() {
            Object[] row = new Object[]{"Pending_Approval", 3L, 1500L, 6L, 10L};
            List<Object[]> rows = new ArrayList<>();
            rows.add(row);
            when(rmaRepository.getSummaryGroupedByBuyerCode(100L)).thenReturn(rows);

            List<RmaSummaryResponse> result = rmaService.getSummary(100L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getStatus()).isEqualTo("Total");
            assertThat(result.get(0).getRmaCount()).isEqualTo(3);
            assertThat(result.get(1).getStatus()).isEqualTo("Pending_Approval");
            assertThat(result.get(1).getDisplayLabel()).isEqualTo("Pending Approval");
        }

        @Test
        @DisplayName("getSummary with null buyerCodeId uses all-summary query")
        void nullBuyer_usesAllQuery() {
            when(rmaRepository.getSummaryGroupedAll()).thenReturn(List.of());

            rmaService.getSummary(null);

            verify(rmaRepository).getSummaryGroupedAll();
            verify(rmaRepository, never()).getSummaryGroupedByBuyerCode(anyLong());
        }

        @Test
        @DisplayName("getAllSummary aggregates correctly")
        void getAllSummary_aggregates() {
            Object[] row1 = new Object[]{"Pending_Approval", 2L, 500L, 4L, 6L};
            Object[] row2 = new Object[]{"Open", 1L, 300L, 2L, 3L};
            List<Object[]> rows = new ArrayList<>();
            rows.add(row1);
            rows.add(row2);
            when(rmaRepository.getSummaryGroupedAll()).thenReturn(rows);

            List<RmaSummaryResponse> result = rmaService.getAllSummary();

            assertThat(result).hasSize(3); // Total + 2 groups
            assertThat(result.get(0).getStatus()).isEqualTo("Total");
            assertThat(result.get(0).getRmaCount()).isEqualTo(3);
            assertThat(result.get(0).getTotalPrice()).isEqualTo(800);
        }
    }

    // --- submitRmaRequest: VAL_RMARequestFile OfferItem matching (gap 0.2) ---

    @Nested
    @DisplayName("submitRmaRequest — OfferItem validation")
    class SubmitRmaRequest {

        private static final Long BUYER_CODE_ID = 100L;
        private static final String REASON = "Defective Battery/ Lower 69%";

        private java.io.InputStream csv(String body) {
            return new ByteArrayInputStream(
                    ("IMEI/Serial,Return Reason\n" + body).getBytes(StandardCharsets.UTF_8));
        }

        private ImeiDetail match(String value, Long deviceId, BigDecimal finalOfferPrice) {
            OfferItem oi = new OfferItem();
            oi.setDeviceId(deviceId);
            oi.setFinalOfferPrice(finalOfferPrice);
            ImeiDetail d = new ImeiDetail();
            d.setImeiNumber(value);
            d.setOfferItem(oi);
            return d;
        }

        private void stubActiveReasons() {
            RmaReason reason = new RmaReason();
            reason.setValidReasons(REASON);
            reason.setIsActive(true);
            when(rmaReasonRepository.findByIsActiveTrueOrderByValidReasonsAsc())
                    .thenReturn(List.of(reason));
        }

        @Test
        @DisplayName("matched lines carry deviceId + salePrice; roll-ups are non-zero")
        void matchedLines_populateDevicePriceAndRollups() {
            stubActiveReasons();
            when(rmaItemRepository.findDuplicateImeis(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of());
            when(imeiDetailRepository.findMatchesForBuyer(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of(
                            match("111", 10L, new BigDecimal("100")),
                            match("222", 20L, new BigDecimal("200"))));
            when(buyerCodeLookup.findCodeById(BUYER_CODE_ID)).thenReturn("BC001");
            when(rmaRepository.countByBuyerCode(BUYER_CODE_ID)).thenReturn(0L);
            when(rmaStatusRepository.findBySystemStatus("Submitted"))
                    .thenReturn(Optional.of(makeStatus("Submitted", "Pending_Approval")));
            when(rmaRepository.save(any(Rma.class))).thenAnswer(inv -> {
                Rma r = inv.getArgument(0);
                if (r.getId() == null) r.setId(500L);
                return r;
            });

            RmaSubmitResponse response = rmaService.submitRmaRequest(
                    BUYER_CODE_ID, 7L, csv("111," + REASON + "\n222," + REASON + "\n"));

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getItemCount()).isEqualTo(2);

            ArgumentCaptor<RmaItem> itemCaptor = ArgumentCaptor.forClass(RmaItem.class);
            verify(rmaItemRepository, times(2)).save(itemCaptor.capture());
            assertThat(itemCaptor.getAllValues())
                    .extracting(RmaItem::getDeviceId)
                    .containsExactlyInAnyOrder(10L, 20L);
            assertThat(itemCaptor.getAllValues())
                    .allSatisfy(it -> assertThat(it.getSalePrice()).isNotNull());

            // Roll-ups on the persisted RMA are non-zero.
            ArgumentCaptor<Rma> rmaCaptor = ArgumentCaptor.forClass(Rma.class);
            verify(rmaRepository, atLeastOnce()).save(rmaCaptor.capture());
            Rma saved = rmaCaptor.getValue();
            assertThat(saved.getRequestQty()).isEqualTo(2);
            assertThat(saved.getRequestSkus()).isEqualTo(2);
            assertThat(saved.getRequestSalesTotal()).isEqualByComparingTo("300");
        }

        @Test
        @DisplayName("matches on serial_number when imei_number differs")
        void matchesOnSerialNumber() {
            stubActiveReasons();
            when(rmaItemRepository.findDuplicateImeis(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of());
            OfferItem oi = new OfferItem();
            oi.setDeviceId(30L);
            oi.setFinalOfferPrice(new BigDecimal("150"));
            ImeiDetail d = new ImeiDetail();
            d.setImeiNumber("OTHER");
            d.setSerialNumber("SER-9");
            d.setOfferItem(oi);
            when(imeiDetailRepository.findMatchesForBuyer(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of(d));
            when(buyerCodeLookup.findCodeById(BUYER_CODE_ID)).thenReturn("BC001");
            when(rmaRepository.countByBuyerCode(BUYER_CODE_ID)).thenReturn(0L);
            when(rmaStatusRepository.findBySystemStatus("Submitted"))
                    .thenReturn(Optional.of(makeStatus("Submitted", "Pending_Approval")));
            when(rmaRepository.save(any(Rma.class))).thenAnswer(inv -> {
                Rma r = inv.getArgument(0);
                if (r.getId() == null) r.setId(501L);
                return r;
            });

            RmaSubmitResponse response = rmaService.submitRmaRequest(
                    BUYER_CODE_ID, 7L, csv("SER-9," + REASON + "\n"));

            assertThat(response.isSuccess()).isTrue();
            ArgumentCaptor<RmaItem> itemCaptor = ArgumentCaptor.forClass(RmaItem.class);
            verify(rmaItemRepository).save(itemCaptor.capture());
            assertThat(itemCaptor.getValue().getDeviceId()).isEqualTo(30L);
        }

        @Test
        @DisplayName("unmatched line is rejected and nothing is persisted")
        void unmatchedLine_rejectedNothingPersisted() {
            stubActiveReasons();
            when(rmaItemRepository.findDuplicateImeis(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of());
            when(imeiDetailRepository.findMatchesForBuyer(anyList(), eq(BUYER_CODE_ID)))
                    .thenReturn(List.of()); // no OfferItem match for this buyer code

            RmaSubmitResponse response = rmaService.submitRmaRequest(
                    BUYER_CODE_ID, 7L, csv("999," + REASON + "\n"));

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrors())
                    .anySatisfy(e -> assertThat(e).contains("999").contains("does not match"));
            // All-or-nothing: no RMA header and no RMA line are persisted.
            verify(rmaRepository, never()).save(any(Rma.class));
            verify(rmaItemRepository, never()).save(any(RmaItem.class));
        }
    }
}
