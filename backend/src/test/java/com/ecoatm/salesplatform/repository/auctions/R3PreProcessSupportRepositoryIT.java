package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link R3PreProcessSupportRepository}.
 *
 * <p>Uses {@link TransactionTemplate} to wrap calls to methods with
 * {@code @Transactional(propagation = MANDATORY)} — the same pattern as
 * {@link com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater}.
 *
 * <p>Fixture seeded by r3-lifecycle-seed.sql: R2 SA (id=6002) has 2 deletion-target
 * bid_data rows (ids 60007, 60008) with bid_amount=0.
 */
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R3PreProcessSupportRepositoryIT extends PostgresIntegrationTest {

    @Autowired R3PreProcessSupportRepository repo;
    @Autowired JdbcTemplate jdbc;
    @Autowired TransactionTemplate txTemplate;

    private static final long R2_SA_ID = 6002L;

    @Test
    @DisplayName("deleteUnsubmittedBids removes only bid_amount=0 rows on the R2 SA")
    void deletes_only_unsubmitted() {
        Integer beforeUnsubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)",
            Integer.class, R2_SA_ID);
        assertThat(beforeUnsubmitted).as("fixture seeds 2 deletion targets on R2 SA").isEqualTo(2);

        int deleted = txTemplate.execute(status -> repo.deleteUnsubmittedBids(R2_SA_ID));

        assertThat(deleted).isEqualTo(2);

        Integer afterUnsubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)",
            Integer.class, R2_SA_ID);
        assertThat(afterUnsubmitted).isZero();
    }

    @Test
    @DisplayName("deleteUnsubmittedBids leaves submitted (bid_amount > 0) rows untouched")
    void leaves_submitted_alone() {
        Integer beforeSubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND bd.bid_amount > 0",
            Integer.class, R2_SA_ID);

        txTemplate.execute(status -> repo.deleteUnsubmittedBids(R2_SA_ID));

        Integer afterSubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND bd.bid_amount > 0",
            Integer.class, R2_SA_ID);
        assertThat(afterSubmitted).isEqualTo(beforeSubmitted);
    }

    @Test
    @DisplayName("rerun is idempotent — second call returns 0")
    void rerun_idempotent() {
        txTemplate.execute(status -> repo.deleteUnsubmittedBids(R2_SA_ID));
        int second = txTemplate.execute(status -> repo.deleteUnsubmittedBids(R2_SA_ID));
        assertThat(second).isZero();
    }
}
