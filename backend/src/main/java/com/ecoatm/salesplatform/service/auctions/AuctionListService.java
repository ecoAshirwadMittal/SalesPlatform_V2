package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AuctionListPageResponse;
import com.ecoatm.salesplatform.dto.AuctionListRow;
import com.ecoatm.salesplatform.dto.SchedulingAuctionListPageResponse;
import com.ecoatm.salesplatform.dto.SchedulingAuctionListRow;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Read-only list queries backing the admin Auctions + Scheduling Auctions grids
 * (QA parity with {@code Mx_Admin.Pages.Auctions} / {@code Scheduling_Auctions}).
 *
 * <p>Writes continue to live on {@link AuctionService} and
 * {@link AuctionScheduleService}; this service exists purely to keep the
 * list-shape SQL (joins + counts + filters) out of the write services.
 */
@Service
public class AuctionListService {

    private final EntityManager em;

    public AuctionListService(EntityManager em) {
        this.em = em;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static Instant toInstant(Object o) {
        if (o == null) return null;
        if (o instanceof Timestamp ts) return ts.toInstant();
        if (o instanceof Instant i) return i;
        return null;
    }

    @Transactional(readOnly = true)
    public AuctionListPageResponse searchAuctions(
            String titleContains,
            Long weekId,
            String status,
            int page,
            int pageSize) {

        String title = blankToNull(titleContains);
        String statusParam = blankToNull(status);
        int offset = page * pageSize;

        String where = """
                WHERE (CAST(:title  AS text)   IS NULL OR LOWER(a.auction_title)  LIKE LOWER(CONCAT('%', CAST(:title AS text), '%')))
                  AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))
                  AND (CAST(:status AS text)   IS NULL OR a.auction_status = CAST(:status AS text))
                """;

        String sql = """
                SELECT a.id,
                       a.auction_title,
                       a.auction_status,
                       a.week_id,
                       w.week_display,
                       a.created_date,
                       a.changed_date,
                       a.created_by,
                       a.updated_by,
                       (SELECT COUNT(*) FROM auctions.scheduling_auctions s WHERE s.auction_id = a.id) AS round_count
                FROM auctions.auctions a
                LEFT JOIN mdm.week w ON w.id = a.week_id
                %s
                ORDER BY a.created_date DESC, a.id DESC
                LIMIT :limit OFFSET :offset
                """.formatted(where);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("title", title)
                .setParameter("weekId", weekId)
                .setParameter("status", statusParam)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        String countSql = """
                SELECT COUNT(*)
                FROM auctions.auctions a
                %s
                """.formatted(where);

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter("title", title)
                .setParameter("weekId", weekId)
                .setParameter("status", statusParam)
                .getSingleResult()).longValue();

        List<AuctionListRow> content = rows.stream()
                .map(r -> new AuctionListRow(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        r[3] == null ? null : ((Number) r[3]).longValue(),
                        (String) r[4],
                        toInstant(r[5]),
                        toInstant(r[6]),
                        (String) r[7],
                        (String) r[8],
                        ((Number) r[9]).intValue()))
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new AuctionListPageResponse(content, page, pageSize, total, totalPages);
    }

    @Transactional(readOnly = true)
    public SchedulingAuctionListPageResponse searchSchedulingAuctions(
            Long auctionId,
            String status,
            String weekDisplayContains,
            int page,
            int pageSize) {

        String statusParam = blankToNull(status);
        String weekDisplay = blankToNull(weekDisplayContains);
        int offset = page * pageSize;

        String where = """
                WHERE (CAST(:auctionId AS bigint) IS NULL OR s.auction_id = CAST(:auctionId AS bigint))
                  AND (CAST(:status AS text)      IS NULL OR s.round_status = CAST(:status AS text))
                  AND (CAST(:weekDisplay AS text) IS NULL OR LOWER(s.auction_week_year) LIKE LOWER(CONCAT('%', CAST(:weekDisplay AS text), '%')))
                """;

        String sql = """
                SELECT s.id,
                       s.auction_id,
                       a.auction_title,
                       s.auction_week_year,
                       s.round,
                       s.name,
                       s.start_datetime,
                       s.end_datetime,
                       s.round_status,
                       s.has_round
                FROM auctions.scheduling_auctions s
                LEFT JOIN auctions.auctions a ON a.id = s.auction_id
                %s
                ORDER BY s.start_datetime DESC NULLS LAST, s.auction_id DESC, s.round ASC
                LIMIT :limit OFFSET :offset
                """.formatted(where);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("auctionId", auctionId)
                .setParameter("status", statusParam)
                .setParameter("weekDisplay", weekDisplay)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        String countSql = """
                SELECT COUNT(*)
                FROM auctions.scheduling_auctions s
                %s
                """.formatted(where);

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter("auctionId", auctionId)
                .setParameter("status", statusParam)
                .setParameter("weekDisplay", weekDisplay)
                .getSingleResult()).longValue();

        List<SchedulingAuctionListRow> content = rows.stream()
                .map(r -> new SchedulingAuctionListRow(
                        ((Number) r[0]).longValue(),
                        r[1] == null ? null : ((Number) r[1]).longValue(),
                        (String) r[2],
                        (String) r[3],
                        ((Number) r[4]).intValue(),
                        (String) r[5],
                        toInstant(r[6]),
                        toInstant(r[7]),
                        (String) r[8],
                        (Boolean) r[9]))
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new SchedulingAuctionListPageResponse(content, page, pageSize, total, totalPages);
    }
}
