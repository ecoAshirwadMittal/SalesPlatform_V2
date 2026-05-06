package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

/**
 * Response body for {@code POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers}.
 *
 * <p>See {@code docs/tasks/auction-r2-buyer-assignment-design.md §6.2} —
 * status mirrors {@code scheduling_auctions.r2_init_status} after the run.
 *
 * <p>{@code qualifiedCount} here means <em>qualified-but-not-special</em>:
 * the service's {@code R2BuyerAssignmentResult.qualifiedCount} is the union
 * of qualified and special-treatment, so the controller computes
 * {@code qualifiedNonSpecial = result.qualifiedCount() - result.specialTreatmentCount()}.
 * Total qualified = {@code qualifiedCount + specialTreatmentCount}.
 */
public record R2BuyerAssignmentResponse(
        long schedulingAuctionId,
        String status,                  // "SUCCESS" | "FAILED" | "SKIPPED"
        String error,                   // nullable
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        int qualifiedCount,             // QBCs with qualification_type=Qualified, is_special_treatment=false
        int specialTreatmentCount,      // QBCs with is_special_treatment=true
        int notQualifiedCount,          // QBCs with qualification_type=Not_Qualified
        int specialBidDataCount,        // bid_data rows seeded for special buyers
        long durationMs) {}
