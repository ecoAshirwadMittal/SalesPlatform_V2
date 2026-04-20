package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRepresentativeRepository extends JpaRepository<SalesRepresentative, Long> {

    List<SalesRepresentative> findByActiveTrueOrderByFirstNameAscLastNameAsc();

    @Query(nativeQuery = true, value = """
        SELECT sr.*
        FROM buyer_mgmt.sales_representatives sr
        JOIN buyer_mgmt.buyer_sales_reps bsr ON bsr.sales_rep_id = sr.id
        WHERE bsr.buyer_id = :buyerId
        ORDER BY sr.first_name, sr.last_name
    """)
    List<SalesRepresentative> findByBuyerId(@Param("buyerId") Long buyerId);
}
