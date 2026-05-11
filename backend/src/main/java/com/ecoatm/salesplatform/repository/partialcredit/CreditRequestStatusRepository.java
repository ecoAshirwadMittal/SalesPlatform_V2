package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRequestStatusRepository extends JpaRepository<CreditRequestStatus, Long> {
    Optional<CreditRequestStatus> findBySystemStatus(SystemStatus systemStatus);
}
