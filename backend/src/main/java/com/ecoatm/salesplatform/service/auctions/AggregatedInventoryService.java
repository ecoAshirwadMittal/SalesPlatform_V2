package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class AggregatedInventoryService {

    /**
     * Sentinel returned for {@code syncStatus} when no Snowflake sync log
     * exists for the selected week (or when no week is selected). Matches
     * the value the UI expects to suppress the sync banner.
     */
    private static final String SYNC_STATUS_NONE = "NONE";

    private final EntityManager em;
    private final SnowflakeSyncLogRepository syncLogRepository;

    public AggregatedInventoryService(EntityManager em,
                                      SnowflakeSyncLogRepository syncLogRepository) {
        this.em = em;
        this.syncLogRepository = syncLogRepository;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static BigDecimal toBd(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public AggregatedInventoryPageResponse search(
            Long weekId,
            String productIdExact,
            String gradesContains,
            String brandContains,
            String modelContains,
            String modelNameContains,
            String carrierContains,
            int page,
            int pageSize) {

        String ecoid = blankToNull(productIdExact);
        String grades = blankToNull(gradesContains);
        String brand = blankToNull(brandContains);
        String model = blankToNull(modelContains);
        String name = blankToNull(modelNameContains);
        String carrier = blankToNull(carrierContains);

        int offset = page * pageSize;

        String where = """
                WHERE a.is_deprecated = false
                  AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))
                  AND (CAST(:ecoid AS text)   IS NULL OR a.ecoid2 = CAST(:ecoid AS text))
                  AND (CAST(:grades AS text)  IS NULL OR LOWER(a.merged_grade)   LIKE LOWER(CONCAT('%', CAST(:grades AS text), '%')))
                  AND (CAST(:brand AS text)   IS NULL OR LOWER(a.brand)          LIKE LOWER(CONCAT('%', CAST(:brand AS text), '%')))
                  AND (CAST(:model AS text)   IS NULL OR LOWER(a.model)          LIKE LOWER(CONCAT('%', CAST(:model AS text), '%')))
                  AND (CAST(:name AS text)    IS NULL OR LOWER(a.name)           LIKE LOWER(CONCAT('%', CAST(:name AS text), '%')))
                  AND (CAST(:carrier AS text) IS NULL OR LOWER(a.carrier)        LIKE LOWER(CONCAT('%', CAST(:carrier AS text), '%')))
                """;

        String sql = """
                SELECT a.id, a.ecoid2, a.merged_grade, a.brand, a.model, a.name, a.carrier,
                       a.dw_total_quantity, a.dw_avg_target_price,
                       a.total_quantity, a.avg_target_price, a.datawipe
                FROM auctions.aggregated_inventory a
                %s
                ORDER BY a.ecoid2 ASC, a.merged_grade ASC
                LIMIT :limit OFFSET :offset
                """.formatted(where);

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("weekId", weekId)
                .setParameter("ecoid", ecoid)
                .setParameter("grades", grades)
                .setParameter("brand", brand)
                .setParameter("model", model)
                .setParameter("name", name)
                .setParameter("carrier", carrier)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        String countSql = """
                SELECT COUNT(*) FROM auctions.aggregated_inventory a
                %s
                """.formatted(where);

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter("weekId", weekId)
                .setParameter("ecoid", ecoid)
                .setParameter("grades", grades)
                .setParameter("brand", brand)
                .setParameter("model", model)
                .setParameter("name", name)
                .setParameter("carrier", carrier)
                .getSingleResult()).longValue();

        List<AggregatedInventoryRow> content = rows.stream()
                .map(r -> new AggregatedInventoryRow(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (String) r[5],
                        (String) r[6],
                        ((Number) r[7]).intValue(),
                        toBd(r[8]),
                        ((Number) r[9]).intValue(),
                        toBd(r[10]),
                        (Boolean) r[11]
                ))
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new AggregatedInventoryPageResponse(content, page, pageSize, total, totalPages);
    }

    /**
     * KPI totals plus the four helper flags consumed by the admin page
     * ({@code hasInventory}, {@code hasAuction}, {@code isCurrentWeek},
     * {@code syncStatus}). With {@code weekId == null} the page is in its
     * pre-selection state, so the flags are forced to safe defaults to
     * suppress the "Create Auction" button and the sync banner.
     */
    @Transactional(readOnly = true)
    public AggregatedInventoryTotalsResponse getTotals(Long weekId) {
        String sql = """
                SELECT
                  COALESCE(SUM(a.total_quantity), 0)                                           AS total_qty,
                  COALESCE(SUM(a.total_payout), 0)                                             AS total_payout,
                  CASE WHEN COALESCE(SUM(a.total_quantity), 0) = 0 THEN 0
                       ELSE SUM(a.avg_target_price * a.total_quantity) / SUM(a.total_quantity) END AS avg_target,
                  COALESCE(SUM(a.dw_total_quantity), 0)                                        AS dw_total_qty,
                  COALESCE(SUM(a.dw_total_payout), 0)                                          AS dw_total_payout,
                  CASE WHEN COALESCE(SUM(a.dw_total_quantity), 0) = 0 THEN 0
                       ELSE SUM(a.dw_avg_target_price * a.dw_total_quantity) / SUM(a.dw_total_quantity) END AS dw_avg_target,
                  MAX(a.changed_date)                                                          AS last_synced
                FROM auctions.aggregated_inventory a
                WHERE a.is_deprecated = false
                  AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))
                """;

        Object[] r = (Object[]) em.createNativeQuery(sql)
                .setParameter("weekId", weekId)
                .getSingleResult();

        Instant lastSynced = null;
        if (r[6] instanceof java.sql.Timestamp ts) lastSynced = ts.toInstant();
        else if (r[6] instanceof Instant inst) lastSynced = inst;

        boolean hasInventory = false;
        boolean hasAuction = false;
        boolean isCurrentWeek = false;
        String syncStatus = SYNC_STATUS_NONE;
        if (weekId != null) {
            // Dedicated EXISTS so "rows present but quantities all zero" still
            // reports hasInventory=true — totalQuantity alone would lie.
            hasInventory = ((Boolean) em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM auctions.aggregated_inventory "
                            + "WHERE week_id = :weekId AND is_deprecated = false)")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            hasAuction = ((Boolean) em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM auctions.auctions WHERE week_id = :weekId)")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            isCurrentWeek = ((Boolean) em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM mdm.week "
                            + "WHERE id = :weekId AND week_end_datetime > now())")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            syncStatus = syncLogRepository
                    .findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                            AggregatedInventorySnowflakeSyncService.SYNC_TYPE,
                            String.valueOf(weekId))
                    .map(SnowflakeSyncLog::getStatus)
                    .orElse(SYNC_STATUS_NONE);
        }

        return new AggregatedInventoryTotalsResponse(
                ((Number) r[0]).intValue(),
                toBd(r[1]),
                toBd(r[2]),
                ((Number) r[3]).intValue(),
                toBd(r[4]),
                toBd(r[5]),
                lastSynced,
                hasInventory,
                hasAuction,
                isCurrentWeek,
                syncStatus
        );
    }

    @Transactional
    public AggregatedInventory updateRow(Long id, AggregatedInventoryUpdateRequest req) {
        var entity = em.find(AggregatedInventory.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("AggregatedInventory not found: " + id);
        }
        entity.setMergedGrade(req.mergedGrade());
        entity.setDatawipe(req.datawipe());
        entity.setTotalQuantity(req.totalQuantity());
        entity.setDwTotalQuantity(req.dwTotalQuantity());
        entity.setTotalQuantityModified(true);
        entity.setChangedDate(Instant.now());
        return em.merge(entity);
    }
}
