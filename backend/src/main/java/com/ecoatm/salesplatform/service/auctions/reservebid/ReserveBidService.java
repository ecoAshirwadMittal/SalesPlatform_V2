package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidSyncRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class ReserveBidService {

    private final ReserveBidRepository repo;
    private final ReserveBidAuditRepository auditRepo;
    private final ReserveBidSyncRepository syncRepo;
    private final ApplicationEventPublisher publisher;
    private final Object snowflakeReader;   // placeholder — replaced in Task 9
    private final ReserveBidExcelParser excelParser;

    public ReserveBidService(ReserveBidRepository repo,
                             ReserveBidAuditRepository auditRepo,
                             ReserveBidSyncRepository syncRepo,
                             ApplicationEventPublisher publisher,
                             Object snowflakeReader,
                             ReserveBidExcelParser excelParser) {
        this.repo = repo;
        this.auditRepo = auditRepo;
        this.syncRepo = syncRepo;
        this.publisher = publisher;
        this.snowflakeReader = snowflakeReader;
        this.excelParser = excelParser;
    }

    @Transactional
    public ReserveBidRow create(long userId, ReserveBidRequest req) {
        if (repo.existsByProductIdAndGrade(req.productId(), req.grade())) {
            throw new ReserveBidException("DUPLICATE_PRODUCT_GRADE",
                    "reserve_bid exists for product=" + req.productId() + " grade=" + req.grade());
        }

        ReserveBid rb = new ReserveBid();
        applyRequest(rb, req);
        rb.setOwnerId(userId);
        rb.setChangedById(userId);
        rb.setLastUpdateDatetime(Instant.now());
        ReserveBid saved = repo.save(rb);

        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(saved.getId()), ReserveBidChangedEvent.Action.UPSERT));
        return toDto(saved);
    }

    @Transactional
    public ReserveBidRow update(long userId, long id, ReserveBidRequest req) {
        ReserveBid rb = repo.findById(id).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));

        BigDecimal oldPrice = rb.getBid();
        applyRequest(rb, req);
        rb.setChangedById(userId);
        rb.setChangedDate(Instant.now());

        boolean priceChanged = oldPrice != null && req.bid() != null
                && oldPrice.compareTo(req.bid()) != 0;
        if (priceChanged) {
            rb.setLastUpdateDatetime(Instant.now());
            ReserveBidAudit a = new ReserveBidAudit();
            a.setReserveBidId(rb.getId());
            a.setOldPrice(oldPrice);
            a.setNewPrice(req.bid());
            a.setOwnerId(userId);
            a.setChangedById(userId);
            auditRepo.save(a);
        }
        ReserveBid saved = repo.save(rb);

        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(saved.getId()), ReserveBidChangedEvent.Action.UPSERT));
        return toDto(saved);
    }

    @Transactional
    public void delete(long id) {
        ReserveBid rb = repo.findById(id).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));
        repo.delete(rb);
        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(rb.getId()), ReserveBidChangedEvent.Action.DELETE));
    }

    @Transactional(readOnly = true)
    public ReserveBidRow findById(long id) {
        return repo.findById(id).map(ReserveBidService::toDto).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));
    }

    @Transactional(readOnly = true)
    public ReserveBidListResponse search(String productId, String grade,
                                         BigDecimal minBid, BigDecimal maxBid,
                                         Instant updatedSince, int page, int size) {
        Page<ReserveBid> p = repo.search(productId, grade, minBid, maxBid, updatedSince,
                PageRequest.of(page, size));
        return new ReserveBidListResponse(
                p.getContent().stream().map(ReserveBidService::toDto).toList(),
                p.getTotalElements(), page, size);
    }

    private static void applyRequest(ReserveBid rb, ReserveBidRequest req) {
        rb.setProductId(req.productId());
        rb.setGrade(req.grade());
        rb.setBrand(req.brand());
        rb.setModel(req.model());
        rb.setBid(req.bid());
        rb.setLastAwardedMinPrice(req.lastAwardedMinPrice());
        rb.setLastAwardedWeek(req.lastAwardedWeek());
        rb.setBidValidWeekDate(req.bidValidWeekDate());
    }

    static ReserveBidRow toDto(ReserveBid rb) {
        return new ReserveBidRow(
                rb.getId(), rb.getProductId(), rb.getGrade(),
                rb.getBrand(), rb.getModel(), rb.getBid(),
                rb.getLastUpdateDatetime(), rb.getLastAwardedMinPrice(),
                rb.getLastAwardedWeek(), rb.getBidValidWeekDate(),
                rb.getChangedDate());
    }

    @Transactional
    public com.ecoatm.salesplatform.dto.ReserveBidUploadResult upload(long userId,
            org.springframework.web.multipart.MultipartFile file) {
        if (excelParser == null) {
            throw new ReserveBidException("PARSER_UNAVAILABLE", "excelParser not wired");
        }
        List<ReserveBidExcelParser.ParsedRow> parsed;
        try {
            parsed = excelParser.parse(file.getInputStream());
        } catch (java.io.IOException ex) {
            throw new ReserveBidValidationException("UPLOAD_PARSE_ERROR",
                    "Cannot read upload: " + ex.getMessage());
        }

        List<com.ecoatm.salesplatform.dto.ReserveBidUploadResult.UploadError> errors = new java.util.ArrayList<>();
        int created = 0, updated = 0, unchanged = 0, auditsGenerated = 0;
        List<Long> changedIds = new java.util.ArrayList<>();
        java.util.Set<String> seenKeys = new java.util.HashSet<>();

        for (var row : parsed) {
            String key = row.productId() + "|" + row.grade();
            if (row.grade() == null || row.grade().isBlank()) {
                errors.add(new com.ecoatm.salesplatform.dto.ReserveBidUploadResult.UploadError(
                        row.rowNumber(), row.productId(), row.grade(), "MISSING_GRADE"));
                continue;
            }
            if (row.productId() == null || row.productId().isBlank()) {
                errors.add(new com.ecoatm.salesplatform.dto.ReserveBidUploadResult.UploadError(
                        row.rowNumber(), row.productId(), row.grade(), "MISSING_PRODUCT_ID"));
                continue;
            }
            if (row.price() == null || row.price().signum() < 0) {
                errors.add(new com.ecoatm.salesplatform.dto.ReserveBidUploadResult.UploadError(
                        row.rowNumber(), row.productId(), row.grade(), "NEGATIVE_PRICE"));
                continue;
            }
            if (!seenKeys.add(key)) {
                errors.add(new com.ecoatm.salesplatform.dto.ReserveBidUploadResult.UploadError(
                        row.rowNumber(), row.productId(), row.grade(), "DUPLICATE_IN_SHEET"));
                continue;
            }

            java.util.Optional<ReserveBid> existing = repo.findByProductIdAndGrade(row.productId(), row.grade());
            ReserveBid rb;
            if (existing.isPresent()) {
                rb = existing.get();
                BigDecimal oldPrice = rb.getBid();
                if (oldPrice != null && oldPrice.compareTo(row.price()) == 0) {
                    unchanged++;
                    continue;
                }
                if (oldPrice != null) {
                    ReserveBidAudit a = new ReserveBidAudit();
                    a.setReserveBidId(rb.getId());
                    a.setOldPrice(oldPrice);
                    a.setNewPrice(row.price());
                    a.setOwnerId(userId);
                    a.setChangedById(userId);
                    auditRepo.save(a);
                    auditsGenerated++;
                }
                rb.setBid(row.price());
                rb.setModel(row.modelName() != null ? row.modelName() : rb.getModel());
                rb.setLastUpdateDatetime(Instant.now());
                rb.setChangedById(userId);
                rb.setChangedDate(Instant.now());
                repo.save(rb);
                updated++;
            } else {
                rb = new ReserveBid();
                rb.setProductId(row.productId());
                rb.setGrade(row.grade());
                rb.setModel(row.modelName());
                rb.setBid(row.price());
                rb.setLastUpdateDatetime(Instant.now());
                rb.setOwnerId(userId);
                rb.setChangedById(userId);
                repo.save(rb);
                created++;
            }
            changedIds.add(rb.getId());
        }

        if (!changedIds.isEmpty()) {
            publisher.publishEvent(new ReserveBidChangedEvent(
                    changedIds, ReserveBidChangedEvent.Action.UPSERT));
        }
        return new com.ecoatm.salesplatform.dto.ReserveBidUploadResult(created, updated, unchanged, auditsGenerated, errors);
    }
}
