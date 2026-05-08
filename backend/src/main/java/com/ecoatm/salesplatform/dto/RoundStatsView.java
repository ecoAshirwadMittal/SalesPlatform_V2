package com.ecoatm.salesplatform.dto;

/**
 * Per-round summary stats rendered inline next to each round header on
 * the schedule editor (gap H5).
 *
 * <p>{@code totalQuantity} and {@code dwTotalQuantity} are auction-wide
 * (the weekly inventory snapshot doesn't change between rounds), so they
 * repeat across the three RoundStatsView rows. {@code buyerCount} is
 * per-round: count of {@code qualified_buyer_codes} rows for the SA
 * with {@code included = true}, or {@code null} when no QBCs exist
 * for that round yet (pre-R2-init / pre-R3-pre-process). Frontend
 * renders "All" for null.
 */
public record RoundStatsView(
        int round,
        Integer buyerCount,
        long totalQuantity,
        long dwTotalQuantity
) {}
