"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  listPurchaseOrders,
  deletePurchaseOrder,
} from "@/lib/api/purchaseOrderClient";
import type {
  PurchaseOrderLifecycleState,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";
import NewPoModal from "./NewPoModal";

/**
 * Local-only side fixes from the 2026-05-08 PO styling spec (S1–S6).
 *
 * These are quick visual cleanups that DO NOT depend on the eventual
 * grid rebuild (Sprint A/B/C in qa-vs-local-po-walkthrough-2026-05-08.md).
 * The full surface still needs replacement; these tightenings make the
 * existing list page presentable in the meantime.
 *   S1 — page heading: <h1> 16px → <h2> 42px / 500 / 54.6px
 *   S2 — title singular: "Purchase Orders" → "Purchase Order"
 *   S3 — pagination footer: "Showing 5 of 5.PrevNext" run-on → flex+gap
 *   S4 — CLOSED pill #888 mid-grey → #606671 darker slate (better contrast)
 *   S5 — Edit / Delete actions: underline + accent colors
 *   S6 — outer wrapper padding removed; the dashboard layout's `main`
 *        already provides 20px, so the inline 1.5rem was double-padding.
 */

const STATE_COLORS: Record<PurchaseOrderLifecycleState, string> = {
  DRAFT: "#a07f00",
  ACTIVE: "#176c4d",
  CLOSED: "#606671", // S4 — bumped from #888 (low contrast against page bg)
};

const PO_TEAL = "#407874"; // CLAUDE.md brand teal — used for Edit link
const PO_DANGER = "#a31b1b"; // Delete (matches existing red affordance)

export default function PurchaseOrdersPage() {
  const router = useRouter();
  const [rows, setRows] = useState<PurchaseOrderRow[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  async function reload() {
    setLoading(true);
    try {
      const r = await listPurchaseOrders({ page, size });
      setRows(r.items);
      setTotal(r.total);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { reload(); }, [page]);

  async function onDelete(id: number) {
    if (!confirm(`Delete PO #${id}? This will cascade-delete all detail rows.`)) return;
    try {
      await deletePurchaseOrder(id);
      reload();
    } catch (e) {
      alert("Delete failed: " + (e as Error).message);
    }
  }

  return (
    /* S6 — no outer padding; dashboard <main> already provides 20px. */
    <div>
      <header style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: "1rem",
      }}>
        {/* S1 + S2 — semantic h2, 42px / 500 / 54.6px line-height, singular title. */}
        <h2 style={{
          margin: 0,
          fontSize: "42px",
          fontWeight: 500,
          lineHeight: "54.6px",
          color: "#3C3C3C",
        }}>
          Purchase Order
        </h2>
        {/*
          PO-4: primary entry is now the modal — single step, week-range
          + xlsx upload in one shot. The /new route still exists as a
          fallback (deep-linkable), but is no longer linked from this
          page.
         */}
        <button
          type="button"
          onClick={() => setModalOpen(true)}
          style={{
            padding: "0.5rem 1rem",
            background: PO_TEAL,
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

      <NewPoModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={(poId) => {
          setModalOpen(false);
          router.push(`/admin/auctions-data-center/purchase-orders/${poId}`);
        }}
      />

      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      {loading && <div>Loading…</div>}

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "#F7F7F7", textAlign: "left" }}>
            <th>ID</th><th>Week range</th><th>State</th>
            <th>Total rows</th><th>Last refresh</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id} style={{ borderBottom: "1px solid #eee" }}>
              <td>{r.id}</td>
              <td>{r.weekRangeLabel}</td>
              <td>
                <span style={{
                  padding: "0.15rem 0.6rem", borderRadius: 999,
                  background: STATE_COLORS[r.state], color: "white", fontSize: "0.85rem",
                }}>{r.state}</span>
              </td>
              <td>{r.totalRecords}</td>
              <td>{r.poRefreshTimestamp
                ? new Date(r.poRefreshTimestamp).toLocaleString() : "—"}</td>
              <td>
                {/* S5 — underlined Edit (teal) + Delete (danger red) actions. */}
                <Link
                  href={`/admin/auctions-data-center/purchase-orders/${r.id}`}
                  style={{ color: PO_TEAL, textDecoration: "underline" }}
                >
                  Edit
                </Link>
                {" · "}
                <button
                  onClick={() => onDelete(r.id)}
                  style={{
                    background: "none",
                    color: PO_DANGER,
                    border: 0,
                    padding: 0,
                    cursor: "pointer",
                    textDecoration: "underline",
                    font: "inherit",
                  }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/*
       * S3 — pagination flex container with explicit gap. The previous
       * markup rendered as one run-on string ("Showing 5 of 5.PrevNext")
       * because the count text and the two buttons sat in the same inline
       * flow with no whitespace between them.
       */}
      <footer style={{
        marginTop: "1rem",
        display: "flex",
        alignItems: "center",
        gap: "0.75rem",
      }}>
        <span>Showing {rows.length} of {total}</span>
        <button onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}>Prev</button>
        <button onClick={() => setPage(p => p + 1)}
                disabled={(page + 1) * size >= total}>Next</button>
      </footer>
    </div>
  );
}
