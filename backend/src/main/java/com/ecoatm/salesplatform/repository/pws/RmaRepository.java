package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Rma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RmaRepository extends JpaRepository<Rma, Long> {

    List<Rma> findByBuyerCodeIdOrderByCreatedDateDesc(Long buyerCodeId);

    List<Rma> findByBuyerCodeIdAndRmaStatus_SystemStatusOrderByCreatedDateDesc(
            Long buyerCodeId, String systemStatus);

    @Query("SELECT r FROM Rma r WHERE r.buyerCodeId = :buyerCodeId " +
           "AND r.rmaStatus.statusGroupedTo = :groupedTo ORDER BY r.createdDate DESC")
    List<Rma> findByBuyerCodeIdAndStatusGroupedTo(
            @Param("buyerCodeId") Long buyerCodeId,
            @Param("groupedTo") String groupedTo);

    @Query("SELECT r.rmaStatus.systemStatus, COUNT(r), " +
           "COALESCE(SUM(r.requestSalesTotal), 0), " +
           "COALESCE(SUM(r.requestSkus), 0), " +
           "COALESCE(SUM(r.requestQty), 0) " +
           "FROM Rma r WHERE r.buyerCodeId = :buyerCodeId " +
           "GROUP BY r.rmaStatus.systemStatus")
    List<Object[]> getSummaryByBuyerCode(@Param("buyerCodeId") Long buyerCodeId);

    @Query("SELECT r.rmaStatus.statusGroupedTo, COUNT(r), " +
           "COALESCE(SUM(r.requestSalesTotal), 0), " +
           "COALESCE(SUM(r.requestSkus), 0), " +
           "COALESCE(SUM(r.requestQty), 0) " +
           "FROM Rma r WHERE r.buyerCodeId = :buyerCodeId " +
           "GROUP BY r.rmaStatus.statusGroupedTo")
    List<Object[]> getSummaryGroupedByBuyerCode(@Param("buyerCodeId") Long buyerCodeId);

    @Query("SELECT r FROM Rma r WHERE r.rmaStatus.statusGroupedTo = :groupedTo ORDER BY r.createdDate DESC")
    List<Rma> findByStatusGroupedTo(@Param("groupedTo") String groupedTo);

    @Query("SELECT r FROM Rma r ORDER BY r.createdDate DESC")
    List<Rma> findAllOrderByCreatedDateDesc();

    @Query("SELECT r.rmaStatus.statusGroupedTo, COUNT(r), " +
           "COALESCE(SUM(r.requestSalesTotal), 0), " +
           "COALESCE(SUM(r.requestSkus), 0), " +
           "COALESCE(SUM(r.requestQty), 0) " +
           "FROM Rma r GROUP BY r.rmaStatus.statusGroupedTo")
    List<Object[]> getSummaryGroupedAll();

    long countByBuyerCodeId(Long buyerCodeId);

    /**
     * Candidate set for the Deposco RMA status sync job
     * ({@code ACT_UpdateRMAFromDeposco}): RMAs that carry an Oracle order
     * number and are NOT in a terminal status (Received / Canceled / Declined).
     * Filters via the {@code rma_status} FK join — the reliable status
     * representation in the migrated data (the direct {@code system_status}
     * column is null/dirty on a chunk of seeded rows). {@code JOIN FETCH}
     * eagerly loads {@code rmaStatus} so the caller can read the status outside
     * a transaction (open-in-view is off). Mirrors the legacy retrieve filter
     * {@code [SystemStatus not terminal] [OracleNumber != empty]}.
     */
    @Query("SELECT r FROM Rma r JOIN FETCH r.rmaStatus rs " +
           "WHERE r.oracleNumber IS NOT NULL AND r.oracleNumber <> '' " +
           "AND rs.systemStatus NOT IN :terminalStatuses " +
           "ORDER BY r.createdDate ASC")
    List<Rma> findPollableForDeposcoSync(@Param("terminalStatuses") Collection<String> terminalStatuses);

    /** Count RMAs for a buyer code — used for RMA number sequence generation. */
    @Query("SELECT COUNT(r) FROM Rma r WHERE r.buyerCodeId = :buyerCodeId")
    long countByBuyerCode(@Param("buyerCodeId") Long buyerCodeId);
}
