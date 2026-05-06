package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class BidRankingRepositoryIT extends PostgresIntegrationTest {

    @Autowired BidRankingRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    void ranks_round1_close_into_round2_columns_with_reserve_floor() {
        int updated = repo.rankClosedRound(999001L, 1);

        assertThat(updated).isPositive();

        // (ECO-A, A): with include_reserve_floor=TRUE, reserve_bid 700 ranks #1.
        // DW01/SCWC tie at 500 ranks #2 (DENSE_RANK ties get same rank, then 300 = #3).
        // 50 below min_bid (100) → excluded → no rank.
        assertRank(999001L, 2);    // DW01 @ 500
        assertRank(999002L, 2);    // SCWC @ 500 (tie)
        assertRank(999003L, 3);    // SCPO @ 300
        assertRank(999004L, null); // ACCPPO @ 50 (below min_bid)
    }

    @Test
    void display_rank_clamps_to_null_outside_window() {
        // bid_ranking_config: display_rank=1, maximum_rank=5
        // (ECO-A, A) with reserve floor: ranks 1,2,2,3 — all within [1,5].
        // Tighten to make ranks fall outside: set max_display_rank=1.
        jdbc.update("UPDATE auctions.bid_ranking_config SET maximum_rank = 1");

        repo.rankClosedRound(999001L, 1);

        // Calculated rank survives in round2_bid_rank; display_round2_bid_rank
        // is NULL when calc_rank > maximum_rank.
        Integer calc = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 999001", Integer.class);
        Integer disp = jdbc.queryForObject(
            "SELECT display_round2_bid_rank FROM auctions.bid_data WHERE id = 999001", Integer.class);

        assertThat(calc).isEqualTo(2);  // raw rank still computed
        assertThat(disp).isNull();       // but clamped out of display window
    }

    @Test
    void exclude_reserve_floor_drops_eb_rows_from_ranking() {
        jdbc.update("UPDATE auctions.bid_ranking_config SET include_reserve_floor = FALSE");

        repo.rankClosedRound(999001L, 1);

        // (ECO-A, A): without EB, the 500/500 tie is now rank #1.
        assertRank(999001L, 1);
        assertRank(999002L, 1);
        assertRank(999003L, 2);
    }

    @Test
    void rerunning_is_idempotent() {
        repo.rankClosedRound(999001L, 1);
        Integer firstRun = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 999001", Integer.class);

        repo.rankClosedRound(999001L, 1);
        Integer secondRun = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 999001", Integer.class);

        assertThat(secondRun).isEqualTo(firstRun);
    }

    @Test
    void closed_round_2_writes_round3_rank_columns() {
        // Promote round 2 bids by copying the round 1 fixture rows into round 2.
        // bid_round_id 999004 is the round-2 bid_round in the fixture
        // (scheduling_auction_id=999002).
        // Filter to fixture rows only (week_id = 999001) to avoid PK collision
        // with production-style seeded rows already in the dev DB.
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            SELECT id + 1000, 999004, buyer_code_id, ecoid, merged_grade, code, company_name,
                   buyer_code_type, submitted_bid_amount, 2, week_id
              FROM auctions.bid_data WHERE bid_round = 1 AND week_id = 999001
            """);

        repo.rankClosedRound(999002L, 2);

        Integer rank = jdbc.queryForObject(
            "SELECT round3_bid_rank FROM auctions.bid_data WHERE id = 1000001", Integer.class);

        assertThat(rank).isPositive();
    }

    @Test
    void rejects_invalid_round() {
        // Spring's @Transactional(MANDATORY) repository wraps IllegalArgumentException
        // in InvalidDataAccessApiUsageException — assert on the root cause.
        assertThatThrownBy(() -> repo.rankClosedRound(999001L, 3))
            .hasMessageContaining("closedRound must be 1 or 2");
    }

    private void assertRank(long bidDataId, Integer expected) {
        Integer actual = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = ?",
            Integer.class, bidDataId);
        assertThat(actual).as("rank for bid_data id=%s", bidDataId).isEqualTo(expected);
    }
}
