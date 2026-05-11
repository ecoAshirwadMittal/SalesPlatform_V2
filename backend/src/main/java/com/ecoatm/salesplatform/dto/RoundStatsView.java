package com.ecoatm.salesplatform.dto;

/**
 * Per-round summary stats rendered inline next to each round header on
 * the schedule editor (gap H5).
 *
 * <p>{@code totalQuantity} and {@code dwTotalQuantity} mirror Mendix's
 * {@code ACT_LoadBuyerTotals}: global count of {@code BuyerCode} rows
 * with an Active linked Buyer, and the subset whose
 * {@code buyer_code_type = 'Data_Wipe'}. Names retain "Quantity" for
 * API stability across the FE; semantically they are buyer-code counts,
 * not inventory units. Both numbers repeat across the three rows.
 *
 * <p>{@code buyerCount} is per-round: count of
 * {@code qualified_buyer_codes} rows for the SA with
 * {@code included = true}, or {@code null} when no QBCs exist for that
 * round yet (pre-R2-init / pre-R3-pre-process, or any time the auction
 * is still Unscheduled and no SA rows exist). Frontend renders "All"
 * for null.
 */
public record RoundStatsView(
        int round,
        Integer buyerCount,
        long totalQuantity,
        long dwTotalQuantity
) {}
