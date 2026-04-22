package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface BidRoundRepository extends JpaRepository<BidRound, Long> {

    /**
     * Phase C Save Schedule guard: reject reschedule when any round of this
     * auction already has a {@code bid_rounds} row. The caller passes the
     * ids of the existing {@code scheduling_auctions} rows for the auction.
     */
    boolean existsBySchedulingAuctionIdIn(Collection<Long> schedulingAuctionIds);

    Optional<BidRound> findBySchedulingAuctionId(Long schedulingAuctionId);
}
