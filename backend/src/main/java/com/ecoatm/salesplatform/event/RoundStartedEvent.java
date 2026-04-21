package com.ecoatm.salesplatform.event;

/**
 * Published post-commit when a SchedulingAuction transitions
 * Scheduled -> Started via the lifecycle cron.
 *
 * Listeners must register @TransactionalEventListener(phase = AFTER_COMMIT).
 * A listener throwing does NOT roll back the round transition.
 */
public record RoundStartedEvent(long roundId, int round, long auctionId, long weekId) {}
