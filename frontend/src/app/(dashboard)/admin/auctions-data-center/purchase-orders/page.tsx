"use client";

import { useEffect, useRef, useState } from "react";
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

/*
 * PO-3 phase 1 — server-side sort headers. Each clickable column header
 * cycles desc → asc → none → desc. Only one column sorts at a time
 * (matches QA's single-sort behavior and Spring's default precedence
 * rules). Columns map to JPA property paths the repository's findFiltered
 * query exposes: derived columns like `state` (computed from week-range
 * vs today) and `weekRangeLabel` (built at projection time per PO-7) are
 * not server-sortable, so they're rendered as plain headers.
 *
 * The full filter row + comparator UI is Sprint A.2; this commit just
 * lights up the sort affordance the existing Pageable surface already
 * supports.
 */
type SortCol = "id" | "weekFrom.id" | "totalRecords" | "poRefreshTimestamp";
type SortDir = "asc" | "desc";
interface SortState { col: SortCol; dir: SortDir }
const DEFAULT_SORT: SortState = { col: "id", dir: "desc" };

/*
 * PO-9 — column-visibility selector. Eye-icon button next to + New PO
 * opens a popover with one checkbox per column. State is persisted to
 * localStorage so the choice survives reloads (matches QA's "remember
 * my view" behavior). All columns are toggleable; no minimum-1
 * enforcement — empty grid is the user's prerogative and they can
 * always re-show via the menu.
 */
type ColKey = "id" | "weekRange" | "state" | "totalRecords" | "lastRefresh" | "actions";
const COL_LABELS: Record<ColKey, string> = {
  id: "ID",
  weekRange: "Week range",
  state: "State",
  totalRecords: "Total rows",
  lastRefresh: "Last refresh",
  actions: "Actions",
};
const ALL_COLS: ColKey[] = ["id", "weekRange", "state", "totalRecords", "lastRefresh", "actions"];
const VISIBILITY_STORAGE_KEY = "po-list.visibleCols.v1";

export default function PurchaseOrdersPage() {
  const router = useRouter();
  const [rows, setRows] = useState<PurchaseOrderRow[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [sort, setSort] = useState<SortState | null>(DEFAULT_SORT);

  // Hydrate from localStorage so the layout survives reloads. SSR-safe:
  // the initial state matches the server-rendered (all-visible) markup,
  // and the effect upgrades after hydration with the user's saved set.
  const [visibleCols, setVisibleCols] = useState<Set<ColKey>>(() => new Set(ALL_COLS));
  const [colMenuOpen, setColMenuOpen] = useState(false);
  const colMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    try {
      const raw = window.localStorage.getItem(VISIBILITY_STORAGE_KEY);
      if (!raw) return;
      const parsed = JSON.parse(raw) as string[];
      const valid = parsed.filter((k): k is ColKey => (ALL_COLS as string[]).includes(k));
      setVisibleCols(new Set(valid));
    } catch { /* corrupt storage — fall back to all-visible */ }
  }, []);

  function toggleCol(col: ColKey) {
    setVisibleCols(prev => {
      const next = new Set(prev);
      if (next.has(col)) next.delete(col);
      else next.add(col);
      try {
        window.localStorage.setItem(VISIBILITY_STORAGE_KEY, JSON.stringify([...next]));
      } catch { /* storage unavailable — column toggle still works in-session */ }
      return next;
    });
  }

  // Click-outside / Escape closes the column menu.
  useEffect(() => {
    if (!colMenuOpen) return;
    function onDoc(e: MouseEvent) {
      if (!colMenuRef.current) return;
      if (!colMenuRef.current.contains(e.target as Node)) setColMenuOpen(false);
    }
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape") setColMenuOpen(false);
    }
    document.addEventListener("mousedown", onDoc);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onDoc);
      document.removeEventListener("keydown", onKey);
    };
  }, [colMenuOpen]);

  async function reload() {
    setLoading(true);
    try {
      const r = await listPurchaseOrders({
        page, size,
        sort: sort ? `${sort.col},${sort.dir}` : undefined,
      });
      setRows(r.items);
      setTotal(r.total);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { reload(); }, [page, sort]);

  function onSort(col: SortCol) {
    setSort(current => {
      if (!current || current.col !== col) return { col, dir: "desc" };
      if (current.dir === "desc") return { col, dir: "asc" };
      return null; // third click clears, falls back to backend default
    });
    setPage(0);
  }

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

        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", position: "relative" }}>
          {/* PO-9 — column visibility toggle */}
          <div ref={colMenuRef} style={{ position: "relative" }}>
            <button
              type="button"
              onClick={() => setColMenuOpen(o => !o)}
              aria-haspopup="menu"
              aria-expanded={colMenuOpen}
              title="Show / hide columns"
              style={{
                padding: "0.4rem 0.6rem",
                background: "#F7F7F7",
                color: "#3C3C3C",
                border: "1px solid #D0D0D0",
                borderRadius: 4,
                cursor: "pointer",
                fontSize: 14,
                fontFamily: "inherit",
              }}
            >
              {/* Eye glyph — matches QA's icon font affordance */}
              <span aria-hidden="true">👁</span>
              <span style={{ marginLeft: 6 }}>Columns</span>
            </button>
            {colMenuOpen && (
              <div
                role="menu"
                style={{
                  position: "absolute",
                  top: "calc(100% + 4px)",
                  right: 0,
                  background: "#fff",
                  border: "1px solid #D0D0D0",
                  borderRadius: 4,
                  boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
                  padding: "0.5rem 0",
                  minWidth: 180,
                  zIndex: 50,
                }}
              >
                {ALL_COLS.map(col => (
                  <label
                    key={col}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "0.5rem",
                      padding: "0.35rem 0.75rem",
                      cursor: "pointer",
                      fontSize: 14,
                      color: "#3C3C3C",
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={visibleCols.has(col)}
                      onChange={() => toggleCol(col)}
                    />
                    {COL_LABELS[col]}
                  </label>
                ))}
              </div>
            )}
          </div>

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
        </div>
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
            {visibleCols.has("id") && <SortableTh col="id" sort={sort} onSort={onSort}>ID</SortableTh>}
            {visibleCols.has("weekRange") && <SortableTh col="weekFrom.id" sort={sort} onSort={onSort}>Week range</SortableTh>}
            {visibleCols.has("state") && <th style={thStyle}>State</th>}
            {visibleCols.has("totalRecords") && <SortableTh col="totalRecords" sort={sort} onSort={onSort}>Total rows</SortableTh>}
            {visibleCols.has("lastRefresh") && <SortableTh col="poRefreshTimestamp" sort={sort} onSort={onSort}>Last refresh</SortableTh>}
            {visibleCols.has("actions") && <th style={thStyle}>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id} style={{ borderBottom: "1px solid #eee" }}>
              {visibleCols.has("id") && <td>{r.id}</td>}
              {visibleCols.has("weekRange") && <td>{r.weekRangeLabel}</td>}
              {visibleCols.has("state") && (
                <td>
                  <span style={{
                    padding: "0.15rem 0.6rem", borderRadius: 999,
                    background: STATE_COLORS[r.state], color: "white", fontSize: "0.85rem",
                  }}>{r.state}</span>
                </td>
              )}
              {visibleCols.has("totalRecords") && <td>{r.totalRecords}</td>}
              {visibleCols.has("lastRefresh") && (
                <td>{r.poRefreshTimestamp
                  ? new Date(r.poRefreshTimestamp).toLocaleString() : "—"}</td>
              )}
              {visibleCols.has("actions") && (
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
              )}
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

const thStyle: React.CSSProperties = {
  padding: "10px 12px",
  fontWeight: 500,
  fontSize: 13,
  color: "#3C3C3C",
  whiteSpace: "nowrap",
};

function SortableTh({
  col,
  sort,
  onSort,
  children,
}: {
  col: SortCol;
  sort: SortState | null;
  onSort: (col: SortCol) => void;
  children: React.ReactNode;
}) {
  const active = sort?.col === col;
  // Single neutral arrow when this column isn't the active sort, so
  // hovering doesn't make every column look hot. Active column gets a
  // direction-specific glyph.
  const glyph = !active ? "↕" : sort.dir === "desc" ? "↓" : "↑";
  return (
    <th style={thStyle}>
      <button
        type="button"
        onClick={() => onSort(col)}
        style={{
          background: "none",
          border: 0,
          padding: 0,
          font: "inherit",
          color: active ? PO_TEAL : "#3C3C3C",
          cursor: "pointer",
          display: "inline-flex",
          alignItems: "center",
          gap: 4,
        }}
      >
        {children}
        <span aria-hidden="true" style={{ fontSize: 11, opacity: active ? 1 : 0.45 }}>{glyph}</span>
      </button>
    </th>
  );
}
