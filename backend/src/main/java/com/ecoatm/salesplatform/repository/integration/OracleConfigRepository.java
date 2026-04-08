package com.ecoatm.salesplatform.repository.integration;

import com.ecoatm.salesplatform.model.integration.OracleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OracleConfigRepository extends JpaRepository<OracleConfig, Long> {
}
