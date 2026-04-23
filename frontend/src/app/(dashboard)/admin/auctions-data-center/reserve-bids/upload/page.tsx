"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidUploadResult } from "@/lib/reserveBidTypes";

export default function UploadReserveBidsPage() {
  const router = useRouter();
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<ReserveBidUploadResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const onUpload = async () => {
    if (!file) return;
    try {
      const r = await reserveBidClient.upload(file);
      setResult(r);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Upload failed");
    }
  };

  return (
    <div style={{ padding: "1.5rem", maxWidth: 720 }}>
      <h1>Upload Reserve Bids</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div>
        <input type="file" accept=".xlsx" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
        <button onClick={onUpload} disabled={!file}>Upload</button>
        <button onClick={() => router.push("/admin/auctions-data-center/reserve-bids")}>Back</button>
      </div>
      {result && (
        <div style={{ marginTop: "1rem" }}>
          <p>Created: {result.created}, Updated: {result.updated}, Unchanged: {result.unchanged},
             Audits: {result.auditsGenerated}</p>
          {result.errors.length > 0 && (
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr><th>Row</th><th>Product</th><th>Grade</th><th>Reason</th></tr>
              </thead>
              <tbody>
                {result.errors.map((e, i) => (
                  <tr key={i}>
                    <td>{e.rowNumber}</td><td>{e.productId ?? "—"}</td>
                    <td>{e.grade ?? "—"}</td><td>{e.reason}</td>
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
