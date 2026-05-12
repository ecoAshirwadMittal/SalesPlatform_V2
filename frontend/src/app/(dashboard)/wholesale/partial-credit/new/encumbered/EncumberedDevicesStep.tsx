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
import { BarcodeEntryCard } from '../_components/BarcodeEntryCard';
import styles from '../../wizard.module.css';

type Stage = 'enter' | 'review';

function BreadcrumbChevron() {
  return (
    <svg
      className={styles.breadcrumbChevron}
      viewBox="0 0 12 12"
      width="12"
      height="12"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M4.5 2.5L8 6L4.5 9.5"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function XmarkIcon() {
  return (
    <svg
      viewBox="0 0 12 12"
      width="12"
      height="12"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M2 2L10 10M10 2L2 10"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
      />
    </svg>
  );
}

function SortIcon() {
  return (
    <svg
      className={styles.sortIcon}
      viewBox="0 0 10 12"
      width="10"
      height="12"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path d="M5 1L5 11" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" />
      <path
        d="M2 3.5L5 0.75L8 3.5"
        stroke="currentColor"
        strokeWidth="1.2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M2 8.5L5 11.25L8 8.5"
        stroke="currentColor"
        strokeWidth="1.2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/**
 * Wizard Step 4 — Encumbered Devices. Figma frame "Encumbered Device" →
 * "Barcodes Entered" → "Encumbered Device Summary" parsed review.
 *
 * Reviewer-only fields (Actual Value, Prolog Result) intentionally do NOT
 * appear on this wizard step — they live on the admin review surface only.
 */
export function EncumberedDevicesStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reconciliationBanner, setReconciliationBanner] = useState<string | null>(null);
  const [stage, setStage] = useState<Stage>('enter');
  const [blob, setBlob] = useState('');
  const [reviewedBarcodes, setReviewedBarcodes] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [barcodeError, setBarcodeError] = useState<string | null>(null);

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

  function onNextFromEntry() {
    if (barcodes.length === 0) {
      setBarcodeError('Enter or upload the encumbered device barcodes');
      return;
    }
    setBarcodeError(null);
    setReviewedBarcodes(barcodes);
    setStage('review');
  }

  function deleteReviewedRow(idx: number) {
    const next = reviewedBarcodes.filter((_, i) => i !== idx);
    setReviewedBarcodes(next);
    setBlob(next.join(', '));
  }

  async function onSubmitFromReview() {
    if (!detail) return;
    if (reviewedBarcodes.length === 0) {
      setStage('enter');
      setBarcodeError('Enter or upload the encumbered device barcodes');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const response = await setEncumberedLines(detail.id, reviewedBarcodes);
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
    return <div className={`pg-partial-credit ${styles.page}`}>{error ?? 'Loading…'}</div>;
  }

  return (
    <div className={`pg-partial-credit ${styles.page}`}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link>
        <BreadcrumbChevron />
      </div>
      <h1 className={styles.heading}>Which devices are encumbered?</h1>

      <StepIndicator
        current="encumbered"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      {stage === 'enter' ? (
        <BarcodeEntryCard
          value={blob}
          onChange={setBlob}
          textareaId="encumbered-barcodes"
          errorText={barcodeError}
          reconciliationBanner={reconciliationBanner}
        />
      ) : (
        <div className={styles.card}>
          {reconciliationBanner && (
            <div className={styles.warningBanner}>{reconciliationBanner}</div>
          )}
          <div className={styles.sectionHeaderRow}>
            <h2 className={styles.sectionHeading}>
              Encumbered Devices
              <span className={styles.countBadge}>({reviewedBarcodes.length})</span>
            </h2>
          </div>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th className={styles.thWithSort}>
                  Encumbered Device Barcode
                  <SortIcon />
                </th>
                <th aria-label="row actions" style={{ width: 32 }} />
              </tr>
            </thead>
            <tbody>
              {reviewedBarcodes.map((bc, idx) => (
                <tr key={`${bc}-${idx}`}>
                  <td>{bc}</td>
                  <td>
                    <button
                      type="button"
                      className={styles.rowDelete}
                      aria-label={`Remove barcode ${bc}`}
                      onClick={() => deleteReviewedRow(idx)}
                    >
                      <XmarkIcon />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className={styles.addMoreRow}>
            <button
              type="button"
              className={styles.addMoreButton}
              // TODO Phase 2: open "Add More Devices" modal per Figma. For
              // now route back to the entry stage.
              onClick={() => setStage('enter')}
            >
              + Add More Devices
            </button>
          </div>
        </div>
      )}

      {error && <div className={styles.errorBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button
          type="button"
          className={styles.buttonSecondary}
          onClick={() => {
            if (stage === 'review') {
              setStage('enter');
              return;
            }
            router.back();
          }}
        >
          Back
        </button>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={stage === 'enter' ? onNextFromEntry : onSubmitFromReview}
          disabled={submitting}
        >
          {submitting ? 'Saving…' : 'Next'}
        </button>
      </div>
    </div>
  );
}
