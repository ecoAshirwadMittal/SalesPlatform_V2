'use client';

import { useState } from 'react';
import {
  FileDropError,
  parseBarcodeBlob,
  parseBarcodesFromFile,
} from '@/lib/partialCreditClient';
import styles from '../../wizard.module.css';

interface BarcodeEntryCardProps {
  /** Current textarea value (raw blob, comma/newline separated). */
  value: string;
  /** Called with the merged blob whenever the textarea or file-drop updates. */
  onChange: (next: string) => void;
  /** Optional override copy. Defaults to the canonical Figma subtitle. */
  subtitle?: string;
  /** Unique id for the textarea — needed when multiple cards co-exist. */
  textareaId?: string;
  /** Inline validation error (red) rendered under the card. */
  errorText?: string | null;
  /** Server reconciliation banner (neutral warning). */
  reconciliationBanner?: string | null;
}

/**
 * Shared barcode-entry card used by all 3 reason steps
 * (Missing / Wrong / Encumbered). Renders the Figma canonical layout:
 *
 *   subtitle → textarea → OR-divider → file dropzone
 *
 * File uploads are POSTed to `parseBarcodesFromFile()` (Sprint 4 chunk 8)
 * and the returned barcodes are merged with whatever the user has already
 * typed via a Set so re-uploading the same file is idempotent.
 */
export function BarcodeEntryCard({
  value,
  onChange,
  subtitle = 'Copy and paste the barcodes into the text field below or upload a file listing the barcodes.',
  textareaId = 'barcode-entry',
  errorText = null,
  reconciliationBanner = null,
}: BarcodeEntryCardProps) {
  const [warnings, setWarnings] = useState<string[]>([]);
  const [fileError, setFileError] = useState<string | null>(null);
  const [parsing, setParsing] = useState(false);

  async function handleFile(file: File | null): Promise<void> {
    if (!file) return;
    setFileError(null);
    setWarnings([]);
    setParsing(true);
    try {
      const parsed = await parseBarcodesFromFile(file);
      // Merge with whatever the buyer already typed — dedupe via a Set so
      // re-uploading the same file is idempotent. Order: existing entries
      // first, new ones appended after.
      const existing = parseBarcodeBlob(value);
      const merged = new Set<string>([...existing, ...parsed.barcodes]);
      onChange(Array.from(merged).join(', '));
      setWarnings(parsed.warnings);
    } catch (e) {
      if (e instanceof FileDropError) {
        setFileError(e.message);
      } else {
        setFileError(e instanceof Error ? e.message : 'Failed to parse file');
      }
    } finally {
      setParsing(false);
    }
  }

  return (
    <div className={styles.card}>
      <p className={styles.cardSubheading}>{subtitle}</p>

      {reconciliationBanner && (
        <div className={styles.warningBanner}>{reconciliationBanner}</div>
      )}

      {/* sr-only label for a11y — Figma uses the subtitle as the only
          visible label above the textarea (no explicit "Barcodes" label). */}
      <label htmlFor={textareaId} className={styles.srOnly}>
        Barcodes
      </label>
      <textarea
        id={textareaId}
        className={styles.textarea}
        placeholder="Enter barcodes"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />

      <div className={styles.orDivider} aria-hidden="true">
        <span className={styles.orDividerLine} />
        <span className={styles.orDividerLabel}>OR</span>
        <span className={styles.orDividerLine} />
      </div>

      <label className={styles.fileDropzone}>
        <span className={styles.fileDropzoneTitle}>
          {parsing ? 'Parsing…' : 'Click or drag and drop file here to upload'}
        </span>
        <span className={styles.fileDropzoneHelper}>
          Accepted formats: .xlsx, .csv, .docx
        </span>
        <input
          type="file"
          className={styles.fileDropzoneInput}
          accept=".xlsx,.csv,.docx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
          disabled={parsing}
          aria-label="Upload barcodes file"
          onChange={(e) => handleFile(e.target.files?.[0] ?? null)}
        />
      </label>

      {warnings.length > 0 && (
        <ul className={styles.fileDropWarnings} role="status">
          {warnings.map((w, i) => (
            <li key={i}>{w}</li>
          ))}
        </ul>
      )}

      {fileError && (
        <div className={styles.errorBanner} role="alert">
          {fileError}
        </div>
      )}

      {errorText && <p className={styles.errorText}>{errorText}</p>}
    </div>
  );
}
