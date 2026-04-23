package com.ecoatm.salesplatform.migration;

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
}
