"use client";

import { useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { uploadPoDetails } from "@/lib/api/purchaseOrderClient";
import type { PODetailUploadResult } from "@/lib/types/purchaseOrder";

export default function UploadPurchaseOrderDetailsPage() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<PODetailUploadResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);

  const onUpload = async () => {
    if (!file) return;
    setUploading(true);
    setError(null);
    try {
      const r = await uploadPoDetails(id, file);
      setResult(r);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={{ padding: "1.5rem", maxWidth: 720 }}>
      <h1>Upload PO #{id} Details</h1>
      <p style={{ color: "#666", fontSize: "0.9rem" }}>
        Wipe-and-replace import. All existing details for this PO will be deleted
        before the new rows are inserted.
      </p>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div style={{ marginTop: "1rem" }}>
        <input
          type="file"
          accept=".xlsx"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          disabled={uploading}
        />
        <button
          onClick={onUpload}
          disabled={!file || uploading}
          style={{ marginLeft: "0.5rem" }}
        >
          {uploading ? "Uploading…" : "Upload"}
        </button>
        <button
          onClick={() => router.push(`/admin/auctions-data-center/purchase-orders/${id}`)}
          style={{ marginLeft: "0.5rem" }}
          disabled={uploading}
        >
          Back
        </button>
      </div>
      {result && (
        <div style={{ marginTop: "1rem" }}>
          <p>
            Deleted: {result.deletedCount}, Created: {result.createdCount},
            Skipped: {result.skippedCount}
          </p>
          {result.errors.length > 0 && (
            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.9rem" }}>
              <thead>
                <tr style={{ background: "#F7F7F7" }}>
                  <th style={{ textAlign: "left", padding: "0.25rem" }}>Row</th>
                  <th style={{ textAlign: "left", padding: "0.25rem" }}>Product</th>
                  <th style={{ textAlign: "left", padding: "0.25rem" }}>Grade</th>
                  <th style={{ textAlign: "left", padding: "0.25rem" }}>Buyer</th>
                  <th style={{ textAlign: "left", padding: "0.25rem" }}>Reason</th>
                </tr>
              </thead>
              <tbody>
                {result.errors.map((e, i) => (
                  <tr key={i} style={{ borderTop: "1px solid #eee" }}>
                    <td style={{ padding: "0.25rem" }}>{e.rowNumber}</td>
                    <td style={{ padding: "0.25rem" }}>{e.productId || "—"}</td>
                    <td style={{ padding: "0.25rem" }}>{e.grade || "—"}</td>
                    <td style={{ padding: "0.25rem" }}>{e.buyerCode || "—"}</td>
                    <td style={{ padding: "0.25rem" }}>{e.reason}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
}
