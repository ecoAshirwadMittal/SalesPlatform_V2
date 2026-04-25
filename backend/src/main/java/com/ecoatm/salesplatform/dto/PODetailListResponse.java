package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PODetailListResponse(
        List<PODetailRow> items,
        long total,
        int page,
        int size) {}
