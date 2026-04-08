package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.CartItemRequest;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.repository.pws.OfferItemRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.model.pws.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferItemRepository offerItemRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OracleConfigRepository oracleConfigRepository;

    @Mock
    private CaseLotRepository caseLotRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EntityManager em;

    @InjectMocks
    private OfferService offerService;

    @BeforeEach
    void injectEntityManager() throws Exception {
        Field emField = OfferService.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(offerService, em);
    }

    // ── helpers ──────────────────────────────────────────────────────────

    private Offer createDraftOffer(Long id, Long buyerCodeId) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setBuyerCodeId(buyerCodeId);
        offer.setOfferType("BUYER");
        offer.setStatus("Draft");
        offer.setTotalQty(0);
        offer.setTotalPrice(BigDecimal.ZERO);
        offer.setItems(new ArrayList<>());
        return offer;
    }

    private OfferItem createOfferItem(Offer offer, String sku, Long deviceId,
                                       int quantity, BigDecimal price) {
        OfferItem item = new OfferItem();
        item.setId((long) (Math.random() * 10000));
        item.setOffer(offer);
        item.setSku(sku);
        item.setDeviceId(deviceId);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setTotalPrice(price.multiply(BigDecimal.valueOf(quantity)));
        item.setItemStatus("Draft");
        return item;
    }

    private Device createDevice(Long id, String sku, BigDecimal listPrice, int availableQty) {
        Device device = new Device();
        device.setId(id);
        device.setListPrice(listPrice);
        device.setAvailableQty(availableQty);
        device.setReservedQty(0);
        return device;
    }

    private CartItemRequest createCartItemRequest(String sku, Long deviceId,
                                                   BigDecimal offerPrice, int quantity) {
        CartItemRequest request = new CartItemRequest();
        request.setSku(sku);
        request.setDeviceId(deviceId);
        request.setOfferPrice(offerPrice);
        request.setQuantity(quantity);
        return request;
    }

    private void stubDraftLookup(Long buyerCodeId, Offer offer) {
        when(offerRepository.findByBuyerCodeIdAndOfferTypeAndStatus(
                buyerCodeId, "BUYER", "Draft"))
                .thenReturn(Optional.of(offer));
    }

    private void stubSavePassthrough() {
        when(offerRepository.save(any(Offer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @SuppressWarnings("unchecked")
    private void stubDeviceMap(Device... devices) {
        List<Device> deviceList = List.of(devices);
        when(deviceRepository.findAllById(any(Iterable.class))).thenReturn(deviceList);
    }

    /**
     * Stubs EntityManager native queries used by generateOfferNumber and resolveUserName.
     * generateOfferNumber issues 3 queries:
     *   1. SELECT bc.code ... -> returns buyerCode string
     *   2. INSERT INTO pws.offer_id_sequence ... -> executeUpdate
     *   3. SELECT max_sequence ... -> returns integer sequence
     * resolveUserName issues:
     *   4. SELECT u.name ... -> returns list of user name strings
     */
    private void stubGenerateOfferNumber(String buyerCode) {
        Query buyerCodeQuery = mock(Query.class);
        Query insertSeqQuery = mock(Query.class);
        Query readSeqQuery = mock(Query.class);
        Query userNameQuery = mock(Query.class);

        // Use answer-based routing since all calls go through em.createNativeQuery(String)
        when(em.createNativeQuery(anyString())).thenAnswer(invocation -> {
            String sql = invocation.getArgument(0, String.class);
            if (sql.contains("buyer_mgmt.buyer_codes")) {
                return buyerCodeQuery;
            } else if (sql.contains("INSERT INTO")) {
                return insertSeqQuery;
            } else if (sql.contains("max_sequence")) {
                return readSeqQuery;
            } else if (sql.contains("identity.users")) {
                return userNameQuery;
            }
            return mock(Query.class);
        });

        when(buyerCodeQuery.setParameter(anyString(), any())).thenReturn(buyerCodeQuery);
        when(buyerCodeQuery.getSingleResult()).thenReturn(buyerCode);

        when(insertSeqQuery.setParameter(anyString(), any())).thenReturn(insertSeqQuery);
        when(insertSeqQuery.executeUpdate()).thenReturn(1);

        when(readSeqQuery.setParameter(anyString(), any())).thenReturn(readSeqQuery);
        when(readSeqQuery.getSingleResult()).thenReturn(Integer.valueOf(1));

        when(userNameQuery.setParameter(anyString(), any())).thenReturn(userNameQuery);
        when(userNameQuery.getResultList()).thenReturn(List.of("testuser@test.com"));
    }

    // ── getOrCreateCart ──────────────────────────────────────────────────

    @Nested
    @DisplayName("getOrCreateCart")
    class GetOrCreateCart {

        @Test
        @DisplayName("existing draft with items returns cart response")
        void existingDraft_returnsCart() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 5,
                    BigDecimal.valueOf(25));
            draft.getItems().add(item);
            draft.setTotalQty(5);
            draft.setTotalPrice(BigDecimal.valueOf(125));

            stubDraftLookup(100L, draft);

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(30), 50);
            stubDeviceMap(device);

            OfferResponse response = offerService.getOrCreateCart(100L);

            assertThat(response).isNotNull();
            assertThat(response.getOfferId()).isEqualTo(1L);
            assertThat(response.getStatus()).isEqualTo("Draft");
            assertThat(response.getBuyerCodeId()).isEqualTo(100L);
            assertThat(response.getItems()).hasSize(1);
        }

        @Test
        @DisplayName("no existing draft creates new BUYER/Draft offer")
        void noDraft_createsNewDraft() {
            when(offerRepository.findByBuyerCodeIdAndOfferTypeAndStatus(
                    100L, "BUYER", "Draft"))
                    .thenReturn(Optional.empty());

            Offer savedOffer = createDraftOffer(2L, 100L);
            when(offerRepository.save(any(Offer.class)))
                    .thenReturn(savedOffer);

            OfferResponse response = offerService.getOrCreateCart(100L);

            verify(offerRepository).save(any(Offer.class));
            assertThat(response).isNotNull();
            assertThat(response.getOfferId()).isEqualTo(2L);
            assertThat(response.getStatus()).isEqualTo("Draft");
        }
    }

    // ── upsertCartItem ──────────────────────────────────────────────────

    @Nested
    @DisplayName("upsertCartItem")
    class UpsertCartItem {

        @Test
        @DisplayName("new item adds to cart")
        void newItem_addsToCart() {
            Offer draft = createDraftOffer(1L, 100L);
            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            CartItemRequest request = createCartItemRequest(
                    "SKU-NEW", 10L, BigDecimal.valueOf(20), 3);

            OfferResponse response = offerService.upsertCartItem(100L, request);

            assertThat(draft.getItems()).hasSize(1);
            assertThat(draft.getItems().get(0).getSku()).isEqualTo("SKU-NEW");
            assertThat(draft.getItems().get(0).getQuantity()).isEqualTo(3);
            assertThat(draft.getItems().get(0).getPrice())
                    .isEqualByComparingTo(BigDecimal.valueOf(20));
            verify(offerRepository).save(draft);
        }

        @Test
        @DisplayName("existing item updates quantity and price")
        void existingItem_updatesQuantityAndPrice() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem existing = createOfferItem(draft, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(15));
            draft.getItems().add(existing);

            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            CartItemRequest request = createCartItemRequest(
                    "SKU-001", 10L, BigDecimal.valueOf(25), 5);

            OfferResponse response = offerService.upsertCartItem(100L, request);

            assertThat(draft.getItems()).hasSize(1);
            OfferItem updated = draft.getItems().get(0);
            assertThat(updated.getQuantity()).isEqualTo(5);
            assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(25));
            assertThat(updated.getTotalPrice())
                    .isEqualByComparingTo(BigDecimal.valueOf(125));
        }

        @Test
        @DisplayName("zero quantity removes existing item")
        void zeroQuantity_removesItem() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem existing = createOfferItem(draft, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(20));
            draft.getItems().add(existing);

            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            CartItemRequest request = createCartItemRequest(
                    "SKU-001", 10L, BigDecimal.valueOf(20), 0);

            OfferResponse response = offerService.upsertCartItem(100L, request);

            assertThat(draft.getItems()).isEmpty();
            verify(offerRepository).save(draft);
        }
    }

    // ── removeItem ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("removeItem")
    class RemoveItem {

        @Test
        @DisplayName("existing item is removed from cart by SKU")
        void existingItem_removesFromCart() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item1 = createOfferItem(draft, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(15));
            OfferItem item2 = createOfferItem(draft, "SKU-002", 11L, 3,
                    BigDecimal.valueOf(25));
            draft.getItems().add(item1);
            draft.getItems().add(item2);

            stubDraftLookup(100L, draft);
            stubSavePassthrough();
            Device device2 = createDevice(11L, "SKU-002", BigDecimal.valueOf(30), 50);
            stubDeviceMap(device2);

            OfferResponse response = offerService.removeItem(100L, "SKU-001");

            assertThat(draft.getItems()).hasSize(1);
            assertThat(draft.getItems().get(0).getSku()).isEqualTo("SKU-002");
            verify(offerRepository).save(draft);
        }
    }

    // ── resetCart ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("resetCart")
    class ResetCart {

        @Test
        @DisplayName("clears all items and zeroes totals")
        void clearsAllItems() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 5,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);
            draft.setTotalQty(5);
            draft.setTotalPrice(BigDecimal.valueOf(100));

            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            OfferResponse response = offerService.resetCart(100L);

            assertThat(draft.getItems()).isEmpty();
            assertThat(draft.getTotalQty()).isEqualTo(0);
            assertThat(draft.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
            verify(offerRepository).save(draft);
        }
    }

    // ── submitCart ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("submitCart")
    class SubmitCart {

        @Test
        @DisplayName("no draft returns alreadySubmitted")
        void noDraft_returnsAlreadySubmitted() {
            when(offerRepository.findByBuyerCodeIdAndOfferTypeAndStatus(
                    100L, "BUYER", "Draft"))
                    .thenReturn(Optional.empty());

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response.isAlreadySubmitted()).isTrue();
            assertThat(response.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("empty cart returns error")
        void emptyCart_returnsError() {
            Offer draft = createDraftOffer(1L, 100L);
            stubDraftLookup(100L, draft);

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors()).isNotEmpty();
            assertThat(response.getValidationErrors().get(0))
                    .contains("Cart is empty");
        }

        @Test
        @DisplayName("items exceeding ATP returns exceedingQty response")
        void itemsExceedAtp_returnsExceedingQty() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 20,
                    BigDecimal.valueOf(30));
            draft.getItems().add(item);
            draft.setTotalQty(20);
            draft.setTotalPrice(BigDecimal.valueOf(600));

            stubDraftLookup(100L, draft);

            // Device with availableQty=5 (below ATP_THRESHOLD of 100) and item qty=20 > 5
            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(30), 5);
            stubDeviceMap(device);

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getExceedingItemSkus()).contains("SKU-001");
        }

        @Test
        @DisplayName("items below list price returns salesReview response")
        void itemsBelowListPrice_returnsSalesReview() {
            Offer draft = createDraftOffer(1L, 100L);
            // offerPrice=20 < listPrice=50
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);
            draft.setTotalQty(3);
            draft.setTotalPrice(BigDecimal.valueOf(60));

            stubDraftLookup(100L, draft);

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.isRequiresSalesReview()).isTrue();
            assertThat(response.getBelowListPriceCount()).isEqualTo(1);
            assertThat(response.getOfferId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("all items at or above list price triggers direct order path")
        void allAboveListPrice_triggersDirectOrder() {
            Offer draft = createDraftOffer(1L, 100L);
            // offerPrice=50 >= listPrice=50
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(50));
            draft.getItems().add(item);
            draft.setTotalQty(2);
            draft.setTotalPrice(BigDecimal.valueOf(100));

            stubDraftLookup(100L, draft);

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            // Mock EM for generateOfferNumber + resolveUserName
            stubGenerateOfferNumber("BC001");

            // Oracle config off => returns without calling Oracle
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());

            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // createDirectOrder -> sendOrderToOracle returns null when no config
            // -> handleOracleResponse with null -> Pending_Order -> error response
            SubmitResponse response = offerService.submitCart(100L, 1L);

            // With no Oracle config, response will be an error (Pending_Order path)
            assertThat(response).isNotNull();
            // The oracle toggle-off path returns OracleResponse with message but null returnCode
            // which falls into the "ReturnCode != 00" branch -> error
            assertThat(response.isSuccess()).isFalse();
        }
    }

    // ── submitOffer ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("submitOffer")
    class SubmitOffer {

        @Test
        @DisplayName("valid draft offer sets status to Sales_Review")
        void validDraftOffer_setsStatusToSalesReview() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            stubSavePassthrough();

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getOfferNumber()).isNotNull();
            assertThat(draft.getStatus()).isEqualTo("Sales_Review");
            assertThat(draft.getSubmissionDate()).isNotNull();
            verify(offerRepository).save(draft);
        }

        @Test
        @DisplayName("offer not found returns error")
        void offerNotFound_returnsError() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            SubmitResponse response = offerService.submitOffer(999L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors()).contains("Offer not found.");
        }

        @Test
        @DisplayName("already submitted offer returns alreadySubmitted")
        void alreadySubmitted_returnsAlreadySubmitted() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Sales_Review");

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(response.isAlreadySubmitted()).isTrue();
            assertThat(response.isSuccess()).isFalse();
            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("sets Accept status for items at or above list price")
        void setsAcceptForAboveListPrice() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(60));
            draft.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            stubSavePassthrough();

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(item.getItemStatus()).isEqualTo("Accept");
        }

        @Test
        @DisplayName("sets Counter status for items below list price")
        void setsCounterForBelowListPrice() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            stubSavePassthrough();

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(item.getItemStatus()).isEqualTo("Counter");
            assertThat(item.getCounterQty()).isEqualTo(3);
            assertThat(item.getCounterPrice())
                    .isEqualByComparingTo(BigDecimal.valueOf(20));
        }
    }

    // ── submitOrder ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("submitOrder")
    class SubmitOrder {

        @Test
        @DisplayName("offer not found returns error")
        void offerNotFound_returnsError() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            SubmitResponse response = offerService.submitOrder(999L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors()).contains("Offer not found.");
        }

        @Test
        @DisplayName("already ordered offer returns error")
        void alreadyOrdered_returnsError() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Ordered");
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors().get(0))
                    .contains("already been submitted");
        }

        @Test
        @DisplayName("pending order offer returns error")
        void pendingOrder_returnsError() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Pending_Order");
            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("no accepted items returns error")
        void noAcceptedItems_returnsError() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Sales_Review");
            OfferItem declinedItem = createOfferItem(offer, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(20));
            declinedItem.setItemStatus("Decline");
            offer.getItems().add(declinedItem);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors().get(0))
                    .contains("not placed");
        }

        @Test
        @DisplayName("accepted items with Oracle toggle off returns Pending_Order")
        void acceptedItems_oracleOff_pendingOrder() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Sales_Review");
            offer.setOfferNumber("BC00126001");
            OfferItem item = createOfferItem(offer, "SKU-001", 10L, 3,
                    BigDecimal.valueOf(50));
            item.setItemStatus("Accept");
            offer.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());
            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            assertThat(response).isNotNull();
            verify(orderRepository, atLeastOnce()).save(any(Order.class));
        }

        @Test
        @DisplayName("finalize status items are included in order")
        void finalizeItems_includedInOrder() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Sales_Review");
            offer.setOfferNumber("BC00126001");
            OfferItem finalizedItem = createOfferItem(offer, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(40));
            finalizedItem.setItemStatus("Finalize");
            offer.getItems().add(finalizedItem);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());
            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            // Finalize items are accepted for order
            assertThat(response).isNotNull();
            verify(orderRepository, atLeastOnce()).save(any(Order.class));
        }

        @Test
        @DisplayName("counter + buyer accept items are included in order")
        void counterAcceptItems_includedInOrder() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Buyer_Acceptance");
            offer.setOfferNumber("BC00126001");
            OfferItem counterItem = createOfferItem(offer, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(40));
            counterItem.setItemStatus("Counter");
            counterItem.setBuyerCounterStatus("Accept");
            counterItem.setCounterPrice(BigDecimal.valueOf(45));
            offer.getItems().add(counterItem);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());
            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            SubmitResponse response = offerService.submitOrder(1L, 1L);

            assertThat(response).isNotNull();
            verify(orderRepository, atLeastOnce()).save(any(Order.class));
        }

        @Test
        @DisplayName("generates offer number when not already set")
        void generatesOfferNumber_whenNull() {
            Offer offer = createDraftOffer(1L, 100L);
            offer.setStatus("Sales_Review");
            offer.setOfferNumber(null);  // not set
            OfferItem item = createOfferItem(offer, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(50));
            item.setItemStatus("Accept");
            offer.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(50), 200);
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());
            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            offerService.submitOrder(1L, 1L);

            assertThat(offer.getOfferNumber()).isNotNull();
            assertThat(offer.getOfferNumber()).startsWith("BC001");
        }
    }

    // ── submitCart additional scenarios ──────────────────────────────────

    @Nested
    @DisplayName("submitCart — caseLot and edge cases")
    class SubmitCartCaseLot {

        @Test
        @DisplayName("case lot item exceeding ATP returns exceedingQty")
        void caseLotExceedingAtp_returnsExceedingQty() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-CL", 10L, 5,
                    BigDecimal.valueOf(30));
            item.setCaseLotId(50L);
            draft.getItems().add(item);
            draft.setTotalQty(5);
            draft.setTotalPrice(BigDecimal.valueOf(150));

            stubDraftLookup(100L, draft);

            Device device = createDevice(10L, "SKU-CL", BigDecimal.valueOf(30), 200);
            stubDeviceMap(device);

            // Case lot with low ATP (3 cases available, item requests 5)
            CaseLot caseLot = new CaseLot();
            caseLot.setId(50L);
            caseLot.setCaseLotAtpQty(3);
            caseLot.setCaseLotSize(10);
            when(caseLotRepository.findById(50L)).thenReturn(Optional.of(caseLot));

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getExceedingItemSkus()).contains("SKU-CL");
        }

        @Test
        @DisplayName("null device in cart is skipped during ATP check")
        void nullDevice_skippedDuringAtpCheck() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-GONE", 999L, 3,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);
            draft.setTotalQty(3);
            draft.setTotalPrice(BigDecimal.valueOf(60));

            stubDraftLookup(100L, draft);
            // No device found for deviceId 999
            when(deviceRepository.findAllById(any())).thenReturn(Collections.emptyList());

            // No device → no list price comparison → goes to direct order path
            stubGenerateOfferNumber("BC001");
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());
            stubSavePassthrough();
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            SubmitResponse response = offerService.submitCart(100L, 1L);

            assertThat(response).isNotNull();
        }
    }

    // ── upsertCartItem caseLot ──────────────────────────────────────────

    @Nested
    @DisplayName("upsertCartItem — caseLot")
    class UpsertCartItemCaseLot {

        @Test
        @DisplayName("case lot item calculates total using caseLotSize")
        void caseLotItem_calculatesTotalWithCaseLotSize() {
            Offer draft = createDraftOffer(1L, 100L);
            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            CaseLot caseLot = new CaseLot();
            caseLot.setId(50L);
            caseLot.setCaseLotSize(10);
            when(caseLotRepository.findById(50L)).thenReturn(Optional.of(caseLot));

            CartItemRequest request = createCartItemRequest("SKU-CL", 10L,
                    BigDecimal.valueOf(5), 3);
            request.setCaseLotId(50L);

            OfferResponse response = offerService.upsertCartItem(100L, request);

            assertThat(draft.getItems()).hasSize(1);
            OfferItem item = draft.getItems().get(0);
            // total = 5 * 10 * 3 = 150
            assertThat(item.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
            assertThat(item.getCaseLotId()).isEqualTo(50L);
        }

        @Test
        @DisplayName("case lot not found defaults caseLotSize to 1")
        void caseLotNotFound_defaultsToSizeOne() {
            Offer draft = createDraftOffer(1L, 100L);
            stubDraftLookup(100L, draft);
            stubSavePassthrough();

            when(caseLotRepository.findById(99L)).thenReturn(Optional.empty());

            CartItemRequest request = createCartItemRequest("SKU-CL2", 10L,
                    BigDecimal.valueOf(10), 2);
            request.setCaseLotId(99L);

            offerService.upsertCartItem(100L, request);

            OfferItem item = draft.getItems().get(0);
            // total = 10 * 1 * 2 = 20 (caseLotSize defaults to 1)
            assertThat(item.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));
        }
    }

    // ── submitOffer edge cases ──────────────────────────────────────────

    @Nested
    @DisplayName("submitOffer — edge cases")
    class SubmitOfferEdgeCases {

        @Test
        @DisplayName("no active items returns error")
        void noActiveItems_returnsError() {
            Offer draft = createDraftOffer(1L, 100L);
            // Item with zero quantity — filtered out
            OfferItem zeroItem = createOfferItem(draft, "SKU-001", 10L, 0,
                    BigDecimal.valueOf(20));
            draft.getItems().add(zeroItem);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getValidationErrors().get(0)).contains("No items");
        }

        @Test
        @DisplayName("item with null device sets Accept status")
        void nullDevice_setsAcceptStatus() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-GONE", 999L, 2,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));
            when(deviceRepository.findAllById(any())).thenReturn(Collections.emptyList());

            stubGenerateOfferNumber("BC001");
            stubSavePassthrough();

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(item.getItemStatus()).isEqualTo("Accept");
        }

        @Test
        @DisplayName("item with null listPrice sets Accept status")
        void nullListPrice_setsAcceptStatus() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 2,
                    BigDecimal.valueOf(20));
            draft.getItems().add(item);

            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));

            Device device = createDevice(10L, "SKU-001", null, 200); // null listPrice
            stubDeviceMap(device);

            stubGenerateOfferNumber("BC001");
            stubSavePassthrough();

            SubmitResponse response = offerService.submitOffer(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(item.getItemStatus()).isEqualTo("Accept");
        }
    }

    // ── exportCartCsv ───────────────────────────────────────────────────

    @Nested
    @DisplayName("exportCartCsv")
    class ExportCartCsv {

        @Test
        @DisplayName("generates CSV with header and item rows")
        void generatesCsvWithItems() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-001", 10L, 5,
                    BigDecimal.valueOf(25));
            draft.getItems().add(item);

            stubDraftLookup(100L, draft);

            Device device = createDevice(10L, "SKU-001", BigDecimal.valueOf(30), 50);
            stubDeviceMap(device);

            String csv = offerService.exportCartCsv(100L);

            assertThat(csv).startsWith("SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Quantity,List Price,Offer Price,Total");
            assertThat(csv).contains("SKU-001");
            assertThat(csv).contains("5");
            assertThat(csv).contains("25");
        }

        @Test
        @DisplayName("empty cart generates header only")
        void emptyCart_generatesHeaderOnly() {
            Offer draft = createDraftOffer(1L, 100L);
            stubDraftLookup(100L, draft);

            String csv = offerService.exportCartCsv(100L);

            assertThat(csv).startsWith("SKU,");
            // Only header line, no data rows
            assertThat(csv.split("\n")).hasSize(1);
        }

        @Test
        @DisplayName("null device in CSV shows empty fields")
        void nullDevice_showsEmptyFields() {
            Offer draft = createDraftOffer(1L, 100L);
            OfferItem item = createOfferItem(draft, "SKU-GONE", 999L, 2,
                    BigDecimal.valueOf(10));
            draft.getItems().add(item);

            stubDraftLookup(100L, draft);
            when(deviceRepository.findAllById(any())).thenReturn(Collections.emptyList());

            String csv = offerService.exportCartCsv(100L);

            assertThat(csv).contains("SKU-GONE");
            assertThat(csv.split("\n")).hasSize(2); // header + 1 data row
        }
    }
}
