package com.ecoatm.salesplatform.event;

import java.util.List;

public record ReserveBidChangedEvent(List<Long> changedIds, Action action) {

    public enum Action { UPSERT, DELETE }
}
