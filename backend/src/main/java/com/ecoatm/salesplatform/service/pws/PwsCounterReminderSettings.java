package com.ecoatm.salesplatform.service.pws;

/**
 * Immutable snapshot of the counter-offer-reminder knobs from the
 * {@code pws.pws_constants} singleton (Mendix {@code EcoATM_PWS.PWSConstants}),
 * read fresh each tick by {@link PwsConstantsReader}.
 *
 * <p>The two {@code hours*} thresholds are {@link Integer} (nullable) on purpose:
 * legacy {@code ACT_SendCounterOfferReminderEmails} branches on
 * {@code HoursSecondCounterReminderEmail != empty}, so a null second threshold
 * is a real, meaningful state — it switches the first-reminder gate from the
 * windowed form ({@code hours >= first && hours < second}) to the open-ended
 * form ({@code hours >= first}).
 *
 * @param sendFirstReminder          {@code pws_constants.send_first_reminder}
 * @param sendSecondReminder         {@code pws_constants.send_second_reminder}
 * @param hoursFirstCounterReminder  {@code pws_constants.hours_first_counter_reminder} (nullable)
 * @param hoursSecondCounterReminder {@code pws_constants.hours_second_counter_reminder} (nullable)
 */
public record PwsCounterReminderSettings(
        boolean sendFirstReminder,
        boolean sendSecondReminder,
        Integer hoursFirstCounterReminder,
        Integer hoursSecondCounterReminder) {}
