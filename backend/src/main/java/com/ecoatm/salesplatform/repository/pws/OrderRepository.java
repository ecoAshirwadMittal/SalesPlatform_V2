package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    /**
     * Orders whose {@code order_date} is non-null and falls inside the inclusive
     * day range {@code [startingDate, endingDate]} — the modern port of the
     * legacy {@code SUB_ChangeOfferStatus_GetOrderList} filter
     * ({@code OrderDate != empty and trimToDays(OrderDate) >= StartingDate and
     * trimToDays(OrderDate) <= EndingDate}).
     *
     * <p>Callers pass a half-open {@code [start, endExclusive)} bound so the
     * comparison covers the whole ending day regardless of the time-of-day
     * component: {@code start = startingDate.atStartOfDay()},
     * {@code endExclusive = endingDate.plusDays(1).atStartOfDay()}. This keeps the
     * query index-friendly (a plain range scan on {@code order_date}) and avoids
     * a {@code CAST(order_date AS date)} that would defeat the index.
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate IS NOT NULL "
            + "AND o.orderDate >= :start AND o.orderDate < :endExclusive")
    List<Order> findByOrderDateWithinRange(@Param("start") LocalDateTime start,
                                           @Param("endExclusive") LocalDateTime endExclusive);
}
