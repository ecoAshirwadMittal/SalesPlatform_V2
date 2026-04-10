package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link BuyerCodeLookupService} against a real PostgreSQL database.
 * <p>
 * This test exists specifically to validate native SQL queries that reference
 * schema-qualified tables (buyer_mgmt.buyer_codes, buyer_mgmt.sales_representatives, etc.).
 * Unit tests with mocked EntityManagers cannot catch wrong table/column names.
 * <p>
 * Uses Testcontainers (PostgreSQL 15) with full Flyway migrations.
 */
@Transactional
class BuyerCodeLookupServiceIT extends PostgresIntegrationTest {

    @Autowired
    private BuyerCodeLookupService lookupService;

    @Autowired
    private EntityManager em;

    private Long buyerCodeId1;
    private Long buyerCodeId2;
    private Long buyerCodeIdOrphan;

    @BeforeEach
    void seedTestData() {
        // Clean test-specific data (leave Flyway-seeded data intact, add our own)
        // Use high IDs to avoid collisions with Flyway-seeded data

        // Sales representative
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.sales_representatives (id, first_name, last_name, active)
                VALUES (90001, 'Jane', 'Doe', true)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        // Buyers
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyers (id, company_name, status)
                VALUES (90001, 'Acme Corp', 'Active'),
                       (90002, 'Globex Inc', 'Active')
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        // Buyer ↔ Sales Rep
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_sales_reps (buyer_id, sales_rep_id)
                VALUES (90001, 90001)
                ON CONFLICT DO NOTHING
                """).executeUpdate();

        // Buyer codes
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status, soft_delete)
                VALUES (90001, 'TEST-001', 'Wholesale', 'Active', false),
                       (90002, 'TEST-002', 'Data_Wipe', 'Active', false),
                       (90003, 'TEST-ORPHAN', 'Wholesale', 'Active', false)
                ON CONFLICT (id) DO NOTHING
                """).executeUpdate();

        // Buyer code ↔ Buyer links
        em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                VALUES (90001, 90001),
                       (90002, 90002)
                ON CONFLICT DO NOTHING
                """).executeUpdate();
        // 90003 (orphan) has no buyer link — tests graceful handling

        buyerCodeId1 = 90001L;
        buyerCodeId2 = 90002L;
        buyerCodeIdOrphan = 90003L;

        em.flush();
    }

    @Nested
    @DisplayName("findCodeById")
    class FindCodeById {

        @Test
        @DisplayName("returns code string for existing buyer code")
        void existingCode() {
            String code = lookupService.findCodeById(buyerCodeId1);
            assertThat(code).isEqualTo("TEST-001");
        }

        @Test
        @DisplayName("returns null for non-existent ID")
        void nonExistentId() {
            String code = lookupService.findCodeById(999999L);
            assertThat(code).isNull();
        }
    }

    @Nested
    @DisplayName("findCodesByIds")
    class FindCodesByIds {

        @Test
        @DisplayName("returns map of id → code for multiple IDs")
        void multipleIds() {
            Map<Long, String> codes = lookupService.findCodesByIds(
                    Set.of(buyerCodeId1, buyerCodeId2));
            assertThat(codes).hasSize(2)
                    .containsEntry(buyerCodeId1, "TEST-001")
                    .containsEntry(buyerCodeId2, "TEST-002");
        }

        @Test
        @DisplayName("returns empty map for empty input")
        void emptyInput() {
            assertThat(lookupService.findCodesByIds(Set.of())).isEmpty();
        }

        @Test
        @DisplayName("returns empty map for null input")
        void nullInput() {
            assertThat(lookupService.findCodesByIds(null)).isEmpty();
        }

        @Test
        @DisplayName("skips non-existent IDs without error")
        void mixedExistentAndNonExistent() {
            Map<Long, String> codes = lookupService.findCodesByIds(
                    Set.of(buyerCodeId1, 999999L));
            assertThat(codes).hasSize(1).containsKey(buyerCodeId1);
        }
    }

    @Nested
    @DisplayName("findCodeAndCompanyByIds")
    class FindCodeAndCompanyByIds {

        @Test
        @DisplayName("returns code and company name via join")
        void existingWithCompany() {
            Map<Long, BuyerCodeLookupService.BuyerCodeInfo> result =
                    lookupService.findCodeAndCompanyByIds(Set.of(buyerCodeId1));
            assertThat(result).containsKey(buyerCodeId1);
            BuyerCodeLookupService.BuyerCodeInfo info = result.get(buyerCodeId1);
            assertThat(info.code()).isEqualTo("TEST-001");
            assertThat(info.companyName()).isEqualTo("Acme Corp");
        }

        @Test
        @DisplayName("returns code with null company for orphan buyer code")
        void orphanBuyerCode() {
            Map<Long, BuyerCodeLookupService.BuyerCodeInfo> result =
                    lookupService.findCodeAndCompanyByIds(Set.of(buyerCodeIdOrphan));
            assertThat(result).containsKey(buyerCodeIdOrphan);
            BuyerCodeLookupService.BuyerCodeInfo info = result.get(buyerCodeIdOrphan);
            assertThat(info.code()).isEqualTo("TEST-ORPHAN");
            assertThat(info.companyName()).isNull();
        }

        @Test
        @DisplayName("returns empty map for empty input")
        void emptyInput() {
            assertThat(lookupService.findCodeAndCompanyByIds(Set.of())).isEmpty();
        }
    }

    @Nested
    @DisplayName("findCompanyNamesByIds")
    class FindCompanyNamesByIds {

        @Test
        @DisplayName("returns company names for buyer codes with linked buyers")
        void existingLinks() {
            Map<Long, String> names = lookupService.findCompanyNamesByIds(
                    Set.of(buyerCodeId1, buyerCodeId2));
            assertThat(names)
                    .containsEntry(buyerCodeId1, "Acme Corp")
                    .containsEntry(buyerCodeId2, "Globex Inc");
        }

        @Test
        @DisplayName("omits buyer codes that have no buyer link")
        void orphanNotIncluded() {
            Map<Long, String> names = lookupService.findCompanyNamesByIds(
                    Set.of(buyerCodeIdOrphan));
            assertThat(names).isEmpty();
        }

        @Test
        @DisplayName("returns empty map for empty input")
        void emptyInput() {
            assertThat(lookupService.findCompanyNamesByIds(Set.of())).isEmpty();
        }
    }

    @Nested
    @DisplayName("findSalesRepsByIds")
    class FindSalesRepsByIds {

        @Test
        @DisplayName("returns sales rep name for buyer code linked through buyer")
        void existingChain() {
            // Chain: buyerCodeId1 → buyer 90001 → sales rep 90001 (Jane Doe)
            Map<Long, String> reps = lookupService.findSalesRepsByIds(Set.of(buyerCodeId1));
            assertThat(reps).containsEntry(buyerCodeId1, "Jane Doe");
        }

        @Test
        @DisplayName("omits buyer codes whose buyer has no sales rep")
        void noSalesRep() {
            // buyerCodeId2 → buyer 90002, which has no entry in buyer_sales_reps
            Map<Long, String> reps = lookupService.findSalesRepsByIds(Set.of(buyerCodeId2));
            assertThat(reps).isEmpty();
        }

        @Test
        @DisplayName("omits orphan buyer codes (no buyer link at all)")
        void orphanBuyerCode() {
            Map<Long, String> reps = lookupService.findSalesRepsByIds(Set.of(buyerCodeIdOrphan));
            assertThat(reps).isEmpty();
        }

        @Test
        @DisplayName("handles mixed IDs — some with reps, some without")
        void mixedIds() {
            Map<Long, String> reps = lookupService.findSalesRepsByIds(
                    Set.of(buyerCodeId1, buyerCodeId2, buyerCodeIdOrphan));
            assertThat(reps).hasSize(1).containsEntry(buyerCodeId1, "Jane Doe");
        }

        @Test
        @DisplayName("returns empty map for empty input")
        void emptyInput() {
            assertThat(lookupService.findSalesRepsByIds(Set.of())).isEmpty();
        }

        @Test
        @DisplayName("returns empty map for null input")
        void nullInput() {
            assertThat(lookupService.findSalesRepsByIds(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findActiveIdByCode")
    class FindActiveIdByCode {

        @Test
        @DisplayName("returns ID for active buyer code")
        void activeCode() {
            Long id = lookupService.findActiveIdByCode("TEST-001");
            assertThat(id).isEqualTo(buyerCodeId1);
        }

        @Test
        @DisplayName("returns null for non-existent code")
        void nonExistentCode() {
            Long id = lookupService.findActiveIdByCode("DOES-NOT-EXIST");
            assertThat(id).isNull();
        }

        @Test
        @DisplayName("returns null for inactive/soft-deleted code")
        void inactiveCode() {
            em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_codes (id, code, status, soft_delete)
                    VALUES (90099, 'INACTIVE-001', 'Inactive', false)
                    ON CONFLICT (id) DO NOTHING
                    """).executeUpdate();
            em.flush();

            Long id = lookupService.findActiveIdByCode("INACTIVE-001");
            assertThat(id).isNull();
        }

        @Test
        @DisplayName("returns null for soft-deleted code")
        void softDeletedCode() {
            em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_codes (id, code, status, soft_delete)
                    VALUES (90098, 'DELETED-001', 'Active', true)
                    ON CONFLICT (id) DO NOTHING
                    """).executeUpdate();
            em.flush();

            Long id = lookupService.findActiveIdByCode("DELETED-001");
            assertThat(id).isNull();
        }
    }
}
