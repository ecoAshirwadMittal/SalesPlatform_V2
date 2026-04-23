package com.ecoatm.salesplatform.service.auctions.reservebid;

public class ReserveBidException extends RuntimeException {
    private final String code;

    public ReserveBidException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() { return code; }
}
