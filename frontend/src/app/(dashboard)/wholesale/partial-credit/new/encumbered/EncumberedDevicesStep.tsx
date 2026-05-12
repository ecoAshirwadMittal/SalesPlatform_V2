'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import {
  type CreditRequestDetail,
  getRequest,
  parseBarcodeBlob,
  setEncumberedLines,
} from '@/lib/partialCreditClient';
import { StepIndicator } from '../../StepIndicator';
import styles from '../../wizard.module.css';

/**
 * Wizard Step 4 — Encumbered Devices. Figma frame "Encumbered Device" →
 * "Barcodes Entered" → "Summary". Mirrors Missing Devices' barcode-entry
 * shape (no damage Q&A on this step).
 */
export function EncumberedDevicesStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reconciliationBanner, setReconciliationBanner] = useState<string | null>(null);
  const [blob, setBlob] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(id) || id <= 0) {
      router.replace('/wholesale/partial-credit/new');
      return;
    }
    getRequest(id)
      .then((d) => {
        setDetail(d);
        setBlob(d.encumberedLines.map((l) => l.barcodeSubmitted).join(', '));
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [id, router]);

  const barcodes = useMemo(() => parseBarcodeBlob(blob), [blob]);
  const canNext = barcodes.length > 0;

  async function onNext() {
    if (!detail || !canNext) return;
    setSubmitting(true);
    setError(null);
    try {
      const response = await setEncumberedLines(detail.id, barcodes);
      // Surface the reconciliation banner so the buyer sees when the
      // server dropped duplicates or non-manifest barcodes (Figma
      // "Removed N duplicate and M not in order").
      if (response.reconciliation.banner) {
        setReconciliationBanner(response.reconciliation.banner);
      }
      router.push(`/wholesale/partial-credit/new/summary?id=${detail.id}`);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to save');
      setSubmitting(false);
    }
  }

  if (!detail) {
    return <div className={styles.page}>{error ?? 'Loading…'}</div>;
  }

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; Encumbered
        Devices
      </div>
      <h1 className={styles.heading}>Which devices are encumbered?</h1>

      <StepIndicator
        current="encumbered"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      <div className={styles.card}>
        <p className={styles.cardSubheading}>
          Copy and paste the barcodes into the text field below.
        </p>
        {reconciliationBanner && (
          <div className={styles.warningBanner}>{reconciliationBanner}</div>
        )}
        <label className={styles.fieldLabel} htmlFor="encumbered-barcodes">
          Barcodes
        </label>
        <textarea
          id="encumbered-barcodes"
          className={styles.textarea}
          placeholder="Enter barcodes (one per line or comma-separated)"
          value={blob}
          onChange={(e) => setBlob(e.target.value)}
        />
        <p className={styles.helperText}>{barcodes.length} barcode(s) entered</p>
      </div>

      {error && <div className={styles.warningBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button type="button" className={styles.buttonSecondary} onClick={() => router.back()}>
          Back
        </button>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={onNext}
          disabled={!canNext || submitting}
        >
          {submitting ? 'Saving…' : 'Next'}
        </button>
      </div>
    </div>
  );
}
