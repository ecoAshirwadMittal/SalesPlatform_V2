package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;

import java.time.Instant;

public record PurchaseOrderRow(
        long id,
        long weekFromId, String weekFromLabel,
        long weekToId,   String weekToLabel,
        String weekRangeLabel,
        PurchaseOrderLifecycleState state,
        int totalRecords,
        Instant poRefreshTimestamp,
        Instant changedDate,
        String changedByUsername) {}
