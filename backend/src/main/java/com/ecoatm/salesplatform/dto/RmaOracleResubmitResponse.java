package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.Rma;

/**
 * Result of a {@code POST /api/v1/pws/rma/{rmaId}/resubmit-oracle} call — the
 * refreshed Oracle status after re-running the create. {@code successful=false}
 * with a populated {@code oracleRmaStatus} is a normal (non-error) response: the
 * resubmit ran but Oracle rejected it, so the admin sees the failure and can
 * retry. Intentionally omits the raw {@code oracle_json_response} body to keep
 * the surface small; the status/number/http code are enough for the UI.
 *
 * @param rmaId           the RMA that was resubmitted
 * @param successful      whether Oracle returned a {@code "00"} success code
 * @param oracleRmaStatus the Oracle return message (or failure reason)
 * @param oracleNumber    the Oracle RMA number on success (SIM value in local dev), else null
 * @param oracleHttpCode  the Oracle HTTP status code, if any
 */
public record RmaOracleResubmitResponse(
        Long rmaId,
        boolean successful,
        String oracleRmaStatus,
        String oracleNumber,
        Integer oracleHttpCode) {

    public static RmaOracleResubmitResponse from(Rma rma) {
        return new RmaOracleResubmitResponse(
                rma.getId(),
                Boolean.TRUE.equals(rma.getIsSuccessful()),
                rma.getOracleRmaStatus(),
                rma.getOracleNumber(),
                rma.getOracleHttpCode());
    }
}
