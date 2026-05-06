package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * IT for the queries on {@link SchedulingAuctionRepository} that aren't
 * already covered by the recalc service tests.
 *
 * <p>The 4C-introduced {@code findWeekIdById} uses a bare-FK JPQL ad-hoc
 * join (since {@code SchedulingAuction.auctionId} is a scalar Long, not a
 * {@code @ManyToOne}). That join syntax is correct under Hibernate 6 but
 * has no association metadata to validate it at startup — so this IT pins
 * it against the real schema, ensuring a column rename or schema drift
 * surfaces here rather than at runtime in the recalc services.
 */
@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class SchedulingAuctionRepositoryIT extends PostgresIntegrationTest {

    @Autowired SchedulingAuctionRepository repo;

    @Test
    void findWeekIdById_returns_week_id_from_associated_auction() {
        // Fixture: scheduling_auction 999001 → auction 999001 → mdm.week 999001
        Long weekId = repo.findWeekIdById(999001L);
        assertThat(weekId).isEqualTo(999001L);
    }

    @Test
    void findWeekIdById_returns_null_when_scheduling_auction_missing() {
        Long weekId = repo.findWeekIdById(88888888L);
        assertThat(weekId).isNull();
    }
}
