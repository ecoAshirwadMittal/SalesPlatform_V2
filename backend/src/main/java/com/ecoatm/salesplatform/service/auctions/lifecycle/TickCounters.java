package com.ecoatm.salesplatform.service.auctions.lifecycle;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class TickCounters {
    int roundsStarted;
    int roundsClosed;
    int errorCount;
    final Set<Long> affectedAuctions = new HashSet<>();

    Map<String, Object> toJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("roundsStarted", roundsStarted);
        m.put("roundsClosed", roundsClosed);
        m.put("auctionsAffected", affectedAuctions.size());
        m.put("errorCount", errorCount);
        return m;
    }
}
