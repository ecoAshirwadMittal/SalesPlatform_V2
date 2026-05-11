package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequestUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestUploadRepository extends JpaRepository<CreditRequestUpload, Long> {
    List<CreditRequestUpload> findByCreditRequestIdOrderById(Long creditRequestId);
}
