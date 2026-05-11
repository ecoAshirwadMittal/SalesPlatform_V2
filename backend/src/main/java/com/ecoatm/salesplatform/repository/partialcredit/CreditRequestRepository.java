package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    Page<CreditRequest> findByBuyerCodeIdOrderByRequestDateDesc(Long buyerCodeId, Pageable pageable);

    Page<CreditRequest> findByStatusIdOrderByRequestDateDesc(Long statusId, Pageable pageable);

    List<CreditRequest> findByOrderNumberAndBuyerCodeId(String orderNumber, Long buyerCodeId);

    /**
     * Returns any non-{@code DECLINED} request for this (order, buyer) pair.
     * The validator uses this to enforce "one active credit request per
     * order"; a prior request fully declined unblocks the buyer to file a
     * new one (decision 2026-05-11, see partial-credit-confluence §Business
     * rules).
     */
    @Query("""
            SELECT cr FROM CreditRequest cr
            JOIN CreditRequestStatus s ON cr.statusId = s.id
            WHERE cr.orderNumber = :orderNumber
              AND cr.buyerCodeId = :buyerCodeId
              AND s.systemStatus <> :declinedStatus
            """)
    List<CreditRequest> findActiveByOrderAndBuyer(
            @Param("orderNumber") String orderNumber,
            @Param("buyerCodeId") Long buyerCodeId,
            @Param("declinedStatus") SystemStatus declinedStatus);
}
