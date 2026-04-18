package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * KPI payload for the admin aggregated-inventory page.
 *
 * <p>The four trailing helper flags ({@code hasInventory}, {@code hasAuction},
 * {@code isCurrentWeek}, {@code syncStatus}) replace Mendix's separate
 * {@code AggInventoryHelper} microflow so the page can drive the "Create
 * Auction" button and the Snowflake sync banner from a single call.
 */
public record AggregatedInventoryTotalsResponse(
        int totalQuantity,
        BigDecimal totalPayout,
        BigDecimal averageTargetPrice,
        int dwTotalQuantity,
        BigDecimal dwTotalPayout,
        BigDecimal dwAverageTargetPrice,
        Instant lastSyncedAt,
        boolean hasInventory,
        boolean hasAuction,
        boolean isCurrentWeek,
        String syncStatus
) {}
