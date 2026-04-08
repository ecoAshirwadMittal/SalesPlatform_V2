package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Mendix ENUM — normalized to Mixed_Case convention (matches DB after V30 migration)
    private static final String STATUS_DRAFT = "Draft";
    private static final String STATUS_SALES_REVIEW = "Sales_Review";
    private static final String STATUS_BUYER_ACCEPTANCE = "Buyer_Acceptance";
    private static final String STATUS_ORDERED = "Ordered";
    private static final String STATUS_PENDING_ORDER = "Pending_Order";
    private static final String STATUS_DECLINED = "Declined";

    private static final String ITEM_ACCEPT = "Accept";
    private static final String ITEM_COUNTER = "Counter";
    private static final String ITEM_DECLINE = "Decline";
    private static final String ITEM_FINALIZE = "Finalize";

    private final OfferRepository offerRepository;
    private final DeviceRepository deviceRepository;
    private final OfferService offerService;

    @PersistenceContext
    private EntityManager em;

    public OfferReviewService(OfferRepository offerRepository,
                               DeviceRepository deviceRepository,
                               OfferService offerService) {
        this.offerRepository = offerRepository;
        this.deviceRepository = deviceRepository;
        this.offerService = offerService;
    }

    /**
     * Get status summary counts for the tabs.
     * Clones DS_GetOfferSummaryByStatus for each status.
     */
    public List<OfferSummary> getStatusSummaries() {
        List<OfferSummary> summaries = new ArrayList<>();

        String[] statuses = { STATUS_SALES_REVIEW, STATUS_BUYER_ACCEPTANCE,
                STATUS_ORDERED, STATUS_PENDING_ORDER, STATUS_DECLINED };
        String[] labels = { "Sales Review", "Buyer Acceptance",
                "Ordered", "Pending Order", "Declined" };

        long totalOffers = 0, totalSkus = 0, totalQty = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 0; i < statuses.length; i++) {
            List<Offer> offers = offerRepository.findByStatusOrderByUpdatedDateDesc(statuses[i]);
            long count = offers.size();
            long skus = 0, qty = 0;
            BigDecimal price = BigDecimal.ZERO;
            for (Offer o : offers) {
                int activeItems = (int) o.getItems().stream()
                        .filter(it -> it.getQuantity() != null && it.getQuantity() > 0).count();
                skus += activeItems;
                qty += o.getTotalQty() != null ? o.getTotalQty() : 0;
                price = price.add(o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO);
            }
            summaries.add(new OfferSummary(statuses[i], labels[i], count, skus, qty, price));
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
        List<Long> offerIds = offers.stream().map(Offer::getId).collect(Collectors.toList());
        List<Long> buyerCodeIds = offers.stream().map(Offer::getBuyerCodeId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        // Buyer code → code string
        Map<Long, String> buyerCodeMap = new HashMap<>();
        // Buyer code → buyer name (company name)
        Map<Long, String> buyerNameMap = new HashMap<>();
        // Buyer code → sales rep name
        Map<Long, String> salesRepMap = new HashMap<>();
        // Offer id → order number
        Map<Long, String> orderNumberMap = new HashMap<>();

        if (!buyerCodeIds.isEmpty()) {
            List<Object[]> codeRows = em.createNativeQuery(
                    "SELECT bc.id, bc.code FROM buyer_mgmt.buyer_codes bc WHERE bc.id IN :ids")
                    .setParameter("ids", buyerCodeIds)
                    .getResultList();
            for (Object[] row : codeRows) {
                buyerCodeMap.put(((Number) row[0]).longValue(), (String) row[1]);
            }

            // Buyer name: buyer_codes → buyer_code_buyers → buyers.company_name
            try {
                List<Object[]> buyerRows = em.createNativeQuery("""
                    SELECT bcb.buyer_code_id, b.company_name
                    FROM buyer_mgmt.buyer_code_buyers bcb
                    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                    WHERE bcb.buyer_code_id IN :ids
                    """).setParameter("ids", buyerCodeIds).getResultList();
                for (Object[] row : buyerRows) {
                    buyerNameMap.put(((Number) row[0]).longValue(), (String) row[1]);
                }
            } catch (Exception e) {
                log.debug("Could not load buyer names: {}", e.getMessage());
            }

            // Sales rep
            try {
                List<Object[]> repRows = em.createNativeQuery("""
                    SELECT bsr.buyer_code_id, sr.first_name, sr.last_name
                    FROM buyer_mgmt.buyer_sales_reps bsr
                    JOIN buyer_mgmt.sales_reps sr ON sr.id = bsr.sales_rep_id
                    WHERE bsr.buyer_code_id IN :ids
                    """).setParameter("ids", buyerCodeIds).getResultList();
                for (Object[] row : repRows) {
                    String name = (row[1] != null ? row[1] : "") + " " + (row[2] != null ? row[2] : "");
                    salesRepMap.put(((Number) row[0]).longValue(), name.trim());
                }
            } catch (Exception e) {
                log.debug("Could not load sales reps: {}", e.getMessage());
            }
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
            return item;
        }).collect(Collectors.toList());
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
                .collect(Collectors.toList());

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
                .filter(i -> ITEM_COUNTER.equals(i.getItemStatus())).collect(Collectors.toList());
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

            offerRepository.save(offer);

            // TODO: SUB_SendPWSCounterOfferEmail
            log.info("Offer moved to Buyer_Acceptance (has counters): offerId={}", offerId);
            return SubmitResponse.offerSubmitted(offerId,
                    offer.getOfferNumber() != null ? offer.getOfferNumber() : String.valueOf(offerId));
        }

        // No counter items — all Accept/Finalize → submit order directly
        log.info("Complete review → direct order (no counters): offerId={}", offerId);
        return offerService.submitOrder(offerId, userId);
    }

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        Set<Long> deviceIds = items.stream()
                .map(OfferItem::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) return Map.of();

        return deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }
}
