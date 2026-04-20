package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * One row materialized from the Snowflake aggregated-inventory query.
 *
 * <p>The upstream {@code UNION ALL} totals row (where {@code DEVICE_ID='Total'})
 * is filtered before rows reach this type — see
 * {@link SnowflakeAggInventoryReader}. Per ADR 2026-04-17, totals are computed
 * at read-time in the Postgres service layer, not synced from Snowflake.
 *
 * <p>The record mirrors the column list in the aggregated-inventory sync plan
 * §5.4. {@code AvgMargin} and {@code DWAvgMargin} are intentionally dropped
 * from the entity and therefore also from this row — the sync never stores
 * them. {@code maxUploadTime} is kept for per-page watermark bookkeeping by
 * the reader; downstream services consume it only for logging.
 *
 * <p>All {@link BigDecimal} fields are preserved verbatim — {@code null} is
 * kept as {@code null} rather than coerced to zero, so the service layer can
 * decide whether a missing value means "no data" or "explicit zero".
 */
public record SnowflakeAggInventoryRow(
        String ecoId,
        String deviceId,
        String mergedGrade,
        boolean datawipe,
        Instant maxUploadTime,
        BigDecimal avgTargetPrice,
        BigDecimal avgPayout,
        BigDecimal totalPayout,
        int totalQuantity,
        BigDecimal dwAvgTargetPrice,
        BigDecimal dwAvgPayout,
        BigDecimal dwTotalPayout,
        int dwTotalQuantity,
        BigDecimal round1TargetPrice,
        BigDecimal round1TargetPriceDw,
        String name,
        String model,
        String brand,
        String carrier,
        String category,
        Instant createdAt) {}
