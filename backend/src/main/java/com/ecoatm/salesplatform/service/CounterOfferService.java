package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Buyer-side counter-offer workflow.
 * Clones Mendix microflows: NAV_PWSCounterOffers, ACT_Offer_BuyerAcceptAllCounters,
 * ACT_Offer_BuyerSubmitCounterResponse, OCh_OfferItem_RecalculateFinalOffer.
 */
@Service
public class CounterOfferService {

    private static final Logger log = LoggerFactory.getLogger(CounterOfferService.class);

    private static final String STATUS_BUYER_ACCEPTANCE = "Buyer_Acceptance";
    private static final String STATUS_ORDERED = "Ordered";
    private static final String STATUS_DECLINED = "Declined";
    private static final String STATUS_CANCELED = "Canceled";

    private static final String ITEM_ACCEPT = "Accept";
    private static final String ITEM_COUNTER = "Counter";
    private static final String ITEM_DECLINE = "Decline";

    private static final String COUNTER_ACCEPT = "Accept";
    private static final String COUNTER_DECLINE = "Decline";

    private static final String DRAWER_ORDERED = "Ordered";
    private static final String DRAWER_BUYER_DECLINED = "Buyer_Declined";

    private final OfferRepository offerRepository;
    private final DeviceRepository deviceRepository;
    private final CaseLotRepository caseLotRepository;
    private final OfferService offerService;
    private final BuyerCodeLookupService buyerCodeLookup;

    public CounterOfferService(OfferRepository offerRepository,
                               DeviceRepository deviceRepository,
                               CaseLotRepository caseLotRepository,
                               OfferService offerService,
                               BuyerCodeLookupService buyerCodeLookup) {
        this.offerRepository = offerRepository;
        this.deviceRepository = deviceRepository;
        this.caseLotRepository = caseLotRepository;
        this.offerService = offerService;
        this.buyerCodeLookup = buyerCodeLookup;
    }

    /**
     * List counter offers for a buyer code (Buyer_Acceptance status).
     * Clones NAV_PWSCounterOffers navigation logic.
     */
    @SuppressWarnings("unchecked")
    public List<OfferListItem> listCounterOffers(Long buyerCodeId) {
        List<Offer> offers = offerRepository.findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc(
                STATUS_BUYER_ACCEPTANCE, buyerCodeId);

        // Load buyer code string
        Map<Long, String> buyerCodeMap = new HashMap<>();
        if (!offers.isEmpty()) {
            buyerCodeMap.putAll(buyerCodeLookup.findCodesByIds(Set.of(buyerCodeId)));
        }

        return offers.stream().map(offer -> {
            OfferListItem item = new OfferListItem();
            item.setOfferId(offer.getId());
            item.setOfferNumber(offer.getOfferNumber());
            item.setStatus(offer.getStatus());
            item.setBuyerCode(buyerCodeMap.get(offer.getBuyerCodeId()));
            item.setTotalQty(offer.getTotalQty());
            item.setTotalPrice(offer.getTotalPrice());
            int skuCount = (int) offer.getItems().stream()
                    .filter(i -> i.getQuantity() != null && i.getQuantity() > 0).count();
            item.setTotalSkus(skuCount);
            item.setSubmissionDate(offer.getSubmissionDate());
            item.setUpdatedDate(offer.getUpdatedDate());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * Get counter offer detail with items, device info, and summary card values.
     * Clones DS_GetOriginalOfferForCounterOffers, DS_GetEcoATMCounterOffers, DS_GetFinalOfferForCounterOffers.
     */
    public OfferResponse getCounterOfferDetail(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        List<OfferItem> items = offer.getItems();
        Map<Long, Device> deviceMap = loadDeviceMap(items);
        Map<Long, CaseLot> caseLotMap = loadCaseLotMap(items);

        List<OfferItemResponse> itemResponses = items.stream()
                .map(item -> OfferItemResponse.fromEntityFull(
                        item,
                        deviceMap.get(item.getDeviceId()),
                        item.getCaseLotId() != null ? caseLotMap.get(item.getCaseLotId()) : null))
                .collect(Collectors.toList());

        return OfferResponse.fromEntity(offer, itemResponses);
    }

    /**
     * Set buyer's Accept/Decline on a single countered item and recalculate finals.
     * Clones OCh_OfferItem_RecalculateFinalOffer.
     */
    @Transactional
    public OfferResponse setBuyerItemAction(Long offerId, Long itemId, String buyerCounterStatus) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        if (!STATUS_BUYER_ACCEPTANCE.equals(offer.getStatus())) {
            throw new RuntimeException("Offer is not in Buyer_Acceptance status");
        }

        OfferItem item = offer.getItems().stream()
                .filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        item.setBuyerCounterStatus(buyerCounterStatus);

        if (COUNTER_ACCEPT.equals(buyerCounterStatus)) {
            applyAcceptToItem(item);
        } else if (COUNTER_DECLINE.equals(buyerCounterStatus)) {
            item.setFinalOfferPrice(BigDecimal.ZERO);
            item.setFinalOfferQuantity(0);
            item.setFinalOfferTotalPrice(BigDecimal.ZERO);
            item.setCounterCasePriceTotal(BigDecimal.ZERO);
        }

        recalculateOfferFinals(offer);
        offerRepository.save(offer);
        return getCounterOfferDetail(offerId);
    }

    /**
     * Accept all Counter + Accept items.
     * Clones ACT_Offer_BuyerAcceptAllCounters.
     */
    @Transactional
    public OfferResponse acceptAllCounters(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        if (!STATUS_BUYER_ACCEPTANCE.equals(offer.getStatus())) {
            throw new RuntimeException("Offer is not in Buyer_Acceptance status");
        }

        for (OfferItem item : offer.getItems()) {
            String status = item.getItemStatus();
            if (ITEM_ACCEPT.equals(status) || ITEM_COUNTER.equals(status)) {
                item.setBuyerCounterStatus(COUNTER_ACCEPT);
                applyAcceptToItem(item);
            }
        }

        recalculateOfferFinals(offer);
        offerRepository.save(offer);
        log.info("Accept All Counters: offerId={}", offerId);
        return getCounterOfferDetail(offerId);
    }

    /**
     * Submit buyer's counter response.
     * Clones ACT_Offer_BuyerSubmitCounterResponse.
     */
    @Transactional
    public SubmitResponse submitCounterResponse(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        if (!STATUS_BUYER_ACCEPTANCE.equals(offer.getStatus())) {
            return SubmitResponse.error(List.of("Offer is not in Buyer_Acceptance status."));
        }

        // Validate: all Counter items must have buyerCounterStatus
        List<OfferItem> counterItems = offer.getItems().stream()
                .filter(i -> ITEM_COUNTER.equals(i.getItemStatus()))
                .collect(Collectors.toList());

        boolean allResponded = counterItems.stream()
                .allMatch(i -> i.getBuyerCounterStatus() != null && !i.getBuyerCounterStatus().isEmpty());
        if (!allResponded) {
            return SubmitResponse.error(List.of(
                    "All countered SKUs must be either Accepted or Rejected before submitting"));
        }

        offer.setCounterResponseSubmittedOn(LocalDateTime.now());

        // Check if any items will be ordered
        boolean hasAcceptedItems = offer.getItems().stream().anyMatch(i ->
                (ITEM_ACCEPT.equals(i.getItemStatus())) ||
                (ITEM_COUNTER.equals(i.getItemStatus()) && COUNTER_ACCEPT.equals(i.getBuyerCounterStatus())));

        if (hasAcceptedItems) {
            // Update drawer statuses before submitting order
            for (OfferItem item : offer.getItems()) {
                if (ITEM_ACCEPT.equals(item.getItemStatus())) {
                    item.setOfferDrawerStatus(DRAWER_ORDERED);
                } else if (ITEM_COUNTER.equals(item.getItemStatus())) {
                    if (COUNTER_ACCEPT.equals(item.getBuyerCounterStatus())) {
                        item.setOfferDrawerStatus(DRAWER_ORDERED);
                    } else {
                        item.setOfferDrawerStatus(DRAWER_BUYER_DECLINED);
                    }
                } else if (ITEM_DECLINE.equals(item.getItemStatus())) {
                    // Already Seller_Declined from review phase
                }
            }

            offer.setFinalOfferSubmittedOn(LocalDateTime.now());
            offerRepository.save(offer);

            log.info("Counter response submitted → ordering: offerId={}", offerId);
            return offerService.submitOrder(offerId, null);
        } else {
            // All counter items declined → decline the offer
            offer.setStatus(STATUS_DECLINED);
            for (OfferItem item : offer.getItems()) {
                if (ITEM_COUNTER.equals(item.getItemStatus())) {
                    item.setOfferDrawerStatus(DRAWER_BUYER_DECLINED);
                }
            }
            offerRepository.save(offer);

            log.info("Counter response submitted → all declined: offerId={}", offerId);
            return SubmitResponse.error(List.of("Offer has been declined since all counter items were rejected."));
        }
    }

    /**
     * Cancel the offer.
     * Clones ACT_Offer_BuyerCancelOffer.
     */
    @Transactional
    public SubmitResponse cancelOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        offer.setStatus(STATUS_CANCELED);
        offer.setCanceledOn(LocalDateTime.now());
        offerRepository.save(offer);

        log.info("Counter offer canceled: offerId={}", offerId);
        return SubmitResponse.offerSubmitted(offerId,
                offer.getOfferNumber() != null ? offer.getOfferNumber() : String.valueOf(offerId));
    }

    // --- Private helpers ---

    private void applyAcceptToItem(OfferItem item) {
        boolean isSPB = item.getCaseLotId() != null;

        if (ITEM_COUNTER.equals(item.getItemStatus())) {
            // Counter item accepted by buyer → use counter values as final
            BigDecimal counterPrice = item.getCounterPrice() != null ? item.getCounterPrice() : BigDecimal.ZERO;
            int counterQty = item.getCounterQty() != null ? item.getCounterQty() : 0;

            item.setFinalOfferPrice(counterPrice);

            if (isSPB) {
                CaseLot caseLot = caseLotRepository.findById(item.getCaseLotId()).orElse(null);
                int caseLotSize = caseLot != null ? caseLot.getCaseLotSize() : 1;
                item.setFinalOfferQuantity(counterQty * caseLotSize);
                BigDecimal caseTotal = counterPrice.multiply(BigDecimal.valueOf(counterQty))
                        .multiply(BigDecimal.valueOf(caseLotSize));
                item.setCounterTotal(caseTotal);
                item.setCounterCasePriceTotal(caseTotal);
                item.setFinalOfferTotalPrice(caseTotal);
            } else {
                item.setFinalOfferQuantity(counterQty);
                BigDecimal total = counterPrice.multiply(BigDecimal.valueOf(counterQty));
                item.setCounterTotal(total);
                item.setFinalOfferTotalPrice(total);
            }
        } else if (ITEM_ACCEPT.equals(item.getItemStatus())) {
            // Accept item (sales accepted original values) → use original offer values
            BigDecimal offerPrice = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            int offerQty = item.getQuantity() != null ? item.getQuantity() : 0;

            item.setFinalOfferPrice(offerPrice);

            if (isSPB) {
                CaseLot caseLot = caseLotRepository.findById(item.getCaseLotId()).orElse(null);
                int caseLotSize = caseLot != null ? caseLot.getCaseLotSize() : 1;
                item.setFinalOfferQuantity(offerQty * caseLotSize);
                BigDecimal total = offerPrice.multiply(BigDecimal.valueOf(offerQty))
                        .multiply(BigDecimal.valueOf(caseLotSize));
                item.setFinalOfferTotalPrice(total);
            } else {
                item.setFinalOfferQuantity(offerQty);
                BigDecimal total = offerPrice.multiply(BigDecimal.valueOf(offerQty));
                item.setFinalOfferTotalPrice(total);
            }
        }
    }

    /**
     * Recalculate offer-level final totals from accepted items.
     * Final = items where (itemStatus=Accept) OR (itemStatus=Counter AND buyerCounterStatus=Accept)
     */
    private void recalculateOfferFinals(Offer offer) {
        List<OfferItem> acceptedItems = offer.getItems().stream()
                .filter(i -> (ITEM_ACCEPT.equals(i.getItemStatus())) ||
                        (ITEM_COUNTER.equals(i.getItemStatus()) && COUNTER_ACCEPT.equals(i.getBuyerCounterStatus())))
                .collect(Collectors.toList());

        int totalSku = acceptedItems.size();
        int totalQty = acceptedItems.stream()
                .mapToInt(i -> i.getFinalOfferQuantity() != null ? i.getFinalOfferQuantity() : 0).sum();
        BigDecimal totalPrice = acceptedItems.stream()
                .map(i -> i.getFinalOfferTotalPrice() != null ? i.getFinalOfferTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        offer.setFinalOfferTotalSku(totalSku);
        offer.setFinalOfferTotalQty(totalQty);
        offer.setFinalOfferTotalPrice(totalPrice);
    }

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        Set<Long> deviceIds = items.stream()
                .map(OfferItem::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (deviceIds.isEmpty()) return Map.of();
        return deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }

    private Map<Long, CaseLot> loadCaseLotMap(List<OfferItem> items) {
        Set<Long> caseLotIds = items.stream()
                .map(OfferItem::getCaseLotId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (caseLotIds.isEmpty()) return Map.of();
        return caseLotRepository.findAllById(caseLotIds).stream()
                .collect(Collectors.toMap(CaseLot::getId, c -> c));
    }
}
