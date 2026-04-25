package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PODetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PODetailRepository extends JpaRepository<PODetail, Long> {

    Page<PODetail> findByPurchaseOrderId(Long purchaseOrderId, Pageable pageable);

    long countByPurchaseOrderId(Long purchaseOrderId);

    @Modifying
    @Query("DELETE FROM PODetail d WHERE d.purchaseOrder.id = :poId")
    int deleteAllByPurchaseOrderId(@Param("poId") Long poId);
}
