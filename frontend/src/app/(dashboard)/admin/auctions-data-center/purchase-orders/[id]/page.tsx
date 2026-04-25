"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import {
  downloadPoDetailsUrl,
  getPurchaseOrder,
  listPoDetails,
  updatePurchaseOrder,
  uploadPoDetails,
} from "@/lib/api/purchaseOrderClient";
import type {
  PODetailRow,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

export default function EditPurchaseOrderPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [po, setPo] = useState<PurchaseOrderRow | null>(null);
  const [details, setDetails] = useState<PODetailRow[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);

  async function reload() {
    try {
      const [p, d] = await Promise.all([getPurchaseOrder(id), listPoDetails(id, 0, 200)]);
      setPo(p);
      setDetails(d.items);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  useEffect(() => { reload(); }, [id]);

  async function onUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const result = await uploadPoDetails(id, file);
      alert(`Wipe-and-replace: deleted ${result.deletedCount}, `
          + `created ${result.createdCount}, skipped ${result.skippedCount}.`);
      reload();
    } catch (e) {
      alert("Upload failed: " + (e as Error).message);
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  }

  async function onSaveHeader(e: React.FormEvent) {
    e.preventDefault();
    if (!po) return;
    try {
      await updatePurchaseOrder(id,
              { weekFromId: po.weekFromId, weekToId: po.weekToId });
      reload();
    } catch (e) {
      alert("Save failed: " + (e as Error).message);
    }
  }

  if (!po) return <div>Loading…</div>;

  return (
    <div style={{ padding: "1.5rem" }}>
      <h1>PO #{po.id} — {po.weekRangeLabel}</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}

      <form onSubmit={onSaveHeader} style={{ marginBottom: "1.5rem" }}>
        <label>Week from id <input type="number" value={po.weekFromId}
              onChange={e => setPo({ ...po, weekFromId: Number(e.target.value) })} /></label>
        {" "}
        <label>Week to id <input type="number" value={po.weekToId}
              onChange={e => setPo({ ...po, weekToId: Number(e.target.value) })} /></label>
        {" "}
        <button type="submit">Save header</button>
      </form>

      <section style={{ marginBottom: "1.5rem" }}>
        <h2>Details ({details.length})</h2>
        <input type="file" accept=".xlsx" onChange={onUpload} disabled={uploading} />
        <a href={downloadPoDetailsUrl(id)} style={{ marginLeft: "1rem" }}>
          Download Excel
        </a>
      </section>

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.9rem" }}>
        <thead>
          <tr style={{ background: "#F7F7F7" }}>
            <th>Product</th><th>Grade</th><th>Model</th>
            <th>Price</th><th>QtyCap</th><th>Buyer</th>
          </tr>
        </thead>
        <tbody>
          {details.map(d => (
            <tr key={d.id}>
              <td>{d.productId}</td><td>{d.grade}</td>
              <td>{d.modelName}</td><td>{d.price}</td>
              <td>{d.qtyCap ?? "—"}</td><td>{d.buyerCode}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
