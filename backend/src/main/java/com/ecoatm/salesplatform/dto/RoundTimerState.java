package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record RoundTimerState(
        Instant now,
        Instant startsAt,
        Instant endsAt,
        long secondsUntilStart,
        long secondsUntilEnd,
        boolean active
) {}
