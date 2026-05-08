"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import styles from "../reserveBidForm.module.css";

export default function NewReserveBidPage() {
  const router = useRouter();
  const [productId, setProductId] = useState("");
  const [grade, setGrade] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [bid, setBid] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const create = async () => {
    setError(null);
    setSubmitting(true);
    try {
      await reserveBidClient.create({ productId, grade, brand, model, bid });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Create failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>New Reserve Bid</h1>
      {error && <div role="alert" className={styles.errorAlert}>{error}</div>}
      <div className={styles.field}>
        <label htmlFor="rb-productId">Product ID</label>
        <input id="rb-productId" value={productId} onChange={(e) => setProductId(e.target.value)} required />
      </div>
      <div className={styles.field}>
        <label htmlFor="rb-grade">Grade</label>
        <input id="rb-grade" value={grade} onChange={(e) => setGrade(e.target.value)} required />
      </div>
      <div className={styles.field}>
        <label htmlFor="rb-brand">Brand</label>
        <input id="rb-brand" value={brand} onChange={(e) => setBrand(e.target.value)} />
      </div>
      <div className={styles.field}>
        <label htmlFor="rb-model">Model</label>
        <input id="rb-model" value={model} onChange={(e) => setModel(e.target.value)} />
      </div>
      <div className={styles.field}>
        <label htmlFor="rb-bid">Bid</label>
        <input id="rb-bid" type="number" step="0.01" value={bid} onChange={(e) => setBid(e.target.value)} required />
      </div>
      <div className={styles.actions}>
        <button className="btn-primary-green" onClick={create} disabled={submitting}>
          {submitting ? "Creating…" : "Create"}
        </button>
        <button className="btn-outline" onClick={() => router.back()} disabled={submitting}>
          Cancel
        </button>
      </div>
    </div>
  );
}
