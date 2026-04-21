package com.ecoatm.salesplatform.dto;

import java.util.List;

public record SchedulingAuctionListPageResponse(
        List<SchedulingAuctionListRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
