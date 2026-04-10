package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectUserService {

    private final EcoATMDirectUserRepository directUserRepository;
    private final EntityManager em;

    // ── List (existing) ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DirectUserPageResponse getDirectUsers(
            String name, String buyer, String roles, String email, String status,
            int page, int pageSize) {

        name = blankToNull(name);
        buyer = blankToNull(buyer);
        roles = blankToNull(roles);
        email = blankToNull(email);
        status = blankToNull(status);

        int offset = page * pageSize;

        List<Object[]> rows = directUserRepository.findDirectUsersFiltered(
                name, buyer, roles, email, status, pageSize, offset);

        long totalElements = directUserRepository.countDirectUsersFiltered(
                name, buyer, roles, email, status);

        List<DirectUserListResponse> content = rows.stream()
                .map(this::mapListRow)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new DirectUserPageResponse(content, page, pageSize, totalElements, totalPages);
    }

    // ── Detail ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DirectUserDetailResponse getDirectUserDetail(Long userId) {
        List<Object[]> rows = directUserRepository.findDirectUserDetail(userId);
        if (rows.isEmpty()) {
            throw new RuntimeException("User not found: " + userId);
        }
        Object[] row = rows.get(0);
        List<Long> roleIds = directUserRepository.findRoleIdsByUserId(userId);
        List<Long> buyerIds = directUserRepository.findBuyerIdsByUserId(userId);

        DirectUserDetailResponse dto = new DirectUserDetailResponse();
        dto.setUserId(((Number) row[0]).longValue());
        dto.setFirstName((String) row[1]);
        dto.setLastName((String) row[2]);
        dto.setFullName((String) row[3]);
        dto.setEmail((String) row[4]);
        dto.setOverallUserStatus((String) row[5]);
        dto.setUserStatus((String) row[6]);
        dto.setInactive(row[7] != null && (Boolean) row[7]);
        dto.setLocalUser(row[8] != null && (Boolean) row[8]);
        dto.setBuyerRole(row[9] != null && (Boolean) row[9]);
        dto.setLandingPagePreference((String) row[10]);
        dto.setInvitedDate(toLocalDateTime(row[11]));
        dto.setLastInviteSent(toLocalDateTime(row[12]));
        dto.setActivationDate(toLocalDateTime(row[13]));
        dto.setLastLogin(toLocalDateTime(row[14]));
        dto.setRoleIds(roleIds);
        dto.setBuyerIds(buyerIds);
        return dto;
    }

    // ── Roles & Buyers lookup ────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return directUserRepository.findAllRoles().stream()
                .map(r -> new RoleResponse(((Number) r[0]).longValue(), (String) r[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BuyerResponse> getAllBuyers() {
        return directUserRepository.findAllBuyers().stream()
                .map(r -> new BuyerResponse(((Number) r[0]).longValue(), (String) r[1]))
                .collect(Collectors.toList());
    }

    // ── Create ───────────────────────────────────────────────────────

    @Transactional
    public DirectUserDetailResponse createDirectUser(DirectUserSaveRequest req) {
        String fullName = (req.getFirstName() + " " + req.getLastName()).trim();
        boolean isLocal = req.getEmail() != null && !req.getEmail().endsWith("@ecoatm.com");

        // 1. Insert into identity.users
        em.createNativeQuery("""
            INSERT INTO identity.users (id, user_type, name, active, created_date, changed_date)
            VALUES (nextval('identity.users_id_seq'), 'EcoATM_UserManagement.EcoATMDirectUser',
                    :name, true, NOW(), NOW())
            """)
                .setParameter("name", req.getEmail())
                .executeUpdate();

        // Get the generated user ID
        Long userId = ((Number) em.createNativeQuery(
                "SELECT currval('identity.users_id_seq')").getSingleResult()).longValue();

        // 2. Insert into identity.accounts
        em.createNativeQuery("""
            INSERT INTO identity.accounts (user_id, full_name, email, is_local_user)
            VALUES (:userId, :fullName, :email, :isLocal)
            """)
                .setParameter("userId", userId)
                .setParameter("fullName", fullName)
                .setParameter("email", req.getEmail())
                .setParameter("isLocal", isLocal)
                .executeUpdate();

        // 3. Insert into user_mgmt.ecoatm_direct_users
        String landingPage = req.getLandingPagePreference() != null
                ? req.getLandingPagePreference() : "Wholesale_Auction";
        String status = req.getUserStatus() != null ? req.getUserStatus() : "Active";
        boolean hasBidderRole = hasBidderRole(req.getRoleIds());

        em.createNativeQuery("""
            INSERT INTO user_mgmt.ecoatm_direct_users
                (user_id, first_name, last_name, landing_page_preference,
                 user_status, overall_user_status, inactive, is_buyer_role,
                 created_date, changed_date)
            VALUES (:userId, :firstName, :lastName, :landingPage,
                    :status, :status, false, :isBuyerRole,
                    NOW(), NOW())
            """)
                .setParameter("userId", userId)
                .setParameter("firstName", req.getFirstName())
                .setParameter("lastName", req.getLastName())
                .setParameter("landingPage", landingPage)
                .setParameter("status", status)
                .setParameter("isBuyerRole", hasBidderRole)
                .executeUpdate();

        // 4. Insert role assignments
        insertRoleAssignments(userId, req.getRoleIds());

        // 5. Insert buyer associations
        insertBuyerAssociations(userId, req.getBuyerIds());

        em.flush();
        em.clear();
        return getDirectUserDetail(userId);
    }

    // ── Update ───────────────────────────────────────────────────────

    @Transactional
    public DirectUserDetailResponse updateDirectUser(Long userId, DirectUserSaveRequest req) {
        String fullName = (req.getFirstName() + " " + req.getLastName()).trim();
        boolean hasBidderRole = hasBidderRole(req.getRoleIds());

        // Compute overall_user_status: Inactive if inactive flag, else user_status
        String overallStatus = req.isInactive() ? "Inactive"
                : (req.getUserStatus() != null ? req.getUserStatus() : "Active");

        // 1. Update accounts
        em.createNativeQuery("""
            UPDATE identity.accounts SET full_name = :fullName, email = :email
            WHERE user_id = :userId
            """)
                .setParameter("fullName", fullName)
                .setParameter("email", req.getEmail())
                .setParameter("userId", userId)
                .executeUpdate();

        // 2. Update identity.users name (login name = email)
        em.createNativeQuery("""
            UPDATE identity.users SET name = :name, changed_date = NOW()
            WHERE id = :userId
            """)
                .setParameter("name", req.getEmail())
                .setParameter("userId", userId)
                .executeUpdate();

        // 3. Update ecoatm_direct_users
        em.createNativeQuery("""
            UPDATE user_mgmt.ecoatm_direct_users SET
                first_name = :firstName, last_name = :lastName,
                user_status = :userStatus, inactive = :inactive,
                overall_user_status = :overallStatus,
                landing_page_preference = :landingPage,
                is_buyer_role = :isBuyerRole,
                changed_date = NOW()
            WHERE user_id = :userId
            """)
                .setParameter("firstName", req.getFirstName())
                .setParameter("lastName", req.getLastName())
                .setParameter("userStatus", req.getUserStatus())
                .setParameter("inactive", req.isInactive())
                .setParameter("overallStatus", overallStatus)
                .setParameter("landingPage", req.getLandingPagePreference())
                .setParameter("isBuyerRole", hasBidderRole)
                .setParameter("userId", userId)
                .executeUpdate();

        // 4. Replace role assignments
        em.createNativeQuery("DELETE FROM identity.user_role_assignments WHERE user_id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        insertRoleAssignments(userId, req.getRoleIds());

        // 5. Replace buyer associations
        em.createNativeQuery("DELETE FROM user_mgmt.user_buyers WHERE user_id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        insertBuyerAssociations(userId, req.getBuyerIds());

        em.flush();
        em.clear();
        return getDirectUserDetail(userId);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void insertRoleAssignments(Long userId, List<Long> roleIds) {
        if (roleIds == null) return;
        for (Long roleId : roleIds) {
            em.createNativeQuery("""
                INSERT INTO identity.user_role_assignments (user_id, role_id)
                VALUES (:userId, :roleId)
                """)
                    .setParameter("userId", userId)
                    .setParameter("roleId", roleId)
                    .executeUpdate();
        }
    }

    private void insertBuyerAssociations(Long userId, List<Long> buyerIds) {
        if (buyerIds == null) return;
        for (Long buyerId : buyerIds) {
            em.createNativeQuery("""
                INSERT INTO user_mgmt.user_buyers (user_id, buyer_id)
                VALUES (:userId, :buyerId)
                """)
                    .setParameter("userId", userId)
                    .setParameter("buyerId", buyerId)
                    .executeUpdate();
        }
    }

    private boolean hasBidderRole(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return false;
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM identity.user_roles WHERE id IN (:roleIds) AND LOWER(name) = 'bidder'")
                .setParameter("roleIds", roleIds)
                .getSingleResult();
        return count.intValue() > 0;
    }

    private DirectUserListResponse mapListRow(Object[] row) {
        LocalDate changedDate = null;
        if (row[7] != null) {
            changedDate = ((Timestamp) row[7]).toLocalDateTime().toLocalDate();
        }
        return new DirectUserListResponse(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                row[4] != null ? ((Number) row[4]).longValue() : null,
                (String) row[5],
                (String) row[6],
                changedDate
        );
    }

    private LocalDateTime toLocalDateTime(Object val) {
        if (val == null) return null;
        if (val instanceof Timestamp ts) return ts.toLocalDateTime();
        if (val instanceof LocalDateTime ldt) return ldt;
        return null;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
