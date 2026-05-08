"use client";

/**
 * New Purchase Order — replaces the dev-scaffold version.
 *
 * Original (pre-2026-05-08) showed `(mdm.week.id)` placeholder text and
 * required users to type a raw numeric week-id — clearly never finished.
 * This iteration wires the existing /api/v1/admin/inventory/weeks loader
 * (used by the inventory page already) and shows real week-display
 * options ("2026 / Wk19"). xlsx upload still happens on the next step
 * (the Edit page's Upload Excel link); the QA-parity single-modal
 * Create+Upload flow is deferred to Sprint B per the PO walkthrough.
 *
 * Styling: heading mirrors the §3 spec (h2 / 42px / 500 / 54.6px).
 * Form fields adopt the §5 week-range selector shape (greyed border,
 * #F7F7F7 bg) so this page reads as part of the same surface family
 * as the inventory + schedule editors.
 */

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import Link from "next/link";
import { createPurchaseOrder } from "@/lib/api/purchaseOrderClient";
import { fetchWeeks, type WeekOption } from "@/lib/aggregatedInventory";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const BORDER = "#D0D0D0";
const BG = "#F7F7F7";
const MUTED = "#606671";

export default function NewPurchaseOrderPage() {
  const router = useRouter();
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekFromId, setWeekFromId] = useState<number | "">("");
  const [weekToId, setWeekToId] = useState<number | "">("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [loadingWeeks, setLoadingWeeks] = useState(true);

  useEffect(() => {
    let cancelled = false;
    fetchWeeks()
      .then((list) => {
        if (cancelled) return;
        setWeeks(list);
      })
      .catch(() => {
        if (cancelled) return;
        setError("Failed to load weeks");
      })
      .finally(() => {
        if (!cancelled) setLoadingWeeks(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (typeof weekFromId !== "number" || typeof weekToId !== "number") {
      setError("Both From Week and To Week are required.");
      return;
    }
    // Validate ordering — backend will too, but this keeps the user out
    // of an avoidable round-trip.
    const fromOrdinal = weeks.findIndex((w) => w.id === weekFromId);
    const toOrdinal = weeks.findIndex((w) => w.id === weekToId);
    if (fromOrdinal !== -1 && toOrdinal !== -1 && toOrdinal > fromOrdinal) {
      // weeks list is sorted newest-first, so a *later* ordinal = an
      // *earlier* week. To-week must be >= from-week chronologically,
      // i.e. ordinal <= from-ordinal in this list.
      setError("To Week must be the same as or later than From Week.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const po = await createPurchaseOrder({ weekFromId, weekToId });
      router.push(`/admin/auctions-data-center/purchase-orders/${po.id}/upload`);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div style={{ maxWidth: 600 }}>
      <Link
        href="/admin/auctions-data-center/purchase-orders"
        style={{ color: TEAL, fontSize: 14, textDecoration: "none" }}
      >
        ← Back to Purchase Order
      </Link>
      <h2 style={{
        margin: "0.5rem 0 1.5rem",
        fontSize: "42px",
        fontWeight: 500,
        lineHeight: "54.6px",
        color: TEXT,
      }}>
        New Purchase Order
      </h2>

      <p style={{ color: MUTED, fontSize: 14, marginBottom: "1.5rem" }}>
        Pick the week range this PO covers. After creating, the next step
        will let you upload an Excel file with the line items.
      </p>

      <form onSubmit={onSubmit}>
        <FormRow label="From Week">
          <WeekSelect
            value={weekFromId}
            weeks={weeks}
            disabled={loadingWeeks}
            onChange={setWeekFromId}
          />
        </FormRow>

        <FormRow label="To Week">
          <WeekSelect
            value={weekToId}
            weeks={weeks}
            disabled={loadingWeeks}
            onChange={setWeekToId}
          />
        </FormRow>

        {error && (
          <div role="alert" style={{
            color: "#a31b1b",
            background: "#fde7e7",
            border: "1px solid #f5c2c2",
            padding: "0.5rem 0.75rem",
            borderRadius: 4,
            marginBottom: "1rem",
            fontSize: 14,
          }}>
            {error}
          </div>
        )}

        <div style={{ display: "flex", gap: "0.75rem", marginTop: "1.5rem" }}>
          <button
            type="submit"
            disabled={submitting || loadingWeeks}
            style={{
              padding: "10px 28px",
              background: TEAL,
              color: "white",
              border: 0,
              borderRadius: 4,
              fontSize: 16,
              cursor: submitting ? "default" : "pointer",
              opacity: submitting || loadingWeeks ? 0.6 : 1,
            }}
          >
            {submitting ? "Creating…" : "Create Purchase Order"}
          </button>
          <Link
            href="/admin/auctions-data-center/purchase-orders"
            style={{
              padding: "10px 28px",
              background: BG,
              color: TEXT,
              border: `1px solid ${BORDER}`,
              borderRadius: 4,
              fontSize: 16,
              textDecoration: "none",
              display: "inline-flex",
              alignItems: "center",
            }}
          >
            Cancel
          </Link>
        </div>
      </form>
    </div>
  );
}

function FormRow({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div style={{ marginBottom: "1rem", display: "flex", alignItems: "center", gap: "1rem" }}>
      <label style={{
        width: 110,
        flexShrink: 0,
        color: TEXT,
        fontSize: 16,
        fontWeight: 500,
      }}>
        {label}
      </label>
      <div style={{ flex: 1 }}>{children}</div>
    </div>
  );
}

function WeekSelect({
  value,
  weeks,
  disabled,
  onChange,
}: {
  value: number | "";
  weeks: WeekOption[];
  disabled: boolean;
  onChange: (id: number | "") => void;
}) {
  return (
    <select
      value={value}
      onChange={(e) =>
        onChange(e.target.value === "" ? "" : Number(e.target.value))
      }
      disabled={disabled}
      required
      style={{
        width: "100%",
        height: 36,
        padding: "0 10px",
        background: BG,
        color: value === "" ? MUTED : TEXT,
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
        fontSize: 16,
        fontFamily: "inherit",
      }}
    >
      <option value="">{disabled ? "Loading weeks…" : "— Choose a week —"}</option>
      {weeks.map((w) => (
        <option key={w.id} value={w.id}>
          {w.weekDisplay}
        </option>
      ))}
    </select>
  );
}
