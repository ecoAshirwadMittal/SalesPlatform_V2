package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectUserServiceTest {

    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private EntityManager em;

    @InjectMocks
    private DirectUserService directUserService;

    @SuppressWarnings("unchecked")
    private static List<Object[]> rowList(Object[]... rows) {
        List<Object[]> list = new ArrayList<>();
        Collections.addAll(list, rows);
        return list;
    }


    // ── getDirectUsers ──────────────────────────────────────────────

    @Nested
    @DisplayName("getDirectUsers")
    class GetDirectUsers {

        @Test
        @DisplayName("returns paginated results with correct metadata")
        void getDirectUsers_returnsPaginatedResults() {
            Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2025, 1, 1, 0, 0));
            Object[] row = new Object[]{1L, "John Doe", "john@test.com", "Active", null, "Buyer Co", "Bidder", ts};
            when(directUserRepository.findDirectUsersFiltered(null, null, null, null, null, 10, 0))
                    .thenReturn(rowList(row));
            when(directUserRepository.countDirectUsersFiltered(null, null, null, null, null))
                    .thenReturn(1L);

            DirectUserPageResponse response = directUserService.getDirectUsers(null, null, null, null, null, 0, 10);

            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
            assertThat(response.getTotalPages()).isEqualTo(1);
            assertThat(response.getPage()).isZero();
        }

        @Test
        @DisplayName("blank filters are converted to null")
        void getDirectUsers_blankFiltersConvertedToNull() {
            when(directUserRepository.findDirectUsersFiltered(null, null, null, null, null, 10, 0))
                    .thenReturn(Collections.emptyList());
            when(directUserRepository.countDirectUsersFiltered(null, null, null, null, null))
                    .thenReturn(0L);

            DirectUserPageResponse response = directUserService.getDirectUsers("", " ", "", "", "", 0, 10);

            assertThat(response.getContent()).isEmpty();
            verify(directUserRepository).findDirectUsersFiltered(null, null, null, null, null, 10, 0);
        }

        @Test
        @DisplayName("pagination offset calculated correctly")
        void getDirectUsers_paginationOffset() {
            when(directUserRepository.findDirectUsersFiltered(null, null, null, null, null, 20, 40))
                    .thenReturn(Collections.emptyList());
            when(directUserRepository.countDirectUsersFiltered(null, null, null, null, null))
                    .thenReturn(50L);

            DirectUserPageResponse response = directUserService.getDirectUsers(null, null, null, null, null, 2, 20);

            assertThat(response.getPage()).isEqualTo(2);
            assertThat(response.getPageSize()).isEqualTo(20);
            assertThat(response.getTotalPages()).isEqualTo(3);
        }
    }

    // ── getDirectUserDetail ─────────────────────────────────────────

    @Nested
    @DisplayName("getDirectUserDetail")
    class GetDirectUserDetail {

        @Test
        @DisplayName("returns user detail with roles and buyers")
        void getDirectUserDetail_returnsDetail() {
            Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2025, 1, 1, 0, 0));
            Object[] row = new Object[]{
                1L, "John", "Doe", "John Doe", "john@test.com",
                "Active", "Active", false, true, false,
                "Wholesale_Auction", ts, ts, ts, ts
            };
            when(directUserRepository.findDirectUserDetail(1L)).thenReturn(rowList(row));
            when(directUserRepository.findRoleIdsByUserId(1L)).thenReturn(List.of(1L, 2L));
            when(directUserRepository.findBuyerIdsByUserId(1L)).thenReturn(List.of(10L));

            DirectUserDetailResponse detail = directUserService.getDirectUserDetail(1L);

            assertThat(detail.getUserId()).isEqualTo(1L);
            assertThat(detail.getFirstName()).isEqualTo("John");
            assertThat(detail.getLastName()).isEqualTo("Doe");
            assertThat(detail.getEmail()).isEqualTo("john@test.com");
            assertThat(detail.getRoleIds()).containsExactly(1L, 2L);
            assertThat(detail.getBuyerIds()).containsExactly(10L);
        }

        @Test
        @DisplayName("user not found throws RuntimeException")
        void getDirectUserDetail_notFound_throws() {
            when(directUserRepository.findDirectUserDetail(999L)).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> directUserService.getDirectUserDetail(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }
    }

    // ── getAllRoles / getAllBuyers ────────────────────────────────────

    @Nested
    @DisplayName("lookup methods")
    class Lookups {

        @Test
        @DisplayName("getAllRoles maps rows to RoleResponse")
        void getAllRoles_mapsCorrectly() {
            Object[] role1 = new Object[]{1L, "Admin"};
            Object[] role2 = new Object[]{2L, "Bidder"};
            when(directUserRepository.findAllRoles()).thenReturn(rowList(role1, role2));

            List<RoleResponse> roles = directUserService.getAllRoles();

            assertThat(roles).hasSize(2);
            assertThat(roles.get(0).getId()).isEqualTo(1L);
            assertThat(roles.get(0).getName()).isEqualTo("Admin");
        }

        @Test
        @DisplayName("getAllBuyers maps rows to BuyerResponse")
        void getAllBuyers_mapsCorrectly() {
            Object[] buyer = new Object[]{10L, "Acme Corp"};
            when(directUserRepository.findAllBuyers()).thenReturn(rowList(buyer));

            List<BuyerResponse> buyers = directUserService.getAllBuyers();

            assertThat(buyers).hasSize(1);
            assertThat(buyers.get(0).getCompanyName()).isEqualTo("Acme Corp");
        }
    }

    // ── createDirectUser ────────────────────────────────────────────

    @Nested
    @DisplayName("createDirectUser")
    class CreateDirectUser {

        @Test
        @DisplayName("creates user with all required inserts")
        @SuppressWarnings("unchecked")
        void createDirectUser_executesAllInserts() {
            DirectUserSaveRequest req = new DirectUserSaveRequest();
            req.setFirstName("Jane");
            req.setLastName("Smith");
            req.setEmail("jane@test.com");
            req.setRoleIds(List.of(1L));
            req.setBuyerIds(List.of(10L));

            // Stub all native queries for insert
            Query insertUserQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO identity.users"))).thenReturn(insertUserQuery);
            when(insertUserQuery.setParameter(anyString(), any())).thenReturn(insertUserQuery);
            when(insertUserQuery.executeUpdate()).thenReturn(1);

            Query seqQuery = mock(Query.class);
            when(em.createNativeQuery(contains("currval"))).thenReturn(seqQuery);
            when(seqQuery.getSingleResult()).thenReturn(42L);

            Query insertAccountQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO identity.accounts"))).thenReturn(insertAccountQuery);
            when(insertAccountQuery.setParameter(anyString(), any())).thenReturn(insertAccountQuery);
            when(insertAccountQuery.executeUpdate()).thenReturn(1);

            Query insertDirectUserQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO user_mgmt.ecoatm_direct_users"))).thenReturn(insertDirectUserQuery);
            when(insertDirectUserQuery.setParameter(anyString(), any())).thenReturn(insertDirectUserQuery);
            when(insertDirectUserQuery.executeUpdate()).thenReturn(1);

            Query insertRoleQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO identity.user_role_assignments"))).thenReturn(insertRoleQuery);
            when(insertRoleQuery.setParameter(anyString(), any())).thenReturn(insertRoleQuery);
            when(insertRoleQuery.executeUpdate()).thenReturn(1);

            Query insertBuyerQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO user_mgmt.user_buyers"))).thenReturn(insertBuyerQuery);
            when(insertBuyerQuery.setParameter(anyString(), any())).thenReturn(insertBuyerQuery);
            when(insertBuyerQuery.executeUpdate()).thenReturn(1);

            // hasBidderRole check
            Query bidderCheckQuery = mock(Query.class);
            when(em.createNativeQuery(contains("LOWER(name) = 'bidder'"))).thenReturn(bidderCheckQuery);
            when(bidderCheckQuery.setParameter(anyString(), any())).thenReturn(bidderCheckQuery);
            when(bidderCheckQuery.getSingleResult()).thenReturn(0L);

            // Stub getDirectUserDetail for the return
            Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2025, 1, 1, 0, 0));
            Object[] detailRow = new Object[]{
                42L, "Jane", "Smith", "Jane Smith", "jane@test.com",
                "Active", "Active", false, true, false,
                "Wholesale_Auction", ts, ts, ts, ts
            };
            when(directUserRepository.findDirectUserDetail(42L)).thenReturn(rowList(detailRow));
            when(directUserRepository.findRoleIdsByUserId(42L)).thenReturn(List.of(1L));
            when(directUserRepository.findBuyerIdsByUserId(42L)).thenReturn(List.of(10L));

            DirectUserDetailResponse result = directUserService.createDirectUser(req);

            assertThat(result.getUserId()).isEqualTo(42L);
            assertThat(result.getFirstName()).isEqualTo("Jane");
            verify(em).flush();
            verify(em).clear();
        }
    }

    // ── updateDirectUser ────────────────────────────────────────────

    @Nested
    @DisplayName("updateDirectUser")
    class UpdateDirectUser {

        @Test
        @DisplayName("updates user and replaces roles and buyers")
        @SuppressWarnings("unchecked")
        void updateDirectUser_executesAllUpdates() {
            DirectUserSaveRequest req = new DirectUserSaveRequest();
            req.setFirstName("Updated");
            req.setLastName("User");
            req.setEmail("updated@test.com");
            req.setUserStatus("Active");
            req.setInactive(false);
            req.setLandingPagePreference("Wholesale_Auction");
            req.setRoleIds(List.of(2L));
            req.setBuyerIds(List.of(20L));

            // hasBidderRole check
            Query bidderCheckQuery = mock(Query.class);
            when(em.createNativeQuery(contains("LOWER(name) = 'bidder'"))).thenReturn(bidderCheckQuery);
            when(bidderCheckQuery.setParameter(anyString(), any())).thenReturn(bidderCheckQuery);
            when(bidderCheckQuery.getSingleResult()).thenReturn(0L);

            // Update queries
            Query updateAccountQuery = mock(Query.class);
            when(em.createNativeQuery(contains("UPDATE identity.accounts"))).thenReturn(updateAccountQuery);
            when(updateAccountQuery.setParameter(anyString(), any())).thenReturn(updateAccountQuery);
            when(updateAccountQuery.executeUpdate()).thenReturn(1);

            Query updateUserQuery = mock(Query.class);
            when(em.createNativeQuery(contains("UPDATE identity.users"))).thenReturn(updateUserQuery);
            when(updateUserQuery.setParameter(anyString(), any())).thenReturn(updateUserQuery);
            when(updateUserQuery.executeUpdate()).thenReturn(1);

            Query updateDirectUserQuery = mock(Query.class);
            when(em.createNativeQuery(contains("UPDATE user_mgmt.ecoatm_direct_users"))).thenReturn(updateDirectUserQuery);
            when(updateDirectUserQuery.setParameter(anyString(), any())).thenReturn(updateDirectUserQuery);
            when(updateDirectUserQuery.executeUpdate()).thenReturn(1);

            Query deleteRolesQuery = mock(Query.class);
            when(em.createNativeQuery(contains("DELETE FROM identity.user_role_assignments"))).thenReturn(deleteRolesQuery);
            when(deleteRolesQuery.setParameter(anyString(), any())).thenReturn(deleteRolesQuery);
            when(deleteRolesQuery.executeUpdate()).thenReturn(1);

            Query insertRoleQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO identity.user_role_assignments"))).thenReturn(insertRoleQuery);
            when(insertRoleQuery.setParameter(anyString(), any())).thenReturn(insertRoleQuery);
            when(insertRoleQuery.executeUpdate()).thenReturn(1);

            Query deleteBuyersQuery = mock(Query.class);
            when(em.createNativeQuery(contains("DELETE FROM user_mgmt.user_buyers"))).thenReturn(deleteBuyersQuery);
            when(deleteBuyersQuery.setParameter(anyString(), any())).thenReturn(deleteBuyersQuery);
            when(deleteBuyersQuery.executeUpdate()).thenReturn(1);

            Query insertBuyerQuery = mock(Query.class);
            when(em.createNativeQuery(contains("INSERT INTO user_mgmt.user_buyers"))).thenReturn(insertBuyerQuery);
            when(insertBuyerQuery.setParameter(anyString(), any())).thenReturn(insertBuyerQuery);
            when(insertBuyerQuery.executeUpdate()).thenReturn(1);

            // Stub getDirectUserDetail for the return
            Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2025, 1, 1, 0, 0));
            Object[] detailRow = new Object[]{
                1L, "Updated", "User", "Updated User", "updated@test.com",
                "Active", "Active", false, true, false,
                "Wholesale_Auction", ts, ts, ts, ts
            };
            when(directUserRepository.findDirectUserDetail(1L)).thenReturn(rowList(detailRow));
            when(directUserRepository.findRoleIdsByUserId(1L)).thenReturn(List.of(2L));
            when(directUserRepository.findBuyerIdsByUserId(1L)).thenReturn(List.of(20L));

            DirectUserDetailResponse result = directUserService.updateDirectUser(1L, req);

            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("Updated");
            verify(em).flush();
            verify(em).clear();
        }
    }
}
