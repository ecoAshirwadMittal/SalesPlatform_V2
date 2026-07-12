package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.mdm.Device;

import java.math.BigDecimal;
import java.time.Instant;
import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.Order;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RmaService {

    private static final Logger log = LoggerFactory.getLogger(RmaService.class);

    private static final String STATUS_APPROVE = "Approve";
    private static final String STATUS_DECLINE = "Decline";
    private static final String STATUS_SUBMITTED = "Submitted";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_DECLINED = "Declined";

    private final RmaRepository rmaRepository;
    private final RmaItemRepository rmaItemRepository;
    private final RmaStatusRepository rmaStatusRepository;
    private final RmaReasonRepository rmaReasonRepository;
    private final DeviceRepository deviceRepository;
    private final ImeiDetailRepository imeiDetailRepository;
    private final OrderRepository orderRepository;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final ApplicationEventPublisher eventPublisher;

    public RmaService(RmaRepository rmaRepository,
                      RmaItemRepository rmaItemRepository,
                      RmaStatusRepository rmaStatusRepository,
                      RmaReasonRepository rmaReasonRepository,
                      DeviceRepository deviceRepository,
                      ImeiDetailRepository imeiDetailRepository,
                      OrderRepository orderRepository,
                      BuyerCodeLookupService buyerCodeLookup,
                      ApplicationEventPublisher eventPublisher) {
        this.rmaRepository = rmaRepository;
        this.rmaItemRepository = rmaItemRepository;
        this.rmaStatusRepository = rmaStatusRepository;
        this.rmaReasonRepository = rmaReasonRepository;
        this.deviceRepository = deviceRepository;
        this.imeiDetailRepository = imeiDetailRepository;
        this.orderRepository = orderRepository;
        this.buyerCodeLookup = buyerCodeLookup;
        this.eventPublisher = eventPublisher;
    }

    /** List RMAs for a buyer code, optionally filtered by status group. */
    @Transactional(readOnly = true)
    public List<RmaResponse> getRmasByBuyerCode(Long buyerCodeId, String statusFilter) {
        List<Rma> rmas;
        if (statusFilter != null && !statusFilter.isEmpty() && !"Total".equalsIgnoreCase(statusFilter)) {
            rmas = rmaRepository.findByBuyerCodeIdAndStatusGroupedTo(buyerCodeId, statusFilter);
        } else {
            rmas = rmaRepository.findByBuyerCodeIdOrderByCreatedDateDesc(buyerCodeId);
        }
        List<RmaResponse> responses = rmas.stream().map(RmaResponse::fromEntity).toList();
        enrichWithBuyerInfo(responses);
        return responses;
    }

    /** Get all RMAs (for Sales view — no buyer code filter). */
    @Transactional(readOnly = true)
    public List<RmaResponse> getAllRmas(String statusFilter) {
        List<Rma> rmas;
        if (statusFilter != null && !statusFilter.isEmpty() && !"Total".equalsIgnoreCase(statusFilter)) {
            rmas = rmaRepository.findByStatusGroupedTo(statusFilter);
        } else {
            rmas = rmaRepository.findAllOrderByCreatedDateDesc();
        }
        List<RmaResponse> responses = rmas.stream().map(RmaResponse::fromEntity).toList();
        enrichWithBuyerInfo(responses);
        return responses;
    }

    /** Get summary cards grouped by status. */
    @Transactional(readOnly = true)
    public List<RmaSummaryResponse> getSummary(Long buyerCodeId) {
        List<Object[]> rows;
        if (buyerCodeId != null) {
            rows = rmaRepository.getSummaryGroupedByBuyerCode(buyerCodeId);
        } else {
            rows = rmaRepository.getSummaryGroupedAll();
        }

        long totalCount = 0, totalPrice = 0, totalSkus = 0, totalQty = 0;
        List<RmaSummaryResponse> summaries = new ArrayList<>();

        for (Object[] row : rows) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();
            long price = ((Number) row[2]).longValue();
            long skus = ((Number) row[3]).longValue();
            long qty = ((Number) row[4]).longValue();

            summaries.add(new RmaSummaryResponse(status, formatDisplayLabel(status), count, price, skus, qty));
            totalCount += count;
            totalPrice += price;
            totalSkus += skus;
            totalQty += qty;
        }

        // Add "Total" card
        summaries.add(0, new RmaSummaryResponse("Total", "Total", totalCount, totalPrice, totalSkus, totalQty));
        return summaries;
    }

    /** Get all-RMAs summary (Sales view — no buyer code filter). */
    @Transactional(readOnly = true)
    public List<RmaSummaryResponse> getAllSummary() {
        List<Object[]> rows = rmaRepository.getSummaryGroupedAll();

        long totalCount = 0, totalPrice = 0, totalSkus = 0, totalQty = 0;
        List<RmaSummaryResponse> summaries = new ArrayList<>();

        for (Object[] row : rows) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();
            long price = ((Number) row[2]).longValue();
            long skus = ((Number) row[3]).longValue();
            long qty = ((Number) row[4]).longValue();

            summaries.add(new RmaSummaryResponse(status, formatDisplayLabel(status), count, price, skus, qty));
            totalCount += count;
            totalPrice += price;
            totalSkus += skus;
            totalQty += qty;
        }

        summaries.add(0, new RmaSummaryResponse("Total", "Total", totalCount, totalPrice, totalSkus, totalQty));
        return summaries;
    }

    /**
     * Returns the buyer code id that owns the given RMA, or {@code null} if the
     * RMA does not exist. Used by the controller to enforce ownership on
     * rmaId-based reads (security review 2026-07-10, CR-3).
     */
    @Transactional(readOnly = true)
    public Long getRmaBuyerCodeId(Long rmaId) {
        return rmaRepository.findById(rmaId).map(Rma::getBuyerCodeId).orElse(null);
    }

    /** Get RMA detail with all items, enriched with device info. */
    @Transactional(readOnly = true)
    public RmaDetailResponse getRmaDetail(Long rmaId) {
        Rma rma = rmaRepository.findById(rmaId)
                .orElseThrow(() -> new RuntimeException("RMA not found: " + rmaId));

        RmaResponse rmaResponse = RmaResponse.fromEntity(rma);
        enrichWithBuyerInfo(List.of(rmaResponse));
        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId);
        List<RmaItemResponse> itemResponses = items.stream()
                .map(this::enrichItemWithDevice)
                .toList();

        return new RmaDetailResponse(rmaResponse, itemResponses);
    }

    /** Approve all items in an RMA. */
    @Transactional
    public RmaDetailResponse approveAllItems(Long rmaId) {
        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId);
        for (RmaItem item : items) {
            item.setStatus(STATUS_APPROVE);
            item.setStatusDisplay("Approved");
        }
        rmaItemRepository.saveAll(items);
        recalculateApprovedValues(rmaId);
        return getRmaDetail(rmaId);
    }

    /** Decline all items in an RMA. */
    @Transactional
    public RmaDetailResponse declineAllItems(Long rmaId) {
        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId);
        for (RmaItem item : items) {
            item.setStatus(STATUS_DECLINE);
            item.setStatusDisplay("Declined");
        }
        rmaItemRepository.saveAll(items);
        recalculateApprovedValues(rmaId);
        return getRmaDetail(rmaId);
    }

    /** Update a single item's status (Approve/Decline). */
    @Transactional
    public RmaDetailResponse updateItemStatus(Long rmaItemId, String status) {
        if (!STATUS_APPROVE.equals(status) && !STATUS_DECLINE.equals(status)) {
            throw new IllegalArgumentException("Invalid status: must be 'Approve' or 'Decline'");
        }
        RmaItem item = rmaItemRepository.findById(rmaItemId)
                .orElseThrow(() -> new RuntimeException("RMA Item not found: " + rmaItemId));
        item.setStatus(status);
        item.setStatusDisplay(STATUS_APPROVE.equals(status) ? "Approved" : "Declined");
        rmaItemRepository.save(item);

        Long rmaId = item.getRma().getId();
        recalculateApprovedValues(rmaId);
        return getRmaDetail(rmaId);
    }

    /** Complete review — finalize the RMA, update status to Approved or Declined. */
    @Transactional
    public RmaDetailResponse completeReview(Long rmaId, Long reviewedByUserId) {
        Rma rma = rmaRepository.findById(rmaId)
                .orElseThrow(() -> new RuntimeException("RMA not found: " + rmaId));

        recalculateApprovedValues(rmaId);
        // Reload after recalc
        rma = rmaRepository.findById(rmaId).orElseThrow();

        rma.setReviewedByUserId(reviewedByUserId);
        rma.setReviewCompletedOn(LocalDateTime.now());

        int approved = rma.getApprovedCount() != null ? rma.getApprovedCount() : 0;
        int declined = rma.getDeclinedCount() != null ? rma.getDeclinedCount() : 0;

        String newStatus;
        if (approved > 0 && declined == 0) {
            newStatus = STATUS_APPROVED;
            rma.setApprovalDate(LocalDateTime.now());
        } else if (approved == 0 && declined > 0) {
            newStatus = STATUS_DECLINED;
        } else {
            // Mixed — treat as Approved (partial)
            newStatus = STATUS_APPROVED;
            rma.setApprovalDate(LocalDateTime.now());
        }

        rmaStatusRepository.findBySystemStatus(newStatus).ifPresent(rma::setRmaStatus);
        rma.setSystemStatus(newStatus);
        rmaRepository.save(rma);

        log.info("RMA {} review completed — status: {}, approved: {}, declined: {}",
                rma.getNumber(), newStatus, approved, declined);

        // Publish inside the completing transaction so every
        // @TransactionalEventListener(AFTER_COMMIT) subscriber (Oracle create
        // here; approval email + Snowflake sync in follow-on tasks) fires only
        // once this review has durably committed. STATUS_DECLINED maps to
        // DECLINED; STATUS_APPROVED and the mixed/partial case both map to
        // APPROVED (mirrors ACT_RMADetails_CompleteReview: partial approve is
        // still an Oracle-bound approval).
        RmaReviewOutcome outcome = STATUS_DECLINED.equals(newStatus)
                ? RmaReviewOutcome.DECLINED
                : RmaReviewOutcome.APPROVED;
        eventPublisher.publishEvent(new RmaReviewCompletedEvent(
                rmaId, outcome, reviewedByUserId, Instant.now()));

        return getRmaDetail(rmaId);
    }

    /** Get available return reasons. */
    @Transactional(readOnly = true)
    public List<RmaReasonResponse> getReturnReasons() {
        return rmaReasonRepository.findByIsActiveTrueOrderByValidReasonsAsc()
                .stream()
                .map(RmaReasonResponse::fromEntity)
                .toList();
    }

    /**
     * Submit an RMA request from a CSV file upload.
     * Validates return reasons, checks for duplicate IMEIs in pending RMAs,
     * generates RMA number, creates RMA + RmaItems.
     *
     * CSV format: IMEI/Serial, Return Reason (header row expected)
     */
    @Transactional
    public RmaSubmitResponse submitRmaRequest(Long buyerCodeId, Long userId, InputStream csvStream) {
        List<String[]> rows = parseCsv(csvStream);
        if (rows.isEmpty()) {
            return RmaSubmitResponse.failure(List.of("File is empty or contains only headers."));
        }

        // Load valid reasons
        Set<String> validReasons = rmaReasonRepository.findByIsActiveTrueOrderByValidReasonsAsc()
                .stream()
                .map(RmaReason::getValidReasons)
                .collect(Collectors.toSet());

        // Collect all IMEIs for batch duplicate check
        List<String> allImeis = rows.stream()
                .map(r -> r[0].trim())
                .filter(s -> !s.isEmpty())
                .toList();

        Set<String> duplicateImeis = new HashSet<>();
        if (!allImeis.isEmpty()) {
            duplicateImeis.addAll(rmaItemRepository.findDuplicateImeis(allImeis, buyerCodeId));
        }

        // Check for duplicates within the uploaded file itself
        Set<String> seenInFile = new HashSet<>();
        Set<String> fileInternalDuplicates = new HashSet<>();
        for (String imei : allImeis) {
            if (!seenInFile.add(imei)) {
                fileInternalDuplicates.add(imei);
            }
        }

        // VAL_RMARequestFile: each uploaded IMEI/serial must match a shipped OfferItem
        // owned by this buyer code. Build a lookup keyed by BOTH imei_number and
        // serial_number so a row can match on either (the upload column is "IMEI/Serial").
        Map<String, ImeiDetail> matchByValue = new HashMap<>();
        if (!allImeis.isEmpty()) {
            for (ImeiDetail detail : imeiDetailRepository.findMatchesForBuyer(allImeis, buyerCodeId)) {
                if (detail.getImeiNumber() != null) {
                    matchByValue.putIfAbsent(detail.getImeiNumber(), detail);
                }
                if (detail.getSerialNumber() != null) {
                    matchByValue.putIfAbsent(detail.getSerialNumber(), detail);
                }
            }
        }

        // Validate each row
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            int rowNum = i + 2; // +2 because row 1 is header, data starts at 2
            String imei = row[0].trim();
            String reason = row.length > 1 ? row[1].trim() : "";

            if (imei.isEmpty()) {
                errors.add("Row " + rowNum + ": IMEI/Serial is required.");
                continue;
            }
            if (reason.isEmpty()) {
                errors.add("Row " + rowNum + ": Return Reason is required for IMEI " + imei + ".");
                continue;
            }
            if (!validReasons.contains(reason)) {
                errors.add("Row " + rowNum + ": Invalid return reason '" + reason + "' for IMEI " + imei + ".");
            }
            if (duplicateImeis.contains(imei)) {
                errors.add("Row " + rowNum + ": IMEI " + imei + " already exists in a pending RMA.");
            }
            if (!matchByValue.containsKey(imei)) {
                errors.add("Row " + rowNum + ": IMEI " + imei
                        + " does not match a shipped device for this buyer code.");
            }
            if (fileInternalDuplicates.contains(imei) && seenInFile.contains(imei)) {
                // Only report once
                if (i == allImeis.indexOf(imei)) {
                    // skip first occurrence, report on second
                } else if (allImeis.indexOf(imei) != i) {
                    errors.add("Row " + rowNum + ": Duplicate IMEI " + imei + " in uploaded file.");
                }
            }
        }

        if (!errors.isEmpty()) {
            return RmaSubmitResponse.failure(errors);
        }

        // Generate RMA number: RMA{BuyerCode}{YY}{Seq}
        String buyerCode = lookupBuyerCode(buyerCodeId);
        String rmaNumber = generateRmaNumber(buyerCodeId, buyerCode);

        // Find "Submitted" status
        RmaStatus submittedStatus = rmaStatusRepository.findBySystemStatus(STATUS_SUBMITTED)
                .orElseThrow(() -> new RuntimeException("RMA status 'Submitted' not found in database"));

        // Create RMA
        Rma rma = new Rma();
        rma.setBuyerCodeId(buyerCodeId);
        rma.setSubmittedByUserId(userId);
        rma.setNumber(rmaNumber);
        rma.setRmaStatus(submittedStatus);
        rma.setSystemStatus(STATUS_SUBMITTED);
        rma.setSubmittedDate(LocalDateTime.now());
        rma.setAllRmaItemsValid(true);
        rma = rmaRepository.save(rma);

        // Create RMA items, copying device / order / sale price from the matched OfferItem
        // (VAL_RMARequestFile: RMAItem_Device, RMAItem_Order, ShipDate, OrderNumber,
        // SalePrice = OfferItem/FinalOfferPrice).
        Set<Long> uniqueDeviceIds = new HashSet<>();
        BigDecimal salesTotal = BigDecimal.ZERO;

        for (String[] row : rows) {
            String imei = row[0].trim();
            String reason = row[1].trim();
            OfferItem offerItem = matchByValue.get(imei).getOfferItem();

            RmaItem item = new RmaItem();
            item.setRma(rma);
            item.setImei(imei);
            item.setReturnReason(reason);
            item.setDeviceId(offerItem.getDeviceId());
            BigDecimal salePrice = offerItem.getFinalOfferPrice();
            item.setSalePrice(salePrice);
            resolveOrder(offerItem).ifPresent(order -> {
                item.setOrderId(order.getId());
                item.setOrderNumber(order.getOrderNumber());
                item.setShipDate(order.getShipDate());
            });
            rmaItemRepository.save(item);

            if (offerItem.getDeviceId() != null) {
                uniqueDeviceIds.add(offerItem.getDeviceId());
            }
            if (salePrice != null) {
                salesTotal = salesTotal.add(salePrice);
            }
        }

        // Roll-ups: requestSkus = distinct device count, requestQty = total items,
        // requestSalesTotal = sum of matched OfferItem sale prices.
        rma.setRequestQty(rows.size());
        rma.setRequestSkus(uniqueDeviceIds.size());
        rma.setRequestSalesTotal(salesTotal);
        rmaRepository.save(rma);

        log.info("RMA {} created for buyerCodeId={} with {} items by userId={}",
                rmaNumber, buyerCodeId, rows.size(), userId);

        return RmaSubmitResponse.success(rma.getId(), rmaNumber, rows.size());
    }

    // -- Private helpers --

    /** Enrich RmaResponse list with buyer code and company name from buyer_mgmt tables. */
    private void enrichWithBuyerInfo(List<RmaResponse> responses) {
        Set<Long> buyerCodeIds = responses.stream()
                .map(RmaResponse::getBuyerCodeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (buyerCodeIds.isEmpty()) return;

        Map<Long, BuyerCodeLookupService.BuyerCodeInfo> infoMap =
                buyerCodeLookup.findCodeAndCompanyByIds(buyerCodeIds);

        for (RmaResponse r : responses) {
            BuyerCodeLookupService.BuyerCodeInfo info = infoMap.get(r.getBuyerCodeId());
            if (info != null) {
                r.setBuyerName(info.code());
                r.setCompanyName(info.companyName());
            }
        }
    }

    /** Look up buyer code string from buyer_codes table. */
    private String lookupBuyerCode(Long buyerCodeId) {
        String code = buyerCodeLookup.findCodeById(buyerCodeId);
        return code != null ? code : "UNKNOWN";
    }

    /**
     * Generate RMA number: RMA{BuyerCode}{YY}{Seq}
     * e.g., RMABC00126001 for buyer code BC001, year 26, sequence 001
     */
    private String generateRmaNumber(Long buyerCodeId, String buyerCode) {
        long existingCount = rmaRepository.countByBuyerCode(buyerCodeId);
        String yearSuffix = String.valueOf(Year.now().getValue()).substring(2);
        String seq = String.format("%03d", existingCount + 1);
        return "RMA" + buyerCode + yearSuffix + seq;
    }

    /**
     * Best-effort resolve of the Order behind a matched OfferItem so the RMA line can
     * carry order number + ship date (legacy read {@code OfferItem_Order}). The modern
     * link is {@code offer_item.offer_id → offer ← order.offer_id}; an offer normally maps
     * to a single order, so the oldest is taken. Returns empty when the OfferItem has no
     * offer or the offer has no order — order fields then stay null (as before this change).
     */
    private Optional<Order> resolveOrder(OfferItem offerItem) {
        if (offerItem.getOffer() == null || offerItem.getOffer().getId() == null) {
            return Optional.empty();
        }
        return orderRepository.findByOfferId(offerItem.getOffer().getId())
                .stream()
                .findFirst();
    }

    /** Parse CSV input stream. Skips header row. Returns list of [imei, reason] arrays. */
    private List<String[]> parseCsv(InputStream inputStream) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 1) {
                    rows.add(parts);
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse CSV file", e);
        }
        return rows;
    }

    private void recalculateApprovedValues(Long rmaId) {
        Rma rma = rmaRepository.findById(rmaId).orElseThrow();
        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId);

        int approvedCount = 0, declinedCount = 0;
        BigDecimal approvedSalesTotal = BigDecimal.ZERO;
        Set<Long> approvedDeviceIds = new HashSet<>();

        for (RmaItem item : items) {
            if (STATUS_APPROVE.equals(item.getStatus())) {
                approvedCount++;
                approvedSalesTotal = approvedSalesTotal.add(
                        item.getSalePrice() != null ? item.getSalePrice() : BigDecimal.ZERO);
                if (item.getDeviceId() != null) {
                    approvedDeviceIds.add(item.getDeviceId());
                }
            } else if (STATUS_DECLINE.equals(item.getStatus())) {
                declinedCount++;
            }
        }

        rma.setApprovedCount(approvedCount);
        rma.setDeclinedCount(declinedCount);
        rma.setApprovedQty(approvedCount);
        rma.setApprovedSkus(approvedDeviceIds.size());
        rma.setApprovedSalesTotal(approvedSalesTotal);
        rmaRepository.save(rma);
    }

    private RmaItemResponse enrichItemWithDevice(RmaItem item) {
        RmaItemResponse response = RmaItemResponse.fromEntity(item);
        if (item.getDeviceId() != null) {
            deviceRepository.findById(item.getDeviceId()).ifPresent(device -> {
                response.setSku(device.getSku());
                response.setDeviceDescription(device.getDescription());
                response.setItemType(device.getItemType());
                if (device.getGrade() != null) {
                    response.setGrade(device.getGrade().getName());
                }
            });
        }
        return response;
    }

    private String formatDisplayLabel(String statusGrouped) {
        if (statusGrouped == null) return "Unknown";
        return statusGrouped.replace("_", " ");
    }
}
