package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PurchaseOrderListResponse;
import com.ecoatm.salesplatform.dto.PurchaseOrderRequest;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final PurchaseOrderValidator validator;
    private final ApplicationEventPublisher events;

    public PurchaseOrderService(PurchaseOrderRepository poRepo,
                                PODetailRepository detailRepo,
                                PurchaseOrderValidator validator,
                                ApplicationEventPublisher events) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.validator = validator;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public PurchaseOrderListResponse list(Long weekFromId, Long weekToId,
                                          Integer yearFrom, Integer yearTo,
                                          Pageable pageable) {
        Page<PurchaseOrder> page = poRepo.findFiltered(
                weekFromId, weekToId, yearFrom, yearTo, pageable);
        List<PurchaseOrderRow> rows = page.getContent().stream().map(this::toRow).toList();
        return new PurchaseOrderListResponse(rows, page.getTotalElements(),
                pageable.getPageNumber(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderRow findById(long id) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        return toRow(po);
    }

    @Transactional
    public PurchaseOrderRow create(PurchaseOrderRequest req) {
        var range = validator.resolveWeekRange(req.weekFromId(), req.weekToId());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(range.from());
        po.setWeekTo(range.to());
        po.setWeekRangeLabel(buildRangeLabel(range.from().getWeekDisplay(),
                                             range.to().getWeekDisplay()));
        po.setTotalRecords(0);
        po.setValidYearWeek(true);
        PurchaseOrder saved = poRepo.save(po);
        events.publishEvent(new PurchaseOrderChangedEvent(saved.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));
        return toRow(saved);
    }

    @Transactional
    public PurchaseOrderRow update(long id, PurchaseOrderRequest req) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        var range = validator.resolveWeekRange(req.weekFromId(), req.weekToId());
        po.setWeekFrom(range.from());
        po.setWeekTo(range.to());
        po.setWeekRangeLabel(buildRangeLabel(range.from().getWeekDisplay(),
                                             range.to().getWeekDisplay()));
        events.publishEvent(new PurchaseOrderChangedEvent(po.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));
        return toRow(po);
    }

    @Transactional
    public void delete(long id) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        poRepo.delete(po);
        events.publishEvent(new PurchaseOrderChangedEvent(id,
                PurchaseOrderChangedEvent.Action.DELETE));
    }

    PurchaseOrderRow toRow(PurchaseOrder po) {
        var state = PurchaseOrderLifecycleState.derive(
                LocalDate.now(ZoneOffset.UTC), po.getWeekFrom(), po.getWeekTo());
        // User entity exposes only a display name (no email field on the modern
        // schema). Using getName() keeps the row's changedByUsername label
        // consistent with the rest of the admin surface.
        String changedBy = po.getChangedBy() == null ? null : po.getChangedBy().getName();
        return new PurchaseOrderRow(
                po.getId(),
                po.getWeekFrom().getId(), po.getWeekFrom().getWeekDisplay(),
                po.getWeekTo().getId(),   po.getWeekTo().getWeekDisplay(),
                po.getWeekRangeLabel(),
                state,
                po.getTotalRecords(),
                po.getPoRefreshTimestamp(),
                po.getChangedDate(),
                changedBy);
    }

    private static String buildRangeLabel(String fromLabel, String toLabel) {
        if (fromLabel == null) fromLabel = "?";
        if (toLabel == null) toLabel = "?";
        return fromLabel + " - " + toLabel;
    }
}
