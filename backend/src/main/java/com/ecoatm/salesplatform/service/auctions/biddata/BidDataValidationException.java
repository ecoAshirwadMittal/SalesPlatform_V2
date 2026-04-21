package com.ecoatm.salesplatform.service.auctions.biddata;

public class BidDataValidationException extends RuntimeException {
    private final String code;

    public BidDataValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
