"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  listPurchaseOrders,
  deletePurchaseOrder,
} from "@/lib/api/purchaseOrderClient";
import type {
  PurchaseOrderLifecycleState,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

const STATE_COLORS: Record<PurchaseOrderLifecycleState, string> = {
  DRAFT: "#a07f00",
  ACTIVE: "#176c4d",
  CLOSED: "#888",
};

export default function PurchaseOrdersPage() {
  const [rows, setRows] = useState<PurchaseOrderRow[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
    <div style={{ padding: "1.5rem" }}>
      <header style={{ display: "flex", justifyContent: "space-between",
                       alignItems: "center", marginBottom: "1rem" }}>
        <h1 style={{ margin: 0 }}>Purchase Orders</h1>
        <Link href="/admin/auctions-data-center/purchase-orders/new"
              style={{ padding: "0.5rem 1rem", background: "#407874",
                       color: "white", borderRadius: 4, textDecoration: "none" }}>
          + New PO
        </Link>
      </header>

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
                <Link href={`/admin/auctions-data-center/purchase-orders/${r.id}`}>Edit</Link>
                {" • "}
                <button onClick={() => onDelete(r.id)}
                        style={{ background: "none", color: "#c00",
                                 border: 0, cursor: "pointer" }}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <footer style={{ marginTop: "1rem" }}>
        Showing {rows.length} of {total}.
        <button onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}>Prev</button>
        <button onClick={() => setPage(p => p + 1)}
                disabled={(page + 1) * size >= total}>Next</button>
      </footer>
    </div>
  );
}
