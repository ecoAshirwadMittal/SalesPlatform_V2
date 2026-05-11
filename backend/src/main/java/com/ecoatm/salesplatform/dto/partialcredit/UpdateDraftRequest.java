package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;

/**
 * PATCH payload for the wizard's step-to-step persistence. Every field is
 * nullable; only fields the wizard sends are applied. Reason flags drive
 * which sub-step the wizard renders next, so a partial PATCH that toggles
 * only {@code hasMissingDevice} is the common case.
 */
public record UpdateDraftRequest(
        Boolean hasMissingDevice,
        Boolean hasWrongDevice,
        Boolean hasEncumberedDevice,
        ShipmentDamaged shipmentDamaged) {}
