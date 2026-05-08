"use client";

import { useState } from "react";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidUploadResult } from "@/lib/reserveBidTypes";
import styles from "./reserveBidForm.module.css";

const TEMPLATE_PATH = "/api/v1/admin/reserve-bids/download";

interface Props {
  /** Called after a successful upload so the host (route or modal) can refresh
   * its data view or close the dialog. Result is forwarded so the host can
   * surface stats if the form's own panel is hidden. */
  onUploaded?: (result: ReserveBidUploadResult) => void;
  /** Optional secondary action label + handler. Route mode passes "Back"
   * routing to the list; modal mode passes "Close" closing the dialog. */
  secondaryLabel?: string;
  onSecondary?: () => void;
}

/**
 * Reusable upload form. Extracted so the legacy /upload route and the
 * list-page modal share the same UX. Owns its own pending/result/error state;
 * notifies the host on success via onUploaded so the host can decide what to
 * do next (route → "Back to list" link, modal → close + refresh).
 */
export default function ReserveBidUploadForm({
  onUploaded,
  secondaryLabel = "Cancel",
  onSecondary,
}: Props) {
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
      onUploaded?.(r);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Upload failed");
    } finally {
      setSubmitting(false);
    }
  };

  const uploadDisabled = !file || submitting;

  return (
    <>
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
        {onSecondary && (
          <button
            type="button"
            className="btn-outline"
            onClick={onSecondary}
            disabled={submitting}
          >
            {secondaryLabel}
          </button>
        )}
      </div>

      {result && (
        <section className={styles.resultPanel} aria-live="polite">
          <h2>Upload complete</h2>
          <ul className={styles.resultStats}>
            <li><strong>Created:</strong> {result.created}</li>
            <li><strong>Updated:</strong> {result.updated}</li>
            <li><strong>Unchanged:</strong> {result.unchanged}</li>
            <li><strong>Audits generated:</strong> {result.auditsGenerated}</li>
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
        </section>
      )}
    </>
  );
}
