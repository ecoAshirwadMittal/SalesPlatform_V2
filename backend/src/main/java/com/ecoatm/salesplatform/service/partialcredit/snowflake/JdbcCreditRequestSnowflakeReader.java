package com.ecoatm.salesplatform.service.partialcredit.snowflake;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * JDBC implementation backed by the Snowflake DataSource bean (see
 * {@code SnowflakeDataSourceConfig}). Selected via
 * {@code partial-credit.snowflake.reader=jdbc}.
 *
 * <p>All queries target {@code AUCTIONS.VW_SALE_ORDER_SHIPMENT}, the view
 * confirmed by the SPKB-3665 spike on 2026-05-11. {@code BUYER_CODE} on the
 * view matches {@code BuyerCode.code} in Postgres exactly, so no translation
 * layer is needed.
 *
 * <p>SQL exceptions are logged + swallowed (the reader degrades to "no
 * results") because the wizard's UX is to surface a single user-friendly
 * error message rather than a stack trace.
 */
@Component
@ConditionalOnProperty(
        value = "partial-credit.snowflake.reader",
        havingValue = "jdbc",
        matchIfMissing = false)
public class JdbcCreditRequestSnowflakeReader implements CreditRequestSnowflakeReader {

    private static final Logger log = LoggerFactory.getLogger(JdbcCreditRequestSnowflakeReader.class);

    /** Default window for the order picker — 30 days back from today. */
    private static final int SHIPPED_WINDOW_DAYS = 30;

    private final JdbcTemplate snowflakeJdbc;

    public JdbcCreditRequestSnowflakeReader(
            @Qualifier("snowflakeDataSource") DataSource snowflakeDataSource) {
        this.snowflakeJdbc = new JdbcTemplate(snowflakeDataSource);
    }

    @Override
    public boolean validateOrderForBuyer(String orderNumber, String buyerCode) {
        try {
            Integer count = snowflakeJdbc.queryForObject(
                    """
                    SELECT 1 FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE ORDER_NUMBER = ? AND BUYER_CODE = ?
                    LIMIT 1
                    """,
                    Integer.class,
                    orderNumber, buyerCode);
            return count != null;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        } catch (Exception ex) {
            log.warn("[pc-reader] validateOrderForBuyer failed order={} buyer={}: {}",
                    orderNumber, buyerCode, ex.getMessage());
            return false;
        }
    }

    @Override
    public Optional<OrderHeader> getOrderHeader(String orderNumber, String buyerCode) {
        try {
            return Optional.ofNullable(snowflakeJdbc.queryForObject(
                    """
                    SELECT MIN(PARTY_NAME) AS PARTY_NAME,
                           MIN(ORDER_CREATED_DATE) AS ORDER_CREATED_DATE,
                           MIN(ORDER_SHIPPED_DATE) AS ORDER_SHIPPED_DATE
                    FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE ORDER_NUMBER = ? AND BUYER_CODE = ?
                    """,
                    (rs, rowNum) -> {
                        // queryForObject returns one row even when the order
                        // is unknown — every column comes back null in that
                        // case. Treat that as "no header" rather than an
                        // OrderHeader with null fields.
                        String partyName = rs.getString("PARTY_NAME");
                        Timestamp created = rs.getTimestamp("ORDER_CREATED_DATE");
                        Timestamp shipped = rs.getTimestamp("ORDER_SHIPPED_DATE");
                        if (partyName == null && created == null && shipped == null) {
                            return null;
                        }
                        return new OrderHeader(
                                orderNumber,
                                buyerCode,
                                partyName,
                                created == null ? null : created.toInstant(),
                                shipped == null ? null : shipped.toInstant());
                    },
                    orderNumber, buyerCode));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("[pc-reader] getOrderHeader failed order={} buyer={}: {}",
                    orderNumber, buyerCode, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ManifestLine> getOrderLines(String orderNumber, String buyerCode) {
        try {
            return snowflakeJdbc.query(
                    """
                    SELECT BARCODE, IMEI, DEVICE_BRAND, DEVICE_MODEL, GRADE,
                           ECOATM_CODE, BOX_NUMBER, AMOUNT, TRACKING_NUMBER
                    FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE ORDER_NUMBER = ? AND BUYER_CODE = ?
                    """,
                    MANIFEST_LINE_MAPPER,
                    orderNumber, buyerCode);
        } catch (Exception ex) {
            log.warn("[pc-reader] getOrderLines failed order={} buyer={}: {}",
                    orderNumber, buyerCode, ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<OrderSummary> listShippedOrdersForBuyer(String buyerCode) {
        Instant cutoff = Instant.now().minus(SHIPPED_WINDOW_DAYS, ChronoUnit.DAYS);
        try {
            return snowflakeJdbc.query(
                    """
                    SELECT ORDER_NUMBER,
                           MIN(PARTY_NAME) AS PARTY_NAME,
                           MIN(ORDER_CREATED_DATE) AS ORDER_CREATED_DATE,
                           MIN(ORDER_SHIPPED_DATE) AS ORDER_SHIPPED_DATE,
                           SUM(AMOUNT) AS TOTAL_AMOUNT,
                           COUNT(*) AS LINE_COUNT
                    FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE BUYER_CODE = ? AND ORDER_SHIPPED_DATE >= ?
                    GROUP BY ORDER_NUMBER
                    ORDER BY MIN(ORDER_SHIPPED_DATE) DESC
                    """,
                    (rs, rowNum) -> new OrderSummary(
                            rs.getString("ORDER_NUMBER"),
                            buyerCode,
                            rs.getString("PARTY_NAME"),
                            timestampToInstant(rs.getTimestamp("ORDER_CREATED_DATE")),
                            timestampToInstant(rs.getTimestamp("ORDER_SHIPPED_DATE")),
                            rs.getBigDecimal("TOTAL_AMOUNT"),
                            rs.getInt("LINE_COUNT")),
                    buyerCode, Timestamp.from(cutoff));
        } catch (Exception ex) {
            log.warn("[pc-reader] listShippedOrdersForBuyer failed buyer={}: {}",
                    buyerCode, ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<ManifestLine> validateBarcodeInOrder(String barcode, String orderNumber, String buyerCode) {
        try {
            return Optional.ofNullable(snowflakeJdbc.queryForObject(
                    """
                    SELECT BARCODE, IMEI, DEVICE_BRAND, DEVICE_MODEL, GRADE,
                           ECOATM_CODE, BOX_NUMBER, AMOUNT, TRACKING_NUMBER
                    FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE BARCODE = ? AND ORDER_NUMBER = ? AND BUYER_CODE = ?
                    LIMIT 1
                    """,
                    MANIFEST_LINE_MAPPER,
                    barcode, orderNumber, buyerCode));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("[pc-reader] validateBarcodeInOrder failed barcode={} order={}: {}",
                    barcode, orderNumber, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getShipStatusForBarcode(String barcode, String orderNumber) {
        try {
            String tracking = snowflakeJdbc.queryForObject(
                    """
                    SELECT TRACKING_NUMBER
                    FROM AUCTIONS.VW_SALE_ORDER_SHIPMENT
                    WHERE BARCODE = ? AND ORDER_NUMBER = ?
                    LIMIT 1
                    """,
                    String.class,
                    barcode, orderNumber);
            // Snowflake returns the column as null when the barcode is on the
            // order but no tracking was assigned — that means "NOT_SHIPPED".
            // Caller maps non-blank → SHIPPED, blank/null → NOT_SHIPPED,
            // empty Optional → UNKNOWN (barcode not on order).
            return Optional.of(tracking == null ? "" : tracking);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("[pc-reader] getShipStatusForBarcode failed barcode={} order={}: {}",
                    barcode, orderNumber, ex.getMessage());
            return Optional.empty();
        }
    }

    private static Instant timestampToInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }

    private static final RowMapper<ManifestLine> MANIFEST_LINE_MAPPER = (rs, rowNum) -> new ManifestLine(
            rs.getString("BARCODE"),
            rs.getString("IMEI"),
            rs.getString("DEVICE_BRAND"),
            rs.getString("DEVICE_MODEL"),
            rs.getString("GRADE"),
            rs.getString("ECOATM_CODE"),
            rs.getString("BOX_NUMBER"),
            rs.getBigDecimal("AMOUNT"),
            rs.getString("TRACKING_NUMBER"));
}
