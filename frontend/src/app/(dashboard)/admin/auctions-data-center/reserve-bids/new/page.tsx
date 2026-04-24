"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";

export default function NewReserveBidPage() {
  const router = useRouter();
  const [productId, setProductId] = useState("");
  const [grade, setGrade] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [bid, setBid] = useState("");
  const [error, setError] = useState<string | null>(null);

  const create = async () => {
    try {
      await reserveBidClient.create({ productId, grade, brand, model, bid });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Create failed");
    }
  };

  return (
    <div style={{ padding: "1.5rem", maxWidth: 520 }}>
      <h1>New Reserve Bid</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div><label>Product ID: <input value={productId} onChange={(e) => setProductId(e.target.value)} required /></label></div>
      <div><label>Grade: <input value={grade} onChange={(e) => setGrade(e.target.value)} required /></label></div>
      <div><label>Brand: <input value={brand} onChange={(e) => setBrand(e.target.value)} /></label></div>
      <div><label>Model: <input value={model} onChange={(e) => setModel(e.target.value)} /></label></div>
      <div><label>Bid: <input type="number" step="0.01" value={bid} onChange={(e) => setBid(e.target.value)} required /></label></div>
      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem" }}>
        <button onClick={create}>Create</button>
        <button onClick={() => router.back()}>Cancel</button>
      </div>
    </div>
  );
}
