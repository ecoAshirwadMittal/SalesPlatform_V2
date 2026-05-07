package com.ecoatm.salesplatform.exception;

/**
 * Thrown by RANKING / TARGET_PRICE / R2_INIT / R3_PREPROCESS / R3_INIT services when their
 * state-flip UPDATE matches 0 rows — meaning another caller (cron tick or admin endpoint)
 * already flipped the status to RUNNING. Maps to HTTP 409 in
 * GlobalExceptionHandler.
 */
public class RecalcAlreadyRunningException extends RuntimeException {

    public enum Process { RANKING, TARGET_PRICE, R2_INIT, R3_PREPROCESS, R3_INIT }

    private final Process process;
    private final long schedulingAuctionId;

    public RecalcAlreadyRunningException(Process process, long schedulingAuctionId) {
        super(process + " recalc already running: schedulingAuctionId=" + schedulingAuctionId);
        this.process = process;
        this.schedulingAuctionId = schedulingAuctionId;
    }

    public Process getProcess() { return process; }
    public long getSchedulingAuctionId() { return schedulingAuctionId; }
}
