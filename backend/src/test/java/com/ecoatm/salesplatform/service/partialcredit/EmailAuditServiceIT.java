package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.partialcredit.EmailAudit;
import com.ecoatm.salesplatform.repository.partialcredit.EmailAuditRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test for {@link EmailAuditService}. Exercises the
 * actual JPA persistence path against PostgreSQL (Testcontainers when
 * Docker is up; the local salesplatform_dev DB otherwise) so the V90
 * column shapes are verified end-to-end — not just at the migration
 * level.
 */
class EmailAuditServiceIT extends PostgresIntegrationTest {

    @Autowired private EmailAuditService service;
    @Autowired private EmailAuditRepository repository;

    @AfterEach
    void clearAudit() {
        // The service writes with REQUIRES_NEW, so rows are not rolled
        // back by Spring's test transaction. Clean up explicitly.
        repository.deleteAll();
    }

    @Test
    @DisplayName("record — writes a success row with the supplied fields")
    void record_writesSuccessRow() {
        Instant before = Instant.now().minusSeconds(2);

        EmailAudit saved = service.record(
                "ReviewCompleted_Approved",
                "buyer@example.com",
                /* creditRequestId */ null,
                /* success */ true,
                /* errorMessage */ null);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        EmailAudit reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getTemplateKey()).isEqualTo("ReviewCompleted_Approved");
        assertThat(reloaded.getRecipientEmail()).isEqualTo("buyer@example.com");
        assertThat(reloaded.getCreditRequestId()).isNull();
        assertThat(reloaded.getSuccess()).isTrue();
        assertThat(reloaded.getErrorMessage()).isNull();
        assertThat(reloaded.getSentAt()).isAfter(before);
    }

    @Test
    @DisplayName("record — writes a failure row with the error message")
    void record_writesFailureRow() {
        EmailAudit saved = service.record(
                "ReviewCompleted_Declined",
                "another@example.com",
                /* creditRequestId */ null,
                /* success */ false,
                /* errorMessage */ "smtp connect refused");

        EmailAudit reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getSuccess()).isFalse();
        assertThat(reloaded.getErrorMessage()).isEqualTo("smtp connect refused");
    }

    @Test
    @DisplayName("recordBatch — writes one row per recipient and they sort newest-first by query")
    void recordBatch_writesOneRowPerRecipient() {
        service.recordBatch(
                "ReviewCompleted_Approved",
                List.of("a@example.com", "b@example.com", "c@example.com"),
                /* creditRequestId */ null,
                /* success */ true,
                /* errorMessage */ null);

        List<EmailAudit> all = repository.findAll();
        assertThat(all).hasSize(3);
        assertThat(all).extracting(EmailAudit::getRecipientEmail)
                .containsExactlyInAnyOrder("a@example.com", "b@example.com", "c@example.com");
    }
}
