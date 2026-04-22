package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidDataRepository extends JpaRepository<BidData, Long> {
    long countByBidRoundId(long bidRoundId);
    List<BidData> findByBidRoundIdOrderByEcoidAscMergedGradeAsc(long bidRoundId);

    List<BidData> findByBidRoundIdAndBuyerCodeIdOrderByEcoidAscMergedGradeAsc(
            long bidRoundId, long buyerCodeId);
}
