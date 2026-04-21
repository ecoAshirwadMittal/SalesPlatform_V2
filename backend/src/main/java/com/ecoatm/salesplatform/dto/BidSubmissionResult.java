package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record BidSubmissionResult(
        long bidRoundId,
        int rowCount,
        Instant submittedDatetime,
        boolean resubmit
) {}
