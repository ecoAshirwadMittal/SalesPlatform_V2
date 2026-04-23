'use client';
import { useCallback, useRef, useState } from 'react';
import { BidderModal } from './BidderModal';
import styles from './importBidsModal.module.css';
import type { BidImportResult } from '@/lib/bidder';

/**
 * ImportBidsModal — four-step instructions + file upload for bulk bid import.
 *
 * QA reference: docs/qa-reference/qa-05-import-bids-modal.png
 *
 * Modal copy is preserved verbatim from the Mendix source per Q2 decision:
 *   - Item 3 is "3.Upload your file here" — no space between "3." and "Upload"
 *     (Mendix rendering artifact, preserved intentionally).
 *
 * States:
 *   1. idle     — instructions + file picker + Import CTA
 *   2. uploading — spinner + "Uploading…" copy, all buttons disabled
 *   3. result   — success/error summary + Done CTA
 */

export interface ImportBidsModalProps {
  isOpen: boolean;
  onClose: () => void;
  /** Parent owns the network call so the modal can be tested with a mock. */
  onSubmit: (file: File) => Promise<BidImportResult>;
  /** Called when the user clicks "Done" after a completed import. */
  onComplete: () => void;
}

type ModalState = 'idle' | 'uploading' | 'result';

export function ImportBidsModal({
  isOpen,
  onClose,
  onSubmit,
  onComplete,
}: ImportBidsModalProps) {
  const [state, setState] = useState<ModalState>('idle');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [result, setResult] = useState<BidImportResult | null>(null);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0] ?? null;
      setSelectedFile(file);
    },
    [],
  );

  const handleImport = useCallback(async () => {
    if (!selectedFile || state !== 'idle') return;
    setState('uploading');
    setUploadError(null);
    try {
      const importResult = await onSubmit(selectedFile);
      setResult(importResult);
      setState('result');
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : 'Upload failed. Please try again.';
      setUploadError(message);
      setState('idle');
    }
  }, [selectedFile, state, onSubmit]);

  const handleDone = useCallback(() => {
    // Reset internal state before calling parent handlers so re-opening
    // the modal starts fresh.
    setState('idle');
    setSelectedFile(null);
    setResult(null);
    setUploadError(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    onComplete();
    onClose();
  }, [onComplete, onClose]);

  const handleClose = useCallback(() => {
    if (state === 'uploading') return; // block close while uploading
    setState('idle');
    setSelectedFile(null);
    setResult(null);
    setUploadError(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    onClose();
  }, [state, onClose]);

  if (!isOpen) return null;

  return (
    <BidderModal title="Import Your Bids" onClose={handleClose}>
      <h2 className={styles.title}>Import Your Bids</h2>

      {state !== 'result' && (
        <>
          <p className={styles.bodyHeading}>To bulk import your bids:</p>

          {/* WHY: The ordered list items are verbatim from the Mendix source.
              Item 3 uses the concatenated "3.Upload" form (no space) which
              is the Mendix rendering artifact preserved per Q2 decision. */}
          <ol className={styles.steps}>
            <li>Export your bid sheet</li>
            <li>Update your bids and qty caps in the excel sheet</li>
            <li>3.Upload your file here</li>
            <li>Please review your updated bids and quantity caps before final submission</li>
          </ol>

          <div className={styles.fileInputWrapper}>
            <input
              ref={fileInputRef}
              type="file"
              accept=".xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
              onChange={handleFileChange}
              disabled={state === 'uploading'}
              className={styles.fileInput}
              aria-label="Choose xlsx file to import"
            />
            <span className={styles.helperText}>Supported format: .xlsx</span>
          </div>

          {uploadError && (
            <p className={styles.errorText} role="alert">
              {uploadError}
            </p>
          )}

          {state === 'uploading' ? (
            <div className={styles.footer}>
              <span className={styles.uploading} aria-live="polite">
                <span className={styles.spinner} aria-hidden="true" />
                Uploading…
              </span>
            </div>
          ) : (
            <div className={styles.footer}>
              <button
                type="button"
                className={`btn-primary-green ${styles.importBtn}`}
                onClick={handleImport}
                disabled={!selectedFile}
                aria-disabled={!selectedFile}
              >
                Import
              </button>
            </div>
          )}
        </>
      )}

      {state === 'result' && result !== null && (
        <div className={styles.resultBody}>
          {result.errors.length === 0 ? (
            <p className={styles.successText}>
              Successfully updated {result.updated} bids.
            </p>
          ) : (
            <>
              <p className={styles.partialText}>
                Updated {result.updated} bids, {result.errors.length} rows had errors:
              </p>
              <ul className={styles.errorList} aria-label="Import errors">
                {result.errors.map((err) => (
                  <li key={err.row} className={styles.errorItem}>
                    <span className={styles.errorRow}>Row {err.row}:</span>{' '}
                    {err.message}
                  </li>
                ))}
              </ul>
            </>
          )}

          <div className={styles.footer}>
            <button
              type="button"
              className={`btn-primary-green ${styles.importBtn}`}
              onClick={handleDone}
            >
              Done
            </button>
          </div>
        </div>
      )}
    </BidderModal>
  );
}
