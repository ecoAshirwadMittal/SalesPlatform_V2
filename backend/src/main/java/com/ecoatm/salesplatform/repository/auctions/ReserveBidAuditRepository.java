package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReserveBidAuditRepository extends JpaRepository<ReserveBidAudit, Long> {

    List<ReserveBidAudit> findByReserveBidIdOrderByCreatedDateDesc(Long reserveBidId);

    Page<ReserveBidAudit> findByReserveBidIdOrderByCreatedDateDesc(Long reserveBidId, Pageable pageable);
}
