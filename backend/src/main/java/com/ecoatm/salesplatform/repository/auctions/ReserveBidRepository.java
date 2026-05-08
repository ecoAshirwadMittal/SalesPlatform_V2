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

    // Native SQL with explicit CAST(:param AS type) — Hibernate 6 + Postgres cannot
    // infer parameter types through getParameterMetaData() for nullable JPQL params
    // when the placeholder appears inside lower()/concat() or numeric comparisons,
    // so Postgres defaults them to bytea and the describe step fails. Same pattern
    // used in AuctionListService and BuyerOverviewService.
    @Query(value = """
        SELECT rb.* FROM auctions.reserve_bid rb
        WHERE (CAST(:productId AS text) IS NULL OR rb.product_id = CAST(:productId AS text))
          AND (CAST(:grade AS text) IS NULL
               OR LOWER(rb.grade) LIKE LOWER(CONCAT('%', CAST(:grade AS text), '%')))
          AND (CAST(:minBid AS numeric) IS NULL OR rb.bid >= CAST(:minBid AS numeric))
          AND (CAST(:maxBid AS numeric) IS NULL OR rb.bid <= CAST(:maxBid AS numeric))
          AND (CAST(:updatedSince AS timestamptz) IS NULL
               OR rb.last_update_datetime >= CAST(:updatedSince AS timestamptz))
        """,
        countQuery = """
        SELECT COUNT(*) FROM auctions.reserve_bid rb
        WHERE (CAST(:productId AS text) IS NULL OR rb.product_id = CAST(:productId AS text))
          AND (CAST(:grade AS text) IS NULL
               OR LOWER(rb.grade) LIKE LOWER(CONCAT('%', CAST(:grade AS text), '%')))
          AND (CAST(:minBid AS numeric) IS NULL OR rb.bid >= CAST(:minBid AS numeric))
          AND (CAST(:maxBid AS numeric) IS NULL OR rb.bid <= CAST(:maxBid AS numeric))
          AND (CAST(:updatedSince AS timestamptz) IS NULL
               OR rb.last_update_datetime >= CAST(:updatedSince AS timestamptz))
        """,
        nativeQuery = true)
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
