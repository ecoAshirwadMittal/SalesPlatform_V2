package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.RmaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RmaStatusRepository extends JpaRepository<RmaStatus, Long> {

    Optional<RmaStatus> findBySystemStatus(String systemStatus);

    List<RmaStatus> findAllByOrderBySortOrderAsc();

    Optional<RmaStatus> findByIsDefaultTrue();
}
