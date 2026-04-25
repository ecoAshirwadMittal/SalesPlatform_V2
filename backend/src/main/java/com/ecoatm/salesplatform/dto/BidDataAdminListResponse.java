package com.ecoatm.salesplatform.dto;

import java.util.List;

public record BidDataAdminListResponse(List<BidDataAdminRow> rows, long total) {}
