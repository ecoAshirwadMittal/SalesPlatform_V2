package com.ecoatm.salesplatform.event;

public record PurchaseOrderChangedEvent(long purchaseOrderId, Action action) {
    public enum Action { UPSERT, DELETE }
}
