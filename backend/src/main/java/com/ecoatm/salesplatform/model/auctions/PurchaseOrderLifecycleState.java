package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.mdm.Week;

import java.time.LocalDate;
import java.time.ZoneOffset;

public enum PurchaseOrderLifecycleState {
    DRAFT,
    ACTIVE,
    CLOSED;

    public static PurchaseOrderLifecycleState derive(LocalDate today, Week from, Week to) {
        LocalDate fromStart = from.getWeekStartDateTime().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate toEnd = to.getWeekEndDateTime().atZone(ZoneOffset.UTC).toLocalDate();
        if (today.isBefore(fromStart)) return DRAFT;
        if (today.isAfter(toEnd))      return CLOSED;
        return ACTIVE;
    }
}
