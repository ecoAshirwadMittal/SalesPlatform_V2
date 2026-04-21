package com.ecoatm.salesplatform.service.auctions.lifecycle;

import java.util.Map;

public record TickResult(Map<String, Object> counters, int errorCount) {
}
