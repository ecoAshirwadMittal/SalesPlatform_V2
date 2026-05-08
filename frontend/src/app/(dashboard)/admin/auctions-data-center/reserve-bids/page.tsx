"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import Link from "next/link";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";
import styles from "./reserveBidsList.module.css";
import ReserveBidAuditModal from "./ReserveBidAuditModal";

const PAGE_SIZE = 20;
const FILTER_DEBOUNCE_MS = 400;

// Backend whitelists these SQL column names for sort. Mapping is also used as
// the source of truth for which columns are sortable in the UI.
type SortColumn = "product_id" | "grade" | "brand" | "model" | "bid" | "last_update_datetime";
type SortDirection = "asc" | "desc";

interface ColumnDef {
  key: string;
  label: string;
  sort?: SortColumn;
  numeric?: boolean;
  toggleable: boolean;
}

const COLUMNS: ColumnDef[] = [
  { key: "productId", label: "Product ID", sort: "product_id", numeric: true,  toggleable: true  },
  { key: "grade",     label: "Grade",      sort: "grade",                       toggleable: true  },
  { key: "brand",     label: "Brand",      sort: "brand",                       toggleable: true  },
  { key: "model",     label: "Model",      sort: "model",                       toggleable: true  },
  { key: "bid",       label: "Bid",        sort: "bid",        numeric: true,  toggleable: true  },
  { key: "updated",   label: "Last Updated", sort: "last_update_datetime",     toggleable: true  },
  { key: "actions",   label: "Audit",                                            toggleable: false },
];

interface Filters {
  productId: string;       // exact match — backend uses rb.product_id = :productId
  grade: string;           // contains — backend uses LIKE %:grade%
  minBid: string;          // numeric (BigDecimal serialised as string)
  maxBid: string;
  updatedSince: string;    // YYYY-MM-DD; converted to ISO before sending
}

const EMPTY_FILTERS: Filters = {
  productId: "",
  grade: "",
  minBid: "",
  maxBid: "",
  updatedSince: "",
};

export default function ReserveBidsPage() {
  const [rows, setRows] = useState<ReserveBidRow[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const [sortColumn, setSortColumn] = useState<SortColumn | null>("product_id");
  const [sortDirection, setSortDirection] = useState<SortDirection>("asc");

  const [hiddenColumns, setHiddenColumns] = useState<Set<string>>(new Set());
  const [columnMenuOpen, setColumnMenuOpen] = useState(false);

  const [auditTarget, setAuditTarget] = useState<{ id: number; productId: string } | null>(null);

  // Debounce filter input → applied filters → fetch
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setApplied(input);
      setPage(0);
    }, FILTER_DEBOUNCE_MS);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [input]);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const updatedSinceIso = applied.updatedSince
        ? new Date(applied.updatedSince + "T00:00:00").toISOString()
        : undefined;
      const sortParam = sortColumn ? `${sortColumn},${sortDirection}` : undefined;
      const res = await reserveBidClient.list({
        productId: applied.productId || undefined,
        grade: applied.grade || undefined,
        minBid: applied.minBid || undefined,
        maxBid: applied.maxBid || undefined,
        updatedSince: updatedSinceIso,
        sort: sortParam,
        page,
        size: PAGE_SIZE,
      });
      setRows(res.rows);
      setTotal(res.total);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Load failed");
    } finally {
      setLoading(false);
    }
  }, [applied, page, sortColumn, sortDirection]);

  useEffect(() => { void load(); }, [load]);

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this reserve bid? This will drop its audit trail.")) return;
    try {
      await reserveBidClient.remove(id);
      void load();
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Delete failed");
    }
  };

  const handleDownload = async () => {
    try {
      const blob = await reserveBidClient.download();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `reserve-bids-${new Date().toISOString().slice(0, 10)}.xlsx`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Download failed");
    }
  };

  const toggleSort = (column: SortColumn) => {
    if (sortColumn === column) {
      setSortDirection((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortColumn(column);
      setSortDirection("asc");
    }
    setPage(0);
  };

  const setFilter = <K extends keyof Filters>(key: K, value: string) =>
    setInput((prev) => ({ ...prev, [key]: value }));

  const visibleColumns = useMemo(
    () => COLUMNS.filter((c) => !hiddenColumns.has(c.key)),
    [hiddenColumns],
  );

  const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  return (
    <div className={styles.page}>
      <div className={styles.headerRow}>
        <h1 className={styles.heading}>Reserve Bids (EB)</h1>
        <div className={styles.actions}>
          <Link href="/admin/auctions-data-center/reserve-bids/upload">
            <button className="btn-outline" type="button">Upload Excel</button>
          </Link>
          <button className="btn-outline" type="button" onClick={handleDownload}>
            Download Excel
          </button>
          <Link href="/admin/auctions-data-center/reserve-bids/new">
            <button className="btn-outline" type="button">New</button>
          </Link>
          <ColumnSelector
            columns={COLUMNS.filter((c) => c.toggleable)}
            hidden={hiddenColumns}
            onToggle={(key) => {
              setHiddenColumns((prev) => {
                const next = new Set(prev);
                if (next.has(key)) next.delete(key);
                else next.add(key);
                return next;
              });
            }}
            open={columnMenuOpen}
            setOpen={setColumnMenuOpen}
          />
        </div>
      </div>

      {error && <div role="alert" className={styles.errorAlert}>{error}</div>}

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              {visibleColumns.map((col) => (
                <th
                  key={col.key}
                  className={col.numeric ? styles.numericCell : undefined}
                  scope="col"
                >
                  {col.sort ? (
                    <button
                      type="button"
                      className={styles.sortButton}
                      onClick={() => toggleSort(col.sort!)}
                      aria-label={`Sort by ${col.label}`}
                    >
                      <span>{col.label}</span>
                      <SortGlyph
                        active={sortColumn === col.sort}
                        direction={sortDirection}
                      />
                    </button>
                  ) : (
                    <span>{col.label}</span>
                  )}
                  <FilterCell
                    column={col}
                    input={input}
                    setFilter={setFilter}
                  />
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {!loading && rows.length === 0 && (
              <tr>
                <td colSpan={visibleColumns.length}>
                  <div className={styles.emptyState}>
                    No reserve bids match the current filters.
                  </div>
                </td>
              </tr>
            )}
            {rows.map((r) => (
              <tr key={r.id}>
                {!hiddenColumns.has("productId") && (
                  <td className={styles.numericCell}>{r.productId}</td>
                )}
                {!hiddenColumns.has("grade") && <td>{r.grade}</td>}
                {!hiddenColumns.has("brand") && <td>{r.brand ?? "—"}</td>}
                {!hiddenColumns.has("model") && <td>{r.model ?? "—"}</td>}
                {!hiddenColumns.has("bid") && (
                  <td className={styles.numericCell}>${formatMoney(r.bid)}</td>
                )}
                {!hiddenColumns.has("updated") && (
                  <td>{formatDateTime(r.lastUpdateDatetime)}</td>
                )}
                <td>
                  <div className={styles.actionsCell}>
                    <Link
                      href={`/admin/auctions-data-center/reserve-bids/${r.id}`}
                      className={styles.rowAction}
                    >
                      Edit
                    </Link>
                    <button
                      type="button"
                      className={styles.rowAction}
                      onClick={() => setAuditTarget({ id: r.id, productId: r.productId })}
                    >
                      Audit
                    </button>
                    <button
                      type="button"
                      className={`${styles.rowAction} ${styles.rowActionDanger}`}
                      onClick={() => handleDelete(r.id)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {loading && <div className={styles.loadingOverlay}>Loading…</div>}

        <div className={styles.pagination}>
          <button
            type="button"
            className={styles.paginationButton}
            onClick={() => setPage(0)}
            disabled={page === 0}
            aria-label="First page"
          >
            «
          </button>
          <button
            type="button"
            className={styles.paginationButton}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            aria-label="Previous page"
          >
            ‹
          </button>
          <span className={styles.paginationCount}>
            {total === 0
              ? "No matches"
              : `Showing ${formatInt(startIdx)} to ${formatInt(endIdx)} of ${formatInt(total)}`}
          </span>
          <button
            type="button"
            className={styles.paginationButton}
            onClick={() => setPage((p) => p + 1)}
            disabled={page + 1 >= totalPages}
            aria-label="Next page"
          >
            ›
          </button>
          <button
            type="button"
            className={styles.paginationButton}
            onClick={() => setPage(totalPages - 1)}
            disabled={page + 1 >= totalPages}
            aria-label="Last page"
          >
            »
          </button>
        </div>
      </div>

      {auditTarget && (
        <ReserveBidAuditModal
          reserveBidId={auditTarget.id}
          productId={auditTarget.productId}
          onClose={() => setAuditTarget(null)}
        />
      )}
    </div>
  );
}

// ── Helpers ────────────────────────────────────────────────────

function formatMoney(value: string): string {
  const n = Number(value);
  if (Number.isNaN(n)) return value;
  return n.toFixed(2);
}

function formatDateTime(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString();
}

function formatInt(n: number): string {
  return new Intl.NumberFormat("en-US").format(n);
}

// ── Sort glyph ─────────────────────────────────────────────────

function SortGlyph({ active, direction }: { active: boolean; direction: SortDirection }) {
  if (!active) {
    return (
      <svg viewBox="0 0 12 12" className={styles.sortIcon} aria-hidden="true">
        <path d="M3 5l3-3 3 3M3 7l3 3 3-3" stroke="currentColor" strokeWidth="1.4" fill="none" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  }
  return (
    <svg viewBox="0 0 12 12" className={`${styles.sortIcon} ${styles.sortIconActive}`} aria-hidden="true">
      {direction === "asc" ? (
        <path d="M3 7l3-3 3 3" stroke="currentColor" strokeWidth="1.6" fill="none" strokeLinecap="round" strokeLinejoin="round" />
      ) : (
        <path d="M3 5l3 3 3-3" stroke="currentColor" strokeWidth="1.6" fill="none" strokeLinecap="round" strokeLinejoin="round" />
      )}
    </svg>
  );
}

// ── Per-column filter cell ─────────────────────────────────────
//
// Backend supports: productId (exact), grade (contains LIKE), minBid+maxBid
// (numeric range), updatedSince (date >=). Brand/Model filters are not wired
// to the backend today, so those headers omit a filter input rather than
// silently doing nothing.

function FilterCell({
  column,
  input,
  setFilter,
}: {
  column: ColumnDef;
  input: Filters;
  setFilter: <K extends keyof Filters>(key: K, value: string) => void;
}) {
  if (column.key === "productId") {
    return (
      <div className={styles.filterCell}>
        <span className={styles.comparatorSelect} aria-hidden="true">=</span>
        <input
          className={styles.filterInput}
          value={input.productId}
          onChange={(e) => setFilter("productId", e.target.value)}
          placeholder="Product ID"
          inputMode="numeric"
          aria-label="Filter by Product ID (exact match)"
        />
      </div>
    );
  }
  if (column.key === "grade") {
    return (
      <div className={styles.filterCell}>
        <span className={styles.comparatorSelect} aria-hidden="true">Ab</span>
        <input
          className={styles.filterInput}
          value={input.grade}
          onChange={(e) => setFilter("grade", e.target.value)}
          placeholder="Contains"
          aria-label="Filter Grade (contains)"
        />
      </div>
    );
  }
  if (column.key === "bid") {
    return (
      <div className={`${styles.filterCell} ${styles.bidRangeInputs}`}>
        <input
          className={styles.filterInput}
          value={input.minBid}
          onChange={(e) => setFilter("minBid", e.target.value)}
          placeholder="Min"
          inputMode="decimal"
          aria-label="Filter Bid minimum"
        />
        <input
          className={styles.filterInput}
          value={input.maxBid}
          onChange={(e) => setFilter("maxBid", e.target.value)}
          placeholder="Max"
          inputMode="decimal"
          aria-label="Filter Bid maximum"
        />
      </div>
    );
  }
  if (column.key === "updated") {
    return (
      <div className={styles.filterCell}>
        <span className={styles.comparatorSelect} aria-hidden="true">≥</span>
        <input
          className={styles.filterInput}
          type="date"
          value={input.updatedSince}
          onChange={(e) => setFilter("updatedSince", e.target.value)}
          aria-label="Filter Last Updated (since)"
        />
      </div>
    );
  }
  // Brand / Model / Actions — no filter input
  return null;
}

// ── Column visibility selector ─────────────────────────────────

function ColumnSelector({
  columns,
  hidden,
  onToggle,
  open,
  setOpen,
}: {
  columns: ColumnDef[];
  hidden: Set<string>;
  onToggle: (key: string) => void;
  open: boolean;
  setOpen: (v: boolean) => void;
}) {
  const wrapRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const onClickOutside = (e: MouseEvent) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) setOpen(false);
    };
    window.addEventListener("mousedown", onClickOutside);
    return () => window.removeEventListener("mousedown", onClickOutside);
  }, [open, setOpen]);

  return (
    <div className={styles.columnSelectorWrap} ref={wrapRef}>
      <button
        type="button"
        className={styles.columnSelectorButton}
        onClick={() => setOpen(!open)}
        aria-haspopup="menu"
        aria-expanded={open}
        aria-label="Toggle column visibility"
      >
        <EyeIcon /> Columns
      </button>
      {open && (
        <div className={styles.columnSelectorMenu} role="menu">
          {columns.map((c) => (
            <label key={c.key} className={styles.columnSelectorOption}>
              <input
                type="checkbox"
                checked={!hidden.has(c.key)}
                onChange={() => onToggle(c.key)}
              />
              <span>{c.label}</span>
            </label>
          ))}
        </div>
      )}
    </div>
  );
}

function EyeIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}
