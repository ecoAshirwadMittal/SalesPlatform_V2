package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OfferListItem;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterOfferServiceTest {

    @Mock private OfferRepository offerRepository;
    @Mock private DeviceRepository deviceRepository;
    @Mock private CaseLotRepository caseLotRepository;
    @Mock private OfferService offerService;
    @Mock private BuyerCodeLookupService buyerCodeLookup;

    @InjectMocks
    private CounterOfferService counterOfferService;

    // --- Helpers ---

    private Offer makeOffer(Long id, String status, List<OfferItem> items) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setOfferNumber("OFF-" + id);
        offer.setStatus(status);
        offer.setBuyerCodeId(100L);
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

    // ── listCounterOffers ───────────────────────────────────────────

    @Nested
    @DisplayName("listCounterOffers")
    class ListCounterOffers {

        @Test
        @DisplayName("returns offers in Buyer_Acceptance status for buyer code")
        @SuppressWarnings("unchecked")
        void listCounterOffers_returnsMatchingOffers() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>(List.of(item)));

            when(offerRepository.findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc("Buyer_Acceptance", 100L))
                    .thenReturn(List.of(offer));

            when(buyerCodeLookup.findCodesByIds(Set.of(100L)))
                    .thenReturn(Map.of(100L, "BC-001"));

            List<OfferListItem> result = counterOfferService.listCounterOffers(100L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBuyerCode()).isEqualTo("BC-001");
        }

        @Test
        @DisplayName("empty result returns empty list")
        void listCounterOffers_noOffers_returnsEmpty() {
            when(offerRepository.findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc("Buyer_Acceptance", 100L))
                    .thenReturn(Collections.emptyList());

            List<OfferListItem> result = counterOfferService.listCounterOffers(100L);

            assertThat(result).isEmpty();
        }
    }

    // ── setBuyerItemAction ──────────────────────────────────────────

    @Nested
    @DisplayName("setBuyerItemAction")
    class SetBuyerItemAction {

        @Test
        @DisplayName("Accept sets final offer values from counter values")
        void setBuyerItemAction_accept_setsFinalValues() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(3);
            item.setCounterPrice(BigDecimal.valueOf(8));
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(deviceRepository.findAllById(anyCollection())).thenReturn(Collections.emptyList());

            counterOfferService.setBuyerItemAction(1L, 1L, "Accept");

            assertThat(item.getBuyerCounterStatus()).isEqualTo("Accept");
            assertThat(item.getFinalOfferPrice()).isEqualByComparingTo(BigDecimal.valueOf(8));
            assertThat(item.getFinalOfferQuantity()).isEqualTo(3);
            assertThat(item.getFinalOfferTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(24));
        }

        @Test
        @DisplayName("Decline zeroes out final values")
        void setBuyerItemAction_decline_zeroesFinalValues() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            item.setCounterQty(3);
            item.setCounterPrice(BigDecimal.valueOf(8));
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(deviceRepository.findAllById(anyCollection())).thenReturn(Collections.emptyList());

            counterOfferService.setBuyerItemAction(1L, 1L, "Decline");

            assertThat(item.getBuyerCounterStatus()).isEqualTo("Decline");
            assertThat(item.getFinalOfferPrice()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(item.getFinalOfferQuantity()).isZero();
        }

        @Test
        @DisplayName("non-Buyer_Acceptance status throws RuntimeException")
        void setBuyerItemAction_wrongStatus_throws() {
            Offer offer = makeOffer(1L, "Sales_Review", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            assertThatThrownBy(() -> counterOfferService.setBuyerItemAction(1L, 1L, "Accept"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not in Buyer_Acceptance");
        }

        @Test
        @DisplayName("Accept on SPB item uses caseLotSize multiplier")
        void setBuyerItemAction_acceptSPB_usesCaseLotMultiplier() {
            OfferItem item = makeItem(1L, "SPB001", 2, BigDecimal.TEN, "Counter");
            item.setCounterQty(3);
            item.setCounterPrice(BigDecimal.valueOf(5));
            item.setCaseLotId(50L);
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>(List.of(item)));

            CaseLot caseLot = new CaseLot();
            caseLot.setId(50L);
            caseLot.setCaseLotSize(10);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(deviceRepository.findAllById(anyCollection())).thenReturn(Collections.emptyList());
            when(caseLotRepository.findById(50L)).thenReturn(Optional.of(caseLot));

            counterOfferService.setBuyerItemAction(1L, 1L, "Accept");

            // counterQty(3) * caseLotSize(10) = 30
            assertThat(item.getFinalOfferQuantity()).isEqualTo(30);
            // counterPrice(5) * counterQty(3) * caseLotSize(10) = 150
            assertThat(item.getFinalOfferTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
        }
    }

    // ── acceptAllCounters ───────────────────────────────────────────

    @Nested
    @DisplayName("acceptAllCounters")
    class AcceptAllCounters {

        @Test
        @DisplayName("accepts all Counter and Accept items")
        void acceptAllCounters_setsAllToAccepted() {
            OfferItem counterItem = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            counterItem.setCounterQty(3);
            counterItem.setCounterPrice(BigDecimal.valueOf(8));
            OfferItem acceptItem = makeItem(2L, "SKU2", 2, BigDecimal.valueOf(5), "Accept");
            OfferItem declineItem = makeItem(3L, "SKU3", 1, BigDecimal.ONE, "Decline");
            Offer offer = makeOffer(1L, "Buyer_Acceptance",
                    new ArrayList<>(List.of(counterItem, acceptItem, declineItem)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(deviceRepository.findAllById(anyCollection())).thenReturn(Collections.emptyList());

            counterOfferService.acceptAllCounters(1L);

            assertThat(counterItem.getBuyerCounterStatus()).isEqualTo("Accept");
            assertThat(acceptItem.getBuyerCounterStatus()).isEqualTo("Accept");
            // Decline item should not be touched
            assertThat(declineItem.getBuyerCounterStatus()).isNull();
        }

        @Test
        @DisplayName("non-Buyer_Acceptance status throws")
        void acceptAllCounters_wrongStatus_throws() {
            Offer offer = makeOffer(1L, "Draft", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            assertThatThrownBy(() -> counterOfferService.acceptAllCounters(1L))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    // ── submitCounterResponse ───────────────────────────────────────

    @Nested
    @DisplayName("submitCounterResponse")
    class SubmitCounterResponse {

        @Test
        @DisplayName("non-Buyer_Acceptance returns error")
        void submitCounterResponse_wrongStatus_returnsError() {
            Offer offer = makeOffer(1L, "Draft", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = counterOfferService.submitCounterResponse(1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("not in Buyer_Acceptance");
        }

        @Test
        @DisplayName("unresponded counter items returns validation error")
        void submitCounterResponse_unrespondedCounters_returnsError() {
            OfferItem item = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            // buyerCounterStatus is null
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>(List.of(item)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = counterOfferService.submitCounterResponse(1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Accepted or Rejected");
        }

        @Test
        @DisplayName("all counter items declined sets offer to Declined")
        void submitCounterResponse_allDeclined_setsDeclined() {
            OfferItem counterItem = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            counterItem.setBuyerCounterStatus("Decline");
            OfferItem declineItem = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Decline");
            Offer offer = makeOffer(1L, "Buyer_Acceptance",
                    new ArrayList<>(List.of(counterItem, declineItem)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            SubmitResponse response = counterOfferService.submitCounterResponse(1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(offer.getStatus()).isEqualTo("Declined");
            assertThat(counterItem.getOfferDrawerStatus()).isEqualTo("Buyer_Declined");
        }

        @Test
        @DisplayName("accepted counter items triggers order submission")
        void submitCounterResponse_hasAccepted_submitsOrder() {
            OfferItem counterItem = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            counterItem.setBuyerCounterStatus("Accept");
            OfferItem acceptItem = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Accept");
            Offer offer = makeOffer(1L, "Buyer_Acceptance",
                    new ArrayList<>(List.of(counterItem, acceptItem)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(offerService.submitOrder(1L, null))
                    .thenReturn(SubmitResponse.submitted(1L, "ORD-001"));

            SubmitResponse response = counterOfferService.submitCounterResponse(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(counterItem.getOfferDrawerStatus()).isEqualTo("Ordered");
            assertThat(acceptItem.getOfferDrawerStatus()).isEqualTo("Ordered");
            verify(offerService).submitOrder(1L, null);
        }

        @Test
        @DisplayName("mixed accept/decline counter items sets correct drawer statuses")
        void submitCounterResponse_mixed_setsCorrectDrawerStatuses() {
            OfferItem accepted = makeItem(1L, "SKU1", 5, BigDecimal.TEN, "Counter");
            accepted.setBuyerCounterStatus("Accept");
            OfferItem declined = makeItem(2L, "SKU2", 3, BigDecimal.ONE, "Counter");
            declined.setBuyerCounterStatus("Decline");
            Offer offer = makeOffer(1L, "Buyer_Acceptance",
                    new ArrayList<>(List.of(accepted, declined)));

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);
            when(offerService.submitOrder(1L, null))
                    .thenReturn(SubmitResponse.submitted(1L, "ORD-002"));

            counterOfferService.submitCounterResponse(1L);

            assertThat(accepted.getOfferDrawerStatus()).isEqualTo("Ordered");
            assertThat(declined.getOfferDrawerStatus()).isEqualTo("Buyer_Declined");
        }
    }

    // ── cancelOffer ─────────────────────────────────────────────────

    @Nested
    @DisplayName("cancelOffer")
    class CancelOffer {

        @Test
        @DisplayName("sets offer to Canceled status")
        void cancelOffer_setsCanceledStatus() {
            Offer offer = makeOffer(1L, "Buyer_Acceptance", new ArrayList<>());
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
            when(offerRepository.save(any())).thenReturn(offer);

            SubmitResponse response = counterOfferService.cancelOffer(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(offer.getStatus()).isEqualTo("Canceled");
            assertThat(offer.getCanceledOn()).isNotNull();
        }

        @Test
        @DisplayName("missing offer throws RuntimeException")
        void cancelOffer_notFound_throws() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> counterOfferService.cancelOffer(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Offer not found");
        }
    }
}
