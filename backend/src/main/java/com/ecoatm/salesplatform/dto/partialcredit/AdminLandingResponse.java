package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminStatusCounters;

import java.util.List;

/**
 * Wire envelope for {@code GET /api/v1/admin/partial-credit}. Carries the
 * paged row list, the four status-chip counters, and the total count so
 * the frontend can render the pagination footer without a second
 * round-trip.
 */
public record AdminLandingResponse(
        List<AdminCreditRequestRow> rows,
        AdminStatusCounters counters,
        long total) {
}
