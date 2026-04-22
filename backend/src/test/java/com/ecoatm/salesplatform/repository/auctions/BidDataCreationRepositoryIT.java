package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.fixtures.BidDataScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Integration tests for {@link BidDataCreationRepository#generate}.
 *
 * <p>Runs against the real PostgreSQL test database (Flyway-built schema)
 * because the CTE relies on Postgres-only syntax (multi-CTE INSERT ... SELECT,
 * RETURNING, ::bigint casts). The H2-backed {@code test} profile cannot
 * support it.
 *
 * <p>{@code @Import(BidDataCreationRepository.class)} is required because
 * {@code @DataJpaTest} only auto-detects Spring Data interfaces — it does not
 * scan the custom {@code @Repository}-annotated POJO.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("pg-test")
@Import(BidDataCreationRepository.class)
class BidDataCreationRepositoryIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private BidDataCreationRepository repo;

    @Test
    void generate_r1_wholesale_buyer_producesOneRowPerInventoryLine() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("AAA1", "A", 10, new BigDecimal("25.00"))
                .inventory("AAA1", "B", 5,  new BigDecimal("15.00"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(2);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
                Integer.class, bidRoundId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void generate_idempotent_skipsWhenRowsExist() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("AAA1", "A", 10, new BigDecimal("25.00"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int first  = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);
        int second = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(first).isEqualTo(1);
        assertThat(second).isEqualTo(0);
    }
}
