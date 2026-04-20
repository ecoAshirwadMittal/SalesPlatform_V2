package com.ecoatm.salesplatform.model.auctions;

/**
 * Mirrors Mendix {@code Enum_SchedulingAuctionStatus}. Values match the
 * {@code chk_sa_round_status} CHECK constraint in V58.
 */
public enum SchedulingAuctionStatus {
    Unscheduled,
    Scheduled,
    Started,
    Closed
}
