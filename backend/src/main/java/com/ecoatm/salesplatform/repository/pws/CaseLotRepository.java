package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.CaseLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseLotRepository extends JpaRepository<CaseLot, Long> {
    List<CaseLot> findByIsActiveTrueOrderByIdAsc();
}
