"use client";

/**
 * PO landing — single PO-picker model (matches QA).
 *
 * QA's PO surface is a single dropdown listing every existing PO by
 * its week-range label. Picking one loads its grid below. There is
 * no concept of "search by week range" — once a PO exists for a
 * multi-week period, it's an object, not a query.
 *
 * Earlier iterations of this page used two dropdowns (Week from /
 * Week to) and a backend by-range lookup, which forced an
 * empty-state UX (no PO matches this range) and a multi-match error
 * branch. Both go away with the single-picker model — the dropdown
 * only shows POs that exist, so 0-match and N-match are impossible
 * by construction.
 *
 * The picked PO renders via <PurchaseOrderEditor /> below. The
 * editor's inner "Edit week range" card is re-enabled here (we hid
 * it briefly when the top dropdowns were redundant range pickers —
 * now that the top is a navigation-only PO selector, the inner card
 * is once again the only way to edit the current PO's range).
 */

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  listAllPurchaseOrders,
} from "@/lib/api/purchaseOrderClient";
import type { PurchaseOrderRow } from "@/lib/types/purchaseOrder";
import NewPoModal from "./NewPoModal";
import { PurchaseOrderEditor } from "./PurchaseOrderEditor";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";

const STATE_COLORS: Record<string, string> = {
  ACTIVE: "#176c4d",
  CLOSED: "#606671",
};

export default function PurchaseOrdersLandingPage() {
  const router = useRouter();
  const [allPos, setAllPos] = useState<PurchaseOrderRow[]>([]);
  const [pickedPoId, setPickedPoId] = useState<number | null>(null);
  const [bootstrapping, setBootstrapping] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  // Bootstrap: fetch all POs, default-pick the most-recent one. The
  // service returns them sorted newest-changed first, so [0] is the
  // natural landing target.
  useEffect(() => {
    let cancelled = false;
    listAllPurchaseOrders()
      .then((pos) => {
        if (cancelled) return;
        setAllPos(pos);
        if (pos.length > 0) setPickedPoId(pos[0].id);
      })
      .catch((e) => { if (!cancelled) setError((e as Error).message); })
      .finally(() => { if (!cancelled) setBootstrapping(false); });
    return () => { cancelled = true; };
  }, []);

  // Lookup helper — used to render the dropdown's selected option text.
  const pickedPo = pickedPoId == null
    ? null
    : allPos.find(p => p.id === pickedPoId) ?? null;

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

      {/* PO picker — primary navigation. */}
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
          <span style={{ color: TEXT, fontWeight: 500 }}>Purchase Order</span>
          <select
            value={pickedPoId ?? ""}
            disabled={bootstrapping || allPos.length === 0}
            onChange={(e) => setPickedPoId(e.target.value === "" ? null : Number(e.target.value))}
            style={{
              height: 32,
              padding: "0 8px",
              background: BG,
              color: pickedPoId == null ? TEXT_MUTED : TEXT,
              border: `1px solid ${BORDER}`,
              borderRadius: 4,
              fontSize: 14,
              fontFamily: "inherit",
              minWidth: 280,
            }}
          >
            {bootstrapping && <option value="">Loading…</option>}
            {!bootstrapping && allPos.length === 0 && <option value="">— No POs yet —</option>}
            {allPos.map(po => (
              <option key={po.id} value={po.id}>
                {po.weekRangeLabel} · {po.state} · {po.totalRecords} line item{po.totalRecords === 1 ? "" : "s"}
              </option>
            ))}
          </select>
        </label>
        {pickedPo && (
          <span style={{
            padding: "0.15rem 0.6rem",
            borderRadius: 999,
            background: STATE_COLORS[pickedPo.state] ?? "#888",
            color: "white",
            fontSize: "0.75rem",
            fontWeight: 600,
            letterSpacing: "0.02em",
          }}>{pickedPo.state}</span>
        )}
      </section>

      <NewPoModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={(poId) => {
          setModalOpen(false);
          // Hard-navigate to the new PO's deep-link surface so the user
          // can immediately upload Excel; the landing's dropdown gets
          // refreshed when they navigate back.
          router.push(`/admin/auctions-data-center/purchase-orders/${poId}`);
        }}
        existingPos={allPos}
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
      ) : allPos.length === 0 ? (
        <EmptyNoPos onCreateClick={() => setModalOpen(true)} />
      ) : pickedPo ? (
        <PurchaseOrderEditor
          poId={pickedPo.id}
          // Inner range-edit card is the only way to edit the current
          // PO's range now that the top selector is navigation-only.
          onRangeChanged={(updated) => {
            // Reflect the new range in the dropdown without a full
            // refetch — splice the updated row in place.
            setAllPos(prev => prev.map(p => p.id === updated.id ? updated : p));
          }}
          onDeleted={(deletedId) => {
            // Drop the row from the dropdown and re-pick the next
            // most-recent PO. If the user deleted the last one, the
            // empty-state card takes over on the next render.
            setAllPos(prev => {
              const next = prev.filter(p => p.id !== deletedId);
              setPickedPoId(next.length > 0 ? next[0].id : null);
              return next;
            });
          }}
        />
      ) : null}
    </div>
  );
}

function EmptyNoPos({ onCreateClick }: { onCreateClick: () => void }) {
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
        No Purchase Orders yet
      </h3>
      <p style={{ margin: "0 0 1rem", fontSize: 14 }}>
        Create the first one to get started.
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
        + New PO
      </button>
    </div>
  );
}
