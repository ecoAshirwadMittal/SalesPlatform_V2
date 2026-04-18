package com.ecoatm.salesplatform.dto;

import java.util.List;

public record AggregatedInventoryPageResponse(
        List<AggregatedInventoryRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
