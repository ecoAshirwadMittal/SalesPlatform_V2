"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";

export default function ReserveBidsPage() {
  const [rows, setRows] = useState<ReserveBidRow[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [productIdFilter, setProductIdFilter] = useState("");
  const [gradeFilter, setGradeFilter] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await reserveBidClient.list({
        productId: productIdFilter || undefined,
        grade: gradeFilter || undefined,
        page,
        size: 20,
      });
      setRows(res.rows);
      setTotal(res.total);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Load failed");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [page, productIdFilter, gradeFilter]);

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this reserve bid? This will drop its audit trail.")) return;
    try {
      await reserveBidClient.remove(id);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Delete failed");
    }
  };

  const handleDownload = async () => {
    const blob = await reserveBidClient.download();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `reserve-bids-${new Date().toISOString().slice(0, 10)}.xlsx`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div style={{ padding: "1.5rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1>Reserve Bids (EB)</h1>
        <div style={{ display: "flex", gap: "0.5rem" }}>
          <Link href="/admin/auctions-data-center/reserve-bids/upload">
            <button>Upload Excel</button>
          </Link>
          <button onClick={handleDownload}>Download Excel</button>
          <Link href="/admin/auctions-data-center/reserve-bids/new">
            <button>New</button>
          </Link>
        </div>
      </div>

      <div style={{ display: "flex", gap: "0.5rem", margin: "1rem 0" }}>
        <input placeholder="Filter productId..." value={productIdFilter}
               onChange={(e) => { setProductIdFilter(e.target.value); setPage(0); }} />
        <input placeholder="Filter grade contains..." value={gradeFilter}
               onChange={(e) => { setGradeFilter(e.target.value); setPage(0); }} />
      </div>

      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>Product ID</th><th>Grade</th><th>Brand</th><th>Model</th>
            <th>Bid</th><th>Last Updated</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td>{r.productId}</td><td>{r.grade}</td>
              <td>{r.brand ?? "—"}</td><td>{r.model ?? "—"}</td>
              <td>{r.bid}</td><td>{new Date(r.lastUpdateDatetime).toLocaleString()}</td>
              <td>
                <Link href={`/admin/auctions-data-center/reserve-bids/${r.id}`}>Edit</Link>{" "}
                <Link href={`/admin/auctions-data-center/reserve-bids/${r.id}/audit`}>Audit</Link>{" "}
                <button onClick={() => handleDelete(r.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem", alignItems: "center" }}>
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>Prev</button>
        <span>Page {page + 1} of {Math.max(1, Math.ceil(total / 20))}</span>
        <button disabled={(page + 1) * 20 >= total} onClick={() => setPage(page + 1)}>Next</button>
      </div>
    </div>
  );
}
