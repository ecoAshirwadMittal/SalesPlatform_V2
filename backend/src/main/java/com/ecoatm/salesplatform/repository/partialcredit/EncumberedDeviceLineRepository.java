package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncumberedDeviceLineRepository extends JpaRepository<EncumberedDeviceLine, Long> {
    List<EncumberedDeviceLine> findByCreditRequestIdOrderById(Long creditRequestId);
}
