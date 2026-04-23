package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(value = "eb.sync.reader", havingValue = "jdbc", matchIfMissing = false)
public class ReserveBidSnowflakeReader {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSnowflakeReader.class);

    private final JdbcTemplate snowflakeJdbc;

    public ReserveBidSnowflakeReader(@Qualifier("snowflakeDataSource") DataSource snowflakeDataSource) {
        this.snowflakeJdbc = new JdbcTemplate(snowflakeDataSource);
    }

    public Optional<Instant> fetchMaxUploadTime() {
        try {
            Instant t = snowflakeJdbc.queryForObject(
                    "SELECT MAX(LAST_UPDATE_DATETIME) FROM AUCTIONS.RESERVE_BID", Instant.class);
            return Optional.ofNullable(t);
        } catch (Exception ex) {
            log.warn("[eb-reader] fetchMaxUploadTime failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public List<ReserveBid> fetchAll() {
        try {
            return snowflakeJdbc.query(
                    "SELECT PRODUCT_ID, GRADE, BRAND, MODEL, BID, LAST_UPDATE_DATETIME, "
                  + "       LAST_AWARDED_MIN_PRICE, LAST_AWARDED_WEEK, BID_VALID_WEEK_DATE "
                  + "FROM AUCTIONS.RESERVE_BID",
                    (rs, rowNum) -> {
                        ReserveBid rb = new ReserveBid();
                        rb.setProductId(rs.getString("PRODUCT_ID"));
                        rb.setGrade(rs.getString("GRADE"));
                        rb.setBrand(rs.getString("BRAND"));
                        rb.setModel(rs.getString("MODEL"));
                        rb.setBid(rs.getBigDecimal("BID"));
                        java.sql.Timestamp ts = rs.getTimestamp("LAST_UPDATE_DATETIME");
                        if (ts != null) rb.setLastUpdateDatetime(ts.toInstant());
                        rb.setLastAwardedMinPrice(rs.getBigDecimal("LAST_AWARDED_MIN_PRICE"));
                        rb.setLastAwardedWeek(rs.getString("LAST_AWARDED_WEEK"));
                        rb.setBidValidWeekDate(rs.getString("BID_VALID_WEEK_DATE"));
                        return rb;
                    });
        } catch (Exception ex) {
            log.warn("[eb-reader] fetchAll failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
