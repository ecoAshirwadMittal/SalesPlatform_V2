package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link Round3BuyerDataReportRepository} R3-specific methods.
 * Tests the bulk insert + delete methods added in sub-project 6, Task 11.
 */
@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class Round3BuyerDataReportRepositoryR3IT {

    @Autowired
    Round3BuyerDataReportRepository repo;

    @Autowired
    JdbcTemplate jdbc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("bulkInsertForSchedulingAuction writes one row per company with comma-joined codes")
    void writes_one_row_per_company() {
        // Seed QBC rows directly (this test isolates the report generation from QBC qualification)
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes " +
            "(scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) " +
            "VALUES " +
            "(?, 60001, 'Qualified', TRUE, FALSE, NOW(), NOW())," +
            "(?, 60002, 'Qualified', TRUE, FALSE, NOW(), NOW())," +
            "(?, 60003, 'Qualified', TRUE, FALSE, NOW(), NOW())",
            R3_SA_ID, R3_SA_ID, R3_SA_ID);

        int inserted = repo.bulkInsertForSchedulingAuction(R3_SA_ID);

        // 60001+60002 belong to Acme; 60003 to Beta → 2 rows
        assertThat(inserted).isEqualTo(2);
        List<Round3BuyerDataReport> rows = repo.findBySchedulingAuctionId(R3_SA_ID);
        assertThat(rows).hasSize(2);

        Round3BuyerDataReport acme = rows.stream()
            .filter(r -> r.getCompanyName().equals("Acme Corp"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Acme Corp report not found"));
        assertThat(acme.getBuyerCodes()).isEqualTo("ACME-DW,ACME-WH");  // alphabetical order
    }

    @Test
    @DisplayName("filters_not_qualified: only Qualified+included rows feed into report generation")
    void filters_not_qualified() {
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes " +
            "(scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) " +
            "VALUES " +
            "(?, 60001, 'Qualified',     TRUE,  FALSE, NOW(), NOW())," +
            "(?, 60003, 'Not_Qualified', FALSE, FALSE, NOW(), NOW())",
            R3_SA_ID, R3_SA_ID);

        repo.bulkInsertForSchedulingAuction(R3_SA_ID);
        List<Round3BuyerDataReport> rows = repo.findBySchedulingAuctionId(R3_SA_ID);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getCompanyName()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("deleteBySchedulingAuctionId clears reports for that SA only")
    void delete_clears_only_target_sa() {
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes " +
            "(scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) " +
            "VALUES (?, 60001, 'Qualified', TRUE, FALSE, NOW(), NOW())",
            R3_SA_ID);
        repo.bulkInsertForSchedulingAuction(R3_SA_ID);

        int deleted = repo.deleteBySchedulingAuctionId(R3_SA_ID);

        assertThat(deleted).isGreaterThan(0);
        assertThat(repo.findBySchedulingAuctionId(R3_SA_ID)).isEmpty();
    }
}
