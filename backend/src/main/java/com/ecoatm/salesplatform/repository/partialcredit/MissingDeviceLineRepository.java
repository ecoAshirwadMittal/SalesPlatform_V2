package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissingDeviceLineRepository extends JpaRepository<MissingDeviceLine, Long> {
    List<MissingDeviceLine> findByCreditRequestIdOrderById(Long creditRequestId);
}
