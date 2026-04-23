"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";

export default function EditReserveBidPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const id = Number(params.id);
  const [row, setRow] = useState<ReserveBidRow | null>(null);
  const [bid, setBid] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    reserveBidClient.get(id).then((r) => {
      setRow(r);
      setBid(r.bid);
      setBrand(r.brand ?? "");
      setModel(r.model ?? "");
    }).catch((e) => setError(e.message));
  }, [id]);

  const save = async () => {
    if (!row) return;
    try {
      await reserveBidClient.update(id, {
        productId: row.productId,
        grade: row.grade,
        brand, model, bid,
        lastAwardedMinPrice: row.lastAwardedMinPrice,
        lastAwardedWeek: row.lastAwardedWeek,
        bidValidWeekDate: row.bidValidWeekDate,
      });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Save failed");
    }
  };

  if (!row) return <div>Loading...</div>;

  return (
    <div style={{ padding: "1.5rem", maxWidth: 520 }}>
      <h1>Edit Reserve Bid #{row.id}</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div><label>Product ID: <input value={row.productId} disabled /></label></div>
      <div><label>Grade: <input value={row.grade} disabled /></label></div>
      <div><label>Brand: <input value={brand} onChange={(e) => setBrand(e.target.value)} /></label></div>
      <div><label>Model: <input value={model} onChange={(e) => setModel(e.target.value)} /></label></div>
      <div><label>Bid: <input type="number" step="0.01" value={bid} onChange={(e) => setBid(e.target.value)} /></label></div>
      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem" }}>
        <button onClick={save}>Save</button>
        <button onClick={() => router.back()}>Cancel</button>
      </div>
    </div>
  );
}
