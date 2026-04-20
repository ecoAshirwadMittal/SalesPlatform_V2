package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerRepository
        extends JpaRepository<Buyer, Long>, JpaSpecificationExecutor<Buyer> {

    Optional<Buyer> findByCompanyNameIgnoreCase(String companyName);

    List<Buyer> findByStatus(BuyerStatus status);
}
