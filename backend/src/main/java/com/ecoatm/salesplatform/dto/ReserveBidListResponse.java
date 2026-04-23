package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidListResponse(List<ReserveBidRow> rows, long total, int page, int size) {}
