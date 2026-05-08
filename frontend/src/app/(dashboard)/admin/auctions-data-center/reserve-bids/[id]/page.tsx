"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidRow } from "@/lib/reserveBidTypes";
import styles from "../reserveBidForm.module.css";

export default function EditReserveBidPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const id = Number(params.id);

  const [loaded, setLoaded] = useState<ReserveBidRow | null>(null);
  const [productId, setProductId] = useState("");
  const [grade, setGrade] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [bid, setBid] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    reserveBidClient
      .get(id)
      .then((r) => {
        setLoaded(r);
        setProductId(r.productId);
        setGrade(r.grade);
        setBrand(r.brand ?? "");
        setModel(r.model ?? "");
        setBid(r.bid);
      })
      .catch((e: unknown) => {
        setError(e instanceof Error ? e.message : "Failed to load reserve bid");
      });
  }, [id]);

  const save = async () => {
    if (!loaded) return;
    setError(null);
    setSubmitting(true);
    try {
      await reserveBidClient.update(id, {
        productId,
        grade,
        brand,
        model,
        bid,
        // Preserve fields not exposed in the form so the PUT round-trips cleanly.
        lastAwardedMinPrice: loaded.lastAwardedMinPrice,
        lastAwardedWeek: loaded.lastAwardedWeek,
        bidValidWeekDate: loaded.bidValidWeekDate,
      });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Save failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>
        {loaded ? `Edit Reserve Bid — Product #${loaded.productId}` : "Edit Reserve Bid — Loading…"}
      </h1>
      <div
        style={{
          color: "var(--color-text-muted)",
          fontSize: "14px",
          marginTop: "-12px",
          marginBottom: "16px",
        }}
      >
        Internal id: {id}
      </div>

      {error && (
        <div role="alert" className={styles.errorAlert}>
          {error}
        </div>
      )}

      {!loaded ? (
        <div>Loading…</div>
      ) : (
        <>
          <div className={styles.field}>
            <label htmlFor="rb-productId">Product ID</label>
            <input
              id="rb-productId"
              value={productId}
              onChange={(e) => setProductId(e.target.value)}
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="rb-grade">Grade</label>
            <input
              id="rb-grade"
              value={grade}
              onChange={(e) => setGrade(e.target.value)}
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="rb-brand">Brand</label>
            <input
              id="rb-brand"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="rb-model">Model</label>
            <input
              id="rb-model"
              value={model}
              onChange={(e) => setModel(e.target.value)}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="rb-bid">Bid</label>
            <input
              id="rb-bid"
              type="number"
              step="0.01"
              value={bid}
              onChange={(e) => setBid(e.target.value)}
              required
            />
          </div>
          <div className={styles.actions}>
            <button
              className="btn-primary-green"
              onClick={save}
              disabled={submitting}
            >
              {submitting ? "Saving…" : "Save"}
            </button>
            <button
              className="btn-outline"
              onClick={() => router.back()}
              disabled={submitting}
            >
              Cancel
            </button>
          </div>
        </>
      )}
    </div>
  );
}
