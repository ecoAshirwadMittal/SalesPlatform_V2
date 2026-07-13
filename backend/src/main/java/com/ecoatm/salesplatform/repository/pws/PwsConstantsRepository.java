package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.PwsConstants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PwsConstantsRepository extends JpaRepository<PwsConstants, Long> {

    /**
     * The singleton constants row. The table is seeded with exactly one row
     * (V29); {@code findTopByOrderByIdAsc} is defensive against a DB that
     * somehow holds more than one.
     */
    Optional<PwsConstants> findTopByOrderByIdAsc();
}
