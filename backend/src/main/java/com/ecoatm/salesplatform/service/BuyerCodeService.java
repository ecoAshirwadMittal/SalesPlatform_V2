package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements Mendix DS_PWS_BuyerCodes microflow logic.
 *
 * Decision tree:
 *   If user does NOT have Bidder role (is_buyer_role = false):
 *     → Return all active buyer codes with type 'Premium_Wholesale' from active buyers
 *   If user HAS Bidder role (is_buyer_role = true):
 *     → Return buyer codes linked to user's buyers, excluding Purchasing_Order types
 *
 * Relationship chain: User → user_buyers → buyers → buyer_code_buyers → buyer_codes
 */
@Service
public class BuyerCodeService {

    private final EntityManager em;

    public BuyerCodeService(EntityManager em) {
        this.em = em;
    }

    /**
     * Load buyer codes for the given user, split by role.
     * Mirrors DS_PWS_BuyerCodes decision logic.
     * Uses a single CTE query to check role and fetch codes in one round-trip.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<BuyerCodeResponse> getBuyerCodesForUser(Long userId) {
        List<Object[]> rows = em.createNativeQuery("""
                WITH role_check AS (
                    SELECT COALESCE(
                        (SELECT edu.is_buyer_role
                         FROM user_mgmt.ecoatm_direct_users edu
                         WHERE edu.user_id = :userId),
                        false
                    ) AS is_buyer
                )
                SELECT DISTINCT bc.id, bc.code, b.company_name, bc.buyer_code_type
                FROM buyer_mgmt.buyer_codes bc
                JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                LEFT JOIN user_mgmt.user_buyers ub
                    ON ub.buyer_id = b.id AND ub.user_id = :userId
                CROSS JOIN role_check rc
                WHERE bc.status = 'Active'
                  AND bc.soft_delete = false
                  AND b.status = 'Active'
                  AND (
                      (rc.is_buyer
                       AND ub.user_id IS NOT NULL
                       AND bc.buyer_code_type NOT IN ('Purchasing_Order', 'Purchasing_Order_Data_Wipe'))
                      OR
                      (NOT rc.is_buyer
                       AND bc.buyer_code_type IN ('Premium_Wholesale', 'Wholesale'))
                  )
                ORDER BY bc.code
                """)
                .setParameter("userId", userId)
                .getResultList();

        return rows.stream()
                .map(r -> new BuyerCodeResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3]
                ))
                .toList();
    }

    /**
     * Validate that the given user is authorized to access the given buyer code.
     * Returns true if the user can access it based on:
     *   - Bidder: must have a link through user_buyers → buyer_code_buyers
     *   - Non-Bidder (Admin/SalesRep): can access any active buyer code
     */
    @Transactional(readOnly = true)
    public boolean isUserAuthorizedForBuyerCode(Long userId, Long buyerCodeId) {
        boolean isBuyerRole = isBuyerRole(userId);

        if (!isBuyerRole) {
            // Admin/SalesRep can access any active buyer code
            return true;
        }

        // Bidder: check the relationship chain exists
        Long count = (Long) em.createNativeQuery("""
                SELECT COUNT(*)
                FROM buyer_mgmt.buyer_codes bc
                JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                JOIN user_mgmt.user_buyers ub ON ub.buyer_id = bcb.buyer_id
                WHERE ub.user_id = :userId
                  AND bc.id = :buyerCodeId
                  AND bc.status = 'Active'
                  AND bc.soft_delete = false
                """, Long.class)
                .setParameter("userId", userId)
                .setParameter("buyerCodeId", buyerCodeId)
                .getSingleResult();

        return count > 0;
    }

    private boolean isBuyerRole(Long userId) {
        List<Boolean> result = em.createNativeQuery("""
                SELECT edu.is_buyer_role
                FROM user_mgmt.ecoatm_direct_users edu
                WHERE edu.user_id = :userId
                """, Boolean.class)
                .setParameter("userId", userId)
                .getResultList();

        return !result.isEmpty() && Boolean.TRUE.equals(result.get(0));
    }
}
