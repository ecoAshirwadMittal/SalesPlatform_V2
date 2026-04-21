package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link BidRoundSelectionFilter}. Exactly two rows exist
 * (round 2, round 3) and are keyed by {@code round}, enforced by the
 * {@code chk_brsf_round} CHECK in V59.
 */
public interface BidRoundSelectionFilterRepository extends JpaRepository<BidRoundSelectionFilter, Long> {

    /**
     * Look up the single filter row for the given round. Both rows are
     * pre-seeded by the extractor (see {@code V69__seed_bid_round_selection_filters.sql}
     * or equivalent), so callers can treat absence as a programmer error.
     */
    Optional<BidRoundSelectionFilter> findByRound(int round);
}
