package com.ecoatm.salesplatform.event;

/**
 * Published by {@code AuctionScheduleService#unschedule} after the status
 * flip, consumed by the Snowflake audit listener (Phase F).
 */
public record AuctionUnscheduledEvent(Long auctionId) {}
