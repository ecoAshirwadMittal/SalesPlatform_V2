import { describe, expect, it } from "vitest";
import { formatLegacyDateTime, BUSINESS_TIME_ZONE } from "./legacyDateTime";

// All assertions are timezone-deterministic: the formatter fixes the zone to
// America/New_York internally, so the CI machine's own TZ never matters.
describe("formatLegacyDateTime", () => {
  it("renders the exact legacy convention for the reserve-bids row-73 instant (winter → EST)", () => {
    // 2025-12-09T19:17Z == 02:17 PM Eastern (EST). Matches the legacy render
    // `12/09/25 at 02:17 PM EST` pixel-for-character.
    expect(formatLegacyDateTime("2025-12-09T19:17:00Z")).toBe("12/09/25 at 02:17 PM EST");
  });

  it("switches the zone label to EDT across daylight saving", () => {
    expect(formatLegacyDateTime("2026-07-04T18:05:00Z")).toBe("07/04/26 at 02:05 PM EDT");
  });

  it("zero-pads month, day, and hour", () => {
    // 2026-03-05T14:03Z == 09:03 AM EST — single-digit month(3)/day(5)/hour(9)
    // all zero-padded.
    expect(formatLegacyDateTime("2026-03-05T14:03:00Z")).toBe("03/05/26 at 09:03 AM EST");
  });

  it("renders 12:00 AM at midnight Eastern (not 00:00)", () => {
    // 2026-01-07T05:00Z == 12:00 AM EST.
    expect(formatLegacyDateTime("2026-01-07T05:00:00Z")).toBe("01/07/26 at 12:00 AM EST");
  });

  it("renders 12:00 PM at noon Eastern (not 00:00)", () => {
    // 2026-06-01T16:00Z == 12:00 PM EDT.
    expect(formatLegacyDateTime("2026-06-01T16:00:00Z")).toBe("06/01/26 at 12:00 PM EDT");
  });

  it("accepts a Date object", () => {
    expect(formatLegacyDateTime(new Date("2025-12-09T19:17:00Z"))).toBe("12/09/25 at 02:17 PM EST");
  });

  it("accepts epoch milliseconds", () => {
    expect(formatLegacyDateTime(Date.parse("2025-12-09T19:17:00Z"))).toBe("12/09/25 at 02:17 PM EST");
  });

  it("returns an empty string for null / undefined / empty / unparseable input", () => {
    expect(formatLegacyDateTime(null)).toBe("");
    expect(formatLegacyDateTime(undefined)).toBe("");
    expect(formatLegacyDateTime("")).toBe("");
    expect(formatLegacyDateTime("not-a-date")).toBe("");
  });

  it("honours an explicit timezone override", () => {
    // Same instant, rendered in Pacific → PST label + shifted clock.
    expect(formatLegacyDateTime("2025-12-09T19:17:00Z", "America/Los_Angeles"))
      .toBe("12/09/25 at 11:17 AM PST");
  });

  it("exposes the Eastern business zone as the default", () => {
    expect(BUSINESS_TIME_ZONE).toBe("America/New_York");
    expect(formatLegacyDateTime("2025-12-09T19:17:00Z", BUSINESS_TIME_ZONE))
      .toBe(formatLegacyDateTime("2025-12-09T19:17:00Z"));
  });
});
