package com.ecoatm.salesplatform.event;

/**
 * Published by {@code AuctionScheduleService#saveSchedule} after a successful
 * schedule write, consumed by the Snowflake audit listener.
 *
 * <p>The listener is wired no-op in Phase C — the actual push to Snowflake
 * lands in Phase F (see {@code docs/tasks/auction-scheduling-plan.md}).
 */
public record AuctionScheduledEvent(Long auctionId) {}
