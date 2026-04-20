package com.ecoatm.salesplatform.model.auctions;

/**
 * Mirrors Mendix {@code Enum_AuctionStatus}. Values match the
 * {@code chk_auctions_status} CHECK constraint in V58.
 */
public enum AuctionStatus {
    Unscheduled,
    Scheduled,
    Started,
    Closed
}
