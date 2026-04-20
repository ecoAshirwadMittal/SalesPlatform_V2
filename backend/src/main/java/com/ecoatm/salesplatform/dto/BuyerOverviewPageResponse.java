package com.ecoatm.salesplatform.dto;

import java.util.List;

public record BuyerOverviewPageResponse(
        List<BuyerOverviewResponse> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
