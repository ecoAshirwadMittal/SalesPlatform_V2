package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.Order;
import com.ecoatm.salesplatform.model.pws.PwsOfferStatus;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.repository.pws.OfferItemRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import com.ecoatm.salesplatform.service.email.PwsOfferEmailEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferService.class);

    private static final String OFFER_TYPE_BUYER = "BUYER";
    private static final int ATP_THRESHOLD = 100;

    // Status literals — see PwsOfferStatus for the authoritative set.
    private static final String STATUS_DRAFT = PwsOfferStatus.DRAFT;
    private static final String STATUS_SALES_REVIEW = PwsOfferStatus.SALES_REVIEW;
    private static final String STATUS_SUBMITTED = PwsOfferStatus.SUBMITTED;
    private static final String STATUS_PENDING_REVIEW = PwsOfferStatus.PENDING_REVIEW;
    private static final String STATUS_ORDERED = PwsOfferStatus.ORDERED;
    private static final String STATUS_PENDING_ORDER = PwsOfferStatus.PENDING_ORDER;
    private static final String STATUS_DECLINED = PwsOfferStatus.DECLINED;

    private static final String ITEM_STATUS_ACCEPT = PwsOfferStatus.ITEM_ACCEPT;
    private static final String ITEM_STATUS_COUNTER = PwsOfferStatus.ITEM_COUNTER;
    private static final String ITEM_STATUS_FINALIZE = PwsOfferStatus.ITEM_FINALIZE;
    private static final String ITEM_STATUS_DECLINE = PwsOfferStatus.ITEM_DECLINE;

    private static final String DRAWER_SALES_REVIEW = PwsOfferStatus.DRAWER_SALES_REVIEW;
    private static final String DRAWER_ORDERED = PwsOfferStatus.DRAWER_ORDERED;
    private static final String DRAWER_ACCEPTED = PwsOfferStatus.DRAWER_ACCEPTED;
    private static final String DRAWER_COUNTERED = PwsOfferStatus.DRAWER_COUNTERED;
    private static final String DRAWER_SELLER_DECLINED = PwsOfferStatus.DRAWER_SELLER_DECLINED;
    private static final String DRAWER_BUYER_DECLINED = PwsOfferStatus.DRAWER_BUYER_DECLINED;

    private static final String COUNTER_STATUS_ACCEPT = PwsOfferStatus.COUNTER_ACCEPT;
    private static final String COUNTER_STATUS_DECLINE = PwsOfferStatus.COUNTER_DECLINE;

    private final OfferRepository offerRepository;
    private final OfferItemRepository offerItemRepository;
    private final DeviceRepository deviceRepository;
    private final OrderRepository orderRepository;
    private final CaseLotRepository caseLotRepository;
    private final ObjectMapper objectMapper;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final EntityManager em;
    private final ApplicationEventPublisher eventPublisher;
    private final OfferItemDeviceLoader deviceLoader;
    private final OracleOrderClient oracleOrderClient;
    private final OfferNumberGenerator offerNumberGenerator;

    public OfferService(OfferRepository offerRepository,
                        OfferItemRepository offerItemRepository,
                        DeviceRepository deviceRepository,
                        OrderRepository orderRepository,
                        CaseLotRepository caseLotRepository,
                        ObjectMapper objectMapper,
                        BuyerCodeLookupService buyerCodeLookup,
                        EntityManager em,
                        ApplicationEventPublisher eventPublisher,
                        OfferItemDeviceLoader deviceLoader,
                        OracleOrderClient oracleOrderClient,
                        OfferNumberGenerator offerNumberGenerator) {
        this.offerRepository = offerRepository;
        this.offerItemRepository = offerItemRepository;
        this.deviceRepository = deviceRepository;
        this.orderRepository = orderRepository;
        this.caseLotRepository = caseLotRepository;
        this.objectMapper = objectMapper;
        this.buyerCodeLookup = buyerCodeLookup;
        this.em = em;
        this.eventPublisher = eventPublisher;
        this.deviceLoader = deviceLoader;
        this.oracleOrderClient = oracleOrderClient;
        this.offerNumberGenerator = offerNumberGenerator;
    }

    @Transactional
    public OfferResponse getOrCreateCart(Long buyerCodeId) {
        Offer offer = findOrCreateDraft(buyerCodeId);
        return buildResponse(offer);
    }

    @Transactional
    public OfferResponse upsertCartItem(Long buyerCodeId, CartItemRequest request) {
        Offer offer = findOrCreateDraft(buyerCodeId);

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            offer.getItems().removeIf(i -> i.getSku().equals(request.getSku()));
        } else {
            // For case lot items, match on both SKU and caseLotId
            Optional<OfferItem> existing = offer.getItems().stream()
                    .filter(i -> i.getSku().equals(request.getSku())
                            && Objects.equals(i.getCaseLotId(), request.getCaseLotId()))
                    .findFirst();

            // For case lot items: quantity = number of cases, totalPrice = offerPrice × caseLotSize × quantity
            BigDecimal totalPrice;
            if (request.getCaseLotId() != null) {
                CaseLot caseLot = caseLotRepository.findById(request.getCaseLotId()).orElse(null);
                int caseLotSize = caseLot != null ? caseLot.getCaseLotSize() : 1;
                totalPrice = request.getOfferPrice()
                        .multiply(BigDecimal.valueOf(caseLotSize))
                        .multiply(BigDecimal.valueOf(request.getQuantity()));
            } else {
                totalPrice = request.getOfferPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            }

            if (existing.isPresent()) {
                OfferItem item = existing.get();
                item.setPrice(request.getOfferPrice());
                item.setQuantity(request.getQuantity());
                item.setTotalPrice(totalPrice);
                item.setCaseLotId(request.getCaseLotId());
            } else {
                OfferItem item = new OfferItem();
                item.setOffer(offer);
                item.setSku(request.getSku());
                item.setDeviceId(request.getDeviceId());
                item.setCaseLotId(request.getCaseLotId());
                item.setPrice(request.getOfferPrice());
                item.setQuantity(request.getQuantity());
                item.setTotalPrice(totalPrice);
                item.setItemStatus(STATUS_DRAFT);
                offer.getItems().add(item);
            }
        }

        recalcTotals(offer);
        offerRepository.save(offer);
        return buildResponse(offer);
    }

    @Transactional
    public OfferResponse removeItem(Long buyerCodeId, String sku) {
        Offer offer = findOrCreateDraft(buyerCodeId);
        offer.getItems().removeIf(i -> i.getSku().equals(sku));
        recalcTotals(offer);
        offerRepository.save(offer);
        return buildResponse(offer);
    }

    @Transactional
    public OfferResponse resetCart(Long buyerCodeId) {
        Offer offer = findOrCreateDraft(buyerCodeId);
        offer.getItems().clear();
        offer.setTotalQty(0);
        offer.setTotalPrice(BigDecimal.ZERO);
        offerRepository.save(offer);
        return buildResponse(offer);
    }

    /**
     * Submit the cart — implements all 4 scenarios from Mendix ACT_SubmitCart:
     *   1. Already submitted (no DRAFT offer found)
     *   2. Items exceed ATP quantity
     *   3. Items below list price → sales review needed
     *   4. All items at/above list price → direct order creation
     */
    @Transactional
    public SubmitResponse submitCart(Long buyerCodeId, Long userId) {
        // Scenario 1: Already submitted — no DRAFT offer exists for this buyer code
        Optional<Offer> draftOpt = offerRepository.findByBuyerCodeIdAndOfferTypeAndStatus(
                buyerCodeId, OFFER_TYPE_BUYER, STATUS_DRAFT);
        if (draftOpt.isEmpty()) {
            log.info("Cart already submitted for buyerCodeId={}", buyerCodeId);
            return SubmitResponse.alreadySubmitted();
        }

        Offer offer = draftOpt.get();
        List<OfferItem> activeItems = offer.getItems().stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0
                        && i.getTotalPrice() != null && i.getTotalPrice().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        // Also check: if offer totals are zero/empty, treat as already submitted
        if (activeItems.isEmpty()
                || (offer.getTotalQty() != null && offer.getTotalQty() == 0)
                || (offer.getTotalPrice() != null && offer.getTotalPrice().compareTo(BigDecimal.ZERO) == 0)) {
            // Empty cart — could be already submitted or genuinely empty
            if (activeItems.isEmpty()) {
                return SubmitResponse.error(List.of("Cart is empty. Add items before submitting."));
            }
        }

        Map<Long, Device> deviceMap = loadDeviceMap(activeItems);

        // Scenario 2: ATP exceeding check
        // Mendix logic: for each item with TotalPrice > 0, check if qty > ATPQty AND ATPQty <= 100
        // For SPB (case lot) items, check against CaseLot.CaseLotATPQty instead of Device.atpQty
        List<String> exceedingSkus = new ArrayList<>();
        for (OfferItem item : activeItems) {
            Device device = deviceMap.get(item.getDeviceId());
            if (device == null) continue;

            Integer atpQty;
            if (item.getCaseLotId() != null) {
                // Case lot item: check against CaseLot.caseLotAtpQty
                CaseLot caseLot = caseLotRepository.findById(item.getCaseLotId()).orElse(null);
                atpQty = caseLot != null ? caseLot.getCaseLotAtpQty() : device.getAtpQty();
            } else {
                // Regular device: uses Device.atpQty (available-to-promise)
                atpQty = device.getAtpQty();
            }

            if (atpQty != null && atpQty <= ATP_THRESHOLD && item.getQuantity() > atpQty) {
                exceedingSkus.add(item.getSku());
            }
        }

        if (!exceedingSkus.isEmpty()) {
            log.info("Cart submit: {} items exceed ATP for buyerCodeId={}", exceedingSkus.size(), buyerCodeId);
            return SubmitResponse.exceedingQty(offer.getId(), exceedingSkus);
        }

        // Scenario 3: Check for items below list price
        List<OfferItem> belowListPriceItems = new ArrayList<>();
        for (OfferItem item : activeItems) {
            Device device = deviceMap.get(item.getDeviceId());
            if (device != null && device.getListPrice() != null
                    && item.getPrice() != null
                    && item.getPrice().compareTo(device.getListPrice()) < 0) {
                belowListPriceItems.add(item);
            }
        }

        if (!belowListPriceItems.isEmpty()) {
            // Keep offer as DRAFT — user must confirm via "Submit Offer" in the Almost Done modal
            log.info("Cart submit: {} items below list price for buyerCodeId={}, showing Almost Done",
                    belowListPriceItems.size(), buyerCodeId);
            return SubmitResponse.salesReview(offer.getId(), belowListPriceItems.size());
        }

        // Scenario 4: All items at or above list price → direct order
        log.info("Cart submit: direct order for buyerCodeId={}, offerId={}", buyerCodeId, offer.getId());
        return createDirectOrder(offer, userId);
    }

    /**
     * Submit offer for sales review — called from "Almost Done" modal's "Submit Offer" button.
     *
     * Clones Mendix ACT_Offer_SubmitOffer flow:
     *   1. SUB_BuyerOffer_CreateOffer — set per-item status (Accept/Counter) based on price vs listPrice
     *   2. SUB_RetrieveOrderStatus — set offer status to SALES_REVIEW
     *   3. SUB_UpdateOfferDrawerStatus — set each item's drawer status to Sales_Review + reserve device qty
     *   4. SUB_BuyerOffer_RemoveRecords — (in unified schema, we just finalize the offer; no separate
     *      BuyerOffer to delete — the DRAFT offer itself becomes the permanent offer)
     *   5. SUB_SendPWSOfferConfirmationEmail — (stubbed: email integration not yet wired)
     */
    @Transactional
    public SubmitResponse submitOffer(Long offerId, Long submittedByUserId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            return SubmitResponse.error(List.of("Offer not found."));
        }

        Offer offer = offerOpt.get();

        // If already submitted, don't re-submit
        if (!STATUS_DRAFT.equals(offer.getStatus())) {
            return SubmitResponse.alreadySubmitted();
        }

        List<OfferItem> activeItems = offer.getItems().stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0
                        && i.getTotalPrice() != null && i.getTotalPrice().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (activeItems.isEmpty()) {
            return SubmitResponse.error(List.of("No items to submit."));
        }

        Map<Long, Device> deviceMap = loadDeviceMap(activeItems);

        // ── SUB_BuyerOffer_CreateOffer logic ──
        // Set per-item SalesOfferItemStatus based on offerPrice vs device listPrice.
        // Mendix: Accept if offerPrice >= listPrice, Counter if offerPrice < listPrice
        for (OfferItem item : activeItems) {
            Device device = deviceMap.get(item.getDeviceId());
            if (device == null || device.getListPrice() == null || item.getPrice() == null) {
                item.setItemStatus(ITEM_STATUS_ACCEPT);
                continue;
            }
            if (item.getPrice().compareTo(device.getListPrice()) >= 0) {
                item.setItemStatus(ITEM_STATUS_ACCEPT);
            } else {
                item.setItemStatus(ITEM_STATUS_COUNTER);
                // Mendix sets CounterQuantity = BuyerOfferItem.Quantity (initial counter = buyer's qty)
                item.setCounterQty(item.getQuantity());
                item.setCounterPrice(item.getPrice());
                item.setCounterTotal(item.getTotalPrice());
            }
        }

        // Recalculate offer totals from active items
        recalcTotals(offer);

        // ── ACr_UpdateOfferID — generate human-readable offer number ──
        String offerNumber = offerNumberGenerator.next(offer.getBuyerCodeId());
        offer.setOfferNumber(offerNumber);

        // ── SUB_RetrieveOrderStatus + Update offer ──
        offer.setStatus(STATUS_SALES_REVIEW);
        offer.setSubmissionDate(LocalDateTime.now());

        // ── SUB_UpdateOfferDrawerStatus (Sales_Review branch) ──
        // Mendix: for Sales_Review, all items get OfferDrawerStatus = Sales_Review
        // Then reserve device quantities
        for (OfferItem item : activeItems) {
            item.setOfferDrawerStatus(DRAWER_SALES_REVIEW);
            reserveDeviceQuantity(item, deviceMap.get(item.getDeviceId()));
        }

        offerRepository.save(offer);

        // ── SUB_SendPWSOfferConfirmationEmail — delivered after commit ──
        if (submittedByUserId != null) {
            eventPublisher.publishEvent(
                    new PwsOfferEmailEvent.OfferConfirmation(offer.getId(), submittedByUserId));
        }
        log.info("Offer {} (offerNumber={}) submitted for sales review (buyerCodeId={})",
                offerId, offerNumber, offer.getBuyerCodeId());

        return SubmitResponse.offerSubmitted(offerId, offerNumber);
    }

    /**
     * Submit order from a finalized offer (post sales review).
     * Clones Mendix ACT_Offer_SubmitOrder / ACT_Offer_SubmitOrder_2:
     *   Called when a sales rep clicks "Submit Order" on an offer that has been through review.
     *   Filters for accepted items, creates Order, sends to Oracle, handles response.
     */
    @Transactional
    public SubmitResponse submitOrder(Long offerId, Long userId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            return SubmitResponse.error(List.of("Offer not found."));
        }

        Offer offer = offerOpt.get();

        // ACT_Offer_SubmitOrder_2 works with a FinalOffer — typically in SALES_REVIEW
        // or Buyer_Acceptance status after the review cycle is complete
        if (STATUS_ORDERED.equals(offer.getStatus()) || STATUS_PENDING_ORDER.equals(offer.getStatus())) {
            return SubmitResponse.error(List.of("This offer has already been submitted for order."));
        }

        List<OfferItem> allItems = offer.getItems();

        // SUB_Order_CreateFromOffer: filter items that are accepted for ordering
        // Mendix: SalesOfferItemStatus='Accept' OR (Counter AND BuyerCounterStatus='Accept')
        //         OR SalesOfferItemStatus='Finalize'
        List<OfferItem> acceptedItems = allItems.stream()
                .filter(this::isItemAcceptedForOrder)
                .toList();

        if (acceptedItems.isEmpty()) {
            log.warn("No accepted items for order submission: offerId={}", offerId);
            return SubmitResponse.error(List.of("Order is not placed. Please try Re-Submitting the Order"));
        }

        Map<Long, Device> deviceMap = loadDeviceMap(acceptedItems);

        // Generate offer number if not already set
        if (offer.getOfferNumber() == null || offer.getOfferNumber().isBlank()) {
            String offerNumber = offerNumberGenerator.next(offer.getBuyerCodeId());
            offer.setOfferNumber(offerNumber);
        }

        // SUB_Offer_CreateOrder — create Order entity linked to Offer and BuyerCode
        Order order = new Order();
        order.setOffer(offer);
        order.setBuyerCodeId(offer.getBuyerCodeId());
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        // SUB_Offer_PrepareOraclePayload — build Oracle request JSON
        String originSystemUser = resolveUserName(userId);
        String oraclePayload = prepareOraclePayload(offer, acceptedItems, deviceMap, originSystemUser);
        order.setJsonContent(oraclePayload);

        // SUB_Order_SendOrderToOracle — delegated to OracleOrderClient
        OracleResponse oracleResponse = oracleOrderClient.submitOrder(oraclePayload);

        // SUB_CreateOrderResponse_ManageResult — handle Oracle response
        return handleOracleResponse(offer, order, acceptedItems, deviceMap, oracleResponse);
    }

    public String exportCartCsv(Long buyerCodeId) {
        Offer offer = findOrCreateDraft(buyerCodeId);
        List<OfferItem> items = offer.getItems().stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0)
                .toList();

        Map<Long, Device> deviceMap = loadDeviceMap(items);

        StringBuilder csv = new StringBuilder();
        csv.append("SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Quantity,List Price,Offer Price,Total\n");

        for (OfferItem item : items) {
            Device d = deviceMap.get(item.getDeviceId());
            csv.append(escapeCsv(item.getSku())).append(',');
            csv.append(escapeCsv(d != null && d.getCategory() != null ? d.getCategory().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getBrand() != null ? d.getBrand().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getModel() != null ? d.getModel().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getCarrier() != null ? d.getCarrier().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getCapacity() != null ? d.getCapacity().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getColor() != null ? d.getColor().getDisplayName() : "")).append(',');
            csv.append(escapeCsv(d != null && d.getGrade() != null ? d.getGrade().getDisplayName() : "")).append(',');
            csv.append(item.getQuantity()).append(',');
            csv.append(d != null && d.getListPrice() != null ? d.getListPrice() : "").append(',');
            csv.append(item.getPrice() != null ? item.getPrice() : "").append(',');
            csv.append(item.getTotalPrice() != null ? item.getTotalPrice() : "");
            csv.append('\n');
        }
        return csv.toString();
    }

    // ---- private helpers ----

    /**
     * Scenario 4: Direct order — all items at/above list price.
     *
     * Clones full Mendix ACT_Offer_SubmitOrder chain:
     *   1. SUB_BuyerOffer_CreateOffer — set per-item SalesOfferItemStatus (Accept for direct order)
     *   2. SUB_BuyerOffer_RemoveRecords — (unified schema: DRAFT→SUBMITTED transition)
     *   3. SUB_Order_CreateFromOffer → SUB_Offer_CreateOrder — filter accepted items, create Order,
     *      link accepted items to Order, set Order_BuyerCode
     *   4. SUB_Offer_PrepareOraclePayload — build JSON payload for Oracle ERP
     *   5. SUB_Order_SendOrderToOracle — send to Oracle (stubbed)
     *   6. SUB_CreateOrderResponse_ManageResult — handle 3 Oracle response outcomes:
     *      a. ReturnCode='00' → Ordered: update Order with Oracle fields, send confirmation email
     *      b. ReturnCode!='00' → Pending_Order: update Order with error, send pending email
     *      c. Response empty → Pending_Order: link Order to Offer, return error
     *   7. SUB_UpdateOfferDrawerStatus (Ordered/Pending_Order branch) — set per-item drawer status
     *   8. SUB_ReserveQuantityForDevice — decrement avail, increment reserved per item
     *   9. SUB_Offer_UpdateSnowflake — (stubbed: Snowflake analytics sync)
     */
    private SubmitResponse createDirectOrder(Offer offer, Long userId) {
        List<OfferItem> activeItems = offer.getItems().stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0
                        && i.getTotalPrice() != null && i.getTotalPrice().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        Map<Long, Device> deviceMap = loadDeviceMap(activeItems);

        // ── Step 1: SUB_BuyerOffer_CreateOffer ──
        // For direct order, all items are at/above list price → all get Accept status
        for (OfferItem item : activeItems) {
            item.setItemStatus(ITEM_STATUS_ACCEPT);
        }

        // ── Step 2: ACr_UpdateOfferID — generate human-readable offer number ──
        String offerNumber = offerNumberGenerator.next(offer.getBuyerCodeId());
        offer.setOfferNumber(offerNumber);

        // ── Step 3: SUB_Order_CreateFromOffer → SUB_Offer_CreateOrder ──
        // Mendix filters: SalesOfferItemStatus='Accept' OR (Counter AND BuyerCounterStatus='Accept')
        //                  OR SalesOfferItemStatus='Finalize'
        // For direct order from cart, all items are Accept, so all are included
        List<OfferItem> acceptedItems = activeItems.stream()
                .filter(item -> isItemAcceptedForOrder(item))
                .toList();

        if (acceptedItems.isEmpty()) {
            log.warn("No accepted items for order: offerId={}", offer.getId());
            return SubmitResponse.error(List.of("Order is not placed. Please try Re-Submitting the Order"));
        }

        Order order = new Order();
        order.setOffer(offer);
        order.setBuyerCodeId(offer.getBuyerCodeId());
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        // ── Step 4: SUB_Offer_PrepareOraclePayload ──
        // Build the Oracle order request payload (for future Oracle integration)
        // Resolve user name for Oracle originSystemUser field
        String originSystemUser = resolveUserName(userId);
        String oraclePayload = prepareOraclePayload(offer, acceptedItems, deviceMap, originSystemUser);
        order.setJsonContent(oraclePayload);

        // ── Step 5: SUB_Order_SendOrderToOracle ──
        // Oracle ERP REST API call delegated to OracleOrderClient.
        OracleResponse oracleResponse = oracleOrderClient.submitOrder(oraclePayload);

        // ── Step 6: SUB_CreateOrderResponse_ManageResult ──
        // Handle 3 possible Oracle response outcomes
        return handleOracleResponse(offer, order, acceptedItems, deviceMap, oracleResponse);
    }

    /**
     * Check if an offer item qualifies for order creation.
     * Mendix filter: SalesOfferItemStatus='Accept'
     *   OR (SalesOfferItemStatus='Counter' AND BuyerCounterStatus='Accept')
     *   OR SalesOfferItemStatus='Finalize'
     */
    private boolean isItemAcceptedForOrder(OfferItem item) {
        String status = item.getItemStatus();
        if (ITEM_STATUS_ACCEPT.equals(status)) return true;
        if (ITEM_STATUS_FINALIZE.equals(status)) return true;
        if (ITEM_STATUS_COUNTER.equals(status)
                && COUNTER_STATUS_ACCEPT.equals(item.getBuyerCounterStatus())) return true;
        return false;
    }

    /**
     * Build Oracle ERP order request payload.
     * Clones SUB_Offer_PrepareOraclePayload: creates OrderRequest with line items.
     * When Oracle integration is wired, this JSON is sent to the Oracle REST endpoint.
     */
    private String prepareOraclePayload(Offer offer, List<OfferItem> acceptedItems,
                                         Map<Long, Device> deviceMap, String originSystemUser) {
        // Retrieve buyer code string for the payload
        String buyerCode = buyerCodeLookup.findCodeById(offer.getBuyerCodeId());
        if (buyerCode == null) {
            buyerCode = String.valueOf(offer.getBuyerCodeId());
        }

        // Mendix format: yyyyMMddHHmmss (SUB_Offer_PrepareOraclePayload step 5)
        String orderDate = LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Mendix: EM_CreateOrderRequest — wrapped in { "request": { ... } }
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("originSystemOrderId", offer.getOfferNumber());
            request.put("orderType", "PWS");
            request.put("orderDate", orderDate);
            request.put("buyerCode", buyerCode);
            request.put("originSystemUser", originSystemUser != null ? originSystemUser : "");
            request.put("shippingInstructions", "");
            request.put("freeFormOrd01", "");
            request.put("freeFormOrd02", "");
            request.put("freeFormOrd03", "");

            List<Map<String, Object>> orderLines = new ArrayList<>();
            for (OfferItem item : acceptedItems) {
                BigDecimal finalOfferPrice = item.getPrice();
                if (ITEM_STATUS_COUNTER.equals(item.getItemStatus())
                        && item.getCounterPrice() != null) {
                    finalOfferPrice = item.getCounterPrice();
                }

                if (item.getCaseLotId() != null) {
                    CaseLot caseLot = caseLotRepository.findById(item.getCaseLotId()).orElse(null);
                    int caseLotSize = caseLot != null ? caseLot.getCaseLotSize() : 1;
                    int numCases = item.getQuantity();

                    for (int c = 0; c < numCases; c++) {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("item_number", item.getSku());
                        line.put("Quantity", String.valueOf(caseLotSize));
                        line.put("unitSellingPrice", String.valueOf(finalOfferPrice));
                        line.put("originSystemLineId", "");
                        line.put("freeFormLine01", "");
                        line.put("freeFormLine02", "");
                        line.put("freeFormLine03", "");
                        orderLines.add(line);
                    }
                } else {
                    Map<String, Object> line = new LinkedHashMap<>();
                    line.put("item_number", item.getSku());
                    line.put("Quantity", String.valueOf(item.getQuantity()));
                    line.put("unitSellingPrice", String.valueOf(finalOfferPrice));
                    line.put("originSystemLineId", "");
                    line.put("freeFormLine01", "");
                    line.put("freeFormLine02", "");
                    line.put("freeFormLine03", "");
                    orderLines.add(line);
                }
            }

            request.put("orderLine", orderLines);
            Map<String, Object> wrapper = Map.of("request", request);
            return objectMapper.writeValueAsString(wrapper);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Oracle payload", e);
        }
    }

    /**
     * Handle Oracle ERP response — 3 outcomes.
     * Clones SUB_CreateOrderResponse_ManageResult:
     *   1. Response exists + ReturnCode='00' → Success (Ordered)
     *   2. Response exists + ReturnCode!='00' → Oracle error (Pending_Order)
     *   3. Response is null → Oracle unreachable (Pending_Order)
     */
    private SubmitResponse handleOracleResponse(Offer offer, Order order,
                                                 List<OfferItem> acceptedItems,
                                                 Map<Long, Device> deviceMap,
                                                 OracleResponse oracleResponse) {
        LocalDateTime now = LocalDateTime.now();
        offer.setFinalOfferSubmittedOn(now);

        if (oracleResponse == null) {
            // ── Outcome 3: Oracle unreachable — empty response ──
            // Mendix: set Order → Offer link, set status Pending_Order
            log.warn("Oracle response empty for offerId={} — setting Pending_Order", offer.getId());

            offer.setStatus(STATUS_PENDING_ORDER);
            offer.setSubmissionDate(now);
            offerRepository.save(offer);

            orderRepository.save(order);

            // SUB_UpdateOfferDrawerStatus (Pending_Order branch — same logic as Ordered)
            updateDrawerStatusOrdered(acceptedItems, deviceMap);
            offerRepository.save(offer);

            return SubmitResponse.error(List.of("Order is not placed. Please try Re-Submitting the Order"));
        }

        if ("00".equals(oracleResponse.getReturnCode())) {
            // ── Outcome 1: Oracle success — ReturnCode='00' ──
            log.info("Oracle success for offerId={}, oracleOrderNumber={}",
                    offer.getId(), oracleResponse.getOrderNumber());

            // Update offer status to Ordered
            offer.setStatus(STATUS_ORDERED);
            offer.setSubmissionDate(now);

            // Update order with Oracle response fields
            order.setOrderNumber(oracleResponse.getOrderNumber());
            order.setOracleStatus(oracleResponse.getReturnMessage());
            order.setOrderLine(oracleResponse.getOrderId());
            order.setOracleHttpCode(oracleResponse.getHttpCode());
            order.setOracleJsonResponse(oracleResponse.getJsonResponse());
            order.setIsSuccessful(true);
            orderRepository.save(order);

            // SUB_UpdateOfferDrawerStatus (Ordered branch)
            updateDrawerStatusOrdered(acceptedItems, deviceMap);
            offerRepository.save(offer);

            // SUB_SendPWSOrderConfirmationEmail — delivered after commit
            eventPublisher.publishEvent(new PwsOfferEmailEvent.OrderConfirmation(offer.getId()));

            // SUB_Offer_UpdateSnowflake (stubbed)
            // TODO: Sync offer data to Snowflake analytics

            log.info("Direct order created: offerId={}, orderNumber={}, items={}",
                    offer.getId(), order.getOrderNumber(), acceptedItems.size());
            return SubmitResponse.submitted(offer.getId(), order.getOrderNumber());

        } else {
            // ── Outcome 2: Oracle error — ReturnCode!='00' ──
            log.warn("Oracle error for offerId={}: code={}, message={}",
                    offer.getId(), oracleResponse.getReturnCode(), oracleResponse.getReturnMessage());

            // Update offer status to Pending_Order
            offer.setStatus(STATUS_PENDING_ORDER);
            offer.setSubmissionDate(now);

            // Update order with Oracle error details
            order.setOrderNumber(oracleResponse.getOrderNumber());
            order.setOracleStatus(oracleResponse.getReturnMessage());
            order.setOrderLine(oracleResponse.getOrderId());
            order.setOracleHttpCode(oracleResponse.getHttpCode());
            order.setOracleJsonResponse(oracleResponse.getJsonResponse());
            order.setIsSuccessful(false);
            orderRepository.save(order);

            // SUB_UpdateOfferDrawerStatus (Pending_Order branch — same as Ordered)
            updateDrawerStatusOrdered(acceptedItems, deviceMap);
            offerRepository.save(offer);

            // SUB_SendPWSPendingOrderEmail — delivered after commit
            eventPublisher.publishEvent(new PwsOfferEmailEvent.PendingOrder(offer.getId()));

            // SUB_PWSErrorCode_GetMessage — look up user-friendly error message
            String userMessage = lookupErrorMessage(oracleResponse.getReturnCode(), "ORACLE");

            return SubmitResponse.error(List.of(userMessage));
        }
    }

    /**
     * Set per-item OfferDrawerStatus for Ordered/Pending_Order branch.
     * Clones SUB_UpdateOfferDrawerStatus (Ordered branch):
     *   - Accept → Ordered
     *   - Decline → Seller_Declined
     *   - Counter + BuyerCounterStatus=Accept → Ordered
     *   - Counter + BuyerCounterStatus=Decline → Buyer_Declined
     * Also reserves device quantities for each item.
     */
    private void updateDrawerStatusOrdered(List<OfferItem> items, Map<Long, Device> deviceMap) {
        for (OfferItem item : items) {
            String drawerStatus = resolveDrawerStatusForOrdered(item);
            item.setOfferDrawerStatus(drawerStatus);
            reserveDeviceQuantity(item, deviceMap.get(item.getDeviceId()));
        }
    }

    /**
     * Resolve the OfferDrawerStatus for an item in the Ordered/Pending_Order branch.
     * Mendix logic from SUB_UpdateOfferDrawerStatus:
     *   Accept → Ordered
     *   Decline → Seller_Declined
     *   Counter + BuyerCounterStatus=Accept → Ordered
     *   Counter + BuyerCounterStatus=Decline → Buyer_Declined
     *   else → null
     */
    private String resolveDrawerStatusForOrdered(OfferItem item) {
        String status = item.getItemStatus();
        if (ITEM_STATUS_ACCEPT.equals(status)) {
            return DRAWER_ORDERED;
        }
        if (ITEM_STATUS_DECLINE.equals(status)) {
            return DRAWER_SELLER_DECLINED;
        }
        if (ITEM_STATUS_COUNTER.equals(status)) {
            if (COUNTER_STATUS_ACCEPT.equals(item.getBuyerCounterStatus())) {
                return DRAWER_ORDERED;
            }
            if (COUNTER_STATUS_DECLINE.equals(item.getBuyerCounterStatus())) {
                return DRAWER_BUYER_DECLINED;
            }
        }
        return null;
    }

    /**
     * Look up a user-friendly error message from the PWSResponseConfig / error_mappings table.
     * Resolve user name (email) from userId for Oracle originSystemUser field.
     * Mendix uses $currentUser/Name which is the user's email/login name.
     */
    private String resolveUserName(Long userId) {
        if (userId == null) return "system";
        try {
            @SuppressWarnings("unchecked")
            List<String> results = em.createNativeQuery(
                    "SELECT u.name FROM identity.users u WHERE u.id = :id")
                    .setParameter("id", userId)
                    .getResultList();
            return results.isEmpty() ? "user-" + userId : results.get(0);
        } catch (Exception e) {
            log.warn("Could not resolve user name for userId={}", userId);
            return "user-" + userId;
        }
    }

    /**
     * Clones SUB_PWSErrorCode_GetMessage: queries by source_system and error_code.
     * Falls back to a generic message if no mapping found.
     */
    private String lookupErrorMessage(String errorCode, String sourceSystem) {
        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = em.createNativeQuery("""
                    SELECT user_error_code, user_error_message, bypass_for_user
                    FROM integration.error_mapping
                    WHERE source_system = :src AND source_error_code = :code
                    """)
                    .setParameter("src", sourceSystem)
                    .setParameter("code", errorCode != null ? errorCode : "")
                    .getResultList();

            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                boolean bypassForUser = row[2] != null && (Boolean) row[2];
                if (!bypassForUser) {
                    return row[0] + " - " + row[1];
                }
            } else {
                log.warn("No error mapping found for source={}, code={}", sourceSystem, errorCode);
            }
        } catch (Exception e) {
            log.warn("Error looking up error mapping for source={}, code={}: {}", sourceSystem, errorCode, e.getMessage());
        }
        return "Order is not placed. Please try Re-Submitting the Order";
    }

    /**
     * Reserve device quantity for a submitted offer item.
     * Clones Mendix SUB_ReserveQuantityForDevice: increments Device.reservedQty
     * and recalculates Device.atpQty = availableQty - reservedQty.
     */
    private void reserveDeviceQuantity(OfferItem item, Device device) {
        if (device == null || item.getQuantity() == null) return;

        int qty = item.getQuantity();
        int currentAvail = device.getAvailableQty() != null ? device.getAvailableQty() : 0;
        int currentReserved = device.getReservedQty() != null ? device.getReservedQty() : 0;

        int newReserved = currentReserved + qty;
        int newAtpQty = Math.max(0, currentAvail - newReserved);

        device.setReservedQty(newReserved);
        device.setAtpQty(newAtpQty);
        deviceRepository.save(device);

        log.debug("Reserved qty {} for device {} (sku={}): reserved {} → {}, atpQty → {}",
                qty, device.getId(), device.getSku(),
                currentReserved, newReserved, newAtpQty);
    }

    private Offer findOrCreateDraft(Long buyerCodeId) {
        return offerRepository.findByBuyerCodeIdAndOfferTypeAndStatus(buyerCodeId, OFFER_TYPE_BUYER, STATUS_DRAFT)
                .orElseGet(() -> {
                    Offer offer = new Offer();
                    offer.setOfferType(OFFER_TYPE_BUYER);
                    offer.setStatus(STATUS_DRAFT);
                    offer.setBuyerCodeId(buyerCodeId);
                    offer.setTotalQty(0);
                    offer.setTotalPrice(BigDecimal.ZERO);
                    return offerRepository.save(offer);
                });
    }

    private void recalcTotals(Offer offer) {
        int totalQty = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OfferItem item : offer.getItems()) {
            if (item.getQuantity() != null && item.getQuantity() > 0 && item.getTotalPrice() != null) {
                totalQty += item.getQuantity();
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }
        offer.setTotalQty(totalQty);
        offer.setTotalPrice(totalPrice);
    }

    private OfferResponse buildResponse(Offer offer) {
        List<OfferItem> items = offer.getItems();
        Map<Long, Device> deviceMap = loadDeviceMap(items);

        List<OfferItemResponse> itemResponses = items.stream()
                .map(item -> OfferItemResponse.fromEntity(item, deviceMap.get(item.getDeviceId())))
                .toList();

        return OfferResponse.fromEntity(offer, itemResponses);
    }

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        return deviceLoader.loadDeviceMap(items);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
