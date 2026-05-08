"use client";

/**
 * PO detail / edit page — replaces the dev-scaffold version (gap PO-1).
 *
 * Pre-2026-05-08 this page rendered:
 *   - "PO #5 — UNKNOWN" title
 *   - "Week from id 557" / "Week to id 101" raw FK ids in the header form
 *   - Bare-text "Save header", "Upload Excel", "Download Excel" links
 *   - 33 detail rows in a flat unstyled table with no header treatment,
 *     no divider lines, no PriceFulfilled/QtyFulfilled columns, no
 *     pagination
 *
 * This iteration replaces all of that with a presentable detail layout.
 * The 8-column grid matches QA's column set (gap PO-6 surfaces the
 * Fulfilled columns the previous code dropped). Styling consumes the
 * existing globals tokens documented in
 * docs/tasks/qa-vs-local-po-styling-spec-2026-05-08.md §1.
 *
 * Out of scope (deferred to Sprint A grid rebuild):
 *   - Sort headers, filter row + comparator dropdown
 *   - Column visibility selector
 *   - Server-side pagination > 200 rows
 */

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import {
  downloadPoDetailsUrl,
  getPurchaseOrder,
  listPoDetails,
  updatePurchaseOrder,
} from "@/lib/api/purchaseOrderClient";
import { fetchWeeks, type WeekOption } from "@/lib/aggregatedInventory";
import type {
  PODetailRow,
  PurchaseOrderLifecycleState,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";
import {
  ColumnVisibilityMenu,
  useColumnVisibility,
} from "@/lib/admin/dataGrid/columnVisibility";

/*
 * PO-9 phase 2 — column visibility for the 8-column line-items grid.
 * Separate storage key from the list page so toggling one doesn't
 * affect the other.
 */
type DetailColKey =
  | "productId" | "grade" | "modelName" | "buyerCode"
  | "price" | "qtyCap" | "priceFulfilled" | "qtyFulfilled";
const DETAIL_COL_LABELS: Record<DetailColKey, string> = {
  productId: "Product ID",
  grade: "Grade",
  modelName: "Model Name",
  buyerCode: "Buyer Code",
  price: "Price",
  qtyCap: "Qty Cap",
  priceFulfilled: "Price Fulfilled",
  qtyFulfilled: "Qty Fulfilled",
};
const DETAIL_ALL_COLS = [
  "productId", "grade", "modelName", "buyerCode",
  "price", "qtyCap", "priceFulfilled", "qtyFulfilled",
] as const satisfies readonly DetailColKey[];
const DETAIL_VISIBILITY_STORAGE_KEY = "po-detail.visibleCols.v1";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const DIVIDER = "#E5E5E5";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";

const STATE_COLORS: Record<PurchaseOrderLifecycleState, string> = {
  DRAFT: "#a07f00",
  ACTIVE: "#176c4d",
  CLOSED: "#606671",
};

const usd = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 2,
});
const num = new Intl.NumberFormat("en-US");

export default function EditPurchaseOrderPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [po, setPo] = useState<PurchaseOrderRow | null>(null);
  const [details, setDetails] = useState<PODetailRow[]>([]);
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [editing, setEditing] = useState(false);
  const [draftFromId, setDraftFromId] = useState<number | "">("");
  const [draftToId, setDraftToId] = useState<number | "">("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const colVis = useColumnVisibility(
    DETAIL_ALL_COLS, DETAIL_COL_LABELS, DETAIL_VISIBILITY_STORAGE_KEY,
  );

  async function reload() {
    try {
      const [p, d] = await Promise.all([
        getPurchaseOrder(id),
        listPoDetails(id, 0, 200),
      ]);
      setPo(p);
      setDetails(d.items);
      setDraftFromId(p.weekFromId);
      setDraftToId(p.weekToId);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  useEffect(() => {
    reload();
    fetchWeeks().then(setWeeks).catch(() => {/* header edit just degrades to ids */});
  }, [id]);

  async function onSaveHeader() {
    if (typeof draftFromId !== "number" || typeof draftToId !== "number") return;
    setSaving(true);
    try {
      await updatePurchaseOrder(id, { weekFromId: draftFromId, weekToId: draftToId });
      setEditing(false);
      await reload();
    } catch (e) {
      setError("Save failed: " + (e as Error).message);
    } finally {
      setSaving(false);
    }
  }

  if (!po) return <div style={{ color: TEXT_MUTED }}>Loading…</div>;

  return (
    <div>
      <Link
        href="/admin/auctions-data-center/purchase-orders"
        style={{ color: TEAL, fontSize: 14, textDecoration: "none" }}
      >
        ← Back to Purchase Order
      </Link>

      <header style={{ margin: "0.5rem 0 1.5rem" }}>
        <h2 style={{
          margin: 0,
          fontSize: "42px",
          fontWeight: 500,
          lineHeight: "54.6px",
          color: TEXT,
        }}>
          PO #{po.id}
        </h2>
        <p style={{ margin: "0.25rem 0 0", color: TEXT_MUTED, fontSize: 14 }}>
          {po.weekRangeLabel}
          {" · "}
          <StatePill state={po.state} />
          {po.poRefreshTimestamp && (
            <>
              {" · last refresh "}
              {new Date(po.poRefreshTimestamp).toLocaleString()}
            </>
          )}
        </p>
      </header>

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

      {/* Header (week range) edit panel — collapsed by default. */}
      <section style={{
        background: "#fff",
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
        padding: "1rem",
        marginBottom: "1.5rem",
      }}>
        {!editing ? (
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div style={{ color: TEXT_MUTED, fontSize: 14 }}>
              <strong style={{ color: TEXT, fontWeight: 500 }}>From:</strong> {po.weekFromLabel}
              {" · "}
              <strong style={{ color: TEXT, fontWeight: 500 }}>To:</strong> {po.weekToLabel}
            </div>
            <button
              onClick={() => setEditing(true)}
              style={ghostBtn}
            >
              Edit week range
            </button>
          </div>
        ) : (
          <div style={{ display: "flex", gap: "0.75rem", alignItems: "center", flexWrap: "wrap" }}>
            <label style={{ color: TEXT_MUTED, fontSize: 14, display: "flex", alignItems: "center", gap: "0.4rem" }}>
              From Week
              <WeekSelect value={draftFromId} weeks={weeks} onChange={setDraftFromId} />
            </label>
            <label style={{ color: TEXT_MUTED, fontSize: 14, display: "flex", alignItems: "center", gap: "0.4rem" }}>
              To Week
              <WeekSelect value={draftToId} weeks={weeks} onChange={setDraftToId} />
            </label>
            <div style={{ display: "flex", gap: "0.5rem", marginLeft: "auto" }}>
              <button onClick={onSaveHeader} disabled={saving} style={primaryBtn}>
                {saving ? "Saving…" : "Save"}
              </button>
              <button
                onClick={() => { setEditing(false); setDraftFromId(po.weekFromId); setDraftToId(po.weekToId); }}
                disabled={saving}
                style={ghostBtn}
              >
                Cancel
              </button>
            </div>
          </div>
        )}
      </section>

      {/* Action toolbar — Upload + Download as proper buttons. */}
      <section style={{
        display: "flex",
        alignItems: "baseline",
        justifyContent: "space-between",
        marginBottom: "0.75rem",
      }}>
        <h3 style={{ margin: 0, fontSize: 18, color: TEXT, fontWeight: 500 }}>
          Line items <span style={{ color: TEXT_MUTED, fontSize: 14, fontWeight: 400 }}>({details.length})</span>
        </h3>
        <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
          <ColumnVisibilityMenu state={colVis} />
          <Link
            href={`/admin/auctions-data-center/purchase-orders/${id}/upload`}
            style={ghostBtnLink}
          >
            ↑ Upload Excel
          </Link>
          <a
            href={downloadPoDetailsUrl(id)}
            style={ghostBtnLink}
          >
            ↓ Download Excel
          </a>
        </div>
      </section>

      {/* Line items grid — 8 columns matching QA. */}
      <div style={{
        background: "#fff",
        border: `1px solid ${BORDER}`,
        borderRadius: 4,
        overflow: "hidden",
      }}>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ background: BG, borderBottom: `1px solid ${BORDER}` }}>
              {colVis.visible.has("productId") && <Th>Product ID</Th>}
              {colVis.visible.has("grade") && <Th>Grade</Th>}
              {colVis.visible.has("modelName") && <Th>Model Name</Th>}
              {colVis.visible.has("buyerCode") && <Th>Buyer Code</Th>}
              {colVis.visible.has("price") && <Th align="right">Price</Th>}
              {colVis.visible.has("qtyCap") && <Th align="right">Qty Cap</Th>}
              {colVis.visible.has("priceFulfilled") && <Th align="right">Price Fulfilled</Th>}
              {colVis.visible.has("qtyFulfilled") && <Th align="right">Qty Fulfilled</Th>}
            </tr>
          </thead>
          <tbody>
            {details.length === 0 && (
              <tr>
                <td colSpan={colVis.visible.size} style={{
                  padding: "2rem",
                  textAlign: "center",
                  color: TEXT_MUTED,
                  fontStyle: "italic",
                }}>
                  No line items yet — upload an Excel file to populate.
                </td>
              </tr>
            )}
            {details.map(d => (
              <tr key={d.id} style={{ borderBottom: `1px solid ${DIVIDER}` }}>
                {colVis.visible.has("productId") && <Td>{d.productId}</Td>}
                {colVis.visible.has("grade") && <Td>{d.grade}</Td>}
                {colVis.visible.has("modelName") && <Td>{d.modelName ?? "—"}</Td>}
                {colVis.visible.has("buyerCode") && <Td>{d.buyerCode}</Td>}
                {colVis.visible.has("price") && <Td align="right">{usd.format(Number(d.price))}</Td>}
                {colVis.visible.has("qtyCap") && <Td align="right">{d.qtyCap == null ? "—" : num.format(d.qtyCap)}</Td>}
                {colVis.visible.has("priceFulfilled") && <Td align="right">{d.priceFulfilled == null ? "—" : usd.format(Number(d.priceFulfilled))}</Td>}
                {colVis.visible.has("qtyFulfilled") && <Td align="right">{d.qtyFulfilled == null ? "—" : num.format(d.qtyFulfilled)}</Td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Th({ children, align }: { children: React.ReactNode; align?: "right" }) {
  return (
    <th style={{
      textAlign: align ?? "left",
      padding: "10px 12px",
      color: TEXT,
      fontWeight: 500,
      fontSize: 13,
      letterSpacing: "0.02em",
      whiteSpace: "nowrap",
    }}>
      {children}
    </th>
  );
}

function Td({ children, align }: { children: React.ReactNode; align?: "right" }) {
  return (
    <td style={{
      padding: "8px 12px",
      color: TEXT,
      textAlign: align ?? "left",
      whiteSpace: "nowrap",
    }}>
      {children}
    </td>
  );
}

function StatePill({ state }: { state: PurchaseOrderLifecycleState }) {
  return (
    <span style={{
      padding: "0.15rem 0.6rem",
      borderRadius: 999,
      background: STATE_COLORS[state],
      color: "white",
      fontSize: "0.75rem",
      fontWeight: 600,
      letterSpacing: "0.02em",
    }}>
      {state}
    </span>
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
      style={{
        height: 32,
        padding: "0 8px",
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
        <option key={w.id} value={w.id}>{w.weekDisplay}</option>
      ))}
    </select>
  );
}

const primaryBtn: React.CSSProperties = {
  padding: "8px 18px",
  background: TEAL,
  color: "white",
  border: 0,
  borderRadius: 4,
  fontSize: 14,
  cursor: "pointer",
  fontFamily: "inherit",
};
const ghostBtn: React.CSSProperties = {
  padding: "8px 18px",
  background: BG,
  color: TEXT,
  border: `1px solid ${BORDER}`,
  borderRadius: 4,
  fontSize: 14,
  cursor: "pointer",
  fontFamily: "inherit",
};
const ghostBtnLink: React.CSSProperties = {
  ...ghostBtn,
  textDecoration: "none",
  display: "inline-flex",
  alignItems: "center",
};
