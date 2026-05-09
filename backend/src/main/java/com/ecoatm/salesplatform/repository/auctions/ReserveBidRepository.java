package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Reserve-bid repository. The dynamic-WHERE filter row lives on the custom
 * slice {@link ReserveBidRepositoryCustom} (impl in
 * {@code ReserveBidRepositoryImpl}). Static finders for upload + sync paths
 * stay here.
 */
public interface ReserveBidRepository extends JpaRepository<ReserveBid, Long>, ReserveBidRepositoryCustom {

    Optional<ReserveBid> findByProductIdAndGrade(String productId, String grade);

    boolean existsByProductIdAndGrade(String productId, String grade);

    @Query("SELECT MAX(rb.lastUpdateDatetime) FROM ReserveBid rb")
    Optional<Instant> findMaxLastUpdateDatetime();

    List<ReserveBid> findByProductIdInAndGradeIn(List<String> productIds, List<String> grades);

    @Modifying
    @Query(value = "DELETE FROM auctions.reserve_bid", nativeQuery = true)
    void deleteAllNative();
}
