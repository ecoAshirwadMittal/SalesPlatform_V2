"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { createPurchaseOrder } from "@/lib/api/purchaseOrderClient";

export default function NewPurchaseOrderPage() {
  const router = useRouter();
  const [weekFromId, setWeekFromId] = useState<number | "">("");
  const [weekToId, setWeekToId] = useState<number | "">("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (typeof weekFromId !== "number" || typeof weekToId !== "number") {
      setError("Both week_from and week_to are required.");
      return;
    }
    setSubmitting(true);
    try {
      const po = await createPurchaseOrder({ weekFromId, weekToId });
      router.push(`/admin/auctions-data-center/purchase-orders/${po.id}`);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div style={{ padding: "1.5rem", maxWidth: 600 }}>
      <h1>New Purchase Order</h1>
      <form onSubmit={onSubmit}>
        <label style={{ display: "block", marginBottom: "1rem" }}>
          Week from (mdm.week.id)
          <input type="number" value={weekFromId}
                 onChange={e => setWeekFromId(Number(e.target.value))}
                 required style={{ width: "100%" }} />
        </label>
        <label style={{ display: "block", marginBottom: "1rem" }}>
          Week to (mdm.week.id)
          <input type="number" value={weekToId}
                 onChange={e => setWeekToId(Number(e.target.value))}
                 required style={{ width: "100%" }} />
        </label>
        {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
        <button type="submit" disabled={submitting}
                style={{ padding: "0.5rem 1rem", background: "#407874",
                         color: "white", border: 0, borderRadius: 4 }}>
          {submitting ? "Saving…" : "Create"}
        </button>
      </form>
    </div>
  );
}
