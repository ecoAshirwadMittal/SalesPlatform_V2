package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

/**
 * Response body for {@code POST /api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3}.
 *
 * <p>{@code qualifiedCount} here means <em>qualified-but-not-special</em>:
 * the service's {@code R3PreProcessResult.qualifiedCount} is the union
 * of qualified and special-treatment, so the controller computes
 * {@code qualifiedNonSpecial = result.qualifiedCount() - result.specialTreatmentCount()}.
 * Total qualified = {@code qualifiedCount + specialTreatmentCount}.
 */
public record R3PreProcessResponse(
    long schedulingAuctionId,
    String status,
    String error,
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int reportRowCount,
    int deletedBidsCount,
    long durationMs
) {}
