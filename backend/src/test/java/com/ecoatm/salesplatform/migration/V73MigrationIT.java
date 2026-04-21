package com.ecoatm.salesplatform.migration;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V73 migration applied cleanly against PostgreSQL. Extends
 * {@link PostgresIntegrationTest} so the {@code pg-test} profile runs Flyway
 * end-to-end (V1..V73) during context startup — the {@code test} profile uses
 * H2 with Flyway disabled and cannot exercise real SQL migrations.
 */
class V73MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void bid_data_docs_table_exists_with_unique_user_buyer_week() {
        Integer tableCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data_docs'",
            Integer.class);
        assertThat(tableCount).isEqualTo(1);

        Integer uqCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.table_constraints "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data_docs' "
            + "AND constraint_name = 'uq_bdd_user_buyer_week'",
            Integer.class);
        assertThat(uqCount).isEqualTo(1);
    }

    @Test
    void bid_data_bid_quantity_is_nullable() {
        String nullable = jdbc.queryForObject(
            "SELECT is_nullable FROM information_schema.columns "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data' "
            + "AND column_name = 'bid_quantity'",
            String.class);
        assertThat(nullable).isEqualTo("YES");
    }

    @Test
    void bid_data_doc_id_column_exists_with_fk() {
        Integer colCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.columns "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data' "
            + "AND column_name = 'bid_data_doc_id'",
            Integer.class);
        assertThat(colCount).isEqualTo(1);
    }
}
