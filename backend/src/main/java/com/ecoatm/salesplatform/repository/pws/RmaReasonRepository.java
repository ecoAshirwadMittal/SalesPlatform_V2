package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.RmaReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RmaReasonRepository extends JpaRepository<RmaReason, Long> {

    List<RmaReason> findByIsActiveTrueOrderByValidReasonsAsc();
}
