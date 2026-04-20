package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.integration.SyncRunLog;

import java.time.LocalDateTime;

public record SyncRunLogResponse(
        Long id,
        String syncType,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer totalItemsReceived,
        Integer devicesUpdated,
        Integer devicesMissing,
        String errorMessage,
        String triggeredBy,
        Long durationMs
) {
    public static SyncRunLogResponse from(SyncRunLog log) {
        Long duration = null;
        if (log.getStartTime() != null && log.getEndTime() != null) {
            duration = java.time.Duration.between(log.getStartTime(), log.getEndTime()).toMillis();
        }
        return new SyncRunLogResponse(
                log.getId(),
                log.getSyncType(),
                log.getStatus(),
                log.getStartTime(),
                log.getEndTime(),
                log.getTotalItemsReceived(),
                log.getDevicesUpdated(),
                log.getDevicesMissing(),
                log.getErrorMessage(),
                log.getTriggeredBy(),
                duration
        );
    }
}
