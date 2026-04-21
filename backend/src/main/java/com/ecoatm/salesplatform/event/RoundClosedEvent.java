package com.ecoatm.salesplatform.event;

/**
 * Published post-commit when a SchedulingAuction transitions
 * Started -> Closed via the lifecycle cron.
 *
 * Listeners must register @TransactionalEventListener(phase = AFTER_COMMIT).
 * A listener throwing does NOT roll back the round transition.
 */
public record RoundClosedEvent(long roundId, int round, long auctionId, long weekId) {}
