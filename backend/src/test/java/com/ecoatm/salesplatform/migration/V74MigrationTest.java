package com.ecoatm.salesplatform.migration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class V74MigrationTest {

    @Autowired JdbcTemplate jdbc;

    @Test
    void reserveBidTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='reserve_bid'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void reserveBidAuditTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='reserve_bid_audit'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void reserveBidSyncSingletonSeeded() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.reserve_bid_sync", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void uniqueProductGradeConstraintEnforced() {
        jdbc.update("INSERT INTO auctions.reserve_bid (product_id, grade, bid) "
                  + "VALUES ('18509', 'A_YYY', 10.00)");
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.reserve_bid "
                               + "(product_id, grade, bid) VALUES ('18509', 'A_YYY', 20.00)"));
    }

    @Test
    void auditCascadesOnReserveBidDelete() {
        Long rbId = jdbc.queryForObject(
                "INSERT INTO auctions.reserve_bid (product_id, grade, bid) "
              + "VALUES ('99001', 'X_YYY', 5.00) RETURNING id", Long.class);
        jdbc.update("INSERT INTO auctions.reserve_bid_audit "
                  + "(reserve_bid_id, old_price, new_price) VALUES (?, 5.00, 6.00)", rbId);
        jdbc.update("DELETE FROM auctions.reserve_bid WHERE id = ?", rbId);
        Integer orphans = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.reserve_bid_audit WHERE reserve_bid_id = ?",
                Integer.class, rbId);
        assertThat(orphans).isZero();
    }

    @Test
    void dataLoaded() {
        Integer rb = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid", Integer.class);
        assertThat(rb).isGreaterThan(14000);  // 14,657 actual from qa-0327 (deduped)
        Integer audit = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid_audit", Integer.class);
        assertThat(audit).isGreaterThanOrEqualTo(4);
        // sync singleton still 1 row after replacement
        Integer sync = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid_sync", Integer.class);
        assertThat(sync).isEqualTo(1);
    }

    @AfterEach
    void cleanup() {
        jdbc.update("DELETE FROM auctions.reserve_bid_audit WHERE reserve_bid_id IN "
                  + "(SELECT id FROM auctions.reserve_bid WHERE product_id IN ('18509','99001'))");
        jdbc.update("DELETE FROM auctions.reserve_bid WHERE product_id IN ('18509','99001')");
    }
}
