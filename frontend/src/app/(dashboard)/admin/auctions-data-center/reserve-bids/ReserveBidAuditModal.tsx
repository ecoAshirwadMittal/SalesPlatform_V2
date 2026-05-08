"use client";

import { useEffect, useState } from "react";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidAuditRow } from "@/lib/reserveBidTypes";
import styles from "./reserveBidsList.module.css";

interface Props {
  reserveBidId: number;
  productId: string;
  onClose: () => void;
}

/**
 * Audit modal — replaces the legacy /[id]/audit route. QA presents this as a
 * dialog overlaying the list so the grid stays in context.
 *
 * Columns mirror QA: Old price | New price | Changed On | Changed By. The
 * redundant Product/Grade columns the dev-scaffold added are dropped — when the
 * modal is scoped to one row, they would repeat the same value on every line.
 */
export default function ReserveBidAuditModal({ reserveBidId, productId, onClose }: Props) {
  const [rows, setRows] = useState<ReserveBidAuditRow[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    reserveBidClient
      .audit(reserveBidId, 0, 100)
      .then((r) => {
        if (cancelled) return;
        setRows(r.rows);
        setTotal(r.total);
      })
      .catch((e: unknown) => {
        if (cancelled) return;
        setError(e instanceof Error ? e.message : "Failed to load audit history");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, [reserveBidId]);

  // Close on Escape — matches the QA modal affordance.
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") onClose(); };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="rb-audit-title"
      onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div className={styles.modalSurface}>
        <div className={styles.modalHeader}>
          <h2 id="rb-audit-title" className={styles.modalTitle}>
            Audit — Product #{productId}
          </h2>
          <button
            type="button"
            className={styles.modalClose}
            aria-label="Close audit dialog"
            onClick={onClose}
          >
            ×
          </button>
        </div>
        <div className={styles.modalBody}>
          {loading && <div>Loading…</div>}
          {error && <div role="alert" className={styles.errorAlert}>{error}</div>}
          {!loading && !error && rows.length === 0 && (
            <div className={styles.emptyState}>No price changes recorded for this reserve bid.</div>
          )}
          {!loading && !error && rows.length > 0 && (
            <table className={styles.auditTable}>
              <thead>
                <tr>
                  <th className={styles.numericCell}>Old price</th>
                  <th className={styles.numericCell}>New price</th>
                  <th>Changed On</th>
                  <th>Changed By</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.id}>
                    <td className={styles.numericCell}>{formatMoney(r.oldPrice)}</td>
                    <td className={styles.numericCell}>{formatMoney(r.newPrice)}</td>
                    <td>{new Date(r.createdDate).toLocaleString()}</td>
                    <td>{r.changedByUsername ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
        {!loading && !error && rows.length > 0 && (
          <div className={styles.modalFooter}>
            Showing {rows.length} of {total} change{total === 1 ? "" : "s"}
          </div>
        )}
      </div>
    </div>
  );
}

function formatMoney(value: string): string {
  const n = Number(value);
  if (Number.isNaN(n)) return value;
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  }).format(n);
}
