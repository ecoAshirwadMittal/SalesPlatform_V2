package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import java.util.List;

public class PurchaseOrderValidationException extends PurchaseOrderException {
    private final List<String> details;

    public PurchaseOrderValidationException(String code, String message, List<String> details) {
        super(code, message);
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public List<String> getDetails() { return details; }
}
