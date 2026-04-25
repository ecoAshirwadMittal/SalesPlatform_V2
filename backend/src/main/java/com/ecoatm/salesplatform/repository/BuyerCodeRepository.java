package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerCodeRepository extends JpaRepository<BuyerCode, Long> {

    @Query(nativeQuery = true, value = """
        SELECT bc.*
        FROM buyer_mgmt.buyer_codes bc
        JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
        WHERE bcb.buyer_id = :buyerId
        ORDER BY bc.code
    """)
    List<BuyerCode> findByBuyerId(@Param("buyerId") Long buyerId);

    @Query(nativeQuery = true, value = """
        SELECT COUNT(*) > 0
        FROM buyer_mgmt.buyer_codes bc
        WHERE LOWER(bc.code) = LOWER(:code)
          AND bc.soft_delete = false
          AND (:excludeId IS NULL OR bc.id != :excludeId)
    """)
    boolean existsByCodeIgnoreCaseAndNotSoftDeleted(
            @Param("code") String code,
            @Param("excludeId") Long excludeId);

    @Query(nativeQuery = true, value = """
        SELECT DISTINCT bc.* FROM buyer_mgmt.buyer_codes bc
        JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
        JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
        WHERE bc.buyer_code_type IN ('Data_Wipe','Wholesale')
          AND bc.soft_delete = false
          AND b.status = 'Active'
    """)
    List<BuyerCode> findActiveWholesaleOrDataWipe();

    @Query("SELECT b.code FROM BuyerCode b WHERE b.code IN :codes")
    List<String> findCodesIn(@Param("codes") List<String> codes);

    @Query("SELECT b FROM BuyerCode b WHERE b.code IN :codes")
    List<BuyerCode> findByCodeIn(@Param("codes") List<String> codes);
}
