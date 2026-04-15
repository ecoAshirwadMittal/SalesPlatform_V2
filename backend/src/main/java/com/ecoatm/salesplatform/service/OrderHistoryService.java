package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.OrderDetailByDeviceResponse;
import com.ecoatm.salesplatform.dto.OrderDetailBySkuResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryTabCounts;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.model.pws.OrderHistoryView;
import com.ecoatm.salesplatform.model.pws.ShipmentDetail;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.ImeiDetailRepository;
import com.ecoatm.salesplatform.repository.pws.OfferItemRepository;
import com.ecoatm.salesplatform.repository.pws.OrderHistoryViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {

    private static final List<String> IN_PROCESS_STATUSES =
            List.of("In Process", "Offer Pending", "Awaiting Carrier Pickup",
                    "Draft", "Submitted");

    private static final int RECENT_DAYS = 7;

    private final OrderHistoryViewRepository orderHistoryViewRepository;
    private final BuyerCodeService buyerCodeService;
    private final OfferItemRepository offerItemRepository;
    private final DeviceRepository deviceRepository;
    private final ImeiDetailRepository imeiDetailRepository;

    public OrderHistoryService(OrderHistoryViewRepository orderHistoryViewRepository,
                               BuyerCodeService buyerCodeService,
                               OfferItemRepository offerItemRepository,
                               DeviceRepository deviceRepository,
                               ImeiDetailRepository imeiDetailRepository) {
        this.orderHistoryViewRepository = orderHistoryViewRepository;
        this.buyerCodeService = buyerCodeService;
        this.offerItemRepository = offerItemRepository;
        this.deviceRepository = deviceRepository;
        this.imeiDetailRepository = imeiDetailRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> listOrders(String tab, Long userId, Long buyerCodeId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        Specification<OrderHistoryView> spec = scopeByUser(userId, buyerCodeId).and(tabFilter(tab));
        return orderHistoryViewRepository.findAll(spec, pageable)
                .map(OrderHistoryResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderHistoryTabCounts getTabCounts(Long userId, Long buyerCodeId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        Specification<OrderHistoryView> scope = scopeByUser(userId, buyerCodeId);
        long recent    = orderHistoryViewRepository.count(scope.and(tabFilter("recent")));
        long inProcess = orderHistoryViewRepository.count(scope.and(tabFilter("inProcess")));
        long complete  = orderHistoryViewRepository.count(scope.and(tabFilter("complete")));
        long all       = orderHistoryViewRepository.count(scope.and(tabFilter("all")));
        return new OrderHistoryTabCounts(recent, inProcess, complete, all);
    }

    @Transactional(readOnly = true)
    public List<OrderDetailBySkuResponse> getDetailsBySku(Long offerId) {
        List<OfferItem> items = offerItemRepository.findByOfferId(offerId);
        Map<Long, Device> deviceMap = loadDeviceMap(
                items.stream().map(OfferItem::getDeviceId).collect(Collectors.toSet()));
        return items.stream()
                .map(item -> {
                    Device device = deviceMap.get(item.getDeviceId());
                    return new OrderDetailBySkuResponse(
                            item.getId(),
                            item.getSku(),
                            device != null ? device.getDescription() : null,
                            item.getFinalOfferQuantity(),
                            item.getShippedQty(),
                            item.getFinalOfferPrice(),
                            item.getFinalOfferTotalPrice()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDetailByDeviceResponse> getDetailsByDevice(Long offerId) {
        List<ImeiDetail> details = imeiDetailRepository.findByOfferItemOfferId(offerId);
        Map<Long, Device> deviceMap = loadDeviceMap(
                details.stream()
                        .map(ImeiDetail::getOfferItem)
                        .filter(Objects::nonNull)
                        .map(OfferItem::getDeviceId)
                        .collect(Collectors.toSet()));
        return details.stream()
                .map(d -> {
                    OfferItem item = d.getOfferItem();
                    Device device = (item != null) ? deviceMap.get(item.getDeviceId()) : null;
                    ShipmentDetail sd = d.getShipmentDetail();
                    return new OrderDetailByDeviceResponse(
                            d.getId(),
                            d.getImeiNumber(),
                            item != null ? item.getSku() : null,
                            device != null ? device.getDescription() : null,
                            item != null ? item.getFinalOfferPrice() : null,
                            d.getSerialNumber(),
                            d.getBoxLpnNumber(),
                            sd != null ? sd.getTrackingNumber() : null,
                            sd != null ? sd.getTrackingUrl() : null
                    );
                })
                .toList();
    }

    private Specification<OrderHistoryView> scopeByUser(Long userId, Long buyerCodeId) {
        List<BuyerCodeResponse> codes = buyerCodeService.getBuyerCodesForUser(userId);
        List<Long> codeIds = codes.stream().map(BuyerCodeResponse::getId).toList();
        if (codeIds.isEmpty()) {
            return (root, query, cb) -> cb.disjunction();
        }
        // If a specific buyer code is selected, filter to just that one (must be in user's codes)
        if (buyerCodeId != null && codeIds.contains(buyerCodeId)) {
            return (root, query, cb) -> cb.equal(root.get("buyerCodeId"), buyerCodeId);
        }
        return (root, query, cb) -> root.get("buyerCodeId").in(codeIds);
    }

    private Map<Long, Device> loadDeviceMap(Set<Long> deviceIds) {
        deviceIds.remove(null);
        if (deviceIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        return deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }

    private Specification<OrderHistoryView> tabFilter(String tab) {
        return switch (tab) {
            case "recent" -> (root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("lastUpdateDate"),
                            LocalDateTime.now().minusDays(RECENT_DAYS));
            case "inProcess" -> (root, query, cb) ->
                    root.get("orderStatus").in(IN_PROCESS_STATUSES);
            case "complete" -> (root, query, cb) ->
                    cb.not(root.get("orderStatus").in(IN_PROCESS_STATUSES));
            case "all" -> (root, query, cb) -> cb.conjunction();
            default -> throw new IllegalArgumentException("Unknown tab: " + tab);
        };
    }
}
