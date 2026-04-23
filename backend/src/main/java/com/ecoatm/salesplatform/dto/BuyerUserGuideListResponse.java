package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * Response body for {@code GET /api/v1/admin/buyer-user-guide}.
 *
 * <p>{@code active} is {@code null} when no guide has been uploaded yet.
 * {@code history} is the last 10 non-deleted uploads ordered newest first.
 */
public record BuyerUserGuideListResponse(
        BuyerUserGuideMetadata active,
        List<BuyerUserGuideMetadata> history
) {}
