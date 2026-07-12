package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.pws.Rma;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Real-Postgres integration test for
 * {@link RmaRepository#findPollableForDeposcoSync} — the candidate finder for
 * the Deposco RMA status sync job (the modern port of legacy
 * {@code ACT_UpdateRMAFromDeposco}).
 *
 * <p>Two properties can only be proven against a real database, not by the
 * mocked-repository {@code RmaDeposcoSyncServiceTest}:
 * <ol>
 *   <li><b>Filter correctness.</b> The JPQL {@code WHERE} must return an RMA
 *       only when it carries an {@code oracle_number} AND its
 *       {@code rma_status} (via the FK join, not the dirty direct
 *       {@code system_status} column) is not in the terminal skip set —
 *       excluding terminal (Received/Declined) and null-Oracle rows.</li>
 *   <li><b>{@code JOIN FETCH} eager init (the LazyInit guard).</b> The service
 *       reads {@code rma.getRmaStatus().getSystemStatus()} OUTSIDE any
 *       transaction ({@code spring.jpa.open-in-view: false}). The
 *       {@code rma_status} association is a LAZY {@code @ManyToOne}, so only the
 *       {@code JOIN FETCH} keeps that read from throwing
 *       {@code LazyInitializationException} once the persistence context
 *       closes. This test is deliberately NOT {@code @Transactional} so the
 *       context closes when the repository method returns and the returned
 *       entities are detached — exactly the service's runtime condition.</li>
 * </ol>
 *
 * <p>Seeds five {@code pws.rma} rows (ids 90001–90005) via {@link JdbcTemplate}
 * (auto-committed, since there is no surrounding test transaction) and removes
 * them again in {@link #cleanup()}. Assertions are scoped to those seeded ids
 * so the shared local dev-DB fallback (which may hold unrelated pollable rows)
 * cannot make them flaky — the same "loose containment" approach the sibling
 * {@code EmailRepositoryIT} uses.
 */
class RmaRepositoryIT extends PostgresIntegrationTest {

    // The service's real terminal set (RmaDeposcoSyncService.TERMINAL_STATUSES is
    // package-private in service.rma, so it is restated here for the finder call).
    private static final Set<String> TERMINAL_STATUSES =
            Set.of("Received", "Canceled", "Declined", "Submitted");

    private static final long POLLABLE_ID = 90001L;          // Receiving + oracle number -> returns
    private static final long TERMINAL_RECEIVED_ID = 90002L; // Received (terminal) -> excluded
    private static final long NULL_ORACLE_ID = 90003L;       // Receiving but no oracle number -> excluded
    private static final long TERMINAL_DECLINED_ID = 90004L; // Declined (terminal) -> excluded
    private static final long SUBMITTED_ID = 90005L;         // Submitted (M-1 safety skip) -> excluded

    private static final Set<Long> SEEDED_IDS = Set.of(
            POLLABLE_ID, TERMINAL_RECEIVED_ID, NULL_ORACLE_ID, TERMINAL_DECLINED_ID, SUBMITTED_ID);

    @Autowired private RmaRepository rmaRepository;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void seed() {
        cleanup(); // idempotent — clear any residue from a previously aborted run
        // Pollable row: the direct system_status column is left NULL on purpose to
        // prove the finder filters via the rma_status FK join, not that column.
        insertRma(POLLABLE_ID, "IT-DEP-RMA-1", "Receiving", "IT-DEP-ORD-1", null,
                LocalDateTime.of(2026, 1, 1, 10, 0));
        insertRma(TERMINAL_RECEIVED_ID, "IT-DEP-RMA-2", "Received", "IT-DEP-ORD-2", "Received",
                LocalDateTime.of(2026, 1, 2, 10, 0));
        insertRma(NULL_ORACLE_ID, "IT-DEP-RMA-3", "Receiving", null, "Receiving",
                LocalDateTime.of(2026, 1, 3, 10, 0));
        insertRma(TERMINAL_DECLINED_ID, "IT-DEP-RMA-4", "Declined", "IT-DEP-ORD-4", "Declined",
                LocalDateTime.of(2026, 1, 4, 10, 0));
        insertRma(SUBMITTED_ID, "IT-DEP-RMA-5", "Submitted", "IT-DEP-ORD-5", "Submitted",
                LocalDateTime.of(2026, 1, 5, 10, 0));
    }

    @AfterEach
    void cleanup() {
        jdbc.update("DELETE FROM pws.rma WHERE id IN (?,?,?,?,?)",
                POLLABLE_ID, TERMINAL_RECEIVED_ID, NULL_ORACLE_ID, TERMINAL_DECLINED_ID, SUBMITTED_ID);
    }

    private void insertRma(long id, String number, String statusSystemName,
                           String oracleNumber, String directSystemStatus, LocalDateTime createdAt) {
        jdbc.update("""
                INSERT INTO pws.rma
                    (id, number, rma_status_id, oracle_number, system_status, created_date, updated_date)
                VALUES
                    (?, ?, (SELECT id FROM pws.rma_status WHERE system_status = ?), ?, ?, ?, ?)
                """,
                id, number, statusSystemName, oracleNumber, directSystemStatus,
                Timestamp.valueOf(createdAt), Timestamp.valueOf(createdAt));
    }

    @Test
    @DisplayName("finder returns only the pollable row — excludes terminal (Received/Declined), Submitted, and null-Oracle rows")
    void findPollable_returnsOnlyPollable_excludesTerminalSubmittedAndNullOracle() {
        List<Rma> result = rmaRepository.findPollableForDeposcoSync(TERMINAL_STATUSES);

        List<Long> allReturnedIds = result.stream().map(Rma::getId).toList();
        assertThat(allReturnedIds)
                .contains(POLLABLE_ID)
                .doesNotContain(TERMINAL_RECEIVED_ID, NULL_ORACLE_ID, TERMINAL_DECLINED_ID, SUBMITTED_ID);

        // Scoped to the seeded ids: among our five rows, exactly the pollable one
        // comes back — robust even if the shared dev DB holds other pollable RMAs.
        List<Long> seededReturned = allReturnedIds.stream().filter(SEEDED_IDS::contains).toList();
        assertThat(seededReturned).containsExactly(POLLABLE_ID);
    }

    @Test
    @DisplayName("JOIN FETCH eagerly loads rma_status — reading the status after the context closes does not throw LazyInitializationException")
    void findPollable_joinFetch_statusReadableAfterContextCloses() {
        // No @Transactional on this class, so the repository method runs in its own
        // transaction and the returned entities are detached on return — the exact
        // open-in-view:false condition the service reads the status under.
        List<Rma> result = rmaRepository.findPollableForDeposcoSync(TERMINAL_STATUSES);

        assertThat(result).isNotEmpty();

        // The brief's crucial assertion: reading the LAZY association after the
        // persistence context has closed must NOT throw — proving JOIN FETCH.
        assertThatCode(() -> {
            String systemStatus = result.get(0).getRmaStatus().getSystemStatus();
            assertThat(systemStatus).isNotNull();
        }).doesNotThrowAnyException();

        // Belt-and-suspenders on our own seeded pollable row: the association is
        // already initialized (not a bare uninitialized proxy) and reads correctly.
        Rma pollable = result.stream()
                .filter(r -> POLLABLE_ID == r.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("seeded pollable RMA " + POLLABLE_ID + " not returned"));
        assertThat(Hibernate.isInitialized(pollable.getRmaStatus()))
                .as("rma_status must be eagerly initialized by JOIN FETCH")
                .isTrue();
        assertThat(pollable.getRmaStatus().getSystemStatus()).isEqualTo("Receiving");
    }
}
