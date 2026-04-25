package com.ecoatm.salesplatform.dto;

import java.util.List;

public record QualifiedBuyerCodePageResponse(
        List<QualifiedBuyerCodeRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
