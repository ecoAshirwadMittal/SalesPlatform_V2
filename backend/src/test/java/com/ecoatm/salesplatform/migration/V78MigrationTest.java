package com.ecoatm.salesplatform.migration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class V78MigrationTest {

    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void cleanupTestRows() {
        jdbc.update("DELETE FROM auctions.purchase_order "
                  + "WHERE week_range_label IN ('TEST', 'CASCADE-TEST', 'BAD-FK')");
    }

    @Test
    void purchaseOrderTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='purchase_order'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void poDetailTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='po_detail'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void uniquePoDetailConstraintEnforced() {
        Long weekId = jdbc.queryForObject(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes ORDER BY id LIMIT 1", Long.class);
        Long poId = jdbc.queryForObject(
                "INSERT INTO auctions.purchase_order "
              + "(week_from_id, week_to_id, week_range_label) "
              + "VALUES (?, ?, 'TEST') RETURNING id", Long.class, weekId, weekId);

        jdbc.update("INSERT INTO auctions.po_detail "
                  + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                  + "VALUES (?, ?, '99001', 'A_YYY', 10.00)", poId, buyerCodeId);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.po_detail "
                                + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                                + "VALUES (?, ?, '99001', 'A_YYY', 20.00)",
                                poId, buyerCodeId));
    }

    @Test
    void poDetailCascadesOnPurchaseOrderDelete() {
        Long weekId = jdbc.queryForObject(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes ORDER BY id LIMIT 1", Long.class);
        Long poId = jdbc.queryForObject(
                "INSERT INTO auctions.purchase_order "
              + "(week_from_id, week_to_id, week_range_label) "
              + "VALUES (?, ?, 'CASCADE-TEST') RETURNING id", Long.class, weekId, weekId);
        jdbc.update("INSERT INTO auctions.po_detail "
                  + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                  + "VALUES (?, ?, '99002', 'B_NNN', 5.00)", poId, buyerCodeId);

        jdbc.update("DELETE FROM auctions.purchase_order WHERE id = ?", poId);

        Integer orphans = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.po_detail WHERE purchase_order_id = ?",
                Integer.class, poId);
        assertThat(orphans).isZero();
    }

    @Test
    void weekFkEnforced() {
        assertThrows(DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.purchase_order "
                                + "(week_from_id, week_to_id, week_range_label) "
                                + "VALUES (-99, -99, 'BAD-FK')"));
    }
}
