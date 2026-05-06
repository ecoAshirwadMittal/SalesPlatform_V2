package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "target-price-writer", havingValue = "jdbc")
public class JdbcTargetPriceSnowflakeWriter implements TargetPriceSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final JdbcTemplate localJdbc;

    public JdbcTargetPriceSnowflakeWriter(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc,
                                          JdbcTemplate localJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
        this.localJdbc = localJdbc;
    }

    @Override
    public void pushTargetPrices(long weekId, int targetRound) {
        // Active-row swap: mark prior slice IS_ACTIVE = FALSE, then INSERT
        // new slice rows with incremented RUN_VERSION + IS_ACTIVE = TRUE.
        // RUN_VERSION = (existing max for this (week, round)) + 1.

        Integer nextRunVersion = snowflakeJdbc.queryForObject("""
            SELECT COALESCE(MAX(RUN_VERSION), 0) + 1
              FROM AUCTIONS.TARGET_PRICE_AUDIT
             WHERE WEEK_ID = ? AND ROUND = ?
            """, Integer.class, weekId, targetRound);

        snowflakeJdbc.update("""
            UPDATE AUCTIONS.TARGET_PRICE_AUDIT
               SET IS_ACTIVE = FALSE
             WHERE WEEK_ID = ? AND ROUND = ? AND IS_ACTIVE = TRUE
            """, weekId, targetRound);

        // Pull the slice from local Postgres. Column names are interpolated
        // because round-suffixed columns can't bind as SQL parameters.
        String sourceSql = """
            SELECT ai.ecoid2 AS ecoid,
                   ai.merged_grade,
                   ai.round%d_target_price       AS target_price,
                   ai.r%d_target_price_factor    AS factor_amount,
                   ai.r%d_target_price_factor_type AS factor_type,
                   ai.round%d_eb_for_target      AS eb_for_target
              FROM auctions.aggregated_inventory ai
             WHERE ai.week_id = ?
               AND ai.is_deprecated = FALSE
            """.formatted(targetRound, targetRound, targetRound, targetRound);
        var rows = localJdbc.queryForList(sourceSql, weekId);

        if (rows.isEmpty()) return;

        for (var row : rows) {
            snowflakeJdbc.update("""
                INSERT INTO AUCTIONS.TARGET_PRICE_AUDIT (
                    WEEK_ID, ROUND, ECOID, MERGED_GRADE,
                    TARGET_PRICE, FACTOR_AMOUNT, FACTOR_TYPE, EB_FOR_TARGET,
                    RUN_VERSION, IS_ACTIVE, RECORDED_AT
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP())
                """,
                weekId, targetRound,
                row.get("ecoid"), row.get("merged_grade"),
                row.get("target_price"), row.get("factor_amount"),
                row.get("factor_type"), row.get("eb_for_target"),
                nextRunVersion);
        }
    }
}
