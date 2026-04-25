package com.ecoatm.salesplatform.dto;

import java.util.List;

public record QualifiedBuyerCodeAdminListResponse(List<QualifiedBuyerCodeAdminRow> rows, long total) {}
