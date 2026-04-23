package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Read-only projection of a {@code BuyerUserGuide} row — returned by the
 * admin GET endpoint and the history table.
 */
public record BuyerUserGuideMetadata(
        long id,
        String fileName,
        String contentType,
        long byteSize,
        long uploadedBy,
        String uploaderName,
        Instant uploadedAt,
        boolean isActive
) {}
