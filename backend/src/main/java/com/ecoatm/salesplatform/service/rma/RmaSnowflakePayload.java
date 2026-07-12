package com.ecoatm.salesplatform.service.rma;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable snapshot of an RMA (header + line items) pushed to Snowflake on a
 * review-completion. This is the modern port of the {@code ExportXml} step in
 * the legacy {@code SUB_SendRMADetailsToSnowflake} microflow, which serialised
 * the full {@code EcoATM_RMA.RMA} aggregate (with its {@code RMAItem}
 * association) to JSON before calling the {@code AUCTIONS.UPSERT_RMA_DATA(?)}
 * stored procedure.
 *
 * <p>A snapshot record (rather than the {@link com.ecoatm.salesplatform.model.pws.Rma}
 * JPA entity) is passed to the writer for two reasons, both mirroring the
 * established {@code PurchaseOrderSnowflakePayload} pattern:
 * <ul>
 *   <li><b>No lazy-loading on the async thread.</b> The push runs on the
 *       {@code snowflakeExecutor} after the review transaction has committed and
 *       with {@code spring.jpa.open-in-view=false}; handing the writer a plain
 *       record avoids a {@code LazyInitializationException} when the logging
 *       writer serialises it.</li>
 *   <li><b>Immutability.</b> The snapshot cannot drift after it is built.</li>
 * </ul>
 *
 * @param rmaId             {@code pws.rma.id}
 * @param rmaNumber         human RMA number ({@code rma.number})
 * @param buyerCodeId       {@code buyer_mgmt.buyer_codes} FK on the RMA
 * @param buyerCode         resolved buyer-code string (Snowflake reporting keys
 *                          on the code, not the surrogate id); may be {@code null}
 *                          if the code can no longer be resolved
 * @param systemStatus      terminal system status ({@code Approved} / {@code Declined})
 * @param oracleRmaStatus   Oracle return message captured by the create listener
 * @param oracleNumber      Oracle order number (present only on a successful create)
 * @param oracleId          Oracle order id (present only on a successful create)
 * @param oracleHttpCode    Oracle HTTP status code
 * @param successful        {@code is_successful} — the Oracle-create outcome
 * @param requestSkus       requested distinct SKUs
 * @param requestQty        requested unit quantity
 * @param requestSalesTotal requested sales total
 * @param approvedSkus      approved distinct SKUs
 * @param approvedQty       approved unit quantity
 * @param approvedSalesTotal approved sales total
 * @param approvedCount     count of approved lines
 * @param declinedCount     count of declined lines
 * @param reviewedByUserId  reviewer ({@code identity.users.id})
 * @param submittedByUserId submitter ({@code identity.users.id})
 * @param submittedDate     when the RMA was submitted
 * @param approvalDate      when the review completed (approval branch)
 * @param reviewCompletedOn when the review transitioned to terminal
 * @param pushTimestamp     wall-clock instant the snapshot was built for the push
 * @param items             per-line snapshots
 */
public record RmaSnowflakePayload(
        long rmaId,
        String rmaNumber,
        Long buyerCodeId,
        String buyerCode,
        String systemStatus,
        String oracleRmaStatus,
        String oracleNumber,
        String oracleId,
        Integer oracleHttpCode,
        Boolean successful,
        Integer requestSkus,
        Integer requestQty,
        BigDecimal requestSalesTotal,
        Integer approvedSkus,
        Integer approvedQty,
        BigDecimal approvedSalesTotal,
        Integer approvedCount,
        Integer declinedCount,
        Long reviewedByUserId,
        Long submittedByUserId,
        LocalDateTime submittedDate,
        LocalDateTime approvalDate,
        LocalDateTime reviewCompletedOn,
        Instant pushTimestamp,
        List<ItemPayload> items) {

    /**
     * One RMA line in the Snowflake snapshot. Mirrors the {@code RMAItem} fields
     * the legacy export carried: the device/IMEI identity, the source order, the
     * per-line money, and the review decision.
     *
     * @param itemId        {@code pws.rma_item.id}
     * @param deviceId      {@code mdm.device} FK
     * @param imei          device IMEI / serial
     * @param orderNumber   originating order number
     * @param salePrice     per-line sale price
     * @param returnReason  buyer-declared return reason
     * @param status        review status ({@code Approve} / {@code Decline})
     * @param statusDisplay display label ({@code Approved} / {@code Declined})
     * @param declineReason reviewer decline reason (present only on declined lines)
     */
    public record ItemPayload(
            long itemId,
            Long deviceId,
            String imei,
            String orderNumber,
            BigDecimal salePrice,
            String returnReason,
            String status,
            String statusDisplay,
            String declineReason) {
    }
}
