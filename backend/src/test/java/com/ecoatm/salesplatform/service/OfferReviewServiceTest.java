package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OfferListItem;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.OfferSummary;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferReviewServiceTest {

    @Mock private OfferRepository offerRepository;
    @Mock private DeviceRepository deviceRepository;
    @Mock private OfferService offerService;
    @Mock private BuyerCodeLookupService buyerCodeLookup;
    @Mock private EntityManager em;
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;
    @Mock private OfferItemDeviceLoader deviceLoader;

    @InjectMocks
    private OfferReviewService offerReviewService;

    @BeforeEach
    void setUp() {
        lenient().when(deviceLoader.loadDeviceMap(anyList())).thenReturn(Map.of());
    }

    // --- Helpers ---

    private Offer makeOffer(Long id, String status, List<OfferItem> items) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setOfferNumber("OFF-" + id);
        offer.setStatus(status);
        offer.setBuyerCodeId(100L);
        offer.setTotalQty(items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum());
        offer.setTotalPrice(items.stream().map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        offer.setItems(items);
        items.forEach(i -> i.setOffer(offer));
        return offer;
    }

    private OfferItem makeItem(Long id, String sku, int qty, BigDecimal price, String itemStatus) {
        OfferItem item = new OfferItem();
        item.setId(id);
        item.setSku(sku);
        item.setDeviceId(id * 10);
        item.setQuantity(qty);
        item.setPrice(price);
        item.setTotalPrice(price.multiply(BigDecimal.valueOf(qty)));
        item.setItemStatus(itemStatus);
        return item;
    }

    private Device makeDevice(Long id, int availableQty) {
        Device device = new Device();
        device.setId(id);
        device.setAvailableQty(availableQty);
        device.setAtpQty(availableQty);
        return device;
    }

    // ── getStatusSummaries ──────────────────────────────────────────

    @Nested
    @DisplayName("getStatusSummaries")
    class GetStatusSummaries {

        @Test
        @DisplayName("returns 6 summaries (5 statuses + Total) with correct counts")
        void getStatusSummaries_returnsAllStatuses() {
            // Aggregate query returns one row for Sales_Review
            List<Object[]> rows = new ArrayList<>();
            rows.add(new Object[]{"Sales_Review", 1L, 1L, 5L, new BigDecimal("50.00")});
            when(offerRepository.getStatusSummaries(anyList()))
                    .thenReturn(rows);

            List<OfferSummary> summaries = offerReviewService.getStatusSummaries();

            assertThat(summaries).hasSize(6);
            assertThat(summaries.get(0).getStatus()).isEqualTo("Sales_Review");
            assertThat(summaries.get(0).getOfferCount()).isEqualTo(1);
            assertThat(summaries.get(5).getStatus()).isEqualTo("Total");
            assertThat(summaries.get(5).getOfferCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("empty database returns all zero summaries")
        void getStatusSummaries_emptyDb_returnsZeros() {
            when(offerRepository.getStatusSummaries(anyList()))
                    .thenReturn(Collections.emptyList());

            List<OfferSummary> summaries = offerReviewService.getStatusSummaries();

            assertThat(summaries).hasSize(6);
            assertThat(summaries.get(5).getOfferCount()).isZero();
            assertThat(summaries.get(5).getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ── listOffers ──────────────────────────────────────────────────

    @Nested
    @DisplayName("listOffers")
    class ListOffers {

        @Test
        @DisplayName("null status returns all non-draft offers")
        @SuppressWarnings("unchecked")
        void listOffers_nullStatus_returnsAllNonDraft() {
            OfferItem item = makeItem(1L, "SKU1", 3, BigDecimal.TEN, null);
            Offer offer = makeOffer(1L, "Sales_Review", List.of(item));

            when(offerRepository.findByStatusNotOrderByUpdatedDateDesc("Draft"))
                    .thenReturn(List.of(offer));
            stubListOffersQueries();

            List<OfferListItem> result = offerReviewService.listOffers(null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOfferId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("specific status filters correctly")
        @SuppressWarnings("unchecked")
        void listOffers_specificStatus_filters() {
            when(offerRepository.findByStatusOrderByUpdatedDateDesc("Ordered"))
                    .thenReturn(Collections.emptyList());

            List<OfferListItem> result = offerReviewService.listOffers("Ordered");

            assertThat(result).isEmpty();
        }

        @SuppressWarnings("unchecked")
        private void stubListOffersQueries() {
            // buyerCodeLookup methods return empty maps by default (Mockito)

            // Order number query still uses EntityManager directly
            Query orderQuery = mock(Query.class);
            when(em.createNativeQuery(contains("order_number"))).thenReturn(orderQuery);
            when(orderQuery.setParameter(anyString(), any())).thenReturn(orderQuery);
            when(orderQuery.getResultList()).thenReturn(Collections.emptyList());
        }
    }

    // ── setItemAction ───────────────────────────────────────────────

    @Nested
    @DisplayName("setItemAction")
    class SetItemAction {

        @Test
        @DisplayName("Accept sets status and clears counter fields")
        void setItemAction_accept_clearsCounterFields() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, null);
            item.setCounterQty(3);
            item.setCounterPrice(BigDecimal.ONE);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.setItemAction(1L, 1L, "Accept");

            assertThat(item.getItemStatus()).isEqualTo("Accept");
            assertThat(item.getCounterQty()).isNull();
            assertThat(item.getCounterPrice()).isNull();
        }

        @Test
        @DisplayName("Counter initializes counter fields from offer values")
        void setItemAction_counter_initializesCounterFields() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, null);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.setItemAction(1L, 1L, "Counter");

            assertThat(item.getItemStatus()).isEqualTo("Counter");
            assertThat(item.getCounterQty()).isEqualTo(5);
            assertThat(item.getCounterPrice()).isEqualByComparingTo(BigDecimal.TEN);
        }

        @Test
        @DisplayName("Decline sets status and clears counter fields")
        void setItemAction_decline_clearsCounterFields() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, null);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.setItemAction(1L, 1L, "Decline");

            assertThat(item.getItemStatus()).isEqualTo("Decline");
            assertThat(item.getCounterQty()).isNull();
        }

        @Test
        @DisplayName("invalid action throws IllegalArgumentException")
        void setItemAction_invalidAction_throws() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, null);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            assertThatThrownBy(() -> offerReviewService.setItemAction(1L, 1L, "Invalid"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("missing offer throws RuntimeException")
        void setItemAction_offerNotFound_throws() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerReviewService.setItemAction(999L, 1L, "Accept"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Offer not found");
        }

        @Test
        @DisplayName("missing item throws RuntimeException")
        void setItemAction_itemNotFound_throws() {
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            assertThatThrownBy(() -> offerReviewService.setItemAction(1L, 999L, "Accept"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Item not found");
        }
    }

    // ── acceptAll / declineAll / finalizeAll ─────────────────────────

    @Nested
    @DisplayName("bulk actions")
    class BulkActions {

        @Test
        @DisplayName("acceptAll sets all items to Accept")
        void acceptAll_setsAllToAccept() {
            OfferItem item1 = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            OfferItem item2 = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Decline");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item1, item2)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.acceptAll(1L);

            assertThat(item1.getItemStatus()).isEqualTo("Accept");
            assertThat(item2.getItemStatus()).isEqualTo("Accept");
            assertThat(item1.getCounterQty()).isNull();
        }

        @Test
        @DisplayName("declineAll sets all items to Decline")
        void declineAll_setsAllToDecline() {
            OfferItem item1 = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Accept");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item1)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.declineAll(1L);

            assertThat(item1.getItemStatus()).isEqualTo("Decline");
        }

        @Test
        @DisplayName("finalizeAll sets all items to Finalize")
        void finalizeAll_setsAllToFinalize() {
            OfferItem item1 = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Accept");
            OfferItem item2 = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Counter");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item1, item2)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.finalizeAll(1L);

            assertThat(item1.getItemStatus()).isEqualTo("Finalize");
            assertThat(item2.getItemStatus()).isEqualTo("Finalize");
        }
    }

    // ── completeReview ──────────────────────────────────────────────

    @Nested
    @DisplayName("completeReview")
    class CompleteReview {

        @Test
        @DisplayName("non-Sales_Review status returns error")
        void completeReview_wrongStatus_returnsError() {
            Offer offer = makeOffer(1L, "Draft", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("not in Sales Review");
        }

        @Test
        @DisplayName("empty items returns error")
        void completeReview_noItems_returnsError() {
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("No items");
        }

        @Test
        @DisplayName("all declined sets offer to Declined status")
        void completeReview_allDeclined_setsDeclinedStatus() {
            OfferItem item1 = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Decline");
            OfferItem item2 = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Decline");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item1, item2)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(offer.getStatus()).isEqualTo("Declined");
            verify(offerRepository).save(offer);
        }

        @Test
        @DisplayName("finalize mixed with accept returns validation error")
        void completeReview_finalizeMixedWithAccept_returnsError() {
            OfferItem item1 = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Finalize");
            OfferItem item2 = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Accept");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item1, item2)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("finalize");
        }

        @Test
        @DisplayName("counter items with missing qty returns error")
        void completeReview_counterItemsInvalidQty_returnsError() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(null);
            item.setCounterPrice(null);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("countered SKUs must be valid");
        }

        @Test
        @DisplayName("counter qty exceeds available qty returns error")
        void completeReview_counterQtyExceedsAvailable_returnsError() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(100);
            item.setCounterPrice(BigDecimal.TEN);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            Device device = makeDevice(10L, 5);
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(deviceLoader.loadDeviceMap(anyList())).thenReturn(Map.of(device.getId(), device));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Available Qty");
        }

        @Test
        @DisplayName("valid counter items moves offer to Buyer_Acceptance")
        void completeReview_validCounters_movesToBuyerAcceptance() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(3);
            item.setCounterPrice(BigDecimal.valueOf(8));
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            Device device = makeDevice(10L, 100);
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(deviceLoader.loadDeviceMap(anyList())).thenReturn(Map.of(device.getId(), device));
            when(offerRepository.save(any())).thenReturn(offer);

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(offer.getStatus()).isEqualTo("Buyer_Acceptance");
            assertThat(item.getOfferDrawerStatus()).isEqualTo("Countered");
        }

        @Test
        @DisplayName("all accepted with no counters delegates to offerService.submitOrder")
        void completeReview_allAccepted_submitsOrder() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Accept");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            Device device = makeDevice(10L, 100);
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(deviceLoader.loadDeviceMap(anyList())).thenReturn(Map.of(device.getId(), device));
            when(offerService.submitOrder(1L, 1L))
                    .thenReturn(SubmitResponse.submitted(1L, "ORD-001"));

            SubmitResponse response = offerReviewService.completeReview(1L, 1L);

            assertThat(response.isSuccess()).isTrue();
            verify(offerService).submitOrder(1L, 1L);
        }
    }

    // ── updateItemCounter ───────────────────────────────────────────

    @Nested
    @DisplayName("updateItemCounter")
    class UpdateItemCounter {

        @Test
        @DisplayName("updates counter qty and recalculates total")
        void updateItemCounter_updatesQtyAndRecalculates() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(5);
            item.setCounterPrice(BigDecimal.TEN);
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            offerReviewService.updateItemCounter(1L, 1L, 3, null);

            assertThat(item.getCounterQty()).isEqualTo(3);
            assertThat(item.getCounterTotal()).isEqualByComparingTo(BigDecimal.valueOf(30));
        }
    }

    // ── getOfferDetail ──────────────────────────────────────────────

    @Nested
    @DisplayName("getOfferDetail")
    class GetOfferDetail {

        @Test
        @DisplayName("returns offer with item details")
        void getOfferDetail_returnsResponse() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Accept");
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            OfferResponse response = offerReviewService.getOfferDetail(1L);

            assertThat(response.getOfferId()).isEqualTo(1L);
            assertThat(response.getItems()).hasSize(1);
        }

        @Test
        @DisplayName("missing offer throws RuntimeException")
        void getOfferDetail_notFound_throws() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerReviewService.getOfferDetail(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Offer not found");
        }
    }
}
