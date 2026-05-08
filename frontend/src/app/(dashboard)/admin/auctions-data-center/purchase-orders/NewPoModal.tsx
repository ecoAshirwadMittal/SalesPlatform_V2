"use client";

/**
 * Single-step Create+Upload PO modal (gap PO-4 / Sprint B).
 *
 * QA's primary entry point for "new PO" is one modal that takes the
 * week range AND the xlsx file at once. The pre-2026-05-08 flow forced
 * users through /new (week-range only) → land on /[id] → click Upload
 * Excel — three pages and two round-trips for a single mental action.
 *
 * Implementation: this is a client-side composition of the two
 * existing endpoints — createPurchaseOrder, then uploadPoDetails —
 * so we don't need a backend change. Failure mode if upload errors
 * after create succeeds: the PO exists with 0 line items, identical
 * to a user closing the tab mid-flow today. The list shows it and
 * they can retry by clicking Edit → Upload Excel.
 *
 * Styling matches the spec §11 (modal panel) and §5 (week selector
 * shape). Keyboard: Escape closes; Enter inside the form submits.
 */

import { useEffect, useRef, useState } from "react";
import {
  createPurchaseOrder,
  uploadPoDetails,
} from "@/lib/api/purchaseOrderClient";
import { fetchWeeks, type WeekOption } from "@/lib/aggregatedInventory";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";

interface Props {
  open: boolean;
  onClose: () => void;
  onCreated: (poId: number) => void;
}

export default function NewPoModal({ open, onClose, onCreated }: Props) {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekFromId, setWeekFromId] = useState<number | "">("");
  const [weekToId, setWeekToId] = useState<number | "">("");
  const [file, setFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!open) return;
    fetchWeeks()
      .then(setWeeks)
      .catch(() => setError("Failed to load weeks"));
  }, [open]);

  // Reset form when the modal closes so the next open doesn't show
  // stale fields. (Mendix's modal does the same — fresh state per open.)
  useEffect(() => {
    if (open) return;
    setWeekFromId("");
    setWeekToId("");
    setFile(null);
    setError(null);
    if (fileRef.current) fileRef.current.value = "";
  }, [open]);

  useEffect(() => {
    if (!open) return;
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape" && !submitting) onClose();
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, submitting, onClose]);

  if (!open) return null;

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (typeof weekFromId !== "number" || typeof weekToId !== "number") {
      setError("Both From Week and To Week are required.");
      return;
    }
    // Same chronological-ordering check the /new page does. Weeks list
    // is newest-first, so a *later* ordinal = an *earlier* week.
    const fromOrdinal = weeks.findIndex((w) => w.id === weekFromId);
    const toOrdinal = weeks.findIndex((w) => w.id === weekToId);
    if (fromOrdinal !== -1 && toOrdinal !== -1 && toOrdinal > fromOrdinal) {
      setError("To Week must be the same as or later than From Week.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const po = await createPurchaseOrder({ weekFromId, weekToId });
      if (file) {
        try {
          await uploadPoDetails(po.id, file);
        } catch (uploadErr) {
          // Header was created but upload failed. Surface clearly so the
          // user knows the PO exists and they can retry from the Edit page.
          setError(
            "PO #" + po.id + " was created but the file upload failed: " +
            (uploadErr as Error).message + ". You can retry from the Edit page.",
          );
          setSubmitting(false);
          // Still navigate to the new PO so the retry surface is reachable.
          onCreated(po.id);
          return;
        }
      }
      onCreated(po.id);
    } catch (createErr) {
      setError((createErr as Error).message);
      setSubmitting(false);
    }
  }

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="new-po-title"
      style={{
        position: "fixed",
        inset: 0,
        background: "rgba(17, 45, 50, 0.45)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
        padding: "1rem",
      }}
      onClick={(e) => {
        // Click on backdrop closes; click inside panel doesn't.
        if (e.target === e.currentTarget && !submitting) onClose();
      }}
    >
      <div
        style={{
          background: "#fff",
          borderRadius: 6,
          width: "100%",
          maxWidth: 560,
          maxHeight: "90vh",
          overflow: "auto",
          boxShadow: "0 20px 60px rgba(0,0,0,0.25)",
        }}
      >
        <header
          style={{
            padding: "1rem 1.25rem",
            borderBottom: `1px solid ${BORDER}`,
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <h3
            id="new-po-title"
            style={{ margin: 0, fontSize: 20, fontWeight: 500, color: TEXT }}
          >
            New Purchase Order
          </h3>
          <button
            type="button"
            onClick={onClose}
            disabled={submitting}
            aria-label="Close"
            style={{
              background: "none",
              border: 0,
              fontSize: 24,
              lineHeight: 1,
              color: TEXT_MUTED,
              cursor: submitting ? "default" : "pointer",
              padding: 0,
            }}
          >
            ×
          </button>
        </header>

        <form onSubmit={onSubmit} style={{ padding: "1.25rem" }}>
          <p style={{ margin: "0 0 1rem", color: TEXT_MUTED, fontSize: 14 }}>
            Pick the week range and (optionally) attach an Excel file. The
            file populates the line items for this PO.
          </p>

          <FormRow label="From Week">
            <WeekSelect value={weekFromId} weeks={weeks} onChange={setWeekFromId} />
          </FormRow>

          <FormRow label="To Week">
            <WeekSelect value={weekToId} weeks={weeks} onChange={setWeekToId} />
          </FormRow>

          <FormRow label="Excel file">
            <input
              ref={fileRef}
              type="file"
              accept=".xlsx,.xls"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              style={{
                width: "100%",
                fontSize: 14,
                color: TEXT,
              }}
            />
            <small style={{ color: TEXT_MUTED, fontSize: 12 }}>
              Optional — you can add line items later from the Edit page.
            </small>
          </FormRow>

          {error && (
            <div
              role="alert"
              style={{
                color: DANGER,
                background: "#fde7e7",
                border: "1px solid #f5c2c2",
                padding: "0.5rem 0.75rem",
                borderRadius: 4,
                marginBottom: "1rem",
                fontSize: 14,
              }}
            >
              {error}
            </div>
          )}

          <footer
            style={{
              display: "flex",
              gap: "0.5rem",
              justifyContent: "flex-end",
              marginTop: "1rem",
              paddingTop: "1rem",
              borderTop: `1px solid ${BORDER}`,
            }}
          >
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              style={{
                padding: "8px 18px",
                background: BG,
                color: TEXT,
                border: `1px solid ${BORDER}`,
                borderRadius: 4,
                fontSize: 14,
                cursor: submitting ? "default" : "pointer",
              }}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              style={{
                padding: "8px 22px",
                background: TEAL,
                color: "white",
                border: 0,
                borderRadius: 4,
                fontSize: 14,
                cursor: submitting ? "default" : "pointer",
                opacity: submitting ? 0.6 : 1,
              }}
            >
              {submitting ? "Creating…" : file ? "Create & Upload" : "Create"}
            </button>
          </footer>
        </form>
      </div>
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
    <div style={{ marginBottom: "1rem" }}>
      <label
        style={{
          display: "block",
          color: TEXT,
          fontSize: 14,
          fontWeight: 500,
          marginBottom: "0.35rem",
        }}
      >
        {label}
      </label>
      {children}
    </div>
  );
}

function WeekSelect({
  value,
  weeks,
  onChange,
}: {
  value: number | "";
  weeks: WeekOption[];
  onChange: (id: number | "") => void;
}) {
  return (
    <select
      value={value}
      onChange={(e) =>
        onChange(e.target.value === "" ? "" : Number(e.target.value))
      }
      required
      style={{
        width: "100%",
        height: 36,
        padding: "0 10px",
        background: BG,
        color: value === "" ? TEXT_MUTED : TEXT,
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
        fontSize: 14,
        fontFamily: "inherit",
      }}
    >
      <option value="">— Choose a week —</option>
      {weeks.map((w) => (
        <option key={w.id} value={w.id}>
          {w.weekDisplay}
        </option>
      ))}
    </select>
  );
}
