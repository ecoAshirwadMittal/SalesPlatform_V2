/**
 * Legacy Mendix date/time display convention (parity ADR 2026-07-12).
 *
 * The legacy AuctionUI app rendered every user-facing timestamp with the
 * Mendix `formatDateTime` pattern `MM/dd/yy 'at' hh:mm a z` in the
 * application's business timezone — US Eastern, the default for every
 * Mendix session in this app — e.g.
 *
 *   12/09/25 at 02:17 PM EST
 *
 * (zero-padded 2-digit month/day, 2-digit year, the literal " at ", a
 * 12-hour zero-padded clock, an AM/PM marker, and the Eastern zone
 * abbreviation which flips EST↔EDT across DST).
 *
 * This is the **systemic** display convention for the parity rebuild: every
 * grid / detail surface should format timestamps through this helper as it
 * is driven to parity, rather than hand-rolling `toLocaleString()` per page
 * (which yields `12/3/2025, 11:16:37 AM` — no zero-pad, 4-digit year,
 * seconds, no zone). See `docs/architecture/decisions.md`.
 *
 * The zone is fixed to `America/New_York` by default so the rendered time
 * and zone label match legacy for every viewer regardless of their own
 * locale — legacy showed Eastern to all users. Callers may override the
 * zone for a specific surface if a future finding requires it.
 */

/** The auction business timezone — legacy Mendix session default. */
export const BUSINESS_TIME_ZONE = "America/New_York";

/**
 * Format an instant in the legacy `MM/DD/YY at hh:mm A z` convention.
 *
 * @param value ISO string, epoch millis, or `Date`. `null` / `undefined` /
 *   empty-string / unparseable input returns `""` (nothing to render —
 *   callers decide their own placeholder, e.g. "—").
 * @param timeZone IANA zone; defaults to the Eastern business zone.
 * @returns e.g. `"12/09/25 at 02:17 PM EST"`, or `""` when there is no
 *   valid instant to show.
 */
export function formatLegacyDateTime(
  value: string | number | Date | null | undefined,
  timeZone: string = BUSINESS_TIME_ZONE,
): string {
  if (value == null || value === "") return "";
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) return "";

  const parts = new Intl.DateTimeFormat("en-US", {
    timeZone,
    year: "2-digit",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: true,
    timeZoneName: "short",
  }).formatToParts(date);

  const part = (type: Intl.DateTimeFormatPartTypes): string =>
    parts.find((p) => p.type === type)?.value ?? "";

  // Assemble in the exact legacy order. The `Intl` en-US literal between the
  // date and time is ", "; the legacy convention is " at ", so we ignore the
  // built-in literals and interpolate the fields directly.
  return (
    `${part("month")}/${part("day")}/${part("year")}` +
    ` at ${part("hour")}:${part("minute")} ${part("dayPeriod")} ${part("timeZoneName")}`
  );
}
