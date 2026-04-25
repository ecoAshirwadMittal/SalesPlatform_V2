package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCodeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualifiedBuyerCodeAuditRepository extends JpaRepository<QualifiedBuyerCodeAudit, Long> {
}
