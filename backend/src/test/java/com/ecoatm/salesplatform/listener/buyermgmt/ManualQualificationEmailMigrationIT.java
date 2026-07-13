package com.ecoatm.salesplatform.listener.buyermgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
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
 * End-to-end proof that Task 2.4.4 actually wired the manual-qualification email
 * onto the unified email module — a real V99 {@code ManualQualification} row, a
 * real {@link ManualQualificationEmailListener} bean, and a real
 * {@code EmailService.sendTemplated} write against Postgres.
 *
 * <p>Deliberately NOT a Mockito unit test: the whole point is proving the
 * listener's {@code @Transactional(REQUIRES_NEW)} attribute is NOT
 * {@code readOnly}, so the {@code email.log} INSERT inside
 * {@code EmailService.sendTemplated} commits (a readOnly regression would make
 * Postgres reject that write — invisible to a mocked {@code EmailService}). It
 * also proves the legacy {@code NF_OnIncludedChanged_New} gate holds on the real
 * event path: only a {@code Started + included} override produces an email.
 *
 * <p>Follows {@code RmaApprovedEmailMigrationIT} / {@code PartialCreditEmailMigrationIT}
 * for exercising a real {@code @TransactionalEventListener(AFTER_COMMIT)} +
 * {@code @Async} listener: publish inside a {@link TransactionTemplate}-managed
 * transaction that actually commits (a {@code @Transactional} test method would
 * roll back and suppress the AFTER_COMMIT phase), then poll for the async side
 * effect. {@link EcoATMDirectUserRepository} is replaced with a {@code @Primary}
 * Mockito mock (not {@code @MockBean}, which would poison the shared context
 * cache) so the test needn't build the buyer/account/direct-user join chain just
 * to resolve one recipient.
 */
class ManualQualificationEmailMigrationIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockDirectUserRepositoryConfig {
        @Bean
        @Primary
        public EcoATMDirectUserRepository mockDirectUserRepository() {
            return Mockito.mock(EcoATMDirectUserRepository.class);
        }
    }

    private static final String RECIPIENT = "manualqualemailit-buyer@example.com";
    private static final String SOURCE_MODULE = "QUALIFICATION";

    @Autowired private JdbcTemplate jdbc;
    @Autowired private EcoATMDirectUserRepository directUserRepository;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private TransactionTemplate transactionTemplate;

    /** Every source_id (qualifiedBuyerCodeId) this test published, for cleanup. */
    private final List<Long> publishedSourceIds = new ArrayList<>();

    @BeforeEach
    void resetMock() {
        Mockito.reset(directUserRepository);
    }

    @AfterEach
    void cleanup() {
        // The listener writes with its own REQUIRES_NEW transaction (a different
        // thread/connection), so nothing here is rolled back by Spring's
        // test-transaction machinery — clean up explicitly.
        for (Long sourceId : publishedSourceIds) {
            jdbc.update(
                    "DELETE FROM email.log WHERE source_module = ? AND source_id = ?",
                    SOURCE_MODULE, sourceId);
        }
    }

    @Test
    @DisplayName("Started + included override writes an email.log row (source_module=QUALIFICATION, status=SENT)")
    void startedIncludedOverride_writesEmailLog() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(any()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT, "IT Buyer"}));

        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        Long qbcId = uniqueSourceId();

        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new QualificationOverriddenEvent(
                        qbcId, buyerCodeId, 900L, true,
                        SchedulingAuctionStatus.Started, 1L, Instant.now())));

        // @Async dispatches onQualificationOverridden to a different thread —
        // poll until the email.log write lands (or the deadline trips the test).
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(100)).untilAsserted(() -> {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM email.log WHERE source_module = ? AND source_id = ?",
                    Long.class, SOURCE_MODULE, qbcId);
            assertThat(count).isEqualTo(1L);
        });

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT template_key, to_address, status FROM email.log "
                        + "WHERE source_module = ? AND source_id = ?", SOURCE_MODULE, qbcId);
        assertThat(row.get("template_key")).isEqualTo("ManualQualification");
        assertThat((String) row.get("to_address")).contains(RECIPIENT);
        // LoggingEmailSender (default when spring.mail.host is unset) never
        // throws, so the real send path resolves to SENT here.
        assertThat(row.get("status")).isEqualTo("SENT");
    }

    @Test
    @DisplayName("A not-Started round OR an included=false override writes no email.log row")
    void notStartedOrNotIncluded_writesNoEmailLog() {
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);

        // Negative 1: round is Scheduled (not Started) even though included=true.
        Long scheduledQbcId = uniqueSourceId();
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new QualificationOverriddenEvent(
                        scheduledQbcId, buyerCodeId, 901L, true,
                        SchedulingAuctionStatus.Scheduled, 1L, Instant.now())));

        // Negative 2: round is Started but the override un-includes (included=false).
        Long unincludedQbcId = uniqueSourceId();
        transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(
                new QualificationOverriddenEvent(
                        unincludedQbcId, buyerCodeId, 902L, false,
                        SchedulingAuctionStatus.Started, 1L, Instant.now())));

        // Give the async pool a beat, then assert neither publish wrote a row —
        // the Started+included gate must reject both.
        await().during(Duration.ofMillis(700)).atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM email.log WHERE source_module = ? AND source_id IN (?, ?)",
                    Long.class, SOURCE_MODULE, scheduledQbcId, unincludedQbcId);
            assertThat(count).isEqualTo(0L);
        });
    }

    /** A unique, positive BIGINT source_id per publish so the row this test
     *  writes never collides with a sibling test or a stale dev-DB row, and
     *  cleanup can target it precisely. */
    private Long uniqueSourceId() {
        long id = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        publishedSourceIds.add(id);
        return id;
    }
}
