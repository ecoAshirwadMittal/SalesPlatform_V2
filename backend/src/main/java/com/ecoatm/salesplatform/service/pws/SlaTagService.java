package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.model.pws.PwsConstants;
import com.ecoatm.salesplatform.repository.pws.CompanyHolidayRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.PwsConstantsRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * SLA-tag service — the single implementation both the manual admin action
 * ({@code PWSAdminController.setSLATags} / {@code removeSLATags}) and the
 * scheduled cron call. Modern port of the legacy Mendix {@code SE_SetSLATag} +
 * {@code SUB_SetSLATag} + {@code SUB_CalculateSLADate} +
 * {@code SUB_RemoveSLATagsForAllOffers}.
 *
 * <p><b>Business-day parity.</b> The overdue cutoff is computed by walking back
 * {@code pws_constants.sla_days} <em>business</em> days from today (via the
 * injected {@link Clock}), skipping Saturdays, Sundays, and any
 * {@code pws.company_holiday} date — exactly matching legacy
 * {@code SUB_CalculateSLADate}. This replaces the previous hardcoded
 * {@code NOW() - INTERVAL '2 days'} calendar interval, which ignored both the
 * configurable {@code sla_days} knob and weekends/holidays.
 *
 * <p>Offers in {@code Sales_Review} or {@code Buyer_Acceptance} whose
 * {@code updated_date} (day-truncated) is on or before that cutoff are flagged
 * {@code offer_beyond_sla = true}.
 */
@Service
public class SlaTagService {

    /** ShedLock lock name — keeps the scheduled tick single-leader across instances. */
    static final String JOB_NAME = "pwsSlaTag";

    /** Fallback when {@code pws_constants} has no row or a non-positive {@code sla_days}. */
    static final int DEFAULT_SLA_DAYS = 2;

    /**
     * SLA-tracked offer statuses. Mixed-case + upper-case variants both kept —
     * matching the migrated data and the pre-refactor {@code PWSAdminController}
     * SQL (legacy canonical values are {@code Sales_Review} / {@code Buyer_Acceptance}).
     */
    static final List<String> SLA_STATUSES =
            List.of("Sales_Review", "SALES_REVIEW", "Buyer_Acceptance", "BUYER_ACCEPTANCE");

    /**
     * Extra calendar days added below the theoretical business-day span when
     * loading the holiday window, so the back-walk can never step past the
     * fetched holiday set even across a dense holiday cluster.
     */
    private static final long HOLIDAY_WINDOW_BUFFER_DAYS = 30L;

    private static final Logger log = LoggerFactory.getLogger(SlaTagService.class);

    private final OfferRepository offerRepository;
    private final CompanyHolidayRepository companyHolidayRepository;
    private final PwsConstantsRepository pwsConstantsRepository;
    private final Clock clock;
    private final boolean enabled;

    public SlaTagService(OfferRepository offerRepository,
                         CompanyHolidayRepository companyHolidayRepository,
                         PwsConstantsRepository pwsConstantsRepository,
                         Clock clock,
                         @Value("${pws.sla-tag.enabled:false}") boolean enabled) {
        this.offerRepository = offerRepository;
        this.companyHolidayRepository = companyHolidayRepository;
        this.pwsConstantsRepository = pwsConstantsRepository;
        this.clock = clock;
        this.enabled = enabled;
    }

    /**
     * Scheduled entry point. Short-circuits when {@code pws.sla-tag.enabled} is
     * false (the default) so the cron is inert until deliberately switched on in
     * an environment. {@code @SchedulerLock} makes it single-leader across
     * instances, reusing the ShedLock setup from {@code SchedulingConfig}. The
     * manual admin endpoints call {@link #tagOverdueOffers()} directly and are
     * unaffected by this flag.
     */
    @Scheduled(fixedDelayString = "${pws.sla-tag.fixed-delay-ms:900000}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = "PT5M", lockAtLeastFor = "PT30S")
    public void scheduledTagOverdueOffers() {
        if (!enabled) {
            log.debug("[{}] disabled — skipping scheduled SLA-tag pass", JOB_NAME);
            return;
        }
        int tagged = tagOverdueOffers();
        log.info("[{}] scheduled SLA-tag pass flagged {} overdue offer(s)", JOB_NAME, tagged);
    }

    /**
     * Flags every SLA-tracked offer past its SLA cutoff. Returns the number of
     * newly-tagged offers.
     */
    @Transactional
    public int tagOverdueOffers() {
        LocalDate cutoff = calculateSlaCutoff();
        int tagged = offerRepository.tagOverdueOffers(SLA_STATUSES, cutoff);
        log.info("[{}] SLA cutoff {} — tagged {} overdue offer(s)", JOB_NAME, cutoff, tagged);
        return tagged;
    }

    /**
     * Clears {@code offer_beyond_sla} on every SLA-tracked offer. Returns the
     * number of rows cleared. Modern port of {@code SUB_RemoveSLATagsForAllOffers}.
     */
    @Transactional
    public int removeAllSlaTags() {
        int cleared = offerRepository.clearAllSlaTags(SLA_STATUSES);
        log.info("[{}] cleared SLA tag on {} offer(s)", JOB_NAME, cleared);
        return cleared;
    }

    /**
     * The date {@code sla_days} business days before today, skipping weekends and
     * company holidays — the modern port of {@code SUB_CalculateSLADate}. Walks
     * back one calendar day at a time, counting a day only when it is neither a
     * weekend nor a holiday, until {@code sla_days} business days have elapsed.
     */
    LocalDate calculateSlaCutoff() {
        int slaDays = resolveSlaDays();
        LocalDate today = LocalDate.now(clock);
        Set<LocalDate> holidays = loadHolidayWindow(today, slaDays);

        LocalDate cursor = today;
        int businessDaysCounted = 0;
        while (businessDaysCounted < slaDays) {
            cursor = cursor.minusDays(1);
            if (isWeekend(cursor) || holidays.contains(cursor)) {
                continue;
            }
            businessDaysCounted++;
        }
        return cursor;
    }

    private Set<LocalDate> loadHolidayWindow(LocalDate today, int slaDays) {
        // Generous lower bound: the back-walk covers at most slaDays business
        // days plus interleaved weekends/holidays, so slaDays*3 + buffer of
        // calendar days is a safe over-estimate for any realistic sla_days.
        LocalDate windowStart = today.minusDays((long) slaDays * 3L + HOLIDAY_WINDOW_BUFFER_DAYS);
        return companyHolidayRepository.findHolidayDatesBetween(windowStart, today);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private int resolveSlaDays() {
        return pwsConstantsRepository.findTopByOrderByIdAsc()
                .map(PwsConstants::getSlaDays)
                .filter(days -> days != null && days > 0)
                .orElse(DEFAULT_SLA_DAYS);
    }
}
