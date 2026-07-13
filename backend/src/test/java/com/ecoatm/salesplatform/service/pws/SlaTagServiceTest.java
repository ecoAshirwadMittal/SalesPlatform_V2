package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.model.pws.PwsConstants;
import com.ecoatm.salesplatform.repository.pws.CompanyHolidayRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.PwsConstantsRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit coverage for the SLA-tag business-day cutoff math and the
 * scheduled-tick gating. The cutoff logic (legacy {@code SUB_CalculateSLADate})
 * is the load-bearing branch — a fixed {@link Clock} anchored on a known Monday
 * makes the weekend/holiday back-walk deterministic.
 *
 * <p>Anchor: 2026-07-13 is a Monday. Walking back 2 business days with no
 * holidays lands on Thursday 2026-07-09 (skip Sun 07-12 + Sat 07-11, count
 * Fri 07-10 and Thu 07-09).
 */
@ExtendWith(MockitoExtension.class)
class SlaTagServiceTest {

    private static final Clock MONDAY_2026_07_13 =
            Clock.fixed(Instant.parse("2026-07-13T10:00:00Z"), ZoneOffset.UTC);

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private CompanyHolidayRepository companyHolidayRepository;
    @Mock
    private PwsConstantsRepository pwsConstantsRepository;

    private SlaTagService newService(boolean enabled) {
        return new SlaTagService(offerRepository, companyHolidayRepository,
                pwsConstantsRepository, MONDAY_2026_07_13, enabled);
    }

    private void stubSlaDays(Integer slaDays) {
        PwsConstants constants = new PwsConstants();
        constants.setSlaDays(slaDays);
        when(pwsConstantsRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(constants));
    }

    private void stubNoConstantsRow() {
        when(pwsConstantsRepository.findTopByOrderByIdAsc()).thenReturn(Optional.empty());
    }

    private void stubHolidays(LocalDate... holidays) {
        when(companyHolidayRepository.findHolidayDatesBetween(any(), any()))
                .thenReturn(Set.of(holidays));
    }

    // ── cutoff math (SUB_CalculateSLADate) ──────────────────────────────

    @Test
    @DisplayName("cutoff walks back sla_days business days, skipping weekends")
    void cutoff_skips_weekends() {
        stubSlaDays(2);
        stubHolidays(); // none

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        // Mon 07-13 → skip Sun 07-12, Sat 07-11 → count Fri 07-10 (1), Thu 07-09 (2).
        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 9));
    }

    @Test
    @DisplayName("cutoff also skips a company holiday, landing one day earlier")
    void cutoff_skips_company_holiday() {
        stubSlaDays(2);
        stubHolidays(LocalDate.of(2026, 7, 9)); // Thu is a holiday

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        // Fri 07-10 (1), Thu 07-09 holiday → skip, Wed 07-08 (2).
        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 8));
    }

    @Test
    @DisplayName("cutoff honors the configured pws_constants.sla_days value")
    void cutoff_honors_configured_sla_days() {
        stubSlaDays(3);
        stubHolidays();

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        // Fri 07-10 (1), Thu 07-09 (2), Wed 07-08 (3).
        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 8));
    }

    @Test
    @DisplayName("sla_days=1 lands on the previous business day (Friday)")
    void cutoff_one_business_day() {
        stubSlaDays(1);
        stubHolidays();

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 10));
    }

    @Test
    @DisplayName("falls back to the default when pws_constants has no row")
    void cutoff_defaults_when_no_constants_row() {
        stubNoConstantsRow();
        stubHolidays();

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        // default sla_days = 2 → Thu 07-09
        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 9));
    }

    @Test
    @DisplayName("falls back to the default when sla_days is non-positive")
    void cutoff_defaults_when_sla_days_non_positive() {
        stubSlaDays(0);
        stubHolidays();

        LocalDate cutoff = newService(false).calculateSlaCutoff();

        assertThat(cutoff).isEqualTo(LocalDate.of(2026, 7, 9));
    }

    // ── delegation ──────────────────────────────────────────────────────

    @Test
    @DisplayName("tagOverdueOffers delegates the SLA statuses + computed cutoff and returns the count")
    void tag_overdue_delegates_statuses_and_cutoff() {
        stubSlaDays(2);
        stubHolidays();
        when(offerRepository.tagOverdueOffers(anyCollection(), any(LocalDate.class))).thenReturn(4);

        int tagged = newService(false).tagOverdueOffers();

        assertThat(tagged).isEqualTo(4);
        ArgumentCaptor<LocalDate> cutoffCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(offerRepository).tagOverdueOffers(
                org.mockito.ArgumentMatchers.eq(SlaTagService.SLA_STATUSES), cutoffCaptor.capture());
        assertThat(cutoffCaptor.getValue()).isEqualTo(LocalDate.of(2026, 7, 9));
    }

    @Test
    @DisplayName("removeAllSlaTags delegates the SLA statuses and returns the cleared count")
    void remove_all_delegates_and_returns_count() {
        when(offerRepository.clearAllSlaTags(SlaTagService.SLA_STATUSES)).thenReturn(3);

        int cleared = newService(false).removeAllSlaTags();

        assertThat(cleared).isEqualTo(3);
        verify(offerRepository).clearAllSlaTags(SlaTagService.SLA_STATUSES);
    }

    // ── scheduled-tick gating ───────────────────────────────────────────

    @Test
    @DisplayName("scheduled tick short-circuits when pws.sla-tag.enabled=false — no repo interaction")
    void scheduled_disabled_short_circuits() {
        newService(false).scheduledTagOverdueOffers();

        verifyNoInteractions(offerRepository, companyHolidayRepository, pwsConstantsRepository);
    }

    @Test
    @DisplayName("scheduled tick runs the tag pass when enabled=true")
    void scheduled_enabled_runs_pass() {
        stubSlaDays(2);
        stubHolidays();
        lenient().when(offerRepository.tagOverdueOffers(anyCollection(), any(LocalDate.class))).thenReturn(1);

        newService(true).scheduledTagOverdueOffers();

        verify(offerRepository).tagOverdueOffers(anyCollection(), any(LocalDate.class));
        verify(offerRepository, never()).clearAllSlaTags(anyCollection());
    }

    @Test
    @DisplayName("scheduled method carries @Scheduled and @SchedulerLock (single-leader cron)")
    void scheduled_method_has_scheduling_annotations() throws NoSuchMethodException {
        Method tick = SlaTagService.class.getDeclaredMethod("scheduledTagOverdueOffers");
        assertThat(tick.isAnnotationPresent(Scheduled.class)).isTrue();

        SchedulerLock lock = tick.getAnnotation(SchedulerLock.class);
        assertThat(lock).isNotNull();
        assertThat(lock.name()).isEqualTo(SlaTagService.JOB_NAME);
    }
}
