package com.ecoatm.salesplatform.service.partialcredit.snowflake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Reads wholesale-order manifest data from the authoritative Snowflake source
 * (view {@code AUCTIONS.VW_SALE_ORDER_SHIPMENT}) so the partial-credit wizard
 * can validate orders, list shipped orders, classify barcodes, and resolve
 * ship-status — all live, with no local snapshot tree.
 *
 * <p>Two implementations exist: {@link LoggingCreditRequestSnowflakeReader} is
 * the dev/test default (returns empty results and logs each call, so unit
 * tests do not require a live Snowflake DataSource) and
 * {@link JdbcCreditRequestSnowflakeReader} is the production wiring (selected
 * by {@code partial-credit.snowflake.reader=jdbc}). The shape mirrors
 * {@code ReserveBidSnowflakeReader} exactly.
 */
public interface CreditRequestSnowflakeReader {

    /**
     * Returns true when at least one manifest row exists for the
     * ({@code orderNumber}, {@code buyerCode}) pair. Used at wizard Step 1
     * "Next" to fail-fast on orders that do not belong to the current buyer.
     */
    boolean validateOrderForBuyer(String orderNumber, String buyerCode);

    /**
     * Fetches the denormalised order header: party name + auction-week date
     * ({@code ORDER_CREATED_DATE}) + ship date ({@code ORDER_SHIPPED_DATE}).
     * Written to {@code credit_requests} at wizard Step 1 so the request row
     * is self-contained after submit.
     */
    Optional<OrderHeader> getOrderHeader(String orderNumber, String buyerCode);

    /**
     * Lists every line on the manifest for the order. Drives the
     * Wrong-Device step's "Expected Device" table and powers the bulk
     * barcode reconciliation used by {@code CreditRequestValidator}.
     */
    List<ManifestLine> getOrderLines(String orderNumber, String buyerCode);

    /**
     * Lists orders shipped to the buyer within the last 30 days
     * ({@code ORDER_SHIPPED_DATE >= today - 30}). Drives the wizard Step 1
     * order picker on the Figma "Start Credit Request" frame.
     */
    List<OrderSummary> listShippedOrdersForBuyer(String buyerCode);

    /**
     * Looks up a single barcode in the context of an order + buyer. Returns
     * the manifest line if the barcode is on the order; empty otherwise.
     * Drives barcode-paste reconciliation per Missing/Wrong/Encumbered step.
     */
    Optional<ManifestLine> validateBarcodeInOrder(String barcode, String orderNumber, String buyerCode);

    /**
     * Returns the manifest's {@code TRACKING_NUMBER} presence for a single
     * barcode, mapped to the {@link com.ecoatm.salesplatform.model.partialcredit.enums.ShipStatus}
     * enum: non-null tracking → {@code SHIPPED}; null → {@code NOT_SHIPPED};
     * barcode absent → {@code UNKNOWN}. Cached on
     * {@code missing_device_lines.ship_status} at submit time.
     */
    Optional<String> getShipStatusForBarcode(String barcode, String orderNumber);

    /**
     * Denormalised header — what the order picker shows and what the
     * wizard writes onto {@code credit_requests} at Step 1.
     */
    record OrderHeader(
            String orderNumber,
            String buyerCode,
            String partyName,
            Instant orderCreatedDate,
            Instant orderShippedDate) {}

    /**
     * Summary row used by the order picker on Step 1. Same shape as
     * {@link OrderHeader} plus the precomputed total amount so the Figma
     * "Order #X shipped on Y — $Z" string can render without N+1 lookups.
     */
    record OrderSummary(
            String orderNumber,
            String buyerCode,
            String partyName,
            Instant orderCreatedDate,
            Instant orderShippedDate,
            BigDecimal totalAmount,
            int lineCount) {}

    /**
     * One manifest line. Fields mirror the columns we denormalise onto
     * {@code missing_device_lines}, {@code wrong_device_lines}, and
     * {@code encumbered_device_lines} at submit time (see
     * partial-credit-implementation-plan.md Appendix A).
     */
    record ManifestLine(
            String barcode,
            String imei,
            String deviceBrand,
            String deviceModel,
            String grade,
            String ecoatmCode,
            String boxNumber,
            BigDecimal amount,
            String trackingNumber) {}
}
