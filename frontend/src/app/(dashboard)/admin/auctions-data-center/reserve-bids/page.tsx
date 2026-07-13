"use client";

import { useCallback, useMemo, useState } from "react";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";
import { formatLegacyDateTime } from "@/lib/format/legacyDateTime";
import {
  type ColumnDef,
  type FetcherArgs,
  DataGrid,
} from "@/components/datagrid";
import styles from "./reserveBidsList.module.css";
import ReserveBidAuditModal from "./ReserveBidAuditModal";
import ReserveBidUploadModal from "./ReserveBidUploadModal";

export default function ReserveBidsPage() {
  const [auditTarget, setAuditTarget] = useState<{ id: number; productId: number } | null>(null);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [refreshNonce, setRefreshNonce] = useState(0);

  const handleDownload = useCallback(async () => {
    const blob = await reserveBidClient.download();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `reserve-bids-${new Date().toISOString().slice(0, 10)}.xlsx`;
    a.click();
    URL.revokeObjectURL(url);
  }, []);

  // The fetcher is the only page-specific concern: serialise + send.
  // refreshNonce is a dep so an upload re-fetches even when no grid state
  // changed.
  const fetcher = useCallback(
    async ({ filters, sort, page, size, signal }: FetcherArgs) => {
      // signal not threaded into reserveBidClient.list yet — left as a
      // future enhancement; abort still cancels the React state update.
      void signal;
      const params: Record<string, string | number | undefined> = {
        ...filters,
        sort: sort ? `${sort.column},${sort.direction}` : undefined,
        page,
        size,
      };
      const res = await reserveBidClient.list(params);
      return { rows: res.rows, total: res.total };
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [refreshNonce],
  );

  const columns: ColumnDef<ReserveBidRow>[] = useMemo(() => [
    {
      key: "productId",
      label: "Product ID",
      sortKey: "product_id",
      numeric: true,
      accessor: (r) => r.productId,
      // RBL-D2: product_id is BIGINT — a numeric filter (eq/neq/gt/gte/lt/lte)
      // matches the backend's now-numeric FilterColumn.PRODUCT_ID (73 < 100,
      // not lexicographic).
      filter: { kind: "numeric", filterKey: "productId", placeholder: "Product ID" },
    },
    {
      key: "grade",
      label: "Grade",
      sortKey: "grade",
      accessor: (r) => r.grade,
      filter: { kind: "text", filterKey: "grade", placeholder: "Grade" },
    },
    {
      key: "brand",
      label: "Brand",
      sortKey: "brand",
      accessor: (r) => r.brand ?? "—",
      filter: { kind: "text", filterKey: "brand", placeholder: "Brand" },
    },
    {
      key: "model",
      label: "Model Name",
      sortKey: "model",
      accessor: (r) => r.model ?? "—",
      filter: { kind: "text", filterKey: "model", placeholder: "Model Name" },
    },
    {
      key: "bid",
      label: "Bid",
      sortKey: "bid",
      numeric: true,
      accessor: (r) => `$${formatMoney(r.bid)}`,
      filter: { kind: "numeric", filterKey: "bid", placeholder: "Bid" },
    },
    {
      key: "updated",
      label: "Last Updated",
      sortKey: "last_update_datetime",
      // RBL-P2: legacy display convention MM/DD/YY at hh:mm A z (Eastern).
      accessor: (r) => formatLegacyDateTime(r.lastUpdateDatetime),
      filter: { kind: "date", filterKey: "lastUpdateDatetime" },
    },
  ], []);

  // RBL-P3: legacy ReserveBid_Overview exposes a single per-row eye that opens
  // the audit view (a Mendix popup → modal here). It has NO Edit / Delete —
  // those live on the separate Administrator-only ReserveBid_Admin_Overview
  // page (KB), which the new app does not surface. So the row carries only the
  // audit eye; the /[id] edit route is kept but no longer linked from here.
  const rowActions = useCallback((r: ReserveBidRow) => (
    <button
      type="button"
      className={styles.rowAuditButton}
      onClick={() => setAuditTarget({ id: r.id, productId: r.productId })}
      aria-label={`View audit history for product ${r.productId}`}
      title="Audit"
    >
      <EyeIcon />
    </button>
  ), []);

  // RBL-P4: legacy toolbar is [Download] [Upload EB Price], left-aligned. No
  // "New" (EB is authored via Excel upload only) and no "Columns" button — the
  // column-visibility eye lives in the grid header (variant="legacy").
  const topBar = (
    <>
      <button
        type="button"
        className={`${styles.toolbarBtn} ${styles.toolbarBtnSecondary}`}
        onClick={handleDownload}
      >
        <DownloadIcon />
        Download
      </button>
      <button
        type="button"
        className={`${styles.toolbarBtn} ${styles.toolbarBtnPrimary}`}
        onClick={() => setUploadOpen(true)}
      >
        <UploadIcon />
        Upload EB Price
      </button>
    </>
  );

  return (
    <div className={styles.page}>
      <div className={styles.headerRow}>
        <h1 className={styles.heading}>Reserve Bids (EB)</h1>
      </div>

      <DataGrid<ReserveBidRow>
        columns={columns}
        fetcher={fetcher}
        rowKey={(r) => r.id}
        rowActions={rowActions}
        variant="legacy"
        // RBL-P1: no forced default sort — the backend defaults an unsorted list
        // to legacy insertion order (legacy_id ASC), reproducing the legacy
        // grid's first screen (product 73, 76, 78, …). User-chosen sorts still win.
        initialSort={null}
        emptyMessage="No reserve bids match the current filters."
        topBarSlot={topBar}
      />

      {auditTarget && (
        <ReserveBidAuditModal
          reserveBidId={auditTarget.id}
          productId={auditTarget.productId}
          onClose={() => setAuditTarget(null)}
        />
      )}

      {uploadOpen && (
        <ReserveBidUploadModal
          onClose={() => setUploadOpen(false)}
          onUploaded={() => setRefreshNonce((n) => n + 1)}
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

// ── Icons ──────────────────────────────────────────────────────

function EyeIcon() {
  return (
    <svg className={styles.rowAuditIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function DownloadIcon() {
  return (
    <svg className={styles.toolbarBtnIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
      <polyline points="7 10 12 15 17 10" />
      <line x1="12" y1="15" x2="12" y2="3" />
    </svg>
  );
}

function UploadIcon() {
  return (
    <svg className={styles.toolbarBtnIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
      <polyline points="17 8 12 3 7 8" />
      <line x1="12" y1="3" x2="12" y2="15" />
    </svg>
  );
}
