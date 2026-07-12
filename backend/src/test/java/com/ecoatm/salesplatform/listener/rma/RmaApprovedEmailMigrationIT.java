package com.ecoatm.salesplatform.listener.rma;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * End-to-end proof that Task C actually wired the RMA approval email onto the
 * unified email module — a real V93 {@code RMA_Approved} row, a real
 * {@link RmaApprovedEmailListener} bean, and a real
 * {@code EmailService.sendTemplated} write against Postgres.
 *
 * <p>Deliberately NOT a Mockito unit test: the whole point is proving the
 * listener's {@code @Transactional(REQUIRES_NEW)} attribute is NOT
 * {@code readOnly}, so the {@code email.log} INSERT inside
 * {@code EmailService.sendTemplated} commits (a readOnly regression would make
 * Postgres reject that write — invisible to a mocked {@code EmailService}).
 *
 * <p>Follows {@code PartialCreditEmailMigrationIT} / {@code AggInventorySyncListenerIT}
 * for exercising a real {@code @TransactionalEventListener(AFTER_COMMIT)} +
 * {@code @Async} listener: publish inside a {@link TransactionTemplate}-managed
 * transaction that actually commits (a {@code @Transactional} test method would
 * roll back and suppress the AFTER_COMMIT phase), then poll for the async side
 * effect. {@link EcoATMDirectUserRepository} is replaced with a {@code @Primary}
 * Mockito mock (not {@code @MockBean}, which would poison the shared context
 * cache) so the test needn't build the buyer/account/direct-user join chain just
 * to resolve one recipient.
 */
class RmaApprovedEmailMigrationIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockDirectUserRepositoryConfig {
        @Bean
        @Primary
        public EcoATMDirectUserRepository mockDirectUserRepository() {
            return Mockito.mock(EcoATMDirectUserRepository.class);
        }
    }

    private static final String RECIPIENT = "rmaemailmigrationit-buyer@example.com";

    @Autowired private JdbcTemplate jdbc;
    @Autowired private RmaRepository rmaRepository;
    @Autowired private EcoATMDirectUserRepository directUserRepository;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private TransactionTemplate transactionTemplate;

    private Long createdRmaId;

    @BeforeEach
    void resetMock() {
        Mockito.reset(directUserRepository);
    }

    @AfterEach
    void cleanup() {
        // The listener writes with its own REQUIRES_NEW transaction (a different
        // thread/connection), so nothing here is rolled back by Spring's
        // test-transaction machinery — clean up explicitly.
        if (createdRmaId != null) {
            jdbc.update(
                    "DELETE FROM email.log WHERE source_module = 'RMA' AND source_id = ?", createdRmaId);
            jdbc.update("DELETE FROM pws.rma WHERE id = ?", createdRmaId);
        }
    }

    @Test
    @DisplayName("APPROVED review writes an email.log row (source_module=RMA, status=SENT)")
    void approvedReview_writesEmailLog() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(any()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT, "IT Buyer"}));

        Long buyerCodeId = jdbc.queryForObject("SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);

        Rma rma = new Rma();
        rma.setNumber("RMA-EMAIL-IT-" + System.nanoTime());
        rma.setBuyerCodeId(buyerCodeId);
        rma.setApprovedQty(2);
        rma.setApprovedSkus(1);
        rma.setApprovedSalesTotal(new BigDecimal("1234.50"));
        rma.setSystemStatus("Approved");
        rma = rmaRepository.save(rma);
        createdRmaId = rma.getId();

        Long rmaId = createdRmaId;
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new RmaReviewCompletedEvent(rmaId, RmaReviewOutcome.APPROVED, 1L, Instant.now())));

        // @Async dispatches onRmaReviewCompleted to a different thread — poll
        // until the email.log write lands (or the deadline trips the test).
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(100)).untilAsserted(() -> {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM email.log WHERE source_module = 'RMA' AND source_id = ?",
                    Long.class, rmaId);
            assertThat(count).isEqualTo(1L);
        });

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT template_key, to_address, status FROM email.log "
                        + "WHERE source_module = 'RMA' AND source_id = ?", rmaId);
        assertThat(row.get("template_key")).isEqualTo("RMA_Approved");
        assertThat((String) row.get("to_address")).contains(RECIPIENT);
        // LoggingEmailSender (default when pws.email.enabled is unset) never
        // throws, so the real send path resolves to SENT here.
        assertThat(row.get("status")).isEqualTo("SENT");
    }

    @Test
    @DisplayName("DECLINED review writes no email.log row")
    void declinedReview_writesNoEmailLog() {
        Long buyerCodeId = jdbc.queryForObject("SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);

        Rma rma = new Rma();
        rma.setNumber("RMA-EMAIL-IT-DECL-" + System.nanoTime());
        rma.setBuyerCodeId(buyerCodeId);
        rma.setSystemStatus("Declined");
        rma = rmaRepository.save(rma);
        createdRmaId = rma.getId();

        Long rmaId = createdRmaId;
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new RmaReviewCompletedEvent(rmaId, RmaReviewOutcome.DECLINED, 1L, Instant.now())));

        // Give the async pool a beat, then assert nothing was written — a
        // DECLINED outcome must not trigger the approval email.
        await().during(Duration.ofMillis(700)).atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM email.log WHERE source_module = 'RMA' AND source_id = ?",
                    Long.class, rmaId);
            assertThat(count).isEqualTo(0L);
        });
    }
}
