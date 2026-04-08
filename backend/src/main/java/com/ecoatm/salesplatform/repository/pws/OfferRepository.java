package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByBuyerCodeIdAndOfferTypeAndStatus(
            Long buyerCodeId, String offerType, String status);

    /** Find all non-DRAFT offers, ordered by most recently updated. */
    List<Offer> findByStatusNotOrderByUpdatedDateDesc(String status);

    /** Find offers by status, ordered by most recently updated. */
    List<Offer> findByStatusOrderByUpdatedDateDesc(String status);

    /** Count offers by status. */
    long countByStatus(String status);

    /** Count all non-DRAFT offers. */
    long countByStatusNot(String status);

    /** Find offers by status and buyer code, ordered by most recently updated. */
    List<Offer> findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc(String status, Long buyerCodeId);
}
