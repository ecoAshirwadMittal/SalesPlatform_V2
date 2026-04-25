package com.ecoatm.salesplatform.service.auctions.purchaseorder;

public class PurchaseOrderException extends RuntimeException {
    private final String code;

    public PurchaseOrderException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
