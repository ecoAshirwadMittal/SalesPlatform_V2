package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Orders placed against a given offer, oldest first. Used to resolve an
     * RMA line's order number + ship date from the matched OfferItem (the legacy
     * {@code VAL_RMARequestFile} read {@code OfferItem_Order}; the modern link is
     * {@code order.offer_id → offer ← offer_item.offer_id}). Best-effort: an offer
     * normally maps to a single order.
     */
    @Query("SELECT o FROM Order o WHERE o.offer.id = :offerId ORDER BY o.id ASC")
    List<Order> findByOfferId(@Param("offerId") Long offerId);
}
