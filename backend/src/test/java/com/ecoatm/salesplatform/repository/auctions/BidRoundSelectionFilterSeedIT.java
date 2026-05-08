package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerInventoryOption;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerQualification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies V86 seed migration created the two default {@code
 * bid_round_selection_filters} rows that the R2/R3 lifecycle and the
 * Selection Rules admin page depend on.
 *
 * <p>Without this seed:
 * <ul>
 *   <li>{@code GET /api/v1/auctions/round-filters/{2,3}} returns 500</li>
 *   <li>{@code R2BuyerAssignmentService} fails at first R2 init</li>
 *   <li>{@code R3PreProcessService} fails at first R2 close</li>
 * </ul>
 */
@Transactional
class BidRoundSelectionFilterSeedIT extends PostgresIntegrationTest {

    @Autowired
    private BidRoundSelectionFilterRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("V86 seeds round=2 row with Mendix-parity defaults")
    void v86_seedsRound2RowWithMendixDefaults() {
        Optional<BidRoundSelectionFilter> r2 = repository.findByRound(2);

        assertThat(r2).as("V86 must seed round=2").isPresent();
        BidRoundSelectionFilter f = r2.get();
        assertThat(f.getRound()).isEqualTo(2);
        assertThat(f.getRegularBuyerQualification()).isEqualTo(RegularBuyerQualification.Only_Qualified);
        assertThat(f.getRegularBuyerInventoryOptions()).isEqualTo(RegularBuyerInventoryOption.InventoryRound1QualifiedBids);
        assertThat(f.getStbAllowAllBuyersOverride()).isFalse();
        assertThat(f.getStbIncludeAllInventory()).isFalse();
    }

    @Test
    @DisplayName("V86 seeds round=3 row with Mendix-parity defaults; R3 knobs NULL")
    void v86_seedsRound3RowWithMendixDefaults() {
        Optional<BidRoundSelectionFilter> r3 = repository.findByRound(3);

        assertThat(r3).as("V86 must seed round=3").isPresent();
        BidRoundSelectionFilter f = r3.get();
        assertThat(f.getRound()).isEqualTo(3);
        assertThat(f.getRegularBuyerQualification()).isEqualTo(RegularBuyerQualification.Only_Qualified);
        assertThat(f.getRegularBuyerInventoryOptions()).isEqualTo(RegularBuyerInventoryOption.InventoryRound1QualifiedBids);

        // R3 qualification knobs default NULL → all-buyers fall-through (per
        // V84 comment + docs/architecture/data-model.md). Verified via JDBC
        // because the entity does not yet map these columns.
        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT bid_percentage_variation, bid_amount_variation, rank_qualification_limit "
                        + "FROM auctions.bid_round_selection_filters WHERE round = 3");
        assertThat(row.get("bid_percentage_variation")).isNull();
        assertThat(row.get("bid_amount_variation")).isNull();
        assertThat(row.get("rank_qualification_limit")).isNull();
    }
}
