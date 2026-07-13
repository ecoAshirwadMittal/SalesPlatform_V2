package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.identity.GrantableRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Grant-authorization resolver over {@code identity.grantable_roles} +
 * {@code identity.user_roles}.
 *
 * <p>Answers "which roles may this caller assign?" for the user-provisioning
 * guard in {@code DirectUserService}. Resolution is deliberately by role NAME
 * (the caller's verified JWT authorities) → {@code user_roles.id}, so a seeded
 * {@code Administrator} always maps to the grantor row that owns the grantable
 * matrix (id 1) regardless of any dev(V15)/QA(V16) role-id skew; then
 * grantor-id → the union of permitted grantee-ids.
 */
@Repository
public interface GrantableRoleRepository
        extends JpaRepository<GrantableRole, GrantableRole.GrantableRoleId> {

    /**
     * Resolves {@code user_roles.id} for a set of role names — maps the caller's
     * authenticated role-name authorities to grantor role-ids robustly (by name,
     * never by a possibly-skewed raw id).
     */
    @Query(nativeQuery = true, value = """
        SELECT id FROM identity.user_roles WHERE name IN (:roleNames)
    """)
    List<Long> findRoleIdsByNames(@Param("roleNames") Collection<String> roleNames);

    /**
     * The union of grantee role-ids that the given grantor role-ids may assign.
     */
    @Query(nativeQuery = true, value = """
        SELECT grantee_role_id FROM identity.grantable_roles
        WHERE grantor_role_id IN (:grantorRoleIds)
    """)
    List<Long> findGranteeRoleIds(@Param("grantorRoleIds") Collection<Long> grantorRoleIds);
}
