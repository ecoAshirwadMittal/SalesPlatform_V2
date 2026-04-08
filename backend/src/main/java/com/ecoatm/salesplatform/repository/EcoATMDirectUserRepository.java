package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.EcoATMDirectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoATMDirectUserRepository extends JpaRepository<EcoATMDirectUser, Long> {

    /**
     * Paginated + filtered query matching the EcoATMDirectUser_Overview grid.
     * Returns: userId, fullName, email, overallUserStatus, submissionId, buyers (comma-joined), roles (comma-joined)
     * Filters: name (contains), buyer (contains), roles (contains), email (contains), status (exact)
     */
    @Query(nativeQuery = true, value = """
        SELECT
            edu.user_id,
            a.full_name,
            a.email,
            edu.overall_user_status,
            edu.submission_id,
            COALESCE(buyers_agg.buyers, '') AS buyers,
            COALESCE(roles_agg.roles, '') AS roles,
            edu.changed_date
        FROM user_mgmt.ecoatm_direct_users edu
        JOIN identity.accounts a ON a.user_id = edu.user_id
        LEFT JOIN LATERAL (
            SELECT string_agg(b.company_name, ', ' ORDER BY b.company_name) AS buyers
            FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyers b ON b.id = ub.buyer_id
            WHERE ub.user_id = edu.user_id
        ) buyers_agg ON true
        LEFT JOIN LATERAL (
            SELECT string_agg(ur.name, ', ' ORDER BY ur.name) AS roles
            FROM identity.user_role_assignments ura
            JOIN identity.user_roles ur ON ur.id = ura.role_id
            WHERE ura.user_id = edu.user_id
        ) roles_agg ON true
        WHERE (:name IS NULL OR LOWER(a.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:buyer IS NULL OR LOWER(COALESCE(buyers_agg.buyers, '')) LIKE LOWER(CONCAT('%', :buyer, '%')))
          AND (:roles IS NULL OR LOWER(COALESCE(roles_agg.roles, '')) LIKE LOWER(CONCAT('%', :roles, '%')))
          AND (:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:status IS NULL OR edu.overall_user_status = :status)
        ORDER BY a.full_name
        LIMIT :pageSize OFFSET :offset
    """)
    List<Object[]> findDirectUsersFiltered(
            @Param("name") String name,
            @Param("buyer") String buyer,
            @Param("roles") String roles,
            @Param("email") String email,
            @Param("status") String status,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );

    @Query(nativeQuery = true, value = """
        SELECT
            edu.user_id,
            edu.first_name,
            edu.last_name,
            a.full_name,
            a.email,
            edu.overall_user_status,
            edu.user_status,
            edu.inactive,
            a.is_local_user,
            edu.is_buyer_role,
            edu.landing_page_preference,
            edu.invited_date,
            edu.last_invite_sent,
            edu.activation_date,
            u.last_login
        FROM user_mgmt.ecoatm_direct_users edu
        JOIN identity.accounts a ON a.user_id = edu.user_id
        JOIN identity.users u ON u.id = edu.user_id
        WHERE edu.user_id = :userId
    """)
    List<Object[]> findDirectUserDetail(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT role_id FROM identity.user_role_assignments WHERE user_id = :userId
    """)
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT buyer_id FROM user_mgmt.user_buyers WHERE user_id = :userId
    """)
    List<Long> findBuyerIdsByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT id, name FROM identity.user_roles ORDER BY name
    """)
    List<Object[]> findAllRoles();

    @Query(nativeQuery = true, value = """
        SELECT id, company_name FROM buyer_mgmt.buyers ORDER BY company_name
    """)
    List<Object[]> findAllBuyers();

    @Query(nativeQuery = true, value = """
        SELECT COUNT(*)
        FROM user_mgmt.ecoatm_direct_users edu
        JOIN identity.accounts a ON a.user_id = edu.user_id
        LEFT JOIN LATERAL (
            SELECT string_agg(b.company_name, ', ' ORDER BY b.company_name) AS buyers
            FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyers b ON b.id = ub.buyer_id
            WHERE ub.user_id = edu.user_id
        ) buyers_agg ON true
        LEFT JOIN LATERAL (
            SELECT string_agg(ur.name, ', ' ORDER BY ur.name) AS roles
            FROM identity.user_role_assignments ura
            JOIN identity.user_roles ur ON ur.id = ura.role_id
            WHERE ura.user_id = edu.user_id
        ) roles_agg ON true
        WHERE (:name IS NULL OR LOWER(a.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:buyer IS NULL OR LOWER(COALESCE(buyers_agg.buyers, '')) LIKE LOWER(CONCAT('%', :buyer, '%')))
          AND (:roles IS NULL OR LOWER(COALESCE(roles_agg.roles, '')) LIKE LOWER(CONCAT('%', :roles, '%')))
          AND (:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:status IS NULL OR edu.overall_user_status = :status)
    """)
    long countDirectUsersFiltered(
            @Param("name") String name,
            @Param("buyer") String buyer,
            @Param("roles") String roles,
            @Param("email") String email,
            @Param("status") String status
    );
}
