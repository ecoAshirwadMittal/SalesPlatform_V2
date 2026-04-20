package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.auctions.WeekSyncWatermark;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Repository tests for {@link WeekSyncWatermarkRepository}.
 * <p>
 * Mirrors the style of {@code AggregatedInventoryRepositoryTest} / {@code WeekRepositoryTest}:
 * {@code @DataJpaTest} against the live Postgres dev database (Flyway V1..V68
 * already seeded, including {@code mdm.week} rows from V65). No embedded DB —
 * the FK to {@code mdm.week(id)} cannot be seeded in H2 without porting every
 * prior migration.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class WeekSyncWatermarkRepositoryTest {

    private static final String SOURCE_SNOWFLAKE = "SNOWFLAKE_AGG_INVENTORY";
    private static final String SOURCE_MANUAL = "MANUAL";

    @Autowired private WeekSyncWatermarkRepository repository;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("save persists a watermark and populates @PrePersist audit timestamps")
    void save_persistsAndReturns() {
        Long weekId = seededWeekId();
        // Postgres TIMESTAMPTZ stores microsecond precision; Instant.now() is
        // nanosecond. Truncate so the round-tripped value is byte-equal.
        Instant uploadAt = Instant.now().minusSeconds(60).truncatedTo(ChronoUnit.MICROS);

        WeekSyncWatermark saved = repository.save(newWatermark(weekId, SOURCE_SNOWFLAKE, uploadAt, 42));
        // Force the SELECT so @PrePersist values round-trip via the DB, not just
        // the in-memory write.
        em.flush();
        em.clear();

        Optional<WeekSyncWatermark> reloaded =
                repository.findById(new WeekSyncWatermark.Key(weekId, SOURCE_SNOWFLAKE));

        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getWeekId()).isEqualTo(weekId);
        assertThat(reloaded.get().getSource()).isEqualTo(SOURCE_SNOWFLAKE);
        assertThat(reloaded.get().getLastSourceUploadAt()).isEqualTo(uploadAt);
        assertThat(reloaded.get().getRowCount()).isEqualTo(42);
        assertThat(reloaded.get().getCreatedAt()).isNotNull();
        assertThat(reloaded.get().getUpdatedAt()).isNotNull();
        assertThat(reloaded.get().getLastSyncedAt()).isNotNull();

        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByWeekIdAndSource returns the row when (weekId, source) exists")
    void findByWeekIdAndSource_returnsRow_whenExists() {
        Long weekId = seededWeekId();

        repository.save(newWatermark(weekId, SOURCE_SNOWFLAKE, Instant.now(), 7));
        em.flush();
        em.clear();

        Optional<WeekSyncWatermark> found =
                repository.findByWeekIdAndSource(weekId, SOURCE_SNOWFLAKE);

        assertThat(found).isPresent();
        assertThat(found.get().getRowCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("findByWeekIdAndSource returns empty when no row matches")
    void findByWeekIdAndSource_returnsEmpty_whenMissing() {
        Long weekId = seededWeekId();

        // No insert on this key — ensure the finder returns empty, not a stale row.
        Optional<WeekSyncWatermark> found =
                repository.findByWeekIdAndSource(weekId, "DOES_NOT_EXIST_SOURCE");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("composite PK permits same weekId with different sources")
    void compositeKey_allowsSameWeekDifferentSources() {
        Long weekId = seededWeekId();

        repository.save(newWatermark(weekId, SOURCE_SNOWFLAKE, Instant.now().minusSeconds(120), 10));
        repository.save(newWatermark(weekId, SOURCE_MANUAL, Instant.now().minusSeconds(60), 3));
        em.flush();
        em.clear();

        Optional<WeekSyncWatermark> snowflake =
                repository.findByWeekIdAndSource(weekId, SOURCE_SNOWFLAKE);
        Optional<WeekSyncWatermark> manual =
                repository.findByWeekIdAndSource(weekId, SOURCE_MANUAL);

        assertThat(snowflake).isPresent();
        assertThat(snowflake.get().getRowCount()).isEqualTo(10);
        assertThat(manual).isPresent();
        assertThat(manual.get().getRowCount()).isEqualTo(3);
    }

    /**
     * Picks any seeded {@code mdm.week} id so inserts satisfy the FK in V68.
     * V65 seeds real rows, so the query always returns something on a
     * correctly-migrated DB.
     */
    private Long seededWeekId() {
        Number id = (Number) em.createNativeQuery(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1")
                .getSingleResult();
        return id.longValue();
    }

    private static WeekSyncWatermark newWatermark(Long weekId, String source,
                                                  Instant uploadAt, int rowCount) {
        WeekSyncWatermark w = new WeekSyncWatermark();
        w.setWeekId(weekId);
        w.setSource(source);
        w.setLastSourceUploadAt(uploadAt);
        w.setRowCount(rowCount);
        return w;
    }
}
