package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReserveBidRepository extends JpaRepository<ReserveBid, Long> {

    Optional<ReserveBid> findByProductIdAndGrade(String productId, String grade);

    boolean existsByProductIdAndGrade(String productId, String grade);

    @Query("""
        SELECT rb FROM ReserveBid rb
        WHERE (:productId IS NULL OR rb.productId = :productId)
          AND (:grade IS NULL OR LOWER(rb.grade) LIKE LOWER(CONCAT('%', :grade, '%')))
          AND (:minBid IS NULL OR rb.bid >= :minBid)
          AND (:maxBid IS NULL OR rb.bid <= :maxBid)
          AND (:updatedSince IS NULL OR rb.lastUpdateDatetime >= :updatedSince)
        """)
    Page<ReserveBid> search(@Param("productId") String productId,
                            @Param("grade") String grade,
                            @Param("minBid") BigDecimal minBid,
                            @Param("maxBid") BigDecimal maxBid,
                            @Param("updatedSince") Instant updatedSince,
                            Pageable pageable);

    @Query("SELECT MAX(rb.lastUpdateDatetime) FROM ReserveBid rb")
    Optional<Instant> findMaxLastUpdateDatetime();

    List<ReserveBid> findByProductIdInAndGradeIn(List<String> productIds, List<String> grades);

    @Modifying
    @Query(value = "DELETE FROM auctions.reserve_bid", nativeQuery = true)
    void deleteAllNative();
}
