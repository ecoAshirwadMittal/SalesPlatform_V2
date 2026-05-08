"use client";

import { useEffect, useState } from "react";
import type { ReserveBidUploadResult } from "@/lib/reserveBidTypes";
import ReserveBidUploadForm from "./ReserveBidUploadForm";
import styles from "./reserveBidsList.module.css";

interface Props {
  onClose: () => void;
  /** Fires after a successful upload so the host can refresh the list. */
  onUploaded?: (result: ReserveBidUploadResult) => void;
}

/**
 * Upload modal — replaces the navigate-to-/upload route per RB-18 in the
 * QA-vs-local audit. QA presents this as a dialog overlaying the list so
 * the grid stays in context after the upload completes.
 *
 * The /upload route is kept as a fallback deep-link surface; both render
 * the same ReserveBidUploadForm body.
 */
export default function ReserveBidUploadModal({ onClose, onUploaded }: Props) {
  // Track whether at least one upload has succeeded so the secondary button
  // copy can flip from "Cancel" (no destructive close) to "Close" (work
  // already saved — user is just dismissing).
  const [didUpload, setDidUpload] = useState(false);

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
      aria-labelledby="rb-upload-title"
      onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div className={styles.modalSurface}>
        <div className={styles.modalHeader}>
          <h2 id="rb-upload-title" className={styles.modalTitle}>
            Upload EB Price
          </h2>
          <button
            type="button"
            className={styles.modalClose}
            aria-label="Close upload dialog"
            onClick={onClose}
          >
            ×
          </button>
        </div>
        <div className={styles.modalBody}>
          <ReserveBidUploadForm
            onUploaded={(r) => {
              setDidUpload(true);
              onUploaded?.(r);
            }}
            secondaryLabel={didUpload ? "Close" : "Cancel"}
            onSecondary={onClose}
          />
        </div>
      </div>
    </div>
  );
}
