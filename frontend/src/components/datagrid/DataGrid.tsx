"use client";

import {
  type ReactNode,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import FilterCell from "./FilterCell";
import {
  type ColumnFilter,
  type ColumnKind,
  type FilterOp,
  DEFAULT_OP_FOR_KIND,
  serializeFilter,
} from "./filterModel";
import styles from "./datagrid.module.css";

// ── Types ──────────────────────────────────────────────────────

export type SortDirection = "asc" | "desc";

export interface SortState {
  column: string;
  direction: SortDirection;
}

export interface FilterFieldDef {
  /** ColumnFilter kind. Drives default op + ops menu + input type. */
  kind: ColumnKind;
  /** Wire-format param key passed to the fetcher. e.g. `productId`. */
  filterKey: string;
  /** Op menu override; defaults to {@link OPS_FOR_KIND}[kind]. */
  availableOps?: FilterOp[];
  /** Initial op when the page first renders. Defaults from kind. */
  initialOp?: FilterOp;
  placeholder?: string;
}

export interface ColumnDef<TRow> {
  key: string;
  label: string;
  /** Cell renderer. Receives the row; returns a ReactNode. */
  accessor: (row: TRow) => ReactNode;
  /** Optional per-column filter. Omit for non-filterable columns. */
  filter?: FilterFieldDef;
  /** SQL column name passed to the fetcher when this column is sorted.
   *  Omit for unsortable columns. */
  sortKey?: string;
  /** Right-align cell + tabular-nums. */
  numeric?: boolean;
  /** Show in the column-visibility selector. Defaults true. Set false
   *  for action columns (Audit / Edit) the user shouldn't be able to
   *  hide. */
  toggleable?: boolean;
  /** Optional fixed/min width applied via inline style. */
  width?: string;
  /** Optional extra class on the th + every td. */
  className?: string;
}

export interface FetcherArgs {
  /** Serialised filter wire-format map: `{ productId: "eq,73", grade: "contains,A_YYY" }`.
   *  Built via serializeFilter on the host's behalf — caller can hand straight
   *  to a URL-param builder. Empty filters are pre-stripped. */
  filters: Record<string, string>;
  /** Active sort, or null if no column is sorted. */
  sort: SortState | null;
  /** Zero-based page. */
  page: number;
  /** Page size. */
  size: number;
  /** Aborts when the host triggers a new fetch (e.g. user typed quickly).
   *  Pass to fetch() to cancel the in-flight request. */
  signal: AbortSignal;
}

export interface FetcherResult<TRow> {
  rows: TRow[];
  total: number;
}

export interface DataGridProps<TRow> {
  columns: ColumnDef<TRow>[];
  /** Fetches a page of rows. Receives serialised filter map + sort + page +
   *  size + an AbortSignal. The grid retries on every relevant state
   *  change and cancels stale in-flight requests. */
  fetcher: (args: FetcherArgs) => Promise<FetcherResult<TRow>>;
  /** Stable per-row key (usually `(r) => r.id`). */
  rowKey: (row: TRow) => string | number;
  /** Optional renderer for the per-row action cell. The grid synthesises
   *  a non-toggleable trailing column for these when present. */
  rowActions?: (row: TRow) => ReactNode;
  /** Action column header label (when rowActions is provided). */
  rowActionsLabel?: string;
  initialSort?: SortState | null;
  /** Per-column initial filter overrides. Useful when a page wants to
   *  pre-filter before the first render. */
  initialFilters?: Record<string, ColumnFilter>;
  pageSize?: number;
  /** Optional content rendered above the grid in a flex-end toolbar
   *  (e.g. Upload / Download / New buttons). The Columns selector is
   *  appended automatically on the right side. */
  topBarSlot?: ReactNode;
  /** Visible when zero rows match the current filters. */
  emptyMessage?: string;
  /** Apply to the outermost wrapper div. */
  className?: string;
  /** Fires when the debounced applied filter state changes. Lets pages
   *  surface the active filters externally — typical use case is wiring
   *  an Export / Download button URL that mirrors the visible grid. */
  onAppliedFiltersChange?: (wire: Record<string, string>) => void;
}

// ── Component ──────────────────────────────────────────────────

const DEFAULT_PAGE_SIZE = 20;
const FILTER_DEBOUNCE_MS = 400;

/**
 * Server-paginated, filter-row-equipped data grid. Owns:
 *
 * - Filter input state (with 400 ms debounce → applied state → fetcher)
 * - Sort column + direction (click header to toggle / cycle)
 * - Hidden columns (Columns selector dropdown)
 * - Pagination state + the visible "Showing N to M of T" footer
 *
 * Calls `fetcher` whenever any of the above change, cancelling the prior
 * in-flight request so rapid typing doesn't render stale rows.
 *
 * Visual chrome (header divider, filter cell rhythm, sort triangles,
 * paginated footer, empty state) matches QA Mendix DataGrid 2 — it's the
 * exact same chrome the Reserve Bids page shipped before this extraction.
 */
export default function DataGrid<TRow>({
  columns,
  fetcher,
  rowKey,
  rowActions,
  rowActionsLabel = "Actions",
  initialSort = null,
  initialFilters,
  pageSize = DEFAULT_PAGE_SIZE,
  topBarSlot,
  emptyMessage = "No rows match the current filters.",
  className,
  onAppliedFiltersChange,
}: DataGridProps<TRow>) {
  // Filter state — input is what the user is typing; applied is what the
  // fetcher saw last. The debounce promotes input → applied so we don't
  // hit the backend on every keystroke.
  const initialFilterState = useMemo(
    () => buildInitialFilterState(columns, initialFilters),
    // We want this to be stable for the lifetime of the component; a new
    // column list usually means a new grid instance, so this is safe.
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [],
  );
  const [input, setInput] = useState<Record<string, ColumnFilter>>(initialFilterState);
  const [applied, setApplied] = useState<Record<string, ColumnFilter>>(initialFilterState);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const [sort, setSort] = useState<SortState | null>(initialSort);
  const [page, setPage] = useState(0);

  const [hiddenColumns, setHiddenColumns] = useState<Set<string>>(new Set());
  const [columnMenuOpen, setColumnMenuOpen] = useState(false);

  const [rows, setRows] = useState<TRow[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Debounce input → applied
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

  // Fetch on applied / sort / page change
  useEffect(() => {
    const ctrl = new AbortController();
    setLoading(true);
    setError(null);
    const filterParams: Record<string, string> = {};
    for (const [filterKey, filter] of Object.entries(applied)) {
      const wire = serializeFilter(filter);
      if (wire != null) filterParams[filterKey] = wire;
    }
    onAppliedFiltersChange?.(filterParams);
    fetcher({ filters: filterParams, sort, page, size: pageSize, signal: ctrl.signal })
      .then((res) => {
        if (ctrl.signal.aborted) return;
        setRows(res.rows);
        setTotal(res.total);
      })
      .catch((e: unknown) => {
        if (ctrl.signal.aborted) return;
        setError(e instanceof Error ? e.message : "Load failed");
      })
      .finally(() => {
        if (!ctrl.signal.aborted) setLoading(false);
      });
    return () => ctrl.abort();
    // onAppliedFiltersChange intentionally omitted — it fires as a side
    // effect of state we already depend on; including it would cause a
    // re-fetch loop when the host's callback identity changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [applied, sort, page, pageSize, fetcher]);

  const updateFilter = useCallback((filterKey: string, next: ColumnFilter) => {
    setInput((prev) => ({ ...prev, [filterKey]: next }));
  }, []);

  const toggleSort = useCallback((column: string) => {
    setSort((prev) => {
      if (!prev || prev.column !== column) return { column, direction: "asc" };
      return { column, direction: prev.direction === "asc" ? "desc" : "asc" };
    });
    setPage(0);
  }, []);

  const visibleColumns = useMemo(
    () => columns.filter((c) => !hiddenColumns.has(c.key)),
    [columns, hiddenColumns],
  );
  const toggleableColumns = useMemo(
    () => columns.filter((c) => c.toggleable !== false),
    [columns],
  );

  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const startIdx = total === 0 ? 0 : page * pageSize + 1;
  const endIdx = Math.min(total, (page + 1) * pageSize);

  const colSpan = visibleColumns.length + (rowActions ? 1 : 0);

  return (
    <div className={className}>
      <div className={styles.toolbar}>
        {topBarSlot}
        <ColumnSelector
          columns={toggleableColumns}
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

      {error && <div role="alert" className={styles.errorBanner}>{error}</div>}

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              {visibleColumns.map((col) => (
                <th
                  key={col.key}
                  className={[col.numeric ? styles.numericCell : "", col.className ?? ""].join(" ").trim() || undefined}
                  scope="col"
                  style={col.width ? { width: col.width } : undefined}
                >
                  {col.sortKey ? (
                    <button
                      type="button"
                      className={styles.sortButton}
                      onClick={() => toggleSort(col.sortKey!)}
                      aria-label={`Sort by ${col.label}`}
                    >
                      <span>{col.label}</span>
                      <SortGlyph
                        active={sort?.column === col.sortKey}
                        direction={sort?.direction ?? "asc"}
                      />
                    </button>
                  ) : (
                    <span>{col.label}</span>
                  )}
                  {col.filter && input[col.filter.filterKey] && (
                    <FilterCell
                      label={col.label}
                      kind={col.filter.kind}
                      filter={input[col.filter.filterKey]}
                      onChange={(next) => updateFilter(col.filter!.filterKey, next)}
                      availableOps={col.filter.availableOps}
                      placeholder={col.filter.placeholder}
                    />
                  )}
                </th>
              ))}
              {rowActions && (
                <th scope="col">{rowActionsLabel}</th>
              )}
            </tr>
          </thead>
          <tbody>
            {!loading && rows.length === 0 && (
              <tr>
                <td colSpan={colSpan}>
                  <div className={styles.emptyState}>{emptyMessage}</div>
                </td>
              </tr>
            )}
            {rows.map((row) => (
              <tr key={rowKey(row)}>
                {visibleColumns.map((col) => (
                  <td
                    key={col.key}
                    className={[col.numeric ? styles.numericCell : "", col.className ?? ""].join(" ").trim() || undefined}
                  >
                    {col.accessor(row)}
                  </td>
                ))}
                {rowActions && (
                  <td>
                    <div className={styles.actionsCell}>{rowActions(row)}</div>
                  </td>
                )}
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
    </div>
  );
}

// ── Helpers ────────────────────────────────────────────────────

function buildInitialFilterState<TRow>(
  columns: ColumnDef<TRow>[],
  overrides: Record<string, ColumnFilter> | undefined,
): Record<string, ColumnFilter> {
  const state: Record<string, ColumnFilter> = {};
  for (const col of columns) {
    if (!col.filter) continue;
    const { filterKey, kind, initialOp } = col.filter;
    state[filterKey] = overrides?.[filterKey] ?? {
      op: initialOp ?? DEFAULT_OP_FOR_KIND[kind],
      value: "",
    };
  }
  return state;
}

function formatInt(n: number): string {
  return new Intl.NumberFormat("en-US").format(n);
}

// ── Sort glyph ─────────────────────────────────────────────────

function SortGlyph({
  active,
  direction,
}: {
  active: boolean;
  direction: SortDirection;
}) {
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

// ── Column visibility selector ─────────────────────────────────

function ColumnSelector<TRow>({
  columns,
  hidden,
  onToggle,
  open,
  setOpen,
}: {
  columns: ColumnDef<TRow>[];
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
