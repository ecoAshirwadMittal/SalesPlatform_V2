"use client";

/**
 * The editable PO body — week-range header card + Upload/Download
 * toolbar + 8-column line-items grid. Extracted from /[id]/page.tsx
 * so the new landing (PO-as-grid with week dropdowns at top) can reuse
 * the entire surface; /[id] becomes a thin wrapper that just supplies
 * a fixed poId + the "← Back" affordance.
 *
 * The page-level header (title + subtitle / week-picker) lives in the
 * caller — this component owns everything from the week-range-edit
 * panel downward.
 *
 * Out of scope (deferred):
 *   - Filter row + comparator dropdown
 *   - Server-side pagination > 200 rows
 */

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  deletePurchaseOrder,
  downloadPoDetailsUrl,
  getPurchaseOrder,
  listPoDetails,
  updatePurchaseOrder,
} from "@/lib/api/purchaseOrderClient";
import { fetchWeeks, type WeekOption } from "@/lib/aggregatedInventory";
import type { PODetailRow, PurchaseOrderRow } from "@/lib/types/purchaseOrder";
import {
  ColumnVisibilityMenu,
  useColumnVisibility,
} from "@/lib/admin/dataGrid/columnVisibility";
import UploadExcelModal from "./UploadExcelModal";

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

type DetailSortDir = "asc" | "desc";
interface DetailSortState { col: DetailColKey; dir: DetailSortDir }

function rawValue(r: PODetailRow, col: DetailColKey): string {
  // Used by both filter (substring match) and sort (string fallback).
  // Nulls render as "" so a filter for "x" won't match a null; sort
  // logic in compareDetail handles its own null ordering separately.
  switch (col) {
    case "productId":      return r.productId ?? "";
    case "grade":          return r.grade ?? "";
    case "modelName":      return r.modelName ?? "";
    case "buyerCode":      return r.buyerCode ?? "";
    case "price":          return r.price ?? "";
    case "qtyCap":         return r.qtyCap == null ? "" : String(r.qtyCap);
    case "priceFulfilled": return r.priceFulfilled ?? "";
    case "qtyFulfilled":   return r.qtyFulfilled == null ? "" : String(r.qtyFulfilled);
  }
}

function matchesFilters(r: PODetailRow, filters: Partial<Record<DetailColKey, string>>): boolean {
  for (const [col, needle] of Object.entries(filters)) {
    if (!needle) continue;
    const hay = rawValue(r, col as DetailColKey).toLowerCase();
    if (!hay.includes(needle.toLowerCase())) return false;
  }
  return true;
}

function compareDetail(a: PODetailRow, b: PODetailRow, col: DetailColKey, dir: DetailSortDir): number {
  const sign = dir === "desc" ? -1 : 1;
  const get = (r: PODetailRow): number | string | null => {
    switch (col) {
      case "productId":      return r.productId;
      case "grade":          return r.grade;
      case "modelName":      return r.modelName;
      case "buyerCode":      return r.buyerCode;
      case "price":          return Number(r.price);
      case "qtyCap":         return r.qtyCap;
      case "priceFulfilled": return r.priceFulfilled == null ? null : Number(r.priceFulfilled);
      case "qtyFulfilled":   return r.qtyFulfilled;
    }
  };
  const av = get(a), bv = get(b);
  if (av == null && bv == null) return 0;
  if (av == null) return 1;
  if (bv == null) return -1;
  if (typeof av === "number" && typeof bv === "number") return sign * (av - bv);
  return sign * String(av).localeCompare(String(bv));
}

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const DIVIDER = "#E5E5E5";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";

const usd = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 2,
});
const num = new Intl.NumberFormat("en-US");

interface Props {
  poId: number;
  /**
   * Optional callback fired after the user changes the week-range and
   * the new range succeeds. The new landing uses this to keep its
   * top dropdowns in sync if the admin edits the range from inside
   * the grid (rare but possible).
   */
  onRangeChanged?: (po: PurchaseOrderRow) => void;
  /**
   * Hide the "From: ... To: ... [Edit week range]" card. Set true on
   * the landing where the same info + edit affordance already lives in
   * the top dropdowns; left default false on /[id] where the inner
   * card is the only edit surface.
   */
  hideRangeCard?: boolean;
  /**
   * Optional callback fired after the PO is successfully deleted. The
   * landing uses this to refresh its dropdown and pick the next PO.
   * When omitted (e.g. /[id] deep-link surface), the editor falls back
   * to navigating to the landing route — there's no per-PO surface
   * left to render.
   */
  onDeleted?: (poId: number) => void;
}

export function PurchaseOrderEditor({
  poId, onRangeChanged, hideRangeCard = false, onDeleted,
}: Props) {
  const router = useRouter();
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
  const [sort, setSort] = useState<DetailSortState | null>(null);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  async function onDeletePo() {
    if (!po) return;
    const lineItemSummary = po.totalRecords > 0
      ? ` This will also delete all ${po.totalRecords} line item${po.totalRecords === 1 ? "" : "s"}.`
      : "";
    if (!window.confirm(
      `Delete PO #${po.id} (${po.weekRangeLabel})?${lineItemSummary} This cannot be undone.`,
    )) return;
    setDeleting(true);
    try {
      await deletePurchaseOrder(po.id);
      if (onDeleted) {
        onDeleted(po.id);
      } else {
        // No parent callback — /[id] deep-link surface. The PO is gone,
        // so there's nothing to render here; bounce to the landing.
        router.push("/admin/auctions-data-center/purchase-orders");
      }
    } catch (e) {
      setError("Delete failed: " + (e as Error).message);
      setDeleting(false);
    }
  }

  /*
   * PO-3 phase 2 — per-column substring filter. Client-side because
   * /details is already capped at 200 rows; round-tripping every keystroke
   * to the backend would be wasteful at this volume. Each column's
   * filter is treated as case-insensitive substring against the rendered
   * value (BigDecimals stringified, nulls treated as empty so they never
   * match a non-empty filter). Sort applies on top of filter.
   */
  const [filters, setFilters] = useState<Partial<Record<DetailColKey, string>>>({});

  function onSort(col: DetailColKey) {
    setSort(curr => {
      if (!curr || curr.col !== col) return { col, dir: "desc" };
      if (curr.dir === "desc") return { col, dir: "asc" };
      return null;
    });
  }

  function setFilter(col: DetailColKey, v: string) {
    setFilters(prev => {
      if (v === "") {
        // Drop the key entirely so the matchesFilters loop short-circuits.
        const next = { ...prev };
        delete next[col];
        return next;
      }
      return { ...prev, [col]: v };
    });
  }

  const filteredDetails = Object.keys(filters).length === 0
    ? details
    : details.filter(d => matchesFilters(d, filters));
  const visibleDetails = sort
    ? [...filteredDetails].sort((a, b) => compareDetail(a, b, sort.col, sort.dir))
    : filteredDetails;

  async function reload() {
    try {
      const [p, d] = await Promise.all([
        getPurchaseOrder(poId),
        listPoDetails(poId, 0, 200),
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
    fetchWeeks({ includeFuture: true }).then(setWeeks).catch(() => {/* header edit just degrades to ids */});
    // poId-driven reload — deliberate dependency.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [poId]);

  async function onSaveHeader() {
    if (typeof draftFromId !== "number" || typeof draftToId !== "number") return;
    setSaving(true);
    try {
      const updated = await updatePurchaseOrder(poId, { weekFromId: draftFromId, weekToId: draftToId });
      setEditing(false);
      await reload();
      onRangeChanged?.(updated);
    } catch (e) {
      setError("Save failed: " + (e as Error).message);
    } finally {
      setSaving(false);
    }
  }

  if (!po) return <div style={{ color: TEXT_MUTED }}>Loading…</div>;

  return (
    <>
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

      {/* Header (week range) edit panel — collapsed by default. Hidden
          on the landing where the top dropdowns already serve this
          purpose. */}
      {!hideRangeCard && (
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
            <button onClick={() => setEditing(true)} style={ghostBtn}>
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
      )}

      {/* Action toolbar — Upload + Download as proper buttons. */}
      <section style={{
        display: "flex",
        alignItems: "baseline",
        justifyContent: "space-between",
        marginBottom: "0.75rem",
      }}>
        <h3 style={{ margin: 0, fontSize: 18, color: TEXT, fontWeight: 500 }}>
          Line items <span style={{ color: TEXT_MUTED, fontSize: 14, fontWeight: 400 }}>
            ({visibleDetails.length === details.length
                ? details.length
                : `${visibleDetails.length} of ${details.length}`})
          </span>
        </h3>
        <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
          <ColumnVisibilityMenu state={colVis} />
          <button
            type="button"
            onClick={() => setUploadOpen(true)}
            style={ghostBtn}
          >
            ↑ Upload Excel
          </button>
          <a href={downloadPoDetailsUrl(poId)} style={ghostBtnLink}>
            ↓ Download Excel
          </a>
          {/* Delete sits to the right with danger styling so it doesn't
              get fat-fingered alongside the safer toolbar actions. */}
          <button
            type="button"
            onClick={onDeletePo}
            disabled={deleting}
            style={dangerGhostBtn(deleting)}
            title="Delete this PO"
          >
            {deleting ? "Deleting…" : "🗑 Delete PO"}
          </button>
        </div>
      </section>

      <UploadExcelModal
        open={uploadOpen}
        poId={poId}
        poLabel={po.weekRangeLabel}
        // po.totalRecords is the authoritative count from the PO
        // header; details.length tops out at the listPoDetails 200-row
        // cap which would understate the deletion warning on big POs.
        existingLineItemCount={po.totalRecords}
        onClose={() => setUploadOpen(false)}
        onUploaded={() => {
          // Refresh details so the user sees the new state without
          // needing to navigate. Modal handles its own close timing
          // (auto-close on clean success, manual on errors).
          reload();
        }}
      />

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
              {colVis.visible.has("productId") && <SortTh col="productId" sort={sort} onSort={onSort}>Product ID</SortTh>}
              {colVis.visible.has("grade") && <SortTh col="grade" sort={sort} onSort={onSort}>Grade</SortTh>}
              {colVis.visible.has("modelName") && <SortTh col="modelName" sort={sort} onSort={onSort}>Model Name</SortTh>}
              {colVis.visible.has("buyerCode") && <SortTh col="buyerCode" sort={sort} onSort={onSort}>Buyer Code</SortTh>}
              {colVis.visible.has("price") && <SortTh col="price" sort={sort} onSort={onSort} align="right">Price</SortTh>}
              {colVis.visible.has("qtyCap") && <SortTh col="qtyCap" sort={sort} onSort={onSort} align="right">Qty Cap</SortTh>}
              {colVis.visible.has("priceFulfilled") && <SortTh col="priceFulfilled" sort={sort} onSort={onSort} align="right">Price Fulfilled</SortTh>}
              {colVis.visible.has("qtyFulfilled") && <SortTh col="qtyFulfilled" sort={sort} onSort={onSort} align="right">Qty Fulfilled</SortTh>}
            </tr>
            {/* PO-3 phase 2 — filter row. Always visible (matches QA's
                always-on filter cells). Each input is a substring match. */}
            <tr style={{ background: BG, borderBottom: `1px solid ${BORDER}` }}>
              {colVis.visible.has("productId") && <FilterCell col="productId" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("grade") && <FilterCell col="grade" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("modelName") && <FilterCell col="modelName" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("buyerCode") && <FilterCell col="buyerCode" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("price") && <FilterCell col="price" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("qtyCap") && <FilterCell col="qtyCap" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("priceFulfilled") && <FilterCell col="priceFulfilled" filters={filters} setFilter={setFilter} />}
              {colVis.visible.has("qtyFulfilled") && <FilterCell col="qtyFulfilled" filters={filters} setFilter={setFilter} />}
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
            {details.length > 0 && visibleDetails.length === 0 && (
              <tr>
                <td colSpan={colVis.visible.size} style={{
                  padding: "2rem",
                  textAlign: "center",
                  color: TEXT_MUTED,
                  fontStyle: "italic",
                }}>
                  No line items match the current filter.
                </td>
              </tr>
            )}
            {visibleDetails.map(d => (
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
    </>
  );
}

function SortTh({
  col, sort, onSort, align, children,
}: {
  col: DetailColKey;
  sort: DetailSortState | null;
  onSort: (col: DetailColKey) => void;
  align?: "right";
  children: React.ReactNode;
}) {
  const active = sort?.col === col;
  const glyph = !active ? "↕" : sort.dir === "desc" ? "↓" : "↑";
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
      <button
        type="button"
        onClick={() => onSort(col)}
        style={{
          background: "none",
          border: 0,
          padding: 0,
          font: "inherit",
          color: active ? TEAL : TEXT,
          cursor: "pointer",
          display: "inline-flex",
          alignItems: "center",
          gap: 4,
          flexDirection: align === "right" ? "row-reverse" : "row",
        }}
      >
        {children}
        <span aria-hidden="true" style={{ fontSize: 11, opacity: active ? 1 : 0.45 }}>{glyph}</span>
      </button>
    </th>
  );
}

function FilterCell({
  col, filters, setFilter,
}: {
  col: DetailColKey;
  filters: Partial<Record<DetailColKey, string>>;
  setFilter: (col: DetailColKey, v: string) => void;
}) {
  const value = filters[col] ?? "";
  return (
    <th style={{
      padding: "4px 8px",
      // Filter row sits below sort row; lighter bg + thinner padding
      // visually demotes it.
      background: BG,
    }}>
      <input
        type="text"
        value={value}
        onChange={(e) => setFilter(col, e.target.value)}
        placeholder="Filter"
        aria-label={`Filter ${DETAIL_COL_LABELS[col]}`}
        style={{
          width: "100%",
          height: 26,
          padding: "0 6px",
          background: "#fff",
          color: TEXT,
          border: `1px solid ${BORDER}`,
          borderRadius: 3,
          fontSize: 13,
          fontFamily: "inherit",
          fontWeight: 400,
          // Active filter cell gets a subtle teal ring so it's clear
          // which columns are constraining the result.
          outline: value ? `1px solid ${TEAL}` : "none",
          outlineOffset: 0,
        }}
      />
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

function WeekSelect({
  value, weeks, onChange,
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

function dangerGhostBtn(disabled: boolean): React.CSSProperties {
  return {
    padding: "8px 14px",
    background: BG,
    color: DANGER,
    border: `1px solid ${DANGER}33`, // muted danger border so the button doesn't shout until hovered
    borderRadius: 4,
    fontSize: 14,
    cursor: disabled ? "default" : "pointer",
    opacity: disabled ? 0.6 : 1,
    fontFamily: "inherit",
  };
}
