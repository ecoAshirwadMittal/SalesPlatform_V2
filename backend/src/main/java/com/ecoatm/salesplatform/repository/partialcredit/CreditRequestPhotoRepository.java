package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequestPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestPhotoRepository extends JpaRepository<CreditRequestPhoto, Long> {
    List<CreditRequestPhoto> findByCreditRequestIdOrderById(Long creditRequestId);

    List<CreditRequestPhoto> findByWrongDeviceLineIdOrderById(Long wrongDeviceLineId);
}
