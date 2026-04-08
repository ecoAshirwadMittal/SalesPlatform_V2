package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerCodeServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BuyerCodeService buyerCodeService;

    // ── helpers ──────────────────────────────────────────────────────────

    /**
     * Stubs the isBuyerRole native query (reads is_buyer_role from ecoatm_direct_users).
     */
    @SuppressWarnings("unchecked")
    private void stubIsBuyerRole(boolean isBuyer) {
        TypedQuery<Boolean> roleQuery = mock(TypedQuery.class);
        when(em.createNativeQuery(contains("is_buyer_role"), eq(Boolean.class)))
                .thenReturn(roleQuery);
        when(roleQuery.setParameter(eq("userId"), anyLong())).thenReturn(roleQuery);
        if (isBuyer) {
            when(roleQuery.getResultList()).thenReturn(List.of(Boolean.TRUE));
        } else {
            when(roleQuery.getResultList()).thenReturn(Collections.emptyList());
        }
    }

    /**
     * Stubs the buyer-codes list query for getBuyerCodesForUser.
     * Returns a Query mock so callers can set up getResultList on it.
     */
    @SuppressWarnings("unchecked")
    private Query stubBuyerCodesQuery(boolean isBidder) {
        Query codesQuery = mock(Query.class);
        if (isBidder) {
            // Bidder path contains "ub.user_id"
            when(em.createNativeQuery(contains("ub.user_id"))).thenReturn(codesQuery);
            when(codesQuery.setParameter(eq("userId"), anyLong())).thenReturn(codesQuery);
        } else {
            // Admin path contains "Premium_Wholesale"
            when(em.createNativeQuery(contains("Premium_Wholesale"))).thenReturn(codesQuery);
        }
        return codesQuery;
    }

    private static long anyLong() {
        return org.mockito.ArgumentMatchers.anyLong();
    }

    // ── getBuyerCodesForUser ─────────────────────────────────────────────

    @Nested
    @DisplayName("getBuyerCodesForUser")
    class GetBuyerCodesForUser {

        @Test
        @DisplayName("bidder user - returns only linked buyer codes")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_bidderUser_returnsLinkedBuyerCodes() {
            // Arrange
            stubIsBuyerRole(true);
            Query codesQuery = stubBuyerCodesQuery(true);

            List<Object[]> mockRows = List.of(
                    new Object[]{1L, "BC-001", "Acme Corp", "Wholesale"},
                    new Object[]{2L, "BC-002", "Acme Corp", "Premium_Wholesale"}
            );
            when(codesQuery.getResultList()).thenReturn(mockRows);

            // Act
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(10L);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getCode()).isEqualTo("BC-001");
            assertThat(result.get(0).getBuyerName()).isEqualTo("Acme Corp");
            assertThat(result.get(0).getBuyerCodeType()).isEqualTo("Wholesale");
            assertThat(result.get(1).getCode()).isEqualTo("BC-002");
        }

        @Test
        @DisplayName("admin user - returns all active buyer codes")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_adminUser_returnsAllBuyerCodes() {
            // Arrange
            stubIsBuyerRole(false);
            Query codesQuery = stubBuyerCodesQuery(false);

            List<Object[]> mockRows = List.of(
                    new Object[]{10L, "ALL-001", "Global Inc", "Premium_Wholesale"},
                    new Object[]{11L, "ALL-002", "Widget Co", "Wholesale"},
                    new Object[]{12L, "ALL-003", "Parts LLC", "Premium_Wholesale"}
            );
            when(codesQuery.getResultList()).thenReturn(mockRows);

            // Act
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(1L);

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getId()).isEqualTo(10L);
            assertThat(result.get(2).getBuyerName()).isEqualTo("Parts LLC");
        }

        @Test
        @DisplayName("bidder with no linked codes - returns empty list")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_bidderNoLinkedCodes_returnsEmptyList() {
            // Arrange
            stubIsBuyerRole(true);
            Query codesQuery = stubBuyerCodesQuery(true);
            when(codesQuery.getResultList()).thenReturn(Collections.emptyList());

            // Act
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(99L);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("admin with no codes in system - returns empty list")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_adminNoCodesExist_returnsEmptyList() {
            // Arrange
            stubIsBuyerRole(false);
            Query codesQuery = stubBuyerCodesQuery(false);
            when(codesQuery.getResultList()).thenReturn(Collections.emptyList());

            // Act
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(1L);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ── isUserAuthorizedForBuyerCode ─────────────────────────────────────

    @Nested
    @DisplayName("isUserAuthorizedForBuyerCode")
    class IsUserAuthorizedForBuyerCode {

        @Test
        @DisplayName("admin user - always returns true without checking link")
        void isUserAuthorizedForBuyerCode_adminUser_alwaysReturnsTrue() {
            // Arrange
            stubIsBuyerRole(false);

            // Act
            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(1L, 999L);

            // Assert
            assertThat(authorized).isTrue();
        }

        @Test
        @DisplayName("bidder with access - returns true")
        @SuppressWarnings("unchecked")
        void isUserAuthorizedForBuyerCode_bidderWithAccess_returnsTrue() {
            // Arrange
            stubIsBuyerRole(true);

            TypedQuery<Long> countQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("COUNT(*)"), eq(Long.class)))
                    .thenReturn(countQuery);
            when(countQuery.setParameter(eq("userId"), eq(10L))).thenReturn(countQuery);
            when(countQuery.setParameter(eq("buyerCodeId"), eq(5L))).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(1L);

            // Act
            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(10L, 5L);

            // Assert
            assertThat(authorized).isTrue();
        }

        @Test
        @DisplayName("bidder without access - returns false")
        @SuppressWarnings("unchecked")
        void isUserAuthorizedForBuyerCode_bidderWithoutAccess_returnsFalse() {
            // Arrange
            stubIsBuyerRole(true);

            TypedQuery<Long> countQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("COUNT(*)"), eq(Long.class)))
                    .thenReturn(countQuery);
            when(countQuery.setParameter(eq("userId"), eq(10L))).thenReturn(countQuery);
            when(countQuery.setParameter(eq("buyerCodeId"), eq(999L))).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(0L);

            // Act
            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(10L, 999L);

            // Assert
            assertThat(authorized).isFalse();
        }
    }

    // ── isBuyerRole (tested indirectly via getBuyerCodesForUser paths) ───

    @Nested
    @DisplayName("isBuyerRole (indirect)")
    class IsBuyerRole {

        @Test
        @DisplayName("user with is_buyer_role=true - bidder path is taken")
        @SuppressWarnings("unchecked")
        void isBuyerRole_userWithBidderRole_returnsTrue() {
            // Arrange - stub role query to return true
            TypedQuery<Boolean> roleQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("is_buyer_role"), eq(Boolean.class)))
                    .thenReturn(roleQuery);
            when(roleQuery.setParameter(eq("userId"), eq(42L))).thenReturn(roleQuery);
            when(roleQuery.getResultList()).thenReturn(List.of(Boolean.TRUE));

            // Stub the bidder buyer-codes query
            Query codesQuery = mock(Query.class);
            when(em.createNativeQuery(contains("ub.user_id"))).thenReturn(codesQuery);
            when(codesQuery.setParameter(eq("userId"), eq(42L))).thenReturn(codesQuery);
            when(codesQuery.getResultList()).thenReturn(Collections.emptyList());

            // Act - call getBuyerCodesForUser which internally calls isBuyerRole
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(42L);

            // Assert - bidder path was taken (empty result, but no error = correct path)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("user without bidder role - admin path is taken")
        @SuppressWarnings("unchecked")
        void isBuyerRole_userWithoutBidderRole_returnsFalse() {
            // Arrange - stub role query to return empty (not a bidder)
            TypedQuery<Boolean> roleQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("is_buyer_role"), eq(Boolean.class)))
                    .thenReturn(roleQuery);
            when(roleQuery.setParameter(eq("userId"), eq(1L))).thenReturn(roleQuery);
            when(roleQuery.getResultList()).thenReturn(Collections.emptyList());

            // Stub the admin buyer-codes query
            Query codesQuery = mock(Query.class);
            when(em.createNativeQuery(contains("Premium_Wholesale"))).thenReturn(codesQuery);
            when(codesQuery.getResultList()).thenReturn(Collections.emptyList());

            // Act
            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(1L);

            // Assert - admin path was taken (empty, no error)
            assertThat(result).isEmpty();
        }
    }
}
