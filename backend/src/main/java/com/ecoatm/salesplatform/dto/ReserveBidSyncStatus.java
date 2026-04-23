package com.ecoatm.salesplatform.dto;

import java.time.Duration;
import java.time.Instant;

public record ReserveBidSyncStatus(
        Instant lastSyncDatetime,
        Instant sourceMaxDatetime,
        Duration drift,
        String state) {

    public static final String IN_SYNC = "IN_SYNC";
    public static final String BEHIND_SOURCE = "BEHIND_SOURCE";
    public static final String NEVER_SYNCED = "NEVER_SYNCED";
}
