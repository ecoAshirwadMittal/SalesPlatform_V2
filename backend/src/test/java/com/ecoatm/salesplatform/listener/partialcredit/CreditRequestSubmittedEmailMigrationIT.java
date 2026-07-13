package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.partialcredit.CreditRequestSubmittedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * End-to-end proof that gap 2.5 Task 1 wired the buyer submission-confirmation
 * email onto the unified email module — a real {@code email.template} row
 * (V101), a real {@link CreditRequestSubmittedEmailListener} bean, and a real
 * {@code EmailService.sendTemplated} write against Postgres.
 *
 * <p>Deliberately NOT a Mockito unit test: it proves the listener's
 * {@code @Transactional(REQUIRES_NEW)} is NOT {@code readOnly}, so the
 * {@code email.log} INSERT inside {@code EmailService.sendTemplated} commits (a
 * readOnly regression would make Postgres reject that write, which a mocked
 * {@code EmailService} could never catch).
 *
 * <p>Mirrors {@code PartialCreditEmailMigrationIT}: publish the event inside a
 * {@link TransactionTemplate}-managed transaction that actually commits (a
 * {@code @Transactional} test method would roll back and suppress the
 * AFTER_COMMIT phase entirely), then poll for the async side effect.
 * {@link EcoATMDirectUserRepository} is replaced with a {@code @Primary} Mockito
 * mock (not {@code @MockBean}, which would poison the shared context cache) so
 * the test doesn't have to build the buyer/buyer-code/account/direct-user join
 * chain just to resolve one recipient — everything else (CreditRequest,
 * email.template, email.log) is the real, unmocked Postgres path.
 */
@TestPropertySource(properties = {
        "partial-credit.submitted-email.enabled=true"
})
class CreditRequestSubmittedEmailMigrationIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockDirectUserRepositoryConfig {
        @Bean
        @Primary
        public EcoATMDirectUserRepository mockDirectUserRepository() {
            return Mockito.mock(EcoATMDirectUserRepository.class);
        }
    }

    private static final String RECIPIENT = "crsubmit-emailmigrationit-buyer@example.com";

    @Autowired private JdbcTemplate jdbc;
    @Autowired private CreditRequestRepository creditRequestRepository;
    @Autowired private CreditRequestStatusRepository statusRepository;
    @Autowired private EcoATMDirectUserRepository directUserRepository;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private TransactionTemplate transactionTemplate;

    private Long createdRequestId;

    @BeforeEach
    void resetMock() {
        Mockito.reset(directUserRepository);
    }

    @AfterEach
    void cleanup() {
        // The listener writes with its own REQUIRES_NEW transaction (on a
        // different thread/connection), so nothing here is rolled back by
        // Spring's test-transaction machinery — clean up explicitly.
        if (createdRequestId != null) {
            jdbc.update(
                    "DELETE FROM email.log WHERE source_module = 'PARTIAL_CREDIT' AND source_id = ?",
                    createdRequestId);
            jdbc.update("DELETE FROM partial_credit.credit_requests WHERE id = ?", createdRequestId);
        }
    }

    @Test
    @DisplayName("V101 seeded the CreditRequestSubmitted key into email.template")
    void v101SeededCreditRequestSubmittedTemplateKey() {
        List<String> keys = jdbc.queryForList(
                "SELECT template_key FROM email.template WHERE template_key = 'CreditRequestSubmitted'",
                String.class);
        assertThat(keys).containsExactly("CreditRequestSubmitted");
    }

    @Test
    @DisplayName("submitting a credit request writes one email.log row (source_module=PARTIAL_CREDIT, SENT)")
    void submitted_writesEmailLog() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(any()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT, "IT Buyer"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(any()))
                .thenReturn(List.of("IT Buyer Co"));

        Long buyerCodeId = jdbc.queryForObject("SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        Long pendingStatusId = statusRepository.findBySystemStatus(SystemStatus.PENDING_APPROVAL)
                .orElseThrow(() -> new IllegalStateException("V89 must seed a PENDING_APPROVAL status row"))
                .getId();

        CreditRequest cr = new CreditRequest();
        cr.setRequestNumber("PCR-SUBMIT-IT-" + System.nanoTime());
        cr.setOrderNumber("SO-SUBMIT-IT");
        cr.setStatusId(pendingStatusId);
        cr.setBuyerCodeId(buyerCodeId);
        cr.setHasMissingDevice(true);
        cr.setRequestedTotal(new BigDecimal("42.00"));
        cr = creditRequestRepository.save(cr);
        createdRequestId = cr.getId();

        Long requestId = createdRequestId;
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new CreditRequestSubmittedEvent(requestId, 1L, Instant.now())));

        // @Async dispatches onCreditRequestSubmitted to a different thread —
        // poll until the email.log write lands (or the deadline trips the test).
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(100)).untilAsserted(() -> {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM email.log WHERE source_module = 'PARTIAL_CREDIT' AND source_id = ?",
                    Long.class, requestId);
            assertThat(count).isEqualTo(1L);
        });

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT template_key, to_address, status FROM email.log "
                        + "WHERE source_module = 'PARTIAL_CREDIT' AND source_id = ?",
                requestId);
        assertThat(row.get("template_key")).isEqualTo("CreditRequestSubmitted");
        assertThat((String) row.get("to_address")).contains(RECIPIENT);
        // LoggingEmailSender (default when pws.email.enabled is unset) never
        // throws, so the real send path resolves to SENT here.
        assertThat(row.get("status")).isEqualTo("SENT");
    }
}
