package com.ecoatm.salesplatform.service.pws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Real-Postgres end-to-end proof that the counter-offer reminder job actually
 * fires against the shipped schema + V105 seed: a {@code Buyer_Acceptance} offer
 * aged 50h past its sales review drives {@link CounterOfferReminderService#runOnce()}
 * to send the SECOND reminder through the real {@code EmailService.sendTemplated}
 * → a real {@code email.log} row → the offer's {@code second_reminder_sent} flag
 * flipped.
 *
 * <p>{@code @Transactional} so the sweep's writes (including any effect on the
 * pre-existing {@code Buyer_Acceptance} offers in the shared dev DB) roll back
 * after each test — the job is fully synchronous (no {@code @Async} thread), so
 * {@code EmailService.sendTemplated}'s {@code @Transactional} joins the test
 * transaction and its {@code email.log} INSERT is visible to the in-test
 * assertions yet rolled back at teardown. Assertions target the seeded offer's
 * id ({@code source_id}) so a full-table sweep can't mask the result.
 *
 * <p>{@link com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository} is
 * replaced with a {@code @Primary} Mockito mock (not {@code @MockBean}, which
 * would poison the shared context cache) so the test needn't build the
 * buyer/account/direct-user join chain just to resolve one recipient — the same
 * pattern {@code ManualQualificationEmailMigrationIT} uses.
 */
@Transactional
class CounterOfferReminderEndToEndIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockDirectUserRepositoryConfig {
        @Bean
        @Primary
        public com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository mockDirectUserRepository() {
            return Mockito.mock(com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository.class);
        }
    }

    private static final String RECIPIENT = "counterreminder-it-buyer@example.com";
    private static final String SOURCE_MODULE = "PWS_COUNTER_REMINDER";

    @Autowired private JdbcTemplate jdbc;
    @Autowired private com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository directUserRepository;
    @Autowired private CounterOfferReminderService service;
    @Autowired private Clock clock;

    // The job's send + flag write are JPA updates that join this @Transactional
    // test's tx but aren't flushed to the DB connection automatically; flush so
    // the raw-JDBC assertions below see the true end state (SENT / flag set)
    // before teardown rolls everything back.
    @PersistenceContext private EntityManager entityManager;

    @BeforeEach
    void stubRecipient() {
        Mockito.reset(directUserRepository);
        // Any buyer code resolves to a single active recipient — the sweep may
        // also touch pre-existing candidates, but @Transactional rolls it back.
        when(directUserRepository.findActiveEmailsByBuyerCodeId(any()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT, "IT Buyer"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(any()))
                .thenReturn(List.of("IT Buyer Co"));
    }

    @Test
    @DisplayName("50h-old Buyer_Acceptance offer → SECOND reminder sent, one email.log row, flag set")
    void agedOffer_sendsSecondReminder_writesLog_setsFlag() {
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        // Anchor uses the SAME Clock the service reads, so the age is an
        // unambiguous ~50h with no timestamp-vs-timezone drift.
        LocalDateTime anchor = LocalDateTime.now(clock).minusHours(50);
        Long offerId = insertOffer(buyerCodeId, anchor, false, false);

        int sent = service.runOnce();
        entityManager.flush();

        // At least this offer was reminded (the sweep may also touch dev-DB rows).
        assertThat(sent).isGreaterThanOrEqualTo(1);

        Long logCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM email.log WHERE source_module = ? AND source_id = ?",
                Long.class, SOURCE_MODULE, offerId);
        assertThat(logCount).isEqualTo(1L);

        Map<String, Object> logRow = jdbc.queryForMap(
                "SELECT template_key, to_address, status FROM email.log "
                        + "WHERE source_module = ? AND source_id = ?", SOURCE_MODULE, offerId);
        assertThat(logRow.get("template_key")).isEqualTo("PwsCounterOfferSecondReminder");
        assertThat((String) logRow.get("to_address")).contains(RECIPIENT);
        // LoggingEmailSender (default when spring.mail.host is unset) never
        // throws, so the real send path resolves to SENT.
        assertThat(logRow.get("status")).isEqualTo("SENT");

        Map<String, Object> offerRow = jdbc.queryForMap(
                "SELECT first_reminder_sent, second_reminder_sent FROM pws.offer WHERE id = ?", offerId);
        assertThat(offerRow.get("second_reminder_sent")).isEqualTo(Boolean.TRUE);
        assertThat(offerRow.get("first_reminder_sent")).isEqualTo(Boolean.FALSE);
    }

    @Test
    @DisplayName("one-shot: a 50h-old offer with second_reminder_sent already true writes no new row")
    void alreadySecondSent_writesNoRow() {
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        LocalDateTime anchor = LocalDateTime.now(clock).minusHours(50);
        Long offerId = insertOffer(buyerCodeId, anchor, false, true);

        service.runOnce();

        Long logCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM email.log WHERE source_module = ? AND source_id = ?",
                Long.class, SOURCE_MODULE, offerId);
        assertThat(logCount).isZero();
    }

    /** Seeds a Buyer_Acceptance offer with a past sales-review anchor. The
     *  {@code updated_date} trigger fires only on UPDATE, so an INSERT with a
     *  back-dated timestamp is safe. Returns the generated id. */
    private Long insertOffer(Long buyerCodeId, LocalDateTime salesReviewCompletedOn,
                             boolean firstSent, boolean secondSent) {
        return jdbc.queryForObject(
                """
                INSERT INTO pws.offer
                    (offer_type, status, buyer_code_id, offer_number,
                     sales_review_completed_on, first_reminder_sent, second_reminder_sent,
                     created_date, updated_date)
                VALUES (?, 'Buyer_Acceptance', ?, ?, ?, ?, ?, NOW(), NOW())
                RETURNING id
                """,
                Long.class,
                "counter",
                buyerCodeId,
                "IT-CR-" + System.nanoTime(),
                Timestamp.valueOf(salesReviewCompletedOn),
                firstSent,
                secondSent);
    }
}
