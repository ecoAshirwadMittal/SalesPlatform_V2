package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest;
import com.ecoatm.salesplatform.dto.SyncStatusResponse;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import com.ecoatm.salesplatform.service.auctions.inventory.InventoryFilterRequest;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterOp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AggregatedInventoryService {

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

    /**
     * Backwards-compatible search overload — every text column defaults to
     * "contains" mode (the legacy behavior). Existing callers (controller
     * passes-through, tests) keep working without churn.
     */
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
        return search(weekId, productIdExact,
                gradesContains, null,
                brandContains, null,
                modelContains, null,
                modelNameContains, null,
                carrierContains, null,
                page, pageSize);
    }

    /**
     * Render an op-aware SQL fragment for a text filter column using
     * named bind parameters. Mirrors the {@code FilterOp} semantics from
     * the reservebid filter package so the inventory search now supports
     * the full 11-op comparator menu (Contains / Starts with / Ends
     * with / Equal / Not equal / Greater than / Greater than or equal /
     * Smaller than / Smaller than or equal / Empty / Not empty) — same
     * set every other admin grid surfaces.
     *
     * <p>Numeric ops on these text columns use lexicographic comparison
     * because the underlying schema is VARCHAR (ecoid2 / merged_grade /
     * brand / model / name / carrier all varchar). Frontend can
     * constrain the visible op menu via {@code availableOps} when lex
     * comparison would confuse users (e.g. ecoid2 displayed as numeric).
     *
     * @param sqlColumn fully-qualified SQL column name
     * @param paramName bind name to use for the value when the op is
     *                  value-bearing
     * @param req       parsed filter; no fragment emitted when inactive
     * @param params    out-map: caller appends the value→bind-name entry
     *                  here and {@code setParameter}s them all on the
     *                  prepared query in order
     * @return SQL fragment to AND into the WHERE clause, or empty
     *         string when the request is inactive
     */
    private static String renderTextFilterFragment(String sqlColumn,
                                                   String paramName,
                                                   InventoryFilterRequest req,
                                                   Map<String, Object> params) {
        if (!req.active()) return "";
        FilterOp op = req.op();
        if (op == FilterOp.EMPTY) {
            return " AND (" + sqlColumn + " IS NULL OR " + sqlColumn + " = '')";
        }
        if (op == FilterOp.NOT_EMPTY) {
            return " AND (" + sqlColumn + " IS NOT NULL AND " + sqlColumn + " <> '')";
        }
        params.put(paramName, req.value());
        String castedParam = "CAST(:" + paramName + " AS text)";
        return " AND " + switch (op) {
            case EQ          -> "LOWER(" + sqlColumn + ") = LOWER("        + castedParam + ")";
            case NEQ         -> "LOWER(" + sqlColumn + ") <> LOWER("       + castedParam + ")";
            case GT          -> "LOWER(" + sqlColumn + ") > LOWER("        + castedParam + ")";
            case GTE         -> "LOWER(" + sqlColumn + ") >= LOWER("       + castedParam + ")";
            case LT          -> "LOWER(" + sqlColumn + ") < LOWER("        + castedParam + ")";
            case LTE         -> "LOWER(" + sqlColumn + ") <= LOWER("       + castedParam + ")";
            case CONTAINS    -> "LOWER(" + sqlColumn + ") LIKE LOWER(CONCAT('%', " + castedParam + ", '%'))";
            case STARTS_WITH -> "LOWER(" + sqlColumn + ") LIKE LOWER(CONCAT(" + castedParam + ", '%'))";
            case ENDS_WITH   -> "LOWER(" + sqlColumn + ") LIKE LOWER(CONCAT('%', " + castedParam + "))";
            case EMPTY, NOT_EMPTY -> throw new IllegalStateException("valueless op leaked past guard");
        };
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public AggregatedInventoryPageResponse search(
            Long weekId,
            String productIdExact,
            String gradesValue, String gradesMode,
            String brandValue, String brandMode,
            String modelValue, String modelMode,
            String modelNameValue, String modelNameMode,
            String carrierValue, String carrierMode,
            int page,
            int pageSize) {

        // Parse each column value into op + value, supporting both the
        // new "op,value" wire format and the legacy bare-value shape
        // with sibling xxxMode params.
        InventoryFilterRequest productReq  = InventoryFilterRequest.parse(productIdExact, "equals"); // legacy: productId is exact
        InventoryFilterRequest gradesReq   = InventoryFilterRequest.parse(gradesValue, gradesMode);
        InventoryFilterRequest brandReq    = InventoryFilterRequest.parse(brandValue, brandMode);
        InventoryFilterRequest modelReq    = InventoryFilterRequest.parse(modelValue, modelMode);
        InventoryFilterRequest modelNameReq= InventoryFilterRequest.parse(modelNameValue, modelNameMode);
        InventoryFilterRequest carrierReq  = InventoryFilterRequest.parse(carrierValue, carrierMode);

        // Build the WHERE clause iteratively so each filter fragment binds
        // exactly the params it needs to a shared map. LinkedHashMap so the
        // iteration order is deterministic for tests that assert query
        // shape via Mockito argument captors.
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("weekId", weekId);
        StringBuilder where = new StringBuilder("WHERE a.is_deprecated = false")
                .append(" AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))")
                .append(renderTextFilterFragment("a.ecoid2",       "ecoid",   productReq,   params))
                .append(renderTextFilterFragment("a.merged_grade", "grades",  gradesReq,    params))
                .append(renderTextFilterFragment("a.brand",        "brand",   brandReq,     params))
                .append(renderTextFilterFragment("a.model",        "model",   modelReq,     params))
                .append(renderTextFilterFragment("a.name",         "name",    modelNameReq, params))
                .append(renderTextFilterFragment("a.carrier",      "carrier", carrierReq,   params));

        int offset = page * pageSize;

        // ecoid2 is VARCHAR to match Mendix, but the ecoATM codes are always
        // numeric — sort by the numeric value so Mendix parity (75, 78, 113...)
        // holds instead of falling into lexicographic order (10003, 1005...).
        String sql = "SELECT a.id, a.ecoid2, a.merged_grade, a.brand, a.model, a.name, a.carrier,"
                + " a.dw_total_quantity, a.dw_avg_target_price,"
                + " a.total_quantity, a.avg_target_price, a.datawipe"
                + " FROM auctions.aggregated_inventory a "
                + where
                + " ORDER BY CAST(a.ecoid2 AS bigint) ASC, a.merged_grade ASC"
                + " LIMIT :limit OFFSET :offset";

        Query dataQ = em.createNativeQuery(sql);
        params.forEach(dataQ::setParameter);
        dataQ.setParameter("limit", pageSize);
        dataQ.setParameter("offset", offset);

        List<Object[]> rows = (List<Object[]>) dataQ.getResultList();

        String countSql = "SELECT COUNT(*) FROM auctions.aggregated_inventory a " + where;
        Query countQ = em.createNativeQuery(countSql);
        params.forEach(countQ::setParameter);
        long total = ((Number) countQ.getSingleResult()).longValue();

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
        // Non-DW KPIs exclude DW-only groups. Snowflake's upstream query wraps
        // TotalPayout and AvgTargetPrice in COALESCE(non-DW, DW), so DW-only
        // groups carry DW values in the non-DW columns. Including them would
        // double-count with the DW KPIs and diverge from Mendix Total-row math.
        String sql = """
                SELECT
                  COALESCE(SUM(a.total_quantity), 0)                                           AS total_qty,
                  COALESCE(SUM(a.total_payout)
                           FILTER (WHERE a.total_quantity > a.dw_total_quantity), 0)           AS total_payout,
                  CASE WHEN COALESCE(SUM(a.total_quantity - a.dw_total_quantity)
                                     FILTER (WHERE a.total_quantity > a.dw_total_quantity), 0) = 0 THEN 0
                       ELSE SUM(a.avg_target_price * (a.total_quantity - a.dw_total_quantity))
                              FILTER (WHERE a.total_quantity > a.dw_total_quantity)
                            / SUM(a.total_quantity - a.dw_total_quantity)
                              FILTER (WHERE a.total_quantity > a.dw_total_quantity) END        AS avg_target,
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
        String syncStatus = SyncStatusResponse.none().status();
        if (weekId != null) {
            // Dedicated EXISTS so "rows present but quantities all zero" still
            // reports hasInventory=true — totalQuantity alone would lie.
            hasInventory = Boolean.TRUE.equals(em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM auctions.aggregated_inventory "
                            + "WHERE week_id = CAST(:weekId AS bigint) AND is_deprecated = false)")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            hasAuction = Boolean.TRUE.equals(em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM auctions.auctions "
                            + "WHERE week_id = CAST(:weekId AS bigint))")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            isCurrentWeek = Boolean.TRUE.equals(em.createNativeQuery(
                    "SELECT EXISTS (SELECT 1 FROM mdm.week "
                            + "WHERE id = CAST(:weekId AS bigint) AND week_end_datetime > now())")
                    .setParameter("weekId", weekId)
                    .getSingleResult());

            syncStatus = syncLogRepository
                    .findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                            AggregatedInventorySnowflakeSyncService.SYNC_TYPE,
                            String.valueOf(weekId))
                    .map(SnowflakeSyncLog::getStatus)
                    .orElse(SyncStatusResponse.none().status());
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
