package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Single-leader scheduled job that emails buyers a 1st / 2nd counter-offer
 * reminder when an offer sits in {@code Buyer_Acceptance} past the configured
 * hours. Modern port of legacy {@code ACT_SendCounterOfferReminderEmails} +
 * {@code SUB_SendCounterOfferReminderEmail} (which call
 * {@code SUB_SendFirstReminderEmail} / {@code SUB_SendSecondReminderEmail}).
 *
 * <p><b>User-locked decisions:</b> buyer-only recipients (no CC sales), an
 * hourly poll, and one-shot delivery guarded by the offer's
 * {@code first_reminder_sent} / {@code second_reminder_sent} flags (V103).
 * Thresholds and per-reminder toggles come from {@code pws.pws_constants}.
 *
 * <p><b>Scheduled shape</b> mirrors {@code RmaDeposcoSyncService}: the
 * {@code @Scheduled} entry point ({@link #sendCounterOfferReminders}) is
 * single-leader via {@code @SchedulerLock}, short-circuits when
 * {@code pws.counter-reminder.enabled} is false (the default), and delegates the
 * real work to {@link #runOnce()} — kept public + un-gated so an integration
 * test can drive one deterministic tick without toggling the flag or the clock.
 *
 * <p><b>Per-row isolation:</b> each offer is processed in its own try/catch so a
 * single bad offer (a disabled template, a transient send fault) never aborts
 * the tick for the offers behind it — mirrors
 * {@code EmailRetryWorker.retryFailedRows}. The flag is flipped + persisted only
 * after {@link EmailService#sendTemplated} returns without throwing; a thrown
 * send (e.g. a disabled template) leaves the flag false so the offer stays
 * eligible next tick. A transport failure does NOT throw — {@code sendTemplated}
 * records a FAILED {@code email.log} row and returns it, so the flag flips and
 * redelivery is owned by {@code EmailRetryWorker} (email.log is the source of
 * truth for delivery), never by re-running this whole job.
 */
@Service
public class CounterOfferReminderService {

    static final String JOB_NAME = "pwsCounterOfferReminder";

    /** {@code email.log.source_module} tag for every send this job triggers. */
    static final String SOURCE_MODULE = "PWS_COUNTER_REMINDER";

    /** Only offers awaiting the buyer's counter response are reminded. */
    static final String BUYER_ACCEPTANCE_STATUS = "Buyer_Acceptance";

    /** {@code email.template.template_key} seeds from V105. */
    static final String FIRST_TEMPLATE_KEY = "PwsCounterOfferFirstReminder";
    static final String SECOND_TEMPLATE_KEY = "PwsCounterOfferSecondReminder";

    private static final Logger log = LoggerFactory.getLogger(CounterOfferReminderService.class);

    private final OfferRepository offerRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final PwsConstantsReader pwsConstantsReader;
    private final EmailService emailService;
    private final Clock clock;
    private final boolean enabled;
    private final String counterOfferUrl;

    public CounterOfferReminderService(
            OfferRepository offerRepository,
            EcoATMDirectUserRepository directUserRepository,
            PwsConstantsReader pwsConstantsReader,
            EmailService emailService,
            Clock clock,
            @Value("${pws.counter-reminder.enabled:false}") boolean enabled,
            @Value("${pws.email.counter-offer-url}") String counterOfferUrl) {
        this.offerRepository = offerRepository;
        this.directUserRepository = directUserRepository;
        this.pwsConstantsReader = pwsConstantsReader;
        this.emailService = emailService;
        this.clock = clock;
        this.enabled = enabled;
        this.counterOfferUrl = counterOfferUrl;
    }

    /**
     * Scheduled entry point (hourly by default). Short-circuits when
     * {@code pws.counter-reminder.enabled} is false so the job is inert until ops
     * reviews the best-effort email copy and flips it on. {@code @SchedulerLock}
     * makes it single-leader across instances, reusing the ShedLock setup from
     * {@code SchedulingConfig}.
     */
    @Scheduled(fixedDelayString = "${pws.counter-reminder.fixed-delay-ms:3600000}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = "PT55M", lockAtLeastFor = "PT1M")
    public void sendCounterOfferReminders() {
        if (!enabled) {
            log.info("[{}] disabled — skipping counter-offer reminder sweep", JOB_NAME);
            return;
        }
        runOnce();
    }

    /**
     * Runs exactly one reminder sweep and returns the number of reminders sent.
     * Public + un-gated so an IT can drive a deterministic tick; the scheduled
     * method is the only production caller and applies the enable gate first.
     */
    public int runOnce() {
        PwsCounterReminderSettings settings = pwsConstantsReader.loadCounterReminderSettings();
        List<Offer> candidates = offerRepository.findCounterReminderCandidates(BUYER_ACCEPTANCE_STATUS);
        LocalDateTime now = LocalDateTime.now(clock);

        int sent = 0;
        for (Offer offer : candidates) {
            try {
                if (processOffer(offer, settings, now)) {
                    sent++;
                }
            } catch (Exception ex) {
                // Isolate the failing offer so the rest of the sweep still runs.
                // offerId is a business identifier — no PII in the log.
                log.error("[{}] counter-offer reminder failed for offerId={}: {}",
                        JOB_NAME, offer.getId(), ex.getMessage(), ex);
            }
        }
        log.info("[{}] evaluated {} candidate offer(s), sent {} reminder(s)",
                JOB_NAME, candidates.size(), sent);
        return sent;
    }

    /**
     * Applies the legacy decision tree to one offer and, when a reminder is due,
     * sends it and flips the matching one-shot flag. Returns whether a reminder
     * was sent.
     */
    private boolean processOffer(Offer offer, PwsCounterReminderSettings settings, LocalDateTime now) {
        LocalDateTime anchor = offer.getSalesReviewCompletedOn();
        if (anchor == null) {
            // Cannot age an offer with no review-completed timestamp — skip
            // rather than NPE. An offer in Buyer_Acceptance normally has it.
            log.warn("[{}] offerId={} in {} has no salesReviewCompletedOn — skipping",
                    JOB_NAME, offer.getId(), BUYER_ACCEPTANCE_STATUS);
            return false;
        }
        long hoursSinceReview = Duration.between(anchor, now).toHours();

        ReminderKind kind = decide(offer, settings, hoursSinceReview);
        if (kind == null) {
            return false;
        }

        List<String> recipients = resolveRecipientEmails(offer.getBuyerCodeId());
        if (recipients.isEmpty()) {
            // Don't flip the flag: leave the offer eligible so a later tick can
            // deliver once the buyer code has an active user. Count only, no PII.
            log.warn("[{}] {} skipped: no active recipients for offerId={} buyerCodeId={}",
                    JOB_NAME, kind.templateKey(), offer.getId(), offer.getBuyerCodeId());
            return false;
        }

        emailService.sendTemplated(
                kind.templateKey(),
                variablesFor(offer),
                new EmailService.SendOverrides(recipients, null, null),
                new EmailService.SourceRef(SOURCE_MODULE, offer.getId()));

        kind.markSent(offer);
        offerRepository.save(offer);

        log.info("[{}] {} sent for offerId={} buyerCodeId={} recipients={}",
                JOB_NAME, kind.templateKey(), offer.getId(), offer.getBuyerCodeId(), recipients.size());
        return true;
    }

    /**
     * The legacy {@code ACT_SendCounterOfferReminderEmails} /
     * {@code SUB_SendCounterOfferReminderEmail} decision tree, distilled:
     *
     * <ul>
     *   <li>SECOND — only when {@code send_second_reminder} is on — fires when
     *       {@code hours >= hours_second} and the second reminder is unsent. It
     *       takes precedence: at/after the second threshold no first reminder is
     *       sent.</li>
     *   <li>FIRST — only when {@code send_first_reminder} is on and the first
     *       reminder is unsent — fires when {@code hours >= hours_first}, further
     *       bounded by {@code hours < hours_second} whenever a second threshold
     *       is configured (the legacy {@code HoursSecond != empty} branch).</li>
     * </ul>
     *
     * Returns {@code null} when no reminder is due.
     */
    private ReminderKind decide(Offer offer, PwsCounterReminderSettings settings, long hoursSinceReview) {
        Integer hoursFirst = settings.hoursFirstCounterReminder();
        Integer hoursSecond = settings.hoursSecondCounterReminder();

        if (settings.sendSecondReminder()
                && hoursSecond != null
                && hoursSinceReview >= hoursSecond
                && !offer.isSecondReminderSent()) {
            return ReminderKind.SECOND;
        }

        if (settings.sendFirstReminder() && hoursFirst != null && !offer.isFirstReminderSent()) {
            boolean withinFirstWindow = hoursSecond != null
                    ? (hoursSinceReview >= hoursFirst && hoursSinceReview < hoursSecond)
                    : (hoursSinceReview >= hoursFirst);
            if (withinFirstWindow) {
                return ReminderKind.FIRST;
            }
        }

        return null;
    }

    /**
     * Variables for the reminder templates. Kept faithful to the initial
     * counter-offer email ({@code SUB_SendPWSCounterOfferEmail}): the buyer's
     * name, their company, the offer reference, and the counter-offer CTA URL.
     * Bodies are best-effort (the reminder microflow bodies are not in
     * migration_context), so this stays a small, stable set.
     */
    private Map<String, Object> variablesFor(Offer offer) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("buyerName", resolveBuyerName(offer.getBuyerCodeId()));
        vars.put("companyName", resolveCompanyName(offer.getBuyerCodeId()));
        vars.put("offerNumber", displayOfferRef(offer));
        vars.put("counterOfferUrl", counterOfferUrl);
        return vars;
    }

    private String displayOfferRef(Offer offer) {
        return offer.getOfferNumber() != null ? offer.getOfferNumber() : String.valueOf(offer.getId());
    }

    /**
     * Active buyer-user emails for the buyer code — the same resolver PWS
     * notifications and the RMA / manual-qualification listeners use, so the
     * recipient list stays consistent across surfaces. Emails are never logged
     * (only the count), matching the sibling senders.
     */
    private List<String> resolveRecipientEmails(Long buyerCodeId) {
        if (buyerCodeId == null) {
            return List.of();
        }
        return directUserRepository.findActiveEmailsByBuyerCodeId(buyerCodeId).stream()
                .map(row -> row.length > 0 ? (String) row[0] : null)
                .filter(email -> email != null && !email.isBlank())
                .toList();
    }

    /** First active buyer-user full name for the greeting; falls back to a
     *  generic salutation when none resolves (mirrors PWSEmailService). */
    private String resolveBuyerName(Long buyerCodeId) {
        if (buyerCodeId == null) {
            return "Valued Customer";
        }
        return directUserRepository.findActiveEmailsByBuyerCodeId(buyerCodeId).stream()
                .map(row -> row.length > 1 ? (String) row[1] : null)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse("Valued Customer");
    }

    private String resolveCompanyName(Long buyerCodeId) {
        if (buyerCodeId == null) {
            return "";
        }
        List<String> names = directUserRepository.findBuyerCompanyNameByBuyerCodeId(buyerCodeId);
        return names.isEmpty() ? "" : names.get(0);
    }

    /** Which reminder to send, its V105 template key, and the one-shot flag it
     *  flips on the offer. */
    private enum ReminderKind {
        FIRST(FIRST_TEMPLATE_KEY) {
            @Override
            void markSent(Offer offer) {
                offer.setFirstReminderSent(true);
            }
        },
        SECOND(SECOND_TEMPLATE_KEY) {
            @Override
            void markSent(Offer offer) {
                offer.setSecondReminderSent(true);
            }
        };

        private final String templateKey;

        ReminderKind(String templateKey) {
            this.templateKey = templateKey;
        }

        String templateKey() {
            return templateKey;
        }

        abstract void markSent(Offer offer);
    }
}
