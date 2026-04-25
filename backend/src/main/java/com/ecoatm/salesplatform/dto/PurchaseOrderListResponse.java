package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PurchaseOrderListResponse(
        List<PurchaseOrderRow> items,
        long total,
        int page,
        int size) {}
