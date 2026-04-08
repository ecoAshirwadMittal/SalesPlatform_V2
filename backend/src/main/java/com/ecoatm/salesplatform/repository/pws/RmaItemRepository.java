package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.RmaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RmaItemRepository extends JpaRepository<RmaItem, Long> {

    List<RmaItem> findByRmaIdOrderByCreatedDateAsc(Long rmaId);

    List<RmaItem> findByRmaIdAndStatusOrderByCreatedDateAsc(Long rmaId, String status);

    long countByRmaIdAndStatus(Long rmaId, String status);

    /** Check if an IMEI already exists in a pending/open RMA for the given buyer code. */
    @Query("SELECT COUNT(ri) FROM RmaItem ri JOIN ri.rma r " +
           "WHERE ri.imei = :imei AND r.buyerCodeId = :buyerCodeId " +
           "AND r.rmaStatus.statusGroupedTo IN ('Pending_Approval', 'Open')")
    long countByImeiAndBuyerCodeInPendingRma(
            @Param("imei") String imei,
            @Param("buyerCodeId") Long buyerCodeId);

    /** Batch check: find all IMEIs from a list that already exist in pending RMAs for this buyer. */
    @Query("SELECT DISTINCT ri.imei FROM RmaItem ri JOIN ri.rma r " +
           "WHERE ri.imei IN :imeis AND r.buyerCodeId = :buyerCodeId " +
           "AND r.rmaStatus.statusGroupedTo IN ('Pending_Approval', 'Open')")
    List<String> findDuplicateImeis(
            @Param("imeis") List<String> imeis,
            @Param("buyerCodeId") Long buyerCodeId);
}
