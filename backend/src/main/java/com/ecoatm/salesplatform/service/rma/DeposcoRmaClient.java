package com.ecoatm.salesplatform.service.rma;

import java.util.Optional;

/**
 * Client abstraction over the Deposco reverse-logistics "RMA status" API
 * (legacy {@code EcoATM_PWSIntegration} Deposco RMA service consumed by
 * {@code ACT_UpdateRMAFromDeposco} / {@code SUB_SyncRMAStatus}).
 *
 * <p>No real Deposco reverse-logistics endpoint exists yet, so the only
 * shipped implementation is {@link LoggingDeposcoRmaClient} — a no-op default
 * that logs the intended poll and reports no update. A real HTTP client can be
 * added later (selected via {@code rma.deposco-sync.client}) without touching
 * the sync service, mirroring the app's Snowflake {@code Logging*Writer} /
 * {@code Jdbc*Writer} default-selection idiom.
 */
public interface DeposcoRmaClient {

    /**
     * Ask Deposco for the current status of the RMA identified by its Oracle
     * order number.
     *
     * @param oracleNumber the RMA's Oracle order number (never {@code null}
     *                     when called by the sync job)
     * @return the reported status, or {@link Optional#empty()} when Deposco
     *         has no update for this RMA
     */
    Optional<DeposcoRmaStatus> fetchStatus(String oracleNumber);
}
