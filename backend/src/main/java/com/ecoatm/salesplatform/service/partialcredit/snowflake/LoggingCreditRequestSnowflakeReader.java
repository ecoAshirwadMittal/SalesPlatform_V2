package com.ecoatm.salesplatform.service.partialcredit.snowflake;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Dev/test default — returns empty results and logs each call. Active when
 * {@code partial-credit.snowflake.reader} is absent or set to {@code logging},
 * which keeps unit tests, IT containers, and local dev free of any Snowflake
 * DataSource dependency. Flip to {@code jdbc} in prod (see
 * {@link JdbcCreditRequestSnowflakeReader}).
 */
@Component
@ConditionalOnProperty(
        value = "partial-credit.snowflake.reader",
        havingValue = "logging",
        matchIfMissing = true)
public class LoggingCreditRequestSnowflakeReader implements CreditRequestSnowflakeReader {

    private static final Logger log = LoggerFactory.getLogger(LoggingCreditRequestSnowflakeReader.class);

    @Override
    public boolean validateOrderForBuyer(String orderNumber, String buyerCode) {
        log.debug("[pc-logging-reader] validateOrderForBuyer order={} buyer={} — returning false", orderNumber, buyerCode);
        return false;
    }

    @Override
    public Optional<OrderHeader> getOrderHeader(String orderNumber, String buyerCode) {
        log.debug("[pc-logging-reader] getOrderHeader order={} buyer={} — returning empty", orderNumber, buyerCode);
        return Optional.empty();
    }

    @Override
    public List<ManifestLine> getOrderLines(String orderNumber, String buyerCode) {
        log.debug("[pc-logging-reader] getOrderLines order={} buyer={} — returning empty list", orderNumber, buyerCode);
        return Collections.emptyList();
    }

    @Override
    public List<OrderSummary> listShippedOrdersForBuyer(String buyerCode) {
        log.debug("[pc-logging-reader] listShippedOrdersForBuyer buyer={} — returning empty list", buyerCode);
        return Collections.emptyList();
    }

    @Override
    public Optional<ManifestLine> validateBarcodeInOrder(String barcode, String orderNumber, String buyerCode) {
        log.debug("[pc-logging-reader] validateBarcodeInOrder barcode={} order={} buyer={} — returning empty",
                barcode, orderNumber, buyerCode);
        return Optional.empty();
    }

    @Override
    public Optional<String> getShipStatusForBarcode(String barcode, String orderNumber) {
        log.debug("[pc-logging-reader] getShipStatusForBarcode barcode={} order={} — returning empty",
                barcode, orderNumber);
        return Optional.empty();
    }
}
