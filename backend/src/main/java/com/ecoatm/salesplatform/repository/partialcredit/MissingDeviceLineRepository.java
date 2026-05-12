package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MissingDeviceLineRepository extends JpaRepository<MissingDeviceLine, Long> {
    List<MissingDeviceLine> findByCreditRequestIdOrderById(Long creditRequestId);

    /** Bulk fetch for the Sprint 4 chunk-7 xlsx export — avoids N+1
     *  queries when the Lines sheet covers thousands of requests. */
    List<MissingDeviceLine> findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(
            Collection<Long> creditRequestIds);
}
