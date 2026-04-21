package com.ecoatm.salesplatform.dto;

import java.util.List;

public record AuctionListPageResponse(
        List<AuctionListRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
