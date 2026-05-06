package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "bid-ranking-writer", havingValue = "jdbc")
public class JdbcBidRankingSnowflakeWriter implements BidRankingSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final JdbcTemplate localJdbc;

    public JdbcBidRankingSnowflakeWriter(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc,
                                          JdbcTemplate localJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
        this.localJdbc = localJdbc;
    }

    @Override
    public void pushBidRankings(long weekId, int targetRound) {
        // Stage rows from local Postgres for the (week, targetRound) slice.
        String sourceSql = """
            SELECT bd.id AS bid_data_id,
                   bd.ecoid,
                   bd.merged_grade,
                   bd.code AS buyer_code,
                   bd.submitted_bid_amount,
                   CASE WHEN ? = 2 THEN bd.round2_bid_rank
                        WHEN ? = 3 THEN bd.round3_bid_rank END AS rank,
                   CASE WHEN ? = 2 THEN bd.display_round2_bid_rank
                        WHEN ? = 3 THEN bd.display_round3_bid_rank END AS display_rank
              FROM auctions.bid_data bd
             WHERE bd.week_id = ?
               AND bd.bid_round = ? - 1
            """;
        var rows = localJdbc.queryForList(
            sourceSql, targetRound, targetRound, targetRound, targetRound, weekId, targetRound);

        if (rows.isEmpty()) return;

        // Snowflake MERGE — upsert rank columns on AUCTIONS.BUYER_BID by
        // (week, round, bid_data_id). Adapt column names if your warehouse
        // schema differs.
        String mergeSql = """
            MERGE INTO AUCTIONS.BUYER_BID t
            USING (SELECT ? AS BID_DATA_ID, ? AS RANK, ? AS DISPLAY_RANK) s
              ON t.BID_DATA_ID = s.BID_DATA_ID
            WHEN MATCHED THEN UPDATE SET
                ROUND%d_RANK         = s.RANK,
                DISPLAY_ROUND%d_RANK = s.DISPLAY_RANK
            """.formatted(targetRound, targetRound);

        for (var row : rows) {
            snowflakeJdbc.update(mergeSql,
                row.get("bid_data_id"),
                row.get("rank"),
                row.get("display_rank"));
        }
    }
}
