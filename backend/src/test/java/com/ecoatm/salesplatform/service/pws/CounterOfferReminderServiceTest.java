package com.ecoatm.salesplatform.service.pws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.service.email.EmailService;
import com.ecoatm.salesplatform.service.email.EmailService.SendOverrides;
import com.ecoatm.salesplatform.service.email.EmailService.SourceRef;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Branch-matrix unit test for {@link CounterOfferReminderService} — the legacy
 * {@code ACT_SendCounterOfferReminderEmails} decision tree, Clock-injected for
 * deterministic offer aging. All collaborators are mocked; the load-bearing
 * assertions are the exact template key / {@link SendOverrides} (buyer-only) /
 * {@link SourceRef} passed to {@link EmailService#sendTemplated}, the one-shot
 * flag flip, and per-row exception isolation.
 */
@ExtendWith(MockitoExtension.class)
class CounterOfferReminderServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2026-07-13T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    private static final LocalDateTime NOW = LocalDateTime.now(FIXED_CLOCK);

    private static final String COUNTER_URL = "https://buy.example/p/counter-offer";
    private static final Long BUYER_CODE_ID = 500L;
    private static final String RECIPIENT_EMAIL = "buyer@example.com";
    private static final String RECIPIENT_NAME = "Buyer Name";

    /** Both toggles on, 24h / 48h — the seeded pws_constants defaults. */
    private static final PwsCounterReminderSettings BOTH_ON = new PwsCounterReminderSettings(true, true, 24, 48);

    @Mock private OfferRepository offerRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private PwsConstantsReader pwsConstantsReader;
    @Mock private EmailService emailService;

    private CounterOfferReminderService service;

    @BeforeEach
    void setUp() {
        service = newService(true);
    }

    private CounterOfferReminderService newService(boolean enabled) {
        return new CounterOfferReminderService(
                offerRepository, directUserRepository, pwsConstantsReader, emailService,
                FIXED_CLOCK, enabled, COUNTER_URL);
    }

    // ── fixtures ─────────────────────────────────────────────────────────────

    private Offer offer(long id, long hoursSinceReview, boolean firstSent, boolean secondSent) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setStatus(CounterOfferReminderService.BUYER_ACCEPTANCE_STATUS);
        offer.setOfferType("counter");
        offer.setBuyerCodeId(BUYER_CODE_ID);
        offer.setOfferNumber("OFF-" + id);
        offer.setSalesReviewCompletedOn(NOW.minusHours(hoursSinceReview));
        offer.setFirstReminderSent(firstSent);
        offer.setSecondReminderSent(secondSent);
        return offer;
    }

    private void stubSettings(PwsCounterReminderSettings settings) {
        when(pwsConstantsReader.loadCounterReminderSettings()).thenReturn(settings);
    }

    private void stubCandidates(Offer... offers) {
        when(offerRepository.findCounterReminderCandidates(
                CounterOfferReminderService.BUYER_ACCEPTANCE_STATUS))
                .thenReturn(List.of(offers));
    }

    private void stubActiveRecipient() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(anyLong()))
                .thenReturn(List.<Object[]>of(new Object[] {RECIPIENT_EMAIL, RECIPIENT_NAME}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(anyLong()))
                .thenReturn(List.of("Acme Corp"));
    }

    // ── threshold branches ───────────────────────────────────────────────────

    @Nested
    class ThresholdBranches {

        @Test
        @DisplayName("hours below the first threshold sends nothing")
        void belowFirstThreshold_sendsNothing() {
            stubSettings(BOTH_ON);
            stubCandidates(offer(1L, 10, false, false));

            int sent = service.runOnce();

            assertThat(sent).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("hours in [first, second) with first unsent sends the FIRST reminder + flags it")
        void betweenFirstAndSecond_firstUnsent_sendsFirst() {
            stubSettings(BOTH_ON);
            stubActiveRecipient();
            Offer offer = offer(7L, 30, false, false);
            stubCandidates(offer);

            int sent = service.runOnce();

            assertThat(sent).isEqualTo(1);

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
            ArgumentCaptor<SendOverrides> overridesCaptor = ArgumentCaptor.forClass(SendOverrides.class);
            ArgumentCaptor<SourceRef> sourceCaptor = ArgumentCaptor.forClass(SourceRef.class);
            verify(emailService).sendTemplated(
                    keyCaptor.capture(), varsCaptor.capture(), overridesCaptor.capture(), sourceCaptor.capture());

            assertThat(keyCaptor.getValue()).isEqualTo("PwsCounterOfferFirstReminder");
            // Buyer-only recipients — TO carries the resolved buyer email, CC/BCC null.
            assertThat(overridesCaptor.getValue()).isEqualTo(new SendOverrides(List.of(RECIPIENT_EMAIL), null, null));
            assertThat(sourceCaptor.getValue()).isEqualTo(new SourceRef("PWS_COUNTER_REMINDER", 7L));
            assertThat(varsCaptor.getValue())
                    .containsEntry("buyerName", RECIPIENT_NAME)
                    .containsEntry("companyName", "Acme Corp")
                    .containsEntry("offerNumber", "OFF-7")
                    .containsEntry("counterOfferUrl", COUNTER_URL);

            assertThat(offer.isFirstReminderSent()).isTrue();
            assertThat(offer.isSecondReminderSent()).isFalse();
            verify(offerRepository).save(offer);
        }

        @Test
        @DisplayName("hours >= second with second unsent sends the SECOND reminder + flags it (takes precedence)")
        void atOrAboveSecond_secondUnsent_sendsSecond() {
            stubSettings(BOTH_ON);
            stubActiveRecipient();
            Offer offer = offer(9L, 50, false, false);
            stubCandidates(offer);

            int sent = service.runOnce();

            assertThat(sent).isEqualTo(1);

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<SourceRef> sourceCaptor = ArgumentCaptor.forClass(SourceRef.class);
            verify(emailService).sendTemplated(
                    keyCaptor.capture(), any(), eq(new SendOverrides(List.of(RECIPIENT_EMAIL), null, null)),
                    sourceCaptor.capture());

            assertThat(keyCaptor.getValue()).isEqualTo("PwsCounterOfferSecondReminder");
            assertThat(sourceCaptor.getValue()).isEqualTo(new SourceRef("PWS_COUNTER_REMINDER", 9L));
            // Second takes precedence at/after the second threshold: first stays unsent.
            assertThat(offer.isSecondReminderSent()).isTrue();
            assertThat(offer.isFirstReminderSent()).isFalse();
            verify(offerRepository).save(offer);
        }

        @Test
        @DisplayName("null second threshold makes the first reminder open-ended (hours >= first)")
        void nullSecondThreshold_openEndedFirst() {
            stubSettings(new PwsCounterReminderSettings(true, true, 24, null));
            stubActiveRecipient();
            Offer offer = offer(11L, 50, false, false);
            stubCandidates(offer);

            int sent = service.runOnce();

            assertThat(sent).isEqualTo(1);
            verify(emailService).sendTemplated(
                    eq("PwsCounterOfferFirstReminder"), any(), any(), eq(new SourceRef("PWS_COUNTER_REMINDER", 11L)));
            assertThat(offer.isFirstReminderSent()).isTrue();
        }
    }

    // ── toggles ──────────────────────────────────────────────────────────────

    @Nested
    class Toggles {

        @Test
        @DisplayName("send_first_reminder=false suppresses the first reminder")
        void sendFirstReminderFalse_suppressesFirst() {
            stubSettings(new PwsCounterReminderSettings(false, true, 24, 48));
            stubCandidates(offer(1L, 30, false, false));

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("send_second_reminder=false suppresses the second reminder (and first is out of window past second)")
        void sendSecondReminderFalse_suppressesSecond() {
            stubSettings(new PwsCounterReminderSettings(true, false, 24, 48));
            stubCandidates(offer(1L, 50, false, false));

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }
    }

    // ── one-shot guards ──────────────────────────────────────────────────────

    @Nested
    class OneShotGuards {

        @Test
        @DisplayName("second already sent: no re-send at/after the second threshold")
        void secondAlreadySent_noResend() {
            stubSettings(BOTH_ON);
            stubCandidates(offer(1L, 50, false, true));

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("first already sent: no re-send while still inside the first window")
        void firstAlreadySent_noResend() {
            stubSettings(BOTH_ON);
            stubCandidates(offer(1L, 30, true, false));

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }
    }

    // ── isolation / guards ───────────────────────────────────────────────────

    @Nested
    class IsolationAndGuards {

        @Test
        @DisplayName("a send exception on one offer does not stop the others")
        void perRowIsolation_oneThrows_othersProceed() {
            stubSettings(BOTH_ON);
            stubActiveRecipient();
            Offer bad = offer(1L, 50, false, false);
            Offer good = offer(2L, 50, false, false);
            stubCandidates(bad, good);

            when(emailService.sendTemplated(any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("template render blew up"))
                    .thenReturn(null);

            int sent = service.runOnce();

            // First offer threw (not counted, not flagged); second still processed.
            assertThat(sent).isEqualTo(1);
            verify(emailService, times(2)).sendTemplated(any(), any(), any(), any());
            assertThat(bad.isSecondReminderSent()).isFalse();
            assertThat(good.isSecondReminderSent()).isTrue();
            verify(offerRepository).save(good);
            verify(offerRepository, never()).save(bad);
        }

        @Test
        @DisplayName("no active recipients: skip without flagging so a later tick can deliver")
        void noRecipients_skipsWithoutFlagging() {
            stubSettings(BOTH_ON);
            when(directUserRepository.findActiveEmailsByBuyerCodeId(anyLong())).thenReturn(List.of());
            Offer offer = offer(1L, 30, false, false);
            stubCandidates(offer);

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
            assertThat(offer.isFirstReminderSent()).isFalse();
        }

        @Test
        @DisplayName("missing salesReviewCompletedOn: skip rather than NPE")
        void missingAnchor_skips() {
            stubSettings(BOTH_ON);
            Offer offer = offer(1L, 30, false, false);
            offer.setSalesReviewCompletedOn(null);
            stubCandidates(offer);

            assertThat(service.runOnce()).isZero();
            verify(emailService, never()).sendTemplated(any(), any(), any(), any());
            verify(offerRepository, never()).save(any());
        }
    }

    // ── scheduled gate ───────────────────────────────────────────────────────

    @Nested
    class ScheduledGate {

        @Test
        @DisplayName("pws.counter-reminder.enabled=false short-circuits the scheduled tick")
        void disabled_shortCircuits() {
            CounterOfferReminderService disabled = newService(false);

            disabled.sendCounterOfferReminders();

            verifyNoInteractions(offerRepository, pwsConstantsReader, emailService, directUserRepository);
        }
    }
}
