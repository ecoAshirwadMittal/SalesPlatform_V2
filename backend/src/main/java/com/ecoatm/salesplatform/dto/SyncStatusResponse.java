package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import java.time.Instant;

/** Response for GET /admin/inventory/weeks/{weekId}/sync/status. */
public record SyncStatusResponse(
        String status,
        Instant lastSyncedAt,
        Integer rowsUpserted,
        String errorMessage) {

    /** Sentinel returned when no log row exists for the requested week. */
    public static SyncStatusResponse none() {
        return new SyncStatusResponse("NONE", null, null, null);
    }

    /**
     * Maps a {@link SnowflakeSyncLog} entity to this DTO.
     * {@code lastSyncedAt} uses {@code finishedAt} when present, falling back to
     * {@code startedAt} for rows that are still in a terminal-less {@code STARTED} state.
     */
    public static SyncStatusResponse from(SnowflakeSyncLog log) {
        Instant ts = log.getFinishedAt() != null ? log.getFinishedAt() : log.getStartedAt();
        return new SyncStatusResponse(
                log.getStatus(),
                ts,
                log.getRowsUpserted(),
                log.getErrorMessage());
    }
}
