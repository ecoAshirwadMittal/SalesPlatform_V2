package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveBidSyncRepository extends JpaRepository<ReserveBidSync, Long> {

    Optional<ReserveBidSync> findFirstByOrderByIdAsc();
}
