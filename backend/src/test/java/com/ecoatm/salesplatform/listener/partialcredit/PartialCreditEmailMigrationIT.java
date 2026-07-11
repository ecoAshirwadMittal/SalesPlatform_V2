package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EmailAuditRepository;
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
 * End-to-end proof that the T11 migration actually wired Partial Credit onto
 * the unified email module — real {@code email.template} rows, a real
 * {@link ReviewCompletedEmailListener} bean, and a real
 * {@code EmailService.sendTemplated} write against Postgres.
 *
 * <p>This is deliberately NOT a Mockito unit test: the whole point is
 * proving the listener's {@code @Transactional} attribute change (T11 drops
 * {@code readOnly=true} — see the listener's javadoc) actually lets the
 * {@code email.log} INSERT inside {@code EmailService.sendTemplated} commit.
 * A readOnly regression would make Postgres reject that write, which a
 * mocked {@code EmailService} could never catch.
 *
 * <p>Follows the {@code AggInventorySyncListenerIT} pattern for exercising a
 * real {@code @TransactionalEventListener(AFTER_COMMIT)} + {@code @Async}
 * listener: publish the event inside a {@link TransactionTemplate}-managed
 * transaction that actually commits (a {@code @Transactional} test method
 * would roll back and suppress the AFTER_COMMIT phase entirely), then poll
 * for the async side effect. {@link EcoATMDirectUserRepository} is replaced
 * with a {@code @Primary} Mockito mock (not {@code @MockBean}, which would
 * poison the shared context cache) so the test doesn't have to build the
 * buyer/buyer-code/account/direct-user join chain just to resolve one
 * recipient — everything else (CreditRequest, email.template,
 * email.log, email_audit) is the real, unmocked Postgres path.
 */
@TestPropertySource(properties = {
        "partial-credit.review-completed-email.enabled=true"
})
class PartialCreditEmailMigrationIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockDirectUserRepositoryConfig {
        @Bean
        @Primary
        public EcoATMDirectUserRepository mockDirectUserRepository() {
            return Mockito.mock(EcoATMDirectUserRepository.class);
        }
    }

    private static final String RECIPIENT = "pcemailmigrationit-buyer@example.com";

    @Autowired private JdbcTemplate jdbc;
    @Autowired private CreditRequestRepository creditRequestRepository;
    @Autowired private CreditRequestStatusRepository statusRepository;
    @Autowired private EmailAuditRepository emailAuditRepository;
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
    @DisplayName("V92 copied the 3 PC template keys into email.template")
    void v92CopiedThreePcTemplateKeysIntoEmailTemplate() {
        List<String> keys = jdbc.queryForList(
                "SELECT template_key FROM email.template WHERE template_key IN "
                        + "('ReviewCompleted_Approved','ReviewCompleted_Declined','PhotoUploadRequested')",
                String.class);

        assertThat(keys).containsExactlyInAnyOrder(
                "ReviewCompleted_Approved", "ReviewCompleted_Declined", "PhotoUploadRequested");
    }

    @Test
    @DisplayName("completing a review writes an email.log row (source_module=PARTIAL_CREDIT), not a new email_audit row")
    void reviewCompleted_writesEmailLog_notEmailAudit() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(any()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT, "IT Buyer"}));

        Long buyerCodeId = jdbc.queryForObject("SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        Long approvedStatusId = statusRepository.findBySystemStatus(SystemStatus.APPROVED)
                .orElseThrow(() -> new IllegalStateException("V89 must seed an APPROVED status row"))
                .getId();

        CreditRequest cr = new CreditRequest();
        cr.setRequestNumber("PCR-EMAIL-IT-" + System.nanoTime());
        cr.setOrderNumber("SO-EMAIL-IT");
        cr.setStatusId(approvedStatusId);
        cr.setBuyerCodeId(buyerCodeId);
        cr.setApprovedTotal(new BigDecimal("42.00"));
        cr = creditRequestRepository.save(cr);
        createdRequestId = cr.getId();

        long auditRowsBefore = emailAuditRepository.findByCreditRequestIdOrderBySentAtDesc(createdRequestId).size();

        Long requestId = createdRequestId;
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new ReviewCompletedEvent(requestId, SystemStatus.APPROVED, 1L, Instant.now())));

        // @Async dispatches onReviewCompleted to a different thread — poll
        // until the email.log write lands (or the deadline trips the test).
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
        assertThat(row.get("template_key")).isEqualTo("ReviewCompleted_Approved");
        assertThat((String) row.get("to_address")).contains(RECIPIENT);
        // LoggingEmailSender (default when pws.email.enabled is unset) never
        // throws, so the real send path resolves to SENT here.
        assertThat(row.get("status")).isEqualTo("SENT");

        // D5 — partial_credit.email_audit is frozen; T11 must not add rows.
        assertThat(emailAuditRepository.findByCreditRequestIdOrderBySentAtDesc(createdRequestId))
                .hasSize((int) auditRowsBefore);
    }
}
