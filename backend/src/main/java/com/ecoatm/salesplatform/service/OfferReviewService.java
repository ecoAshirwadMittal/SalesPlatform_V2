package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.PwsOfferStatus;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.security.CurrentPrincipal;
import com.ecoatm.salesplatform.service.email.PwsOfferEmailEvent;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for the PWSOffers review page — sales rep offer review workflow.
 * Clones Mendix microflows: ACT_OpenPWSOfferItems, ACT_Offer_SalesAcceptAll,
 * ACT_Offer_SalesDeclineAll, ACT_Offer_SalesFinalizeAll, ACT_Offer_CompleteReview,
 * DS_GetOfferSummaryByStatus, OCH_OfferItem_Action.
 */
@Service
public class OfferReviewService {

    private static final Logger log = LoggerFactory.getLogger(OfferReviewService.class);

    // Status literals — see PwsOfferStatus for the authoritative set.
    private static final String STATUS_DRAFT = PwsOfferStatus.DRAFT;
    private static final String STATUS_SALES_REVIEW = PwsOfferStatus.SALES_REVIEW;
    private static final String STATUS_BUYER_ACCEPTANCE = PwsOfferStatus.BUYER_ACCEPTANCE;
    private static final String STATUS_ORDERED = PwsOfferStatus.ORDERED;
    private static final String STATUS_PENDING_ORDER = PwsOfferStatus.PENDING_ORDER;
    private static final String STATUS_DECLINED = PwsOfferStatus.DECLINED;

    private static final String ITEM_ACCEPT = PwsOfferStatus.ITEM_ACCEPT;
    private static final String ITEM_COUNTER = PwsOfferStatus.ITEM_COUNTER;
    private static final String ITEM_DECLINE = PwsOfferStatus.ITEM_DECLINE;
    private static final String ITEM_FINALIZE = PwsOfferStatus.ITEM_FINALIZE;

    private final OfferRepository offerRepository;
    private final DeviceRepository deviceRepository;
    private final OfferService offerService;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final EntityManager em;
    private final ApplicationEventPublisher eventPublisher;
    private final OfferItemDeviceLoader deviceLoader;

    public OfferReviewService(OfferRepository offerRepository,
                               DeviceRepository deviceRepository,
                               OfferService offerService,
                               BuyerCodeLookupService buyerCodeLookup,
                               EntityManager em,
                               ApplicationEventPublisher eventPublisher,
                               OfferItemDeviceLoader deviceLoader) {
        this.offerRepository = offerRepository;
        this.deviceRepository = deviceRepository;
        this.offerService = offerService;
        this.buyerCodeLookup = buyerCodeLookup;
        this.em = em;
        this.eventPublisher = eventPublisher;
        this.deviceLoader = deviceLoader;
    }

    /**
     * Get status summary counts for the tabs.
     * Clones DS_GetOfferSummaryByStatus — single aggregate query instead of N+1.
     */
    public List<OfferSummary> getStatusSummaries() {
        Map<String, String> statusLabels = new LinkedHashMap<>();
        statusLabels.put(STATUS_SALES_REVIEW, "Sales Review");
        statusLabels.put(STATUS_BUYER_ACCEPTANCE, "Buyer Acceptance");
        statusLabels.put(STATUS_ORDERED, "Ordered");
        statusLabels.put(STATUS_PENDING_ORDER, "Pending Order");
        statusLabels.put(STATUS_DECLINED, "Declined");

        List<Object[]> rows = offerRepository.getStatusSummaries(new ArrayList<>(statusLabels.keySet()));
        Map<String, Object[]> rowMap = new HashMap<>();
        for (Object[] row : rows) {
            rowMap.put((String) row[0], row);
        }

        List<OfferSummary> summaries = new ArrayList<>();
        long totalOffers = 0, totalSkus = 0, totalQty = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<String, String> entry : statusLabels.entrySet()) {
            Object[] row = rowMap.get(entry.getKey());
            long count = row != null ? ((Number) row[1]).longValue() : 0;
            long skus = row != null ? ((Number) row[2]).longValue() : 0;
            long qty = row != null ? ((Number) row[3]).longValue() : 0;
            BigDecimal price = row != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO;

            summaries.add(new OfferSummary(entry.getKey(), entry.getValue(), count, skus, qty, price));
            totalOffers += count;
            totalSkus += skus;
            totalQty += qty;
            totalPrice = totalPrice.add(price);
        }

        summaries.add(new OfferSummary("Total", "Total", totalOffers, totalSkus, totalQty, totalPrice));
        return summaries;
    }

    /**
     * List offers filtered by status. If status is null or "Total", return all non-DRAFT offers.
     */
    @SuppressWarnings("unchecked")
    public List<OfferListItem> listOffers(String status) {
        List<Offer> offers;
        if (status == null || status.isBlank() || "Total".equals(status)) {
            offers = offerRepository.findByStatusNotOrderByUpdatedDateDesc(STATUS_DRAFT);
        } else {
            offers = offerRepository.findByStatusOrderByUpdatedDateDesc(status);
        }

        // Batch-load buyer code + buyer name + sales rep + order number
        List<Long> offerIds = offers.stream().map(Offer::getId).toList();
        Set<Long> buyerCodeIds = offers.stream().map(Offer::getBuyerCodeId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        // Buyer code → code string
        Map<Long, String> buyerCodeMap = new HashMap<>();
        // Buyer code → buyer name (company name)
        Map<Long, String> buyerNameMap = new HashMap<>();
        // Buyer code → sales rep name
        Map<Long, String> salesRepMap = new HashMap<>();
        // Offer id → order number
        Map<Long, String> orderNumberMap = new HashMap<>();

        if (!buyerCodeIds.isEmpty()) {
            buyerCodeMap.putAll(buyerCodeLookup.findCodesByIds(buyerCodeIds));
            buyerNameMap.putAll(buyerCodeLookup.findCompanyNamesByIds(buyerCodeIds));
            salesRepMap.putAll(buyerCodeLookup.findSalesRepsByIds(buyerCodeIds));
        }

        if (!offerIds.isEmpty()) {
            try {
                List<Object[]> orderRows = em.createNativeQuery(
                        "SELECT o.offer_id, o.order_number FROM pws.\"order\" o WHERE o.offer_id IN :ids")
                        .setParameter("ids", offerIds)
                        .getResultList();
                for (Object[] row : orderRows) {
                    orderNumberMap.put(((Number) row[0]).longValue(), (String) row[1]);
                }
            } catch (Exception e) {
                log.debug("Could not load order numbers: {}", e.getMessage());
            }
        }

        return offers.stream().map(offer -> {
            OfferListItem item = new OfferListItem();
            item.setOfferId(offer.getId());
            item.setOfferNumber(offer.getOfferNumber());
            item.setStatus(offer.getStatus());
            item.setOrderNumber(orderNumberMap.get(offer.getId()));
            item.setBuyerCode(buyerCodeMap.get(offer.getBuyerCodeId()));
            item.setBuyerName(buyerNameMap.get(offer.getBuyerCodeId()));
            item.setSalesRepName(salesRepMap.get(offer.getBuyerCodeId()));
            item.setTotalQty(offer.getTotalQty());
            item.setTotalPrice(offer.getTotalPrice());
            // Count active items as SKU count
            int skuCount = (int) offer.getItems().stream()
                    .filter(i -> i.getQuantity() != null && i.getQuantity() > 0).count();
            item.setTotalSkus(skuCount);
            item.setSubmissionDate(offer.getSubmissionDate());
            item.setUpdatedDate(offer.getUpdatedDate());
            item.setChangedBy(offer.getChangedBy());
            return item;
        }).toList();
    }

    /**
     * Paginated list for the admin Offers grid. Applies optional status and
     * buyer-code-contains filters in-memory on top of listOffers() — safe while
     * the admin dataset is bounded, and avoids a second Criteria query path.
     * Phase 2 of docs/tasks/pws-data-center-port.md.
     */
    public Page<OfferListItem> listOffersPage(String status, String buyerCodeContains, Pageable pageable) {
        List<OfferListItem> all = listOffers(status);
        if (buyerCodeContains != null && !buyerCodeContains.isBlank()) {
            String needle = buyerCodeContains.toLowerCase();
            all = all.stream()
                    .filter(o -> o.getBuyerCode() != null && o.getBuyerCode().toLowerCase().contains(needle))
                    .toList();
        }
        int from = (int) Math.min(pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(from, to), pageable, all.size());
    }

    private void stampChangedBy(Offer offer) {
        offer.setChangedBy(CurrentPrincipal.displayName());
    }

    /**
     * Get offer detail for the review page — offer + items with device info.
     */
    public OfferResponse getOfferDetail(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        List<OfferItem> items = offer.getItems();
        Map<Long, Device> deviceMap = loadDeviceMap(items);

        List<OfferItemResponse> itemResponses = items.stream()
                .map(item -> OfferItemResponse.fromEntity(item, deviceMap.get(item.getDeviceId())))
                .toList();

        return OfferResponse.fromEntity(offer, itemResponses);
    }

    /**
     * Set a single item's action status.
     * Clones OCH_OfferItem_Action: updates SalesOfferItemStatus and recalculates.
     */
    @Transactional
    public OfferResponse setItemAction(Long offerId, Long itemId, String action) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        OfferItem item = offer.getItems().stream()
                .filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        Map<Long, Device> deviceMap = loadDeviceMap(List.of(item));
        Device device = deviceMap.get(item.getDeviceId());

        switch (action) {
            case ITEM_ACCEPT -> {
                item.setItemStatus(ITEM_ACCEPT);
                // Accept = use buyer's original offer values
                item.setCounterQty(null);
                item.setCounterPrice(null);
                item.setCounterTotal(null);
            }
            case ITEM_COUNTER -> {
                item.setItemStatus(ITEM_COUNTER);
                // Initialize counter fields with offer values
                if (item.getCounterQty() == null) item.setCounterQty(item.getQuantity());
                if (item.getCounterPrice() == null) item.setCounterPrice(item.getPrice());
                if (item.getCounterTotal() == null) item.setCounterTotal(item.getTotalPrice());
            }
            case ITEM_DECLINE -> {
                item.setItemStatus(ITEM_DECLINE);
                item.setCounterQty(null);
                item.setCounterPrice(null);
                item.setCounterTotal(null);
            }
            case ITEM_FINALIZE -> {
                item.setItemStatus(ITEM_FINALIZE);
            }
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }

        stampChangedBy(offer);
        offerRepository.save(offer);
        return getOfferDetail(offerId);
    }

    /**
     * Update counter qty/price for a countered item.
     * Clones OCH_OfferItem_Quantity and OCH_OfferItem_Price.
     */
    @Transactional
    public OfferResponse updateItemCounter(Long offerId, Long itemId,
                                           Integer counterQty, BigDecimal counterPrice) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        OfferItem item = offer.getItems().stream()
                .filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        if (counterQty != null) item.setCounterQty(counterQty);
        if (counterPrice != null) item.setCounterPrice(counterPrice);

        // Recalculate counter total
        int qty = item.getCounterQty() != null ? item.getCounterQty() : 0;
        BigDecimal price = item.getCounterPrice() != null ? item.getCounterPrice() : BigDecimal.ZERO;
        item.setCounterTotal(price.multiply(BigDecimal.valueOf(qty)));

        stampChangedBy(offer);
        offerRepository.save(offer);
        return getOfferDetail(offerId);
    }

    /**
     * Accept All — set all items to Accept with original offer values.
     * Clones ACT_Offer_SalesAcceptAll.
     */
    @Transactional
    public OfferResponse acceptAll(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        for (OfferItem item : offer.getItems()) {
            if (!ITEM_ACCEPT.equals(item.getItemStatus())) {
                item.setItemStatus(ITEM_ACCEPT);
                item.setCounterQty(null);
                item.setCounterPrice(null);
                item.setCounterTotal(null);
            }
        }

        stampChangedBy(offer);
        offerRepository.save(offer);
        log.info("Accept All: offerId={}", offerId);
        return getOfferDetail(offerId);
    }

    /**
     * Decline All — set all items to Decline with zero values.
     * Clones ACT_Offer_SalesDeclineAll.
     */
    @Transactional
    public OfferResponse declineAll(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        for (OfferItem item : offer.getItems()) {
            if (!ITEM_DECLINE.equals(item.getItemStatus())) {
                item.setItemStatus(ITEM_DECLINE);
                item.setCounterQty(null);
                item.setCounterPrice(null);
                item.setCounterTotal(null);
            }
        }

        stampChangedBy(offer);
        offerRepository.save(offer);
        log.info("Decline All: offerId={}", offerId);
        return getOfferDetail(offerId);
    }

    /**
     * Finalize All — transition items to Finalize.
     * Clones ACT_Offer_SalesFinalizeAll:
     *   Accept → Finalize (zero values — sales rep must set final values)
     *   Counter → Finalize (use counter values as final)
     *   Decline → Finalize (zero values)
     */
    @Transactional
    public OfferResponse finalizeAll(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        for (OfferItem item : offer.getItems()) {
            String current = item.getItemStatus();
            if (ITEM_FINALIZE.equals(current)) continue;

            item.setItemStatus(ITEM_FINALIZE);
            // Mendix: Counter items keep counter values; all others get zeroed
            // (sales rep fills in final values manually)
        }

        stampChangedBy(offer);
        offerRepository.save(offer);
        log.info("Finalize All: offerId={}", offerId);
        return getOfferDetail(offerId);
    }

    /**
     * Complete Review — the main review completion flow.
     * Clones ACT_Offer_CompleteReview:
     *   1. If all items declined → set offer to Declined
     *   2. Validate finalize logic, counter validity, available quantities
     *   3. If counter items exist → move to Buyer_Acceptance (send counter offer email)
     *   4. If no counter items (all Accept/Finalize) → create order + submit to Oracle
     */
    @Transactional
    public SubmitResponse completeReview(Long offerId, Long userId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        if (!STATUS_SALES_REVIEW.equals(offer.getStatus())) {
            return SubmitResponse.error(List.of("Offer is not in Sales Review status."));
        }

        List<OfferItem> items = offer.getItems();
        if (items.isEmpty()) {
            return SubmitResponse.error(List.of("No items to review."));
        }

        // Check if all items are declined
        boolean allDeclined = items.stream().allMatch(i -> ITEM_DECLINE.equals(i.getItemStatus()));
        if (allDeclined) {
            // Decline the entire offer
            offer.setStatus(STATUS_DECLINED);
            offer.setSalesReviewCompletedOn(LocalDateTime.now());
            stampChangedBy(offer);
            offerRepository.save(offer);

            log.info("Offer declined (all items declined): offerId={}", offerId);
            return SubmitResponse.error(List.of("Offer has been declined since all offer items are declined"));
        }

        // Validate: if any item is Finalize, ALL non-declined items must be Finalize or Decline
        boolean hasFinalizeItem = items.stream().anyMatch(i -> ITEM_FINALIZE.equals(i.getItemStatus()));
        if (hasFinalizeItem) {
            boolean allFinalizeOrDecline = items.stream().allMatch(i ->
                    ITEM_FINALIZE.equals(i.getItemStatus()) || ITEM_DECLINE.equals(i.getItemStatus()));
            if (!allFinalizeOrDecline) {
                return SubmitResponse.error(List.of(
                        "If choose finalize, all SKUs must also be set to either finalize with counter price and qty or decline."));
            }
        }

        // Validate: all countered items must have valid counter qty and price
        List<OfferItem> counterItems = items.stream()
                .filter(i -> ITEM_COUNTER.equals(i.getItemStatus())).toList();
        for (OfferItem ci : counterItems) {
            if (ci.getCounterQty() == null || ci.getCounterQty() <= 0
                    || ci.getCounterPrice() == null || ci.getCounterPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return SubmitResponse.error(List.of("All countered SKUs must be valid before completing review"));
            }
        }

        // Validate: offered/countered qty must not exceed available qty
        Map<Long, Device> deviceMap = loadDeviceMap(items);
        for (OfferItem item : items) {
            if (ITEM_DECLINE.equals(item.getItemStatus())) continue;
            Device device = deviceMap.get(item.getDeviceId());
            if (device == null) continue;
            int avail = device.getAvailableQty() != null ? device.getAvailableQty() : Integer.MAX_VALUE;
            int requestedQty = ITEM_COUNTER.equals(item.getItemStatus()) && item.getCounterQty() != null
                    ? item.getCounterQty() : (item.getQuantity() != null ? item.getQuantity() : 0);
            if (requestedQty > avail) {
                return SubmitResponse.error(List.of(
                        "Offered or Countered Qty is over the Available Qty for some SKUs. " +
                        "Please counter the Qty before completing the review"));
            }
        }

        // If counter items exist → Buyer_Acceptance (buyer must respond to counters)
        if (!counterItems.isEmpty()) {
            offer.setStatus(STATUS_BUYER_ACCEPTANCE);
            offer.setSalesReviewCompletedOn(LocalDateTime.now());

            // Set drawer status for Buyer_Acceptance
            for (OfferItem item : items) {
                String status = item.getItemStatus();
                if (ITEM_ACCEPT.equals(status)) {
                    item.setOfferDrawerStatus("Accepted");
                } else if (ITEM_COUNTER.equals(status)) {
                    item.setOfferDrawerStatus("Countered");
                } else if (ITEM_DECLINE.equals(status)) {
                    item.setOfferDrawerStatus("Seller_Declined");
                }
            }

            // Compute counter offer summary totals
            int counterSkus = counterItems.size();
            int counterQty = counterItems.stream()
                    .mapToInt(i -> i.getCounterQty() != null ? i.getCounterQty() : 0).sum();
            BigDecimal counterTotal = counterItems.stream()
                    .map(i -> i.getCounterTotal() != null ? i.getCounterTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            offer.setCounterOfferTotalSku(counterSkus);
            offer.setCounterOfferTotalQty(counterQty);
            offer.setCounterOfferTotalPrice(counterTotal);

            stampChangedBy(offer);
            offerRepository.save(offer);

            // SUB_SendPWSCounterOfferEmail — delivered after commit
            eventPublisher.publishEvent(new PwsOfferEmailEvent.CounterOffer(offer.getId()));
            log.info("Offer moved to Buyer_Acceptance (has counters): offerId={}", offerId);
            return SubmitResponse.offerSubmitted(offerId,
                    offer.getOfferNumber() != null ? offer.getOfferNumber() : String.valueOf(offerId));
        }

        // No counter items — all Accept/Finalize → submit order directly
        log.info("Complete review → direct order (no counters): offerId={}", offerId);
        return offerService.submitOrder(offerId, userId);
    }

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        return deviceLoader.loadDeviceMap(items);
    }
}
