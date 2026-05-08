package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.mdm.Week;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Two-state lifecycle: a PO is ACTIVE until its week range fully ends,
 * after which it transitions to CLOSED. There is no DRAFT —
 * "created but not yet started" is treated as ACTIVE because the
 * record exists from the moment of creation; the user can already
 * upload line items, edit the range, etc. before the start week
 * arrives. The earlier DRAFT state was an over-modelling that the
 * UI surfaced confusingly (a CLOSED-looking pill on perfectly-valid
 * future POs).
 */
public enum PurchaseOrderLifecycleState {
    ACTIVE,
    CLOSED;

    public static PurchaseOrderLifecycleState derive(LocalDate today, Week from, Week to) {
        LocalDate toEnd = to.getWeekEndDateTime().atZone(ZoneOffset.UTC).toLocalDate();
        return today.isAfter(toEnd) ? CLOSED : ACTIVE;
    }
}
