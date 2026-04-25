package com.ecoatm.salesplatform.dto;

import java.util.List;

public record BidReportPageResponse(
        List<BidReportRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
