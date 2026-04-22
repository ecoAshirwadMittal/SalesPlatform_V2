package com.ecoatm.salesplatform.service.auctions.biddata;

public class BidDataSubmissionException extends RuntimeException {
    private final String code;

    public BidDataSubmissionException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
