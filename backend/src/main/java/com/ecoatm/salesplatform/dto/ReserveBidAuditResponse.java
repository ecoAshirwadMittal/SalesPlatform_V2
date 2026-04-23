package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidAuditResponse(List<ReserveBidAuditRow> rows, long total, int page, int size) {}
