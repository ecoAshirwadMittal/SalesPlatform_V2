package com.ecoatm.salesplatform.service.auctions.r1init;

public class SchedulingAuctionNotFoundException extends RuntimeException {
    private final Long schedulingAuctionId;

    public SchedulingAuctionNotFoundException(Long schedulingAuctionId) {
        super("SchedulingAuction not found: id=" + schedulingAuctionId);
        this.schedulingAuctionId = schedulingAuctionId;
    }

    public Long getSchedulingAuctionId() {
        return schedulingAuctionId;
    }
}
