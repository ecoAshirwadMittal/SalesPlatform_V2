package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.id <= :weekId
          AND po.weekTo.id   >= :weekId
        ORDER BY po.changedDate DESC
        """)
    Page<PurchaseOrder> findActiveOnDate(@Param("weekId") Long weekId, Pageable pageable);

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE (:weekFromId IS NULL OR po.weekFrom.id >= :weekFromId)
          AND (:weekToId   IS NULL OR po.weekTo.id   <= :weekToId)
          AND (:yearFrom   IS NULL OR po.weekFrom.year >= :yearFrom)
          AND (:yearTo     IS NULL OR po.weekTo.year   <= :yearTo)
        """)
    Page<PurchaseOrder> findFiltered(
            @Param("weekFromId") Long weekFromId,
            @Param("weekToId") Long weekToId,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            Pageable pageable);
}
