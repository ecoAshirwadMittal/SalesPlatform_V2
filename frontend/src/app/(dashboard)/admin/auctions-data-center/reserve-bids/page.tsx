"use client";

import { useCallback, useMemo, useState } from "react";
import Link from "next/link";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";
import {
  type ColumnDef,
  type FetcherArgs,
  type FilterOp,
  DataGrid,
} from "@/components/datagrid";
import styles from "./reserveBidsList.module.css";
import ReserveBidAuditModal from "./ReserveBidAuditModal";
import ReserveBidUploadModal from "./ReserveBidUploadModal";

// Product ID is VARCHAR in the DB. Show only ops that don't trip
// SalesOps' "73 < 100" lex-vs-numeric expectations.
const PRODUCT_ID_OPS: FilterOp[] = [
  "eq", "neq", "contains", "startsWith", "endsWith", "empty", "notEmpty",
];

export default function ReserveBidsPage() {
  const [auditTarget, setAuditTarget] = useState<{ id: number; productId: string } | null>(null);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [refreshNonce, setRefreshNonce] = useState(0);

  const handleDelete = useCallback(async (id: number) => {
    if (!confirm("Delete this reserve bid? This will drop its audit trail.")) return;
    await reserveBidClient.remove(id);
    setRefreshNonce((n) => n + 1);
  }, []);

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
  // refreshNonce is a dep so deletes / uploads re-fetch even when no
  // grid state changed.
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
      filter: { kind: "text", filterKey: "productId", availableOps: PRODUCT_ID_OPS, placeholder: "Product ID" },
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
      accessor: (r) => formatDateTime(r.lastUpdateDatetime),
      filter: { kind: "date", filterKey: "lastUpdateDatetime" },
    },
  ], []);

  const rowActions = useCallback((r: ReserveBidRow) => (
    <>
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
    </>
  ), [handleDelete]);

  const topBar = (
    <>
      <button className="btn-outline" type="button" onClick={() => setUploadOpen(true)}>
        Upload EB Price
      </button>
      <button className="btn-outline" type="button" onClick={handleDownload}>
        Download
      </button>
      <Link href="/admin/auctions-data-center/reserve-bids/new">
        <button className="btn-outline" type="button">New</button>
      </Link>
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
        rowActionsLabel="Audit"
        initialSort={{ column: "product_id", direction: "asc" }}
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

function formatDateTime(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString();
}
