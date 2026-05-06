package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration coverage for the R2-init bulk-insert extensions on
 * {@link QualifiedBuyerCodeRepository} (Task 8 of sub-project 5).
 *
 * <p>Reuses the T6 R2 fixture (9 buyer codes, mix of WH/DW/PO/Disabled) plus
 * the T7 special-buyer extension. Asserts the three branches of the QBC
 * three-set INSERT — Qualified, Special-Treatment-as-Qualified, and
 * Not_Qualified — and verifies the {@code is_special_treatment} flag is
 * scoped only to the special set.
 *
 * <p>Junction tables {@code qbc_buyer_codes} and {@code qbc_scheduling_auctions}
 * were dropped in V72 (flatten). The {@code bulkInsertJunctions} method is
 * retained as a post-V72 no-op for spec parity and is asserted to return 0.
 */
@Transactional
@Sql(scripts = {
    "/fixtures/auctions/r2-init-seed.sql",
    "/fixtures/auctions/r2-special-buyer-extra-seed.sql"
})
class QualifiedBuyerCodeRepositoryR2IT extends PostgresIntegrationTest {

    private static final long R2_SA_ID = 999102L;

    // Buyer-code id constants from r2-init-seed.sql + r2-special-buyer-extra-seed.sql.
    private static final long CODE_WH_ABOVE = 999101L; // qualified
    private static final long CODE_WH_PCT   = 999102L; // qualified
    private static final long CODE_WH_FAR   = 999104L; // not_qualified target
    private static final long CODE_SPECIAL  = 999111L; // is_special_treatment

    @Autowired QualifiedBuyerCodeRepository repo;
    @Autowired JdbcTemplate jdbc;

    // ---- bulkInsertForR2: three-set semantics ---------------------------

    @Test
    @DisplayName("bulkInsertForR2 writes Qualified for codes in qualifiedIds set with is_special_treatment=FALSE")
    void qualified_set_marked_qualified_not_special() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE, CODE_WH_PCT};
        Long[] special   = new Long[] {};

        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        QualifiedBuyerCode aboveQbc = onlyQbcForCode(R2_SA_ID, CODE_WH_ABOVE);
        assertThat(aboveQbc.getQualificationType()).isEqualTo(QualificationType.Qualified);
        assertThat(aboveQbc.isIncluded()).isTrue();
        assertThat(aboveQbc.isSpecialTreatment()).isFalse();
    }

    @Test
    @DisplayName("bulkInsertForR2 writes Qualified + is_special_treatment=TRUE for codes in specialIds set")
    void special_set_marked_qualified_and_special() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {CODE_SPECIAL};

        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        QualifiedBuyerCode specialQbc = onlyQbcForCode(R2_SA_ID, CODE_SPECIAL);
        assertThat(specialQbc.getQualificationType()).isEqualTo(QualificationType.Qualified);
        assertThat(specialQbc.isIncluded()).isTrue();
        assertThat(specialQbc.isSpecialTreatment()).isTrue();
    }

    @Test
    @DisplayName("bulkInsertForR2 writes Not_Qualified + is_special_treatment=FALSE for active codes outside both sets")
    void not_qualified_set_marked_not_qualified() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {};

        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        QualifiedBuyerCode farQbc = onlyQbcForCode(R2_SA_ID, CODE_WH_FAR);
        assertThat(farQbc.getQualificationType()).isEqualTo(QualificationType.Not_Qualified);
        assertThat(farQbc.isIncluded()).isFalse();
        assertThat(farQbc.isSpecialTreatment()).isFalse();
    }

    @Test
    @DisplayName("bulkInsertForR2 filters to active Wholesale/Data_Wipe codes — PO and Disabled-buyer codes excluded")
    void filters_to_active_wholesale_datawipe() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {};

        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        // PO (999107) and Disabled-buyer's WH code (999108) must NOT have a row.
        Integer poCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM buyer_mgmt.qualified_buyer_codes "
                + "WHERE scheduling_auction_id = ? AND buyer_code_id = ?",
            Integer.class, R2_SA_ID, 999107L);
        Integer disabledCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM buyer_mgmt.qualified_buyer_codes "
                + "WHERE scheduling_auction_id = ? AND buyer_code_id = ?",
            Integer.class, R2_SA_ID, 999108L);
        assertThat(poCount).isZero();
        assertThat(disabledCount).isZero();
    }

    @Test
    @DisplayName("bulkInsertForR2 emits exactly one row per active DW/WH code (M:M fan-out collapsed)")
    void no_duplicate_rows_per_code() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {};

        int rowsInserted = repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        // Distinct buyer_code_ids written for the SA equals total rows — no
        // duplicate row per code despite the buyer_code_buyers M:M join.
        Integer distinctCodes = jdbc.queryForObject(
            "SELECT COUNT(DISTINCT buyer_code_id) "
                + "FROM buyer_mgmt.qualified_buyer_codes "
                + "WHERE scheduling_auction_id = ?",
            Integer.class, R2_SA_ID);
        Integer totalRows = jdbc.queryForObject(
            "SELECT COUNT(*) FROM buyer_mgmt.qualified_buyer_codes "
                + "WHERE scheduling_auction_id = ?",
            Integer.class, R2_SA_ID);
        assertThat(totalRows).isEqualTo(distinctCodes);
        assertThat(rowsInserted).isEqualTo(totalRows);
    }

    // ---- findBySchedulingAuctionId derived query ------------------------

    @Test
    @DisplayName("findBySchedulingAuctionId returns all QBCs scoped to the SA")
    void find_by_sa_returns_inserted_rows() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE, CODE_WH_PCT};
        Long[] special   = new Long[] {CODE_SPECIAL};

        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        List<QualifiedBuyerCode> qbcs = repo.findBySchedulingAuctionId(R2_SA_ID);
        assertThat(qbcs)
            .as("every active DW/WH code seeded gets one row")
            .isNotEmpty()
            .allSatisfy(q -> assertThat(q.getSchedulingAuctionId()).isEqualTo(R2_SA_ID));
        assertThat(qbcs)
            .extracting(QualifiedBuyerCode::getBuyerCodeId)
            .contains(CODE_WH_ABOVE, CODE_WH_PCT, CODE_SPECIAL);
    }

    // ---- bulkInsertJunctions: post-V72 no-op -----------------------------

    @Test
    @DisplayName("bulkInsertJunctions is a post-V72 no-op (junction tables dropped) — returns 0")
    void junctions_no_op_post_v72() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {};
        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        int rows = repo.bulkInsertJunctions(R2_SA_ID);

        // Post-V72, qbc_scheduling_auctions and qbc_buyer_codes are dropped:
        // the FK-flattened columns on qualified_buyer_codes are populated by
        // bulkInsertForR2 itself. Method is retained for spec parity.
        assertThat(rows).isZero();
    }

    @Test
    @DisplayName("bulkInsertJunctions idempotent — second call also returns 0")
    void junctions_idempotent() {
        Long[] qualified = new Long[] {CODE_WH_ABOVE};
        Long[] special   = new Long[] {};
        repo.bulkInsertForR2(R2_SA_ID, qualified, special);

        repo.bulkInsertJunctions(R2_SA_ID);
        int second = repo.bulkInsertJunctions(R2_SA_ID);

        assertThat(second).isZero();
    }

    // ---- helpers --------------------------------------------------------

    private QualifiedBuyerCode onlyQbcForCode(long saId, long buyerCodeId) {
        return repo.findBySchedulingAuctionIdAndBuyerCodeId(saId, buyerCodeId)
            .orElseThrow(() -> new AssertionError(
                "expected a QBC row for sa=" + saId + " bc=" + buyerCodeId));
    }
}
