package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.BidReportPageResponse;
import com.ecoatm.salesplatform.dto.BidReportRow;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Read-only service backing the admin R3 Bid Report page.
 *
 * <p>Joins {@code auctions.bid_data} → {@code bid_rounds} →
 * {@code scheduling_auctions} WHERE {@code round = 3}, optionally
 * scoped to a single auction via {@code auctionId}. The query mirrors
 * the Mendix {@code ACT_BidDataDoc_ExportExcel} export logic for
 * Round 3 data.
 */
@Service
public class BidReportService {

    private static final String WHERE =
            """
            WHERE sa.round = 3
              AND (CAST(:auctionId AS bigint) IS NULL
                   OR sa.auction_id = CAST(:auctionId AS bigint))
            """;

    private static final String SELECT_SQL =
            """
            SELECT bd.id,
                   bd.bid_round_id,
                   sa.auction_id,
                   sa.id          AS scheduling_auction_id,
                   bd.buyer_code_id,
                   bd.ecoid,
                   bd.merged_grade,
                   bd.buyer_code_type,
                   bd.bid_quantity,
                   bd.bid_amount,
                   bd.submitted_bid_quantity,
                   bd.submitted_bid_amount,
                   bd.target_price,
                   bd.maximum_quantity,
                   bd.submitted_datetime,
                   bd.changed_date
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds        br ON br.id = bd.bid_round_id
            JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
            %s
            ORDER BY bd.id DESC
            LIMIT :limit OFFSET :offset
            """;

    private static final String COUNT_SQL =
            """
            SELECT COUNT(*)
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds        br ON br.id = bd.bid_round_id
            JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
            %s
            """;

    private final EntityManager em;

    public BidReportService(EntityManager em) {
        this.em = em;
    }

    @Transactional(readOnly = true)
    public BidReportPageResponse getR3Report(Long auctionId, int page, int pageSize) {
        int offset = page * pageSize;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(SELECT_SQL.formatted(WHERE))
                .setParameter("auctionId", auctionId)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        long total = ((Number) em.createNativeQuery(COUNT_SQL.formatted(WHERE))
                .setParameter("auctionId", auctionId)
                .getSingleResult()).longValue();

        List<BidReportRow> content = rows.stream()
                .map(BidReportService::toRow)
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new BidReportPageResponse(content, page, pageSize, total, totalPages);
    }

    private static BidReportRow toRow(Object[] r) {
        return new BidReportRow(
                ((Number) r[0]).longValue(),           // id
                ((Number) r[1]).longValue(),           // bidRoundId
                r[2] == null ? null : ((Number) r[2]).longValue(), // auctionId
                ((Number) r[3]).longValue(),           // schedulingAuctionId
                ((Number) r[4]).longValue(),           // buyerCodeId
                (String) r[5],                         // ecoid
                (String) r[6],                         // mergedGrade
                (String) r[7],                         // buyerCodeType
                r[8] == null ? null : ((Number) r[8]).intValue(),  // bidQuantity
                (BigDecimal) r[9],                     // bidAmount
                r[10] == null ? null : ((Number) r[10]).intValue(), // submittedBidQuantity
                (BigDecimal) r[11],                    // submittedBidAmount
                (BigDecimal) r[12],                    // targetPrice
                r[13] == null ? null : ((Number) r[13]).intValue(), // maximumQuantity
                toInstant(r[14]),                      // submittedDatetime
                toInstant(r[15])                       // changedDate
        );
    }

    private static Instant toInstant(Object o) {
        if (o == null) return null;
        if (o instanceof Timestamp ts) return ts.toInstant();
        if (o instanceof Instant i) return i;
        return null;
    }
}
