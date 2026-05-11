package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    Page<CreditRequest> findByBuyerCodeIdOrderByRequestDateDesc(Long buyerCodeId, Pageable pageable);

    Page<CreditRequest> findByStatusIdOrderByRequestDateDesc(Long statusId, Pageable pageable);

    List<CreditRequest> findByOrderNumberAndBuyerCodeId(String orderNumber, Long buyerCodeId);
}
