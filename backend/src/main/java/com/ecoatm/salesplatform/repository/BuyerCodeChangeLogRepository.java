package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCodeChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerCodeChangeLogRepository extends JpaRepository<BuyerCodeChangeLog, Long> {

    /** Audit rows for one buyer code, newest change first. */
    List<BuyerCodeChangeLog> findByBuyerCodeIdOrderByIdDesc(Long buyerCodeId);
}
