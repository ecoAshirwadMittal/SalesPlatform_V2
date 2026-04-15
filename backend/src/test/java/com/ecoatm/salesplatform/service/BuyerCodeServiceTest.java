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
     * Stubs the isBuyerRole native query (used by isUserAuthorizedForBuyerCode).
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
     * Stubs the CTE query used by getBuyerCodesForUser (single combined query).
     */
    @SuppressWarnings("unchecked")
    private Query stubCteQuery() {
        Query cteQuery = mock(Query.class);
        // The CTE query contains "role_check" — use that as the matcher
        when(em.createNativeQuery(contains("role_check"))).thenReturn(cteQuery);
        when(cteQuery.setParameter(eq("userId"), anyLong())).thenReturn(cteQuery);
        return cteQuery;
    }

    private static long anyLong() {
        return org.mockito.ArgumentMatchers.anyLong();
    }

    // ── getBuyerCodesForUser ─────────────────────────────────────────────

    @Nested
    @DisplayName("getBuyerCodesForUser")
    class GetBuyerCodesForUser {

        @Test
        @DisplayName("returns buyer codes from CTE query result")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_returnsCodes() {
            Query cteQuery = stubCteQuery();

            List<Object[]> mockRows = List.of(
                    new Object[]{1L, "BC-001", "Acme Corp", "Wholesale"},
                    new Object[]{2L, "BC-002", "Acme Corp", "Premium_Wholesale"}
            );
            when(cteQuery.getResultList()).thenReturn(mockRows);

            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(10L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getCode()).isEqualTo("BC-001");
            assertThat(result.get(0).getBuyerName()).isEqualTo("Acme Corp");
            assertThat(result.get(0).getBuyerCodeType()).isEqualTo("Wholesale");
            assertThat(result.get(1).getCode()).isEqualTo("BC-002");
        }

        @Test
        @DisplayName("returns multiple buyer codes for admin user")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_adminUser_returnsAllBuyerCodes() {
            Query cteQuery = stubCteQuery();

            List<Object[]> mockRows = List.of(
                    new Object[]{10L, "ALL-001", "Global Inc", "Premium_Wholesale"},
                    new Object[]{11L, "ALL-002", "Widget Co", "Wholesale"},
                    new Object[]{12L, "ALL-003", "Parts LLC", "Premium_Wholesale"}
            );
            when(cteQuery.getResultList()).thenReturn(mockRows);

            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(1L);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getId()).isEqualTo(10L);
            assertThat(result.get(2).getBuyerName()).isEqualTo("Parts LLC");
        }

        @Test
        @DisplayName("returns empty list when no codes match")
        @SuppressWarnings("unchecked")
        void getBuyerCodesForUser_noMatchingCodes_returnsEmptyList() {
            Query cteQuery = stubCteQuery();
            when(cteQuery.getResultList()).thenReturn(Collections.emptyList());

            List<BuyerCodeResponse> result = buyerCodeService.getBuyerCodesForUser(99L);

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
            stubIsBuyerRole(false);

            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(1L, 999L);

            assertThat(authorized).isTrue();
        }

        @Test
        @DisplayName("bidder with access - returns true")
        @SuppressWarnings("unchecked")
        void isUserAuthorizedForBuyerCode_bidderWithAccess_returnsTrue() {
            stubIsBuyerRole(true);

            TypedQuery<Long> countQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("COUNT(*)"), eq(Long.class)))
                    .thenReturn(countQuery);
            when(countQuery.setParameter(eq("userId"), eq(10L))).thenReturn(countQuery);
            when(countQuery.setParameter(eq("buyerCodeId"), eq(5L))).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(1L);

            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(10L, 5L);

            assertThat(authorized).isTrue();
        }

        @Test
        @DisplayName("bidder without access - returns false")
        @SuppressWarnings("unchecked")
        void isUserAuthorizedForBuyerCode_bidderWithoutAccess_returnsFalse() {
            stubIsBuyerRole(true);

            TypedQuery<Long> countQuery = mock(TypedQuery.class);
            when(em.createNativeQuery(contains("COUNT(*)"), eq(Long.class)))
                    .thenReturn(countQuery);
            when(countQuery.setParameter(eq("userId"), eq(10L))).thenReturn(countQuery);
            when(countQuery.setParameter(eq("buyerCodeId"), eq(999L))).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(0L);

            boolean authorized = buyerCodeService.isUserAuthorizedForBuyerCode(10L, 999L);

            assertThat(authorized).isFalse();
        }
    }
}
