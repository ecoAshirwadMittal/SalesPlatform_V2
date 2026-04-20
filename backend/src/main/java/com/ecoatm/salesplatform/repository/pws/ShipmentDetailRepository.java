package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.ShipmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentDetailRepository extends JpaRepository<ShipmentDetail, Long> {
    Page<ShipmentDetail> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
