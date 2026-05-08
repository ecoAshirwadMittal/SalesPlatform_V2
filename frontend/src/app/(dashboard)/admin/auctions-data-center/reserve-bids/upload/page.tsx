"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidUploadResult } from "@/lib/reserveBidTypes";
import styles from "../reserveBidForm.module.css";

const LIST_PATH = "/admin/auctions-data-center/reserve-bids";
const TEMPLATE_PATH = "/api/v1/admin/reserve-bids/download";

export default function UploadReserveBidsPage() {
  const router = useRouter();
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<ReserveBidUploadResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const onUpload = async () => {
    if (!file) return;
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const r = await reserveBidClient.upload(file);
      setResult(r);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Upload failed");
    } finally {
      setSubmitting(false);
    }
  };

  const uploadDisabled = !file || submitting;

  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>Upload Reserve Bids</h1>

      {error && (
        <div role="alert" className={styles.errorAlert}>
          {error}
        </div>
      )}

      <div className={styles.field}>
        <label htmlFor="rb-upload-file">Excel file (.xlsx)</label>
        <input
          id="rb-upload-file"
          type="file"
          accept=".xlsx,.xls"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          disabled={submitting}
        />
        <a className={styles.templateLink} href={TEMPLATE_PATH}>
          Download Excel template
        </a>
      </div>

      <div className={styles.actions}>
        <button
          type="button"
          className="btn-primary-green"
          onClick={onUpload}
          disabled={uploadDisabled}
          aria-disabled={uploadDisabled}
        >
          {submitting ? "Uploading…" : "Upload"}
        </button>
        <button
          type="button"
          className="btn-outline"
          onClick={() => router.push(LIST_PATH)}
          disabled={submitting}
        >
          Back
        </button>
      </div>

      {result && (
        <section className={styles.resultPanel} aria-live="polite">
          <h2>Upload complete</h2>
          <ul className={styles.resultStats}>
            <li>
              <strong>Created:</strong> {result.created}
            </li>
            <li>
              <strong>Updated:</strong> {result.updated}
            </li>
            <li>
              <strong>Unchanged:</strong> {result.unchanged}
            </li>
            <li>
              <strong>Audits generated:</strong> {result.auditsGenerated}
            </li>
          </ul>

          {result.errors.length > 0 && (
            <>
              <p className={styles.errorListHeading}>
                Errors ({result.errors.length})
              </p>
              <ul className={styles.errorList}>
                {result.errors.map((e, i) => (
                  <li key={i}>
                    Row {e.rowNumber}
                    {e.productId ? ` — ${e.productId}` : ""}
                    {e.grade ? ` / ${e.grade}` : ""}: {e.reason}
                  </li>
                ))}
              </ul>
            </>
          )}

          <div className={styles.resultActions}>
            <button
              type="button"
              className="btn-outline"
              onClick={() => router.push(LIST_PATH)}
            >
              Back to list
            </button>
          </div>
        </section>
      )}
    </div>
  );
}
