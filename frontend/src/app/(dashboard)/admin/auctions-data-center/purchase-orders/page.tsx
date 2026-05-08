"use client";

/**
 * PO landing — PO-as-grid surface (replaces the old list view).
 *
 * QA's PO module is a single PO grid keyed by week range, not a list of
 * POs. Two dropdowns at the top ("Week from" / "Week to") select the
 * range, the backend looks up the matching PO via /by-range, and the
 * shared <PurchaseOrderEditor /> renders the line items below.
 *
 * Default selection: most recently changed PO (the user is almost
 * always coming back to "the one I just touched"). The dropdowns hydrate
 * to that PO's range; switching either dropdown re-fetches.
 *
 * Match cardinality:
 *   - 0 → empty-state CTA: "No PO for this range — create one?" with
 *         a button that opens the existing NewPoModal pre-filled
 *   - 1 → render the editor for that PO
 *   - 2+ → red error banner ("Multiple POs cover this range — please
 *         clean up the duplicates"). Schema allows this; UX surfaces
 *         it rather than silently picking one
 *
 * The list view + browse-all surface are intentionally gone. /[id]
 * remains as a deep-link wrapper so existing URLs (audit logs etc.)
 * still resolve.
 */

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  findMostRecentPurchaseOrder,
  findPosByRange,
} from "@/lib/api/purchaseOrderClient";
import { fetchWeeks, type WeekOption } from "@/lib/aggregatedInventory";
import type { PurchaseOrderRow } from "@/lib/types/purchaseOrder";
import NewPoModal from "./NewPoModal";
import { PurchaseOrderEditor } from "./PurchaseOrderEditor";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";

export default function PurchaseOrdersLandingPage() {
  const router = useRouter();
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekFromId, setWeekFromId] = useState<number | null>(null);
  const [weekToId, setWeekToId] = useState<number | null>(null);
  const [matches, setMatches] = useState<PurchaseOrderRow[] | null>(null);
  const [bootstrapping, setBootstrapping] = useState(true);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  // Bootstrap: load weeks list + most-recent PO; pre-fill dropdowns to
  // that PO's range so the user lands on a populated grid.
  useEffect(() => {
    let cancelled = false;
    Promise.all([fetchWeeks(), findMostRecentPurchaseOrder()])
      .then(([weekList, mostRecent]) => {
        if (cancelled) return;
        setWeeks(weekList);
        if (mostRecent) {
          setWeekFromId(mostRecent.weekFromId);
          setWeekToId(mostRecent.weekToId);
          setMatches([mostRecent]);
        } else if (weekList.length > 0) {
          // No POs exist at all yet — pre-fill both dropdowns to the
          // newest week so the empty-state CTA proposes something
          // sensible if the user clicks Create.
          setWeekFromId(weekList[0].id);
          setWeekToId(weekList[0].id);
          setMatches([]);
        }
      })
      .catch((e) => { if (!cancelled) setError((e as Error).message); })
      .finally(() => { if (!cancelled) setBootstrapping(false); });
    return () => { cancelled = true; };
  }, []);

  // Re-fetch when either dropdown changes — but skip during bootstrap so
  // we don't double-fetch immediately after seeding.
  useEffect(() => {
    if (bootstrapping) return;
    if (weekFromId == null || weekToId == null) return;
    let cancelled = false;
    setSearching(true);
    setError(null);
    findPosByRange(weekFromId, weekToId)
      .then((r) => { if (!cancelled) setMatches(r.matches); })
      .catch((e) => { if (!cancelled) setError((e as Error).message); })
      .finally(() => { if (!cancelled) setSearching(false); });
    return () => { cancelled = true; };
  }, [weekFromId, weekToId, bootstrapping]);

  const single = matches && matches.length === 1 ? matches[0] : null;
  const multi = matches && matches.length > 1 ? matches : null;
  const empty = matches != null && matches.length === 0;

  return (
    <div>
      <header style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: "1rem",
        flexWrap: "wrap",
        gap: "1rem",
      }}>
        <h2 style={{
          margin: 0,
          fontSize: "42px",
          fontWeight: 500,
          lineHeight: "54.6px",
          color: TEXT,
        }}>
          Purchase Order
        </h2>

        <button
          type="button"
          onClick={() => setModalOpen(true)}
          style={{
            padding: "0.5rem 1rem",
            background: TEAL,
            color: "white",
            border: 0,
            borderRadius: 4,
            cursor: "pointer",
            fontSize: 14,
            fontFamily: "inherit",
          }}
        >
          + New PO
        </button>
      </header>

      {/* Week-range picker — primary navigation. */}
      <section style={{
        display: "flex",
        gap: "0.75rem",
        alignItems: "center",
        flexWrap: "wrap",
        marginBottom: "1.5rem",
        padding: "0.75rem 1rem",
        background: "#fff",
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
      }}>
        <label style={{ display: "flex", alignItems: "center", gap: "0.5rem", color: TEXT_MUTED, fontSize: 14 }}>
          <span style={{ color: TEXT, fontWeight: 500 }}>Week from</span>
          <WeekSelect value={weekFromId} weeks={weeks} onChange={setWeekFromId} disabled={bootstrapping} />
        </label>
        <label style={{ display: "flex", alignItems: "center", gap: "0.5rem", color: TEXT_MUTED, fontSize: 14 }}>
          <span style={{ color: TEXT, fontWeight: 500 }}>Week to</span>
          <WeekSelect value={weekToId} weeks={weeks} onChange={setWeekToId} disabled={bootstrapping} />
        </label>
        {searching && (
          <span style={{ color: TEXT_MUTED, fontSize: 13, marginLeft: "auto" }}>
            Looking up…
          </span>
        )}
      </section>

      <NewPoModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={(poId) => {
          setModalOpen(false);
          // Navigate to the deep-link surface for the freshly-created
          // PO; the user almost always wants to upload Excel next.
          router.push(`/admin/auctions-data-center/purchase-orders/${poId}`);
        }}
      />

      {error && (
        <div role="alert" style={{
          color: DANGER,
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

      {bootstrapping ? (
        <div style={{ color: TEXT_MUTED }}>Loading…</div>
      ) : multi ? (
        <MultiMatchError matches={multi} />
      ) : empty ? (
        <EmptyRangeState onCreateClick={() => setModalOpen(true)} />
      ) : single ? (
        <PurchaseOrderEditor poId={single.id} />
      ) : null}
    </div>
  );
}

function MultiMatchError({ matches }: { matches: PurchaseOrderRow[] }) {
  return (
    <div role="alert" style={{
      background: "#fde7e7",
      border: `1px solid #f5c2c2`,
      borderRadius: 4,
      padding: "1rem 1.25rem",
      color: DANGER,
      fontSize: 14,
    }}>
      <strong style={{ display: "block", marginBottom: "0.4rem", fontSize: 16 }}>
        Multiple POs cover this week range
      </strong>
      <p style={{ margin: "0 0 0.75rem", color: "#6b1414" }}>
        The schema allows duplicates but the UI shows one PO per range.
        Open each below and merge or delete the extras to clean this up.
      </p>
      <ul style={{ margin: 0, paddingLeft: "1.25rem", color: "#6b1414" }}>
        {matches.map(m => (
          <li key={m.id}>
            <a
              href={`/admin/auctions-data-center/purchase-orders/${m.id}`}
              style={{ color: DANGER, textDecoration: "underline" }}
            >
              PO #{m.id} ({m.totalRecords} line items, last changed {m.changedDate ? new Date(m.changedDate).toLocaleString() : "—"})
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
}

function EmptyRangeState({ onCreateClick }: { onCreateClick: () => void }) {
  return (
    <div style={{
      background: "#fff",
      border: `1px dashed ${BORDER}`,
      borderRadius: 4,
      padding: "2.5rem 1.5rem",
      textAlign: "center",
      color: TEXT_MUTED,
    }}>
      <h3 style={{ margin: "0 0 0.5rem", color: TEXT, fontSize: 18, fontWeight: 500 }}>
        No PO for this week range
      </h3>
      <p style={{ margin: "0 0 1rem", fontSize: 14 }}>
        Create a new PO covering this range, or pick a different range from the dropdowns above.
      </p>
      <button
        type="button"
        onClick={onCreateClick}
        style={{
          padding: "0.5rem 1.25rem",
          background: TEAL,
          color: "white",
          border: 0,
          borderRadius: 4,
          cursor: "pointer",
          fontSize: 14,
          fontFamily: "inherit",
        }}
      >
        + Create PO for this range
      </button>
    </div>
  );
}

function WeekSelect({
  value, weeks, disabled, onChange,
}: {
  value: number | null;
  weeks: WeekOption[];
  disabled: boolean;
  onChange: (id: number) => void;
}) {
  return (
    <select
      value={value ?? ""}
      disabled={disabled}
      onChange={(e) => {
        const v = e.target.value;
        if (v !== "") onChange(Number(v));
      }}
      style={{
        height: 32,
        padding: "0 8px",
        background: BG,
        color: value == null ? TEXT_MUTED : TEXT,
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
        fontSize: 14,
        fontFamily: "inherit",
        minWidth: 160,
      }}
    >
      {value == null && <option value="">{disabled ? "Loading…" : "— Choose a week —"}</option>}
      {weeks.map((w) => (
        <option key={w.id} value={w.id}>{w.weekDisplay}</option>
      ))}
    </select>
  );
}
