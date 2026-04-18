package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies V68 shape on a real PostgreSQL database.
 * <p>
 * Case 1 (migration applies cleanly) is covered implicitly: Flyway ran V1..V68
 * during Spring context startup — the fact that this test loads proves it.
 * <p>
 * Cases 2 + 3 assert the post-migration schema shape directly via
 * information_schema / pg_indexes.
 * <p>
 * Case 4 (dedupe logic) is asserted functionally: with V68 in place the partial
 * unique index must reject a second live row on the same key. That is the
 * behavior the Phase 4 upsert depends on — and it is what the dedupe CTE in
 * V68 exists to guarantee on any DB that already had duplicates before V68.
 */
@Transactional
class WeekSyncWatermarkMigrationIT extends PostgresIntegrationTest {

    @Autowired private EntityManager em;

    @Test
    @DisplayName("auctions.week_sync_watermark has expected columns and types")
    void watermarkTableShape() {
        @SuppressWarnings("unchecked")
        List<Object[]> cols = em.createNativeQuery("""
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns
                WHERE table_schema = 'auctions'
                  AND table_name = 'week_sync_watermark'
                ORDER BY ordinal_position
                """).getResultList();

        assertThat(cols)
            .extracting(r -> (String) r[0])
            .containsExactly(
                "week_id",
                "source",
                "last_source_upload_at",
                "last_synced_at",
                "row_count",
                "created_at",
                "updated_at");

        // Spot-check a few types and nullability.
        assertThat(cols.get(0)[1]).isEqualTo("bigint");                         // week_id
        assertThat(cols.get(0)[2]).isEqualTo("NO");
        assertThat(cols.get(1)[1]).isEqualTo("character varying");              // source
        assertThat(cols.get(1)[2]).isEqualTo("NO");
        assertThat(cols.get(2)[1]).isEqualTo("timestamp with time zone");       // last_source_upload_at
        assertThat(cols.get(2)[2]).isEqualTo("NO");
        assertThat(cols.get(4)[1]).isEqualTo("integer");                        // row_count
    }

    @Test
    @DisplayName("week_sync_watermark has composite PK (week_id, source) and FK to mdm.week")
    void watermarkPrimaryKeyAndForeignKey() {
        @SuppressWarnings("unchecked")
        List<String> pkCols = em.createNativeQuery("""
                SELECT kcu.column_name
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu
                  ON kcu.constraint_name = tc.constraint_name
                 AND kcu.table_schema = tc.table_schema
                WHERE tc.table_schema = 'auctions'
                  AND tc.table_name = 'week_sync_watermark'
                  AND tc.constraint_type = 'PRIMARY KEY'
                ORDER BY kcu.ordinal_position
                """).getResultList();
        assertThat(pkCols).containsExactly("week_id", "source");

        // pg_constraint is the authoritative source for FK target resolution.
        // information_schema.constraint_column_usage has quirks across PG
        // versions when the PK and FK live in different schemas.
        @SuppressWarnings("unchecked")
        List<Object[]> fk = em.createNativeQuery("""
                SELECT
                    fn.nspname  AS fk_schema,
                    fc.relname  AS fk_table,
                    fa.attname  AS fk_column,
                    rn.nspname  AS ref_schema,
                    rc.relname  AS ref_table,
                    ra.attname  AS ref_column
                FROM pg_constraint con
                JOIN pg_class     fc ON fc.oid = con.conrelid
                JOIN pg_namespace fn ON fn.oid = fc.relnamespace
                JOIN pg_class     rc ON rc.oid = con.confrelid
                JOIN pg_namespace rn ON rn.oid = rc.relnamespace
                JOIN pg_attribute fa ON fa.attrelid = con.conrelid
                                    AND fa.attnum = con.conkey[1]
                JOIN pg_attribute ra ON ra.attrelid = con.confrelid
                                    AND ra.attnum = con.confkey[1]
                WHERE con.contype = 'f'
                  AND fn.nspname = 'auctions'
                  AND fc.relname = 'week_sync_watermark'
                """).getResultList();
        assertThat(fk).hasSize(1);
        assertThat(fk.get(0)[2]).isEqualTo("week_id");   // local column
        assertThat(fk.get(0)[3]).isEqualTo("mdm");       // referenced schema
        assertThat(fk.get(0)[4]).isEqualTo("week");      // referenced table
        assertThat(fk.get(0)[5]).isEqualTo("id");        // referenced column
    }

    @Test
    @DisplayName("uq_agi_ecoid_grade_dw_week exists as partial unique index")
    void uniquePartialIndexExists() {
        Object def = em.createNativeQuery("""
                SELECT indexdef
                FROM pg_indexes
                WHERE schemaname = 'auctions'
                  AND tablename  = 'aggregated_inventory'
                  AND indexname  = 'uq_agi_ecoid_grade_dw_week'
                """).getSingleResult();

        String indexDef = (String) def;
        assertThat(indexDef).contains("UNIQUE INDEX");
        assertThat(indexDef).contains("ecoid2");
        assertThat(indexDef).contains("merged_grade");
        assertThat(indexDef).contains("datawipe");
        assertThat(indexDef).contains("week_id");
        assertThat(indexDef).contains("WHERE");
        // "is_deprecated = false" — the partial predicate.
        assertThat(indexDef.toLowerCase()).contains("is_deprecated");
        assertThat(indexDef.toLowerCase()).contains("false");
    }

    @Test
    @DisplayName("partial unique index rejects a second live row on the same key")
    void uniqueIndexRejectsDuplicateLiveRow() {
        // Pick any existing week id. Seeded by V65.
        Long weekId = ((Number) em.createNativeQuery(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1")
                .getSingleResult()).longValue();

        String ecoid = "V68_IT_ECOID_" + System.nanoTime();

        em.createNativeQuery("""
                INSERT INTO auctions.aggregated_inventory
                    (ecoid2, merged_grade, datawipe, week_id, is_deprecated)
                VALUES (:e, 'A', false, :w, false)
                """)
            .setParameter("e", ecoid)
            .setParameter("w", weekId)
            .executeUpdate();
        em.flush();

        // Second insert with the same (ecoid2, merged_grade, datawipe, week_id)
        // and is_deprecated=false must violate the partial unique index.
        //
        // Harden the exception-chain assertion: `hasMessageContaining` on the
        // wrapping PersistenceException can silently pass on any non-duplicate
        // error with the index name buried in an unrelated part of the
        // message. Instead walk the chain down to the PSQLException root
        // cause and match on the constraint name there — that is where
        // Postgres actually names the violated index.
        // The class-level @Transactional ensures the failing insert rolls
        // back cleanly with the rest of the test; no poisoning of other
        // tests because each test runs in its own rolled-back tx.
        assertThatThrownBy(() -> {
            em.createNativeQuery("""
                    INSERT INTO auctions.aggregated_inventory
                        (ecoid2, merged_grade, datawipe, week_id, is_deprecated)
                    VALUES (:e, 'A', false, :w, false)
                    """)
                .setParameter("e", ecoid)
                .setParameter("w", weekId)
                .executeUpdate();
            em.flush();
        })
            .isInstanceOfAny(DataAccessException.class, PersistenceException.class)
            .hasRootCauseInstanceOf(PSQLException.class)
            .rootCause()
            .hasMessageContaining("uq_agi_ecoid_grade_dw_week");
    }

    @Test
    @DisplayName("partial unique index permits a deprecated row alongside a live one")
    void uniqueIndexAllowsDeprecatedDuplicate() {
        Long weekId = ((Number) em.createNativeQuery(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1")
                .getSingleResult()).longValue();

        String ecoid = "V68_IT_DEPR_" + System.nanoTime();

        em.createNativeQuery("""
                INSERT INTO auctions.aggregated_inventory
                    (ecoid2, merged_grade, datawipe, week_id, is_deprecated)
                VALUES (:e, 'A', false, :w, true)
                """)
            .setParameter("e", ecoid)
            .setParameter("w", weekId)
            .executeUpdate();

        em.createNativeQuery("""
                INSERT INTO auctions.aggregated_inventory
                    (ecoid2, merged_grade, datawipe, week_id, is_deprecated)
                VALUES (:e, 'A', false, :w, false)
                """)
            .setParameter("e", ecoid)
            .setParameter("w", weekId)
            .executeUpdate();
        em.flush();

        Number liveCount = (Number) em.createNativeQuery("""
                SELECT COUNT(*) FROM auctions.aggregated_inventory
                WHERE ecoid2 = :e AND is_deprecated = false
                """)
            .setParameter("e", ecoid)
            .getSingleResult();
        assertThat(liveCount.intValue()).isEqualTo(1);
    }
}
