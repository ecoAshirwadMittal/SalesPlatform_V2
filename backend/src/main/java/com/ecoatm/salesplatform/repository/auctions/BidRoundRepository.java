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

    /**
     * Per-(scheduling_auction, buyer_code) lookup. Required because the
     * {@code idx_br_sa_buyer_code} unique-shape (V59) means a given
     * {@code scheduling_auction_id} has one row per qualifying buyer code,
     * not a single row. {@link #findBySchedulingAuctionId} would throw
     * {@code IncorrectResultSizeDataAccessException} the moment R1 init
     * seeds more than one buyer code.
     */
    Optional<BidRound> findBySchedulingAuctionIdAndBuyerCodeId(
            Long schedulingAuctionId, Long buyerCodeId);
}
