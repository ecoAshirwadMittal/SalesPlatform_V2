package com.ecoatm.salesplatform.service.auctions.snowflake;

/**
 * Wraps a {@link java.sql.SQLException} from the Snowflake datasource so upstream
 * code can catch a single domain exception without depending on
 * {@code java.sql}.
 */
public final class SnowflakeReadException extends RuntimeException {
    public SnowflakeReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
