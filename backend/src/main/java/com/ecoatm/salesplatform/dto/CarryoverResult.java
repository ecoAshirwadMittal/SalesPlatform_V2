package com.ecoatm.salesplatform.dto;

/**
 * Result DTO for the carryover endpoint.
 *
 * <ul>
 *   <li>{@code copied} — rows whose {@code bid_quantity}/{@code bid_amount}
 *       were updated from the previous week's submitted values.
 *   <li>{@code notFound} — rows in the current round that had no matching
 *       {@code (ecoid, merged_grade)} row in the prior week's slice, or
 *       whose prior row had no submitted bid to copy.
 *   <li>{@code prevWeek} — the {@code week_display} of the week whose data
 *       was used as the source (e.g. {@code "2026 / Wk15"}), or {@code null}
 *       when no prior week was found (stub path) — the UI falls back to
 *       "from last week".
 * </ul>
 */
public record CarryoverResult(
        int copied,
        int notFound,
        String prevWeek
) {}
