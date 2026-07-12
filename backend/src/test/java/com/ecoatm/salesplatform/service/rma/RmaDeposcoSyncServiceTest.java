package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaStatus;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.repository.pws.RmaStatusRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RmaDeposcoSyncService}. Mocks every collaborator
 * ({@link RmaRepository}, {@link RmaStatusRepository}, {@link DeposcoRmaClient})
 * and injects a fixed {@link Clock} so the job is deterministic. Covers the
 * job's orchestration only — the finder's real SQL filtering (Oracle-number
 * present + non-terminal status via the {@code rma_status} join) is a repository
 * concern proven against real Postgres, not here.
 */
@ExtendWith(MockitoExtension.class)
class RmaDeposcoSyncServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-11T12:00:00Z"), ZoneOffset.UTC);

    @Mock private RmaRepository rmaRepository;
    @Mock private RmaStatusRepository rmaStatusRepository;
    @Mock private DeposcoRmaClient deposcoRmaClient;

    private RmaDeposcoSyncService enabledService() {
        return new RmaDeposcoSyncService(
                rmaRepository, rmaStatusRepository, deposcoRmaClient, FIXED_CLOCK, true);
    }

    private RmaStatus status(String systemStatus) {
        RmaStatus s = new RmaStatus();
        s.setSystemStatus(systemStatus);
        return s;
    }

    private Rma rma(Long id, String oracleNumber, String systemStatus) {
        Rma r = new Rma();
        r.setId(id);
        r.setNumber("RMA-" + id);
        r.setOracleNumber(oracleNumber);
        r.setRmaStatus(status(systemStatus));
        r.setSystemStatus(systemStatus);
        return r;
    }

    @Test
    @DisplayName("client reports Received -> RMA advances to Received and is persisted")
    void sync_received_advancesAndPersists() {
        Rma r = rma(1L, "ORD-1", "Receiving");
        RmaStatus received = status("Received");
        when(rmaRepository.findPollableForDeposcoSync(any())).thenReturn(List.of(r));
        when(deposcoRmaClient.fetchStatus("ORD-1"))
                .thenReturn(Optional.of(new DeposcoRmaStatus("Received")));
        when(rmaStatusRepository.findBySystemStatus("Received")).thenReturn(Optional.of(received));

        int advanced = enabledService().sync();

        assertThat(advanced).isEqualTo(1);
        assertThat(r.getSystemStatus()).isEqualTo("Received");
        assertThat(r.getRmaStatus()).isSameAs(received);
        verify(rmaRepository).save(r);
    }

    @Test
    @DisplayName("client reports no update -> RMA left unchanged, not persisted")
    void sync_noUpdate_leavesUnchanged() {
        Rma r = rma(2L, "ORD-2", "Receiving");
        when(rmaRepository.findPollableForDeposcoSync(any())).thenReturn(List.of(r));
        when(deposcoRmaClient.fetchStatus("ORD-2")).thenReturn(Optional.empty());

        int advanced = enabledService().sync();

        assertThat(advanced).isZero();
        assertThat(r.getSystemStatus()).isEqualTo("Receiving");
        verify(rmaRepository, never()).save(any());
        verify(rmaStatusRepository, never()).findBySystemStatus(anyString());
    }

    @Test
    @DisplayName("client reports a non-Received status -> RMA left unchanged")
    void sync_nonReceivedStatus_leavesUnchanged() {
        Rma r = rma(3L, "ORD-3", "Receiving");
        when(rmaRepository.findPollableForDeposcoSync(any())).thenReturn(List.of(r));
        when(deposcoRmaClient.fetchStatus("ORD-3"))
                .thenReturn(Optional.of(new DeposcoRmaStatus("Receiving")));

        int advanced = enabledService().sync();

        assertThat(advanced).isZero();
        assertThat(r.getSystemStatus()).isEqualTo("Receiving");
        verify(rmaRepository, never()).save(any());
    }

    @Test
    @DisplayName("terminal RMAs (Received/Canceled/Declined/Submitted) are never polled or advanced")
    void sync_terminalRmas_skipped() {
        // M-1: both Declined (product decision 2026-07-12 — treat as terminal,
        // a deliberate divergence from legacy which polls Declined) and Submitted
        // (safety invariant — an unreviewed RMA must never be auto-advanced to
        // Received, even once the Oracle push starts setting its oracle_number)
        // must be in the skip set and never handed to the Deposco client.
        Rma received = rma(4L, "ORD-4", "Received");
        Rma canceled = rma(5L, "ORD-5", "Canceled");
        Rma declined = rma(6L, "ORD-6", "Declined");
        Rma submitted = rma(13L, "ORD-13", "Submitted");
        when(rmaRepository.findPollableForDeposcoSync(any()))
                .thenReturn(List.of(received, canceled, declined, submitted));

        int advanced = enabledService().sync();

        assertThat(advanced).isZero();
        verify(deposcoRmaClient, never()).fetchStatus(anyString());
        verify(rmaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RMAs with a null Oracle number are not polled")
    void sync_nullOracleNumber_notPolled() {
        Rma r = rma(7L, null, "Receiving");
        when(rmaRepository.findPollableForDeposcoSync(any())).thenReturn(List.of(r));

        int advanced = enabledService().sync();

        assertThat(advanced).isZero();
        verify(deposcoRmaClient, never()).fetchStatus(any());
        verify(rmaRepository, never()).save(any());
    }

    @Test
    @DisplayName("only the pollable RMA in a mixed batch is advanced")
    void sync_mixedBatch_advancesOnlyPollable() {
        Rma pollable = rma(8L, "ORD-8", "Receiving");
        Rma terminal = rma(9L, "ORD-9", "Received");
        Rma noOracle = rma(10L, null, "New");
        RmaStatus received = status("Received");
        when(rmaRepository.findPollableForDeposcoSync(any()))
                .thenReturn(List.of(pollable, terminal, noOracle));
        when(deposcoRmaClient.fetchStatus("ORD-8"))
                .thenReturn(Optional.of(new DeposcoRmaStatus("Received")));
        when(rmaStatusRepository.findBySystemStatus("Received")).thenReturn(Optional.of(received));

        int advanced = enabledService().sync();

        assertThat(advanced).isEqualTo(1);
        verify(deposcoRmaClient).fetchStatus("ORD-8");
        verify(deposcoRmaClient, never()).fetchStatus("ORD-9");
        verify(rmaRepository).save(pollable);
        verify(rmaRepository, never()).save(terminal);
    }

    @Test
    @DisplayName("scheduled entry point short-circuits when the toggle is disabled")
    void pollDeposco_disabled_shortCircuits() {
        RmaDeposcoSyncService disabled = new RmaDeposcoSyncService(
                rmaRepository, rmaStatusRepository, deposcoRmaClient, FIXED_CLOCK, false);

        disabled.pollDeposco();

        verifyNoInteractions(rmaRepository, deposcoRmaClient, rmaStatusRepository);
    }

    @Test
    @DisplayName("scheduled entry point runs the poll when enabled")
    void pollDeposco_enabled_invokesFinder() {
        when(rmaRepository.findPollableForDeposcoSync(any())).thenReturn(List.of());

        enabledService().pollDeposco();

        verify(rmaRepository).findPollableForDeposcoSync(any());
    }

    @Test
    @DisplayName("scheduled method carries @SchedulerLock(name=\"rmaDeposcoSync\") and @Scheduled")
    void pollDeposco_hasSchedulerLockAndScheduled() throws NoSuchMethodException {
        Method m = RmaDeposcoSyncService.class.getMethod("pollDeposco");

        SchedulerLock lock = m.getAnnotation(SchedulerLock.class);
        assertThat(lock).as("@SchedulerLock present").isNotNull();
        assertThat(lock.name()).isEqualTo("rmaDeposcoSync");
        assertThat(m.getAnnotation(Scheduled.class)).as("@Scheduled present").isNotNull();
    }

    @Test
    @DisplayName("the enabled toggle defaults to false (rma.deposco-sync.enabled:false)")
    void enabledToggle_defaultsToFalse() {
        Constructor<?> ctor = RmaDeposcoSyncService.class.getDeclaredConstructors()[0];
        Value valueAnnotation = null;
        for (Parameter p : ctor.getParameters()) {
            if (p.getType() == boolean.class) {
                valueAnnotation = p.getAnnotation(Value.class);
            }
        }
        assertThat(valueAnnotation).as("@Value on the enabled flag").isNotNull();
        assertThat(valueAnnotation.value()).isEqualTo("${rma.deposco-sync.enabled:false}");
    }
}
