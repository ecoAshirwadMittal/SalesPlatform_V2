package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Smoke test that validates every native SQL query in the codebase actually
 * executes against a real PostgreSQL database without SQL errors.
 * <p>
 * This catches:
 * - Wrong table names (e.g., "sales_reps" vs "sales_representatives")
 * - Wrong column names
 * - Invalid schema references
 * - Broken joins
 * <p>
 * These errors are invisible to unit tests because the EntityManager is mocked.
 * <p>
 * Each test exercises one native query path. The queries may return empty results
 * (no test data needed for the specific path) — the point is they must NOT throw
 * a PSQLException for invalid SQL.
 */
@Transactional
class NativeQuerySmokeIT extends PostgresIntegrationTest {

    @Autowired private EntityManager em;

    @Nested
    @DisplayName("AuthService native queries")
    class AuthServiceQueries {

        @Test
        @DisplayName("getUserRoles query executes")
        void userRolesQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT ur.name FROM identity.user_roles ur " +
                    "JOIN identity.user_role_assignments ura ON ura.role_id = ur.id " +
                    "WHERE ura.user_id = :userId")
                    .setParameter("userId", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("buildUserInfo query executes")
        void userInfoQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT edu.first_name, edu.last_name, a.full_name, a.email
                    FROM user_mgmt.ecoatm_direct_users edu
                    JOIN identity.accounts a ON a.user_id = edu.user_id
                    WHERE edu.user_id = :userId
                    """)
                    .setParameter("userId", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("BuyerCodeService native queries")
    class BuyerCodeServiceQueries {

        @Test
        @DisplayName("bidder buyer codes query executes")
        void bidderBuyerCodesQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT DISTINCT bc.id, bc.code, b.company_name, bc.buyer_code_type
                    FROM buyer_mgmt.buyer_codes bc
                    JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                    JOIN user_mgmt.user_buyers ub ON ub.buyer_id = b.id
                    WHERE ub.user_id = :userId
                      AND bc.status = 'Active'
                      AND bc.soft_delete = false
                      AND b.status = 'Active'
                      AND bc.buyer_code_type NOT IN ('Purchasing_Order', 'Purchasing_Order_Data_Wipe')
                    ORDER BY bc.code
                    """)
                    .setParameter("userId", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("admin buyer codes query executes")
        void adminBuyerCodesQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT DISTINCT bc.id, bc.code, b.company_name, bc.buyer_code_type
                    FROM buyer_mgmt.buyer_codes bc
                    JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                    WHERE bc.status = 'Active'
                      AND bc.soft_delete = false
                      AND b.status = 'Active'
                      AND bc.buyer_code_type IN ('Premium_Wholesale', 'Wholesale')
                    ORDER BY bc.code
                    """)
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("authorization check query executes")
        void authorizationCheckQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT COUNT(*)
                    FROM buyer_mgmt.buyer_codes bc
                    JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                    JOIN user_mgmt.user_buyers ub ON ub.buyer_id = bcb.buyer_id
                    WHERE ub.user_id = :userId
                      AND bc.id = :buyerCodeId
                      AND bc.status = 'Active'
                      AND bc.soft_delete = false
                    """, Long.class)
                    .setParameter("userId", 1L)
                    .setParameter("buyerCodeId", 1L)
                    .getSingleResult()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("isBuyerRole query executes")
        void isBuyerRoleQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT edu.is_buyer_role
                    FROM user_mgmt.ecoatm_direct_users edu
                    WHERE edu.user_id = :userId
                    """, Boolean.class)
                    .setParameter("userId", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("OfferReviewService native queries")
    class OfferReviewServiceQueries {

        @Test
        @DisplayName("order number lookup query executes")
        void orderNumberQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT o.offer_id, o.order_number FROM pws.\"order\" o WHERE o.offer_id IN :ids")
                    .setParameter("ids", List.of(1L))
                    .getResultList()
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("OfferService native queries")
    class OfferServiceQueries {

        @Test
        @DisplayName("resolveUserName query executes")
        void resolveUserNameQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT u.name FROM identity.users u WHERE u.id = :id")
                    .setParameter("id", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("error mapping lookup query executes")
        void errorMappingQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT user_error_code, user_error_message, bypass_for_user
                    FROM integration.error_mapping
                    WHERE source_system = :src AND source_error_code = :code
                    """)
                    .setParameter("src", "Oracle")
                    .setParameter("code", "TEST")
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("offer_id_sequence upsert RETURNING query executes")
        void offerIdSequenceUpsertReturning() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    INSERT INTO pws.offer_id_sequence (buyer_code_id, year_prefix, max_sequence)
                    VALUES (:bcId, :yp, 1)
                    ON CONFLICT (buyer_code_id, year_prefix)
                    DO UPDATE SET max_sequence = pws.offer_id_sequence.max_sequence + 1
                    RETURNING max_sequence
                    """)
                    .setParameter("bcId", 99999L)
                    .setParameter("yp", "99")
                    .getSingleResult()
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("BuyerCodeLookupService native queries")
    class BuyerCodeLookupServiceQueries {

        @Test
        @DisplayName("findCodeById query executes")
        void findCodeByIdQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT bc.code FROM buyer_mgmt.buyer_codes bc WHERE bc.id = :id")
                    .setParameter("id", 1L)
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("findCodesByIds query executes")
        void findCodesByIdsQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT bc.id, bc.code FROM buyer_mgmt.buyer_codes bc WHERE bc.id IN :ids")
                    .setParameter("ids", List.of(1L))
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("findCodeAndCompanyByIds query executes")
        void findCodeAndCompanyQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT bc.id, bc.code, b.company_name " +
                    "FROM buyer_mgmt.buyer_codes bc " +
                    "LEFT JOIN buyer_mgmt.buyer_code_buyers bcb ON bc.id = bcb.buyer_code_id " +
                    "LEFT JOIN buyer_mgmt.buyers b ON bcb.buyer_id = b.id " +
                    "WHERE bc.id IN :ids")
                    .setParameter("ids", List.of(1L))
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("findCompanyNamesByIds query executes")
        void findCompanyNamesQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT bcb.buyer_code_id, b.company_name
                    FROM buyer_mgmt.buyer_code_buyers bcb
                    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                    WHERE bcb.buyer_code_id IN :ids
                    """)
                    .setParameter("ids", List.of(1L))
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("findSalesRepsByIds query executes")
        void findSalesRepsQuery() {
            assertThatCode(() ->
                em.createNativeQuery("""
                    SELECT bcb.buyer_code_id, sr.first_name, sr.last_name
                    FROM buyer_mgmt.buyer_code_buyers bcb
                    JOIN buyer_mgmt.buyer_sales_reps bsr ON bsr.buyer_id = bcb.buyer_id
                    JOIN buyer_mgmt.sales_representatives sr ON sr.id = bsr.sales_rep_id
                    WHERE bcb.buyer_code_id IN :ids
                    """)
                    .setParameter("ids", List.of(1L))
                    .getResultList()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("findActiveIdByCode query executes")
        void findActiveIdByCodeQuery() {
            assertThatCode(() ->
                em.createNativeQuery(
                    "SELECT id FROM buyer_mgmt.buyer_codes WHERE code = :code AND status = 'Active' AND soft_delete = false",
                    Long.class)
                    .setParameter("code", "NONEXISTENT")
                    .getResultList()
            ).doesNotThrowAnyException();
        }
    }
}
