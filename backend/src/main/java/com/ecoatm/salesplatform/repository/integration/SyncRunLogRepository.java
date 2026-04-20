package com.ecoatm.salesplatform.repository.integration;

import com.ecoatm.salesplatform.model.integration.SyncRunLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncRunLogRepository extends JpaRepository<SyncRunLog, Long> {
    Page<SyncRunLog> findAllByOrderByStartTimeDesc(Pageable pageable);
}
