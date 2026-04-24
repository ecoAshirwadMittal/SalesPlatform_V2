package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Single row of bid-data as returned by the bidder dashboard grid and the
 * single-row save endpoint. Phase 6B adds five MDM-sourced display columns
 * ({@code brand}, {@code model}, {@code modelName}, {@code carrier},
 * {@code added}) that are populated by the grid-load path via a JOIN to
 * {@code auctions.aggregated_inventory} and {@code mdm.*}. The single-row
 * save path returns {@code null} for these fields — the frontend retains the
 * last grid-load values for display-only columns when a save response
 * arrives.
 */
public record BidDataRow(
        long id,
        long bidRoundId,
        String ecoid,
        // Phase 6B MDM display fields — null when returned from the save endpoint.
        // `added` is ai.created_date (TIMESTAMPTZ) — the timestamp the SKU first
        // landed in aggregated_inventory. Grid renders it as a date.
        String brand,
        String model,
        String modelName,
        String carrier,
        Instant added,
        String mergedGrade,
        String buyerCodeType,
        Integer bidQuantity,
        BigDecimal bidAmount,
        BigDecimal targetPrice,
        Integer maximumQuantity,
        BigDecimal payout,
        Integer submittedBidQuantity,
        BigDecimal submittedBidAmount,
        Integer lastValidBidQuantity,
        BigDecimal lastValidBidAmount,
        Instant submittedDatetime,
        Instant changedDate
) {}
