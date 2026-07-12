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

    /** Full management list (active + inactive) for the admin CRUD grid. */
    List<SalesRepresentative> findAllByOrderByFirstNameAscLastNameAsc();

    @Query(nativeQuery = true, value = """
        SELECT sr.*
        FROM buyer_mgmt.sales_representatives sr
        JOIN buyer_mgmt.buyer_sales_reps bsr ON bsr.sales_rep_id = sr.id
        WHERE bsr.buyer_id = :buyerId
        ORDER BY sr.first_name, sr.last_name
    """)
    List<SalesRepresentative> findByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * Case-insensitive duplicate-name guard for create — ports the legacy
     * {@code Act_SaveSaleRep} check across all existing reps.
     */
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    /**
     * Case-insensitive duplicate-name guard for update — excludes the rep being
     * edited so re-saving unchanged names is not flagged as a self-collision.
     */
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot(
            String firstName, String lastName, Long id);

    /**
     * Delete guard — counts {@code pws.offer} rows referencing this rep. Ports
     * the legacy {@code ACT_DeleteSalesRep} "has associated Offers" check.
     * Native (cross-schema) so the service need not depend on the Offer entity.
     */
    @Query(nativeQuery = true, value =
            "SELECT COUNT(*) FROM pws.offer WHERE sales_rep_id = :id")
    long countOffersReferencing(@Param("id") Long id);

    /**
     * Next PK for an insert. The table's id is a plain BIGINT with no
     * sequence/identity (V8 DDL), so the id must be assigned before persist.
     * {@code MAX(id)+1} is safe here: this admin CRUD is low-frequency and
     * single-writer, and each call runs inside the write transaction.
     */
    @Query("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SalesRepresentative s")
    long nextId();
}
