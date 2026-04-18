package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record WeekOption(
        Long id,
        String weekDisplay,
        Instant weekStartDateTime,
        Instant weekEndDateTime
) {}
