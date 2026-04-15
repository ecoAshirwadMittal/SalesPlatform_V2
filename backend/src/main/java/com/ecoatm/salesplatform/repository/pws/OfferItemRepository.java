package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {

    Optional<OfferItem> findByOfferAndSku(Offer offer, String sku);

    void deleteByOfferAndSku(Offer offer, String sku);

    void deleteByOffer(Offer offer);

    List<OfferItem> findByOfferId(Long offerId);

    /**
     * Sum reserved (ordered) quantity for a device across active offers.
     * Mirrors Mendix SUB_UpdateReservedQuanityPerDevice logic:
     * counts items on offers with status Ordered, Pending_Order, or In_Process.
     */
    @Query(value = """
            SELECT COALESCE(SUM(oi.quantity), 0)
            FROM pws.offer_item oi
            JOIN pws.offer o ON o.id = oi.offer_id
            WHERE oi.device_id = :deviceId
              AND o.status IN ('Ordered', 'Pending_Order', 'In_Process')
              AND oi.quantity > 0
            """, nativeQuery = true)
    int sumReservedQtyByDeviceId(@Param("deviceId") Long deviceId);
}
