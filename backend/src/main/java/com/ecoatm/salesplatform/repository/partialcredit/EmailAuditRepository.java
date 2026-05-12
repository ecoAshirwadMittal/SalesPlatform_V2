package com.ecoatm.salesplatform.repository.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.EmailAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAuditRepository extends JpaRepository<EmailAudit, Long> {

    /** "Find every send attempt for this request" — backs the
     *  did-the-buyer-get-the-email diagnostic query. */
    List<EmailAudit> findByCreditRequestIdOrderBySentAtDesc(Long creditRequestId);
}
