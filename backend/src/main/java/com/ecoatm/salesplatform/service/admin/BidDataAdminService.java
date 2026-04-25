package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.BidDataAdminListResponse;
import com.ecoatm.salesplatform.dto.BidDataAdminRow;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidDataAudit;
import com.ecoatm.salesplatform.repository.auctions.BidDataAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataSubmissionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * P8 Lane 3A — admin Bid Data view + remove.
 *
 * <p>List semantics: the admin grid filters by (bidRoundId, buyerCodeId)
 * with an optional {@code submittedBidAmountGt0} flag that narrows to rows
 * the bidder has submitted. Soft-deleted rows ({@code is_deprecated=true})
 * are excluded by default.
 *
 * <p>Remove semantics: soft-delete by flipping {@code is_deprecated} to
 * TRUE. The Mendix legacy DELETE was hard, but the schema's
 * {@code is_deprecated} column was specifically added so admin removals stay
 * reversible. Each call also writes an audit row with a snapshot of the
 * pre-delete bid columns so the trail is self-contained even if a future
 * cleanup job hard-deletes the parent row.
 */
@Service
public class BidDataAdminService {

    private final BidDataRepository bidDataRepo;
    private final BidDataAuditRepository auditRepo;

    @PersistenceContext
    private EntityManager em;

    public BidDataAdminService(BidDataRepository bidDataRepo,
                               BidDataAuditRepository auditRepo) {
        this.bidDataRepo = bidDataRepo;
        this.auditRepo = auditRepo;
    }

    @Transactional(readOnly = true, timeout = 10)
    public BidDataAdminListResponse list(Long bidRoundId, Long buyerCodeId,
                                         boolean submittedBidAmountGt0) {
        // Native query keeps the WHERE clause flexible without requiring a
        // dynamic JPQL builder. The repo's findByBidRoundIdAndBuyerCodeId…
        // helper is intentionally not reused: it sorts by ecoid+grade (good
        // for the bidder grid) but doesn't filter on is_deprecated or the
        // submitted-only flag the admin UI needs.
        StringBuilder sql = new StringBuilder("""
                SELECT id, bid_round_id, buyer_code_id, ecoid, merged_grade,
                       bid_quantity, bid_amount, submitted_bid_quantity,
                       submitted_bid_amount, submitted_datetime, changed_date,
                       is_deprecated
                  FROM auctions.bid_data
                 WHERE is_deprecated = false
                """);
        List<Object> params = new ArrayList<>();
        int idx = 1;
        if (bidRoundId != null) {
            sql.append(" AND bid_round_id = ?").append(idx++);
            params.add(bidRoundId);
        }
        if (buyerCodeId != null) {
            sql.append(" AND buyer_code_id = ?").append(idx++);
            params.add(buyerCodeId);
        }
        if (submittedBidAmountGt0) {
            sql.append(" AND submitted_bid_amount IS NOT NULL AND submitted_bid_amount > 0");
        }
        sql.append(" ORDER BY id ASC");

        Query q = em.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            q.setParameter(i + 1, params.get(i));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> raw = q.getResultList();
        List<BidDataAdminRow> rows = new ArrayList<>(raw.size());
        for (Object[] r : raw) {
            rows.add(toRow(r));
        }
        return new BidDataAdminListResponse(rows, rows.size());
    }

    @Transactional(timeout = 10)
    public void softDelete(long bidDataId, long actingUserId) {
        BidData row = bidDataRepo.findById(bidDataId)
                .orElseThrow(() -> new BidDataSubmissionException("BID_DATA_NOT_FOUND",
                        "Bid data not found: " + bidDataId));

        // Snapshot BEFORE flipping the flag so the audit row reflects
        // pre-action state — matches the reserve_bid_audit pattern.
        BidDataAudit audit = new BidDataAudit();
        audit.setBidDataId(row.getId());
        audit.setAction("SOFT_DELETE");
        audit.setBidRoundId(row.getBidRoundId());
        audit.setBuyerCodeId(row.getBuyerCodeId());
        audit.setBidAmount(row.getBidAmount());
        audit.setBidQuantity(row.getBidQuantity());
        audit.setSubmittedBidAmount(row.getSubmittedBidAmount());
        audit.setSubmittedBidQuantity(row.getSubmittedBidQuantity());
        audit.setChangedById(actingUserId);
        auditRepo.save(audit);

        row.setDeprecated(true);
        row.setChangedDate(Instant.now());
        row.setChangedById(actingUserId);
        bidDataRepo.save(row);
    }

    private static BidDataAdminRow toRow(Object[] r) {
        // Native-query column order matches the SELECT above. JDBC returns
        // numeric columns as the platform-default Number subtypes; we coerce
        // to the record's declared types defensively.
        return new BidDataAdminRow(
                ((Number) r[0]).longValue(),
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                (String) r[3],
                (String) r[4],
                r[5] == null ? null : ((Number) r[5]).intValue(),
                (BigDecimal) r[6],
                r[7] == null ? null : ((Number) r[7]).intValue(),
                (BigDecimal) r[8],
                toInstant(r[9]),
                toInstant(r[10]),
                Boolean.TRUE.equals(r[11])
        );
    }

    private static Instant toInstant(Object o) {
        if (o == null) return null;
        if (o instanceof Instant i) return i;
        if (o instanceof java.sql.Timestamp t) return t.toInstant();
        if (o instanceof java.time.OffsetDateTime odt) return odt.toInstant();
        return null;
    }
}
