package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SyncLogWriter}.
 *
 * <p>Uses a real Postgres datasource (pg-test profile) so the REQUIRES_NEW
 * transaction semantics and column constraints are exercised against the actual
 * schema. Tests run inside a surrounding @Transactional so rows are rolled back
 * after each test.
 */
@SpringBootTest
@Transactional
class SyncLogWriterTest {

    @Autowired SyncLogWriter syncLogWriter;
    @Autowired SnowflakeSyncLogRepository repository;

    @Test
    void writeFailed_insertsSingleFailedRowWithTruncatedMessage() {
        Long id = syncLogWriter.writeFailed("TEST_SYNC", "key=42", "boom".repeat(500));
        assertThat(id).isNotNull();

        SnowflakeSyncLog row = repository.findById(id).orElseThrow();
        assertThat(row.getStatus()).isEqualTo("FAILED");
        assertThat(row.getSyncType()).isEqualTo("TEST_SYNC");
        assertThat(row.getTargetKey()).isEqualTo("key=42");
        assertThat(row.getErrorMessage()).hasSize(1000); // truncated from 2000
        assertThat(row.getStartedAt()).isNotNull();
        assertThat(row.getFinishedAt()).isNotNull();
        assertThat(row.getRowsRead()).isZero();
        assertThat(row.getRowsUpserted()).isZero();
    }

    @Test
    void writeFailed_shortMessageNotTruncated() {
        Long id = syncLogWriter.writeFailed("TEST_SYNC", "key=99", "short error");
        assertThat(id).isNotNull();

        SnowflakeSyncLog row = repository.findById(id).orElseThrow();
        assertThat(row.getErrorMessage()).isEqualTo("short error");
    }
}
