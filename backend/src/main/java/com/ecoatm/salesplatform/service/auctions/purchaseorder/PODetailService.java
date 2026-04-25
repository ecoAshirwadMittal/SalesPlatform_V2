package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailListResponse;
import com.ecoatm.salesplatform.dto.PODetailRow;
import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
public class PODetailService {

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final BuyerCodeRepository buyerCodeRepo;
    private final POExcelParser parser;
    private final PurchaseOrderValidator validator;
    private final ApplicationEventPublisher events;

    public PODetailService(PurchaseOrderRepository poRepo,
                           PODetailRepository detailRepo,
                           BuyerCodeRepository buyerCodeRepo,
                           POExcelParser parser,
                           PurchaseOrderValidator validator,
                           ApplicationEventPublisher events) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.buyerCodeRepo = buyerCodeRepo;
        this.parser = parser;
        this.validator = validator;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public PODetailListResponse list(long purchaseOrderId, Pageable pageable) {
        Page<PODetail> page = detailRepo.findByPurchaseOrderId(purchaseOrderId, pageable);
        List<PODetailRow> rows = page.getContent().stream().map(this::toRow).toList();
        return new PODetailListResponse(rows, page.getTotalElements(),
                pageable.getPageNumber(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public List<PODetailRow> listAllForExport(long purchaseOrderId) {
        return detailRepo.findByPurchaseOrderId(purchaseOrderId,
                        Pageable.unpaged()).getContent()
                .stream().map(this::toRow).toList();
    }

    @Transactional
    public PODetailUploadResult upload(long purchaseOrderId, InputStream excel) {
        PurchaseOrder po = poRepo.findById(purchaseOrderId).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + purchaseOrderId + " not found"));

        List<POExcelParser.ParsedRow> parsed = parser.parse(excel);

        // Step 1: validate buyer codes (whole-upload short-circuit)
        List<String> codes = parsed.stream().map(POExcelParser.ParsedRow::buyerCode)
                .distinct().toList();
        validator.requireBuyerCodes(codes);

        Map<String, BuyerCode> bcByCode = new HashMap<>();
        for (BuyerCode bc : buyerCodeRepo.findByCodeIn(codes)) {
            bcByCode.put(bc.getCode(), bc);
        }

        // Step 2: dedupe (productId, grade, buyerCode) within sheet
        record Key(String productId, String grade, String buyerCode) {}
        Set<Key> seen = new HashSet<>();
        List<PODetailUploadResult.UploadError> skipped = new ArrayList<>();
        List<POExcelParser.ParsedRow> kept = new ArrayList<>();
        for (POExcelParser.ParsedRow r : parsed) {
            Key key = new Key(r.productId(), r.grade(), r.buyerCode());
            if (!seen.add(key)) {
                skipped.add(new PODetailUploadResult.UploadError(
                        r.rowNumber(), r.productId(), r.grade(), r.buyerCode(),
                        "DUPLICATE_IN_SHEET — first occurrence wins"));
                continue;
            }
            kept.add(r);
        }

        // Step 3: wipe-and-replace
        int deleted = detailRepo.deleteAllByPurchaseOrderId(purchaseOrderId);

        List<PODetail> toSave = new ArrayList<>(kept.size());
        for (POExcelParser.ParsedRow r : kept) {
            PODetail d = new PODetail();
            d.setPurchaseOrder(po);
            d.setBuyerCode(bcByCode.get(r.buyerCode()));
            d.setProductId(r.productId());
            d.setGrade(r.grade());
            d.setModelName(r.modelName());
            d.setPrice(r.price());
            d.setQtyCap(r.qtyCap());
            d.setTempBuyerCode(r.buyerCode());
            toSave.add(d);
        }
        detailRepo.saveAll(toSave);

        po.setTotalRecords(toSave.size());
        po.setPoRefreshTimestamp(Instant.now());

        events.publishEvent(new PurchaseOrderChangedEvent(po.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));

        return new PODetailUploadResult(toSave.size(), deleted, skipped.size(), skipped);
    }

    PODetailRow toRow(PODetail d) {
        return new PODetailRow(d.getId(), d.getPurchaseOrder().getId(),
                d.getBuyerCode().getId(), d.getBuyerCode().getCode(),
                d.getProductId(), d.getGrade(), d.getModelName(),
                d.getPrice(), d.getQtyCap(),
                d.getPriceFulfilled(), d.getQtyFulfilled());
    }
}
