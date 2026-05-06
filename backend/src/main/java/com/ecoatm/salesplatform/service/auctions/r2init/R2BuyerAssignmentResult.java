package com.ecoatm.salesplatform.service.auctions.r2init;

/**
 * Result of {@link R2BuyerAssignmentService#run(long)}.
 *
 * <p>Counts:
 * <ul>
 *   <li>{@code qualifiedCount} — distinct buyer codes with
 *       {@code qualification_type=Qualified} (union of regular qualified
 *       and special-treatment sets).</li>
 *   <li>{@code specialTreatmentCount} — subset of {@code qualifiedCount}
 *       where {@code is_special_treatment=TRUE}.</li>
 *   <li>{@code notQualifiedCount} — total inserted rows minus
 *       {@code qualifiedCount}.</li>
 *   <li>{@code specialBidDataCount} — total {@code bid_data} rows seeded
 *       across all special-treatment buyer codes.</li>
 *   <li>{@code durationMs} — wall-clock duration of phases 3-6.</li>
 *   <li>{@code skipped} — TRUE iff the config gate
 *       ({@code calculate_round2_buyer_participation=FALSE}) short-circuited
 *       the process; all counts will be zero in that case.</li>
 * </ul>
 */
public record R2BuyerAssignmentResult(
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int specialBidDataCount,
    long durationMs,
    boolean skipped
) {}
