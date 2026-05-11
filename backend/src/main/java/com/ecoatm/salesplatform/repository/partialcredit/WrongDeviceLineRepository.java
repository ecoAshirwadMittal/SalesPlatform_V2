package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WrongDeviceLineRepository extends JpaRepository<WrongDeviceLine, Long> {
    List<WrongDeviceLine> findByCreditRequestIdOrderById(Long creditRequestId);
}
