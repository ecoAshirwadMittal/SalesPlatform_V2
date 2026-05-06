package com.ecoatm.salesplatform.service.auctions.recalc;

/**
 * Outcome of a single recalc invocation. Returned by {@link BidRankingService#run}
 * and {@link TargetPriceRecalcService#run} so the controller can populate
 * {@code RecalcResponse} without re-fetching the {@code SchedulingAuction}.
 *
 * @param round         the closed round (1 or 2) that was processed
 * @param rowsAffected  number of rows updated by the bulk CTE UPDATE
 */
public record RecalcResult(int round, int rowsAffected) {}
