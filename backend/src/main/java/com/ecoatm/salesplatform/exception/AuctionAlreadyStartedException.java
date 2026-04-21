package com.ecoatm.salesplatform.exception;

/**
 * Thrown when an admin attempts a state transition (reschedule, unschedule,
 * delete) on an auction that already has at least one round in
 * {@code SchedulingAuctionStatus.Started}.
 *
 * <p>Mendix parity: {@code ACT_UnscheduleAuction} shows the warning
 * "Auction has started. Unscheduling the auction is not available." — the
 * same invariant applies to Save (reschedule) and Delete. Mapped to HTTP
 * 409 by {@link GlobalExceptionHandler}.
 */
public class AuctionAlreadyStartedException extends RuntimeException {

    public AuctionAlreadyStartedException() {
        super("Auction has started; this action is not available.");
    }

    public AuctionAlreadyStartedException(String message) {
        super(message);
    }
}
