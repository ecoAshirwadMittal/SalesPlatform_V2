package com.ecoatm.salesplatform.service.auctions.lifecycle;

/**
 * Thrown when a round picked up by findIdsToClose/findIdsToStart no longer
 * matches the predicate after the FOR UPDATE re-read. Benign — indicates
 * another tick (or admin action) already transitioned the row.
 */
public class RoundAlreadyTransitionedException extends RuntimeException {
    public RoundAlreadyTransitionedException(long roundId) {
        super("Round already transitioned: id=" + roundId);
    }
}
