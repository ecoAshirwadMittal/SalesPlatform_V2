package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * Response envelope for {@code GET /api/v1/admin/round3-reports?weekId=…}.
 *
 * <p>Contains the rows plus the resolved week id (echoed back so clients
 * can confirm what they queried — useful when the admin UI binds the
 * dropdown selection to the response). {@code count} is denormalised so
 * the UI doesn't recompute {@code rows.length} when it just wants a
 * "0 rows" empty-state badge.
 */
public record Round3ReportResponse(
        Long weekId,
        int count,
        List<Round3ReportRow> rows
) {
    public static Round3ReportResponse of(Long weekId, List<Round3ReportRow> rows) {
        return new Round3ReportResponse(weekId, rows.size(), rows);
    }
}
