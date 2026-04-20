package com.ecoatm.salesplatform.model.auctions;

/**
 * Mirrors Mendix {@code ENUM_ReminderEmails}. Values match the
 * {@code chk_sa_reminders} CHECK constraint in V58.
 */
public enum ReminderEmails {
    NoneSent,
    OneHourSent,
    FourHourSent,
    AllSent
}
