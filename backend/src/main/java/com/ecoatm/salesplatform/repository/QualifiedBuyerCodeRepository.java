package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link QualifiedBuyerCode}.
 * <p>
 * R1 init (sub-project 2) rewrites a SchedulingAuction's QBC set every tick
 * via {@code SUB_ClearQualifiedBuyerList} parity: delete all QBC rows for
 * the SA, then recompute and insert fresh rows. {@link #deleteBySchedulingAuctionId}
 * is the delete half of that flow.
 */
@Repository
public interface QualifiedBuyerCodeRepository extends JpaRepository<QualifiedBuyerCode, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM buyer_mgmt.qualified_buyer_codes
             WHERE scheduling_auction_id = :saId
            """, nativeQuery = true)
    int deleteBySchedulingAuctionId(@Param("saId") Long saId);

    Optional<QualifiedBuyerCode> findBySchedulingAuctionIdAndBuyerCodeId(
            Long schedulingAuctionId, Long buyerCodeId);

    List<QualifiedBuyerCode> findBySchedulingAuctionIdInAndBuyerCodeId(
            Collection<Long> schedulingAuctionIds, Long buyerCodeId);
}
