'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import {
  type CreditRequestDetail,
  getRequest,
  parseBarcodeBlob,
  setMissingLines,
  updateDraft,
} from '@/lib/partialCreditClient';
import { StepIndicator } from '../../StepIndicator';
import { BarcodeEntryCard } from '../_components/BarcodeEntryCard';
import styles from '../../wizard.module.css';

type Stage = 'enter' | 'review';

/**
 * Inline chevron used by the breadcrumb — replaces the ASCII "›" glyph.
 * Sized 12px so it sits centered with the breadcrumb text baseline.
 */
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

/**
 * Inline xmark icon for per-row delete buttons on the parsed-review list.
 */
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

/**
 * Sort-arrows icon rendered next to sortable column headers — currently
 * decorative; sorting wiring will land with the parsed-review table
 * extensions (Phase 2 modal work).
 */
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
 * Wizard Step 2 — Missing Devices. Figma frames "Missing Device - Start"
 * → "Barcodes Entered" → "Yes/No Damage" → "Barcode List" review.
 *
 * Two-stage:
 *   1. enter  — barcode-entry card (textarea → OR → dropzone) + damage Q.
 *   2. review — parsed-list table with per-row delete and Add More Devices.
 *
 * Photo uploads on the Yes-damage path remain Sprint 4 / Phase 2 scope.
 */
export function MissingDevicesStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reconciliationBanner, setReconciliationBanner] = useState<string | null>(null);
  const [stage, setStage] = useState<Stage>('enter');
  const [blob, setBlob] = useState('');
  const [reviewedBarcodes, setReviewedBarcodes] = useState<string[]>([]);
  const [damage, setDamage] = useState<'YES' | 'NO' | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [barcodeError, setBarcodeError] = useState<string | null>(null);
  const [damageError, setDamageError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isFinite(id) || id <= 0) {
      router.replace('/wholesale/partial-credit/new');
      return;
    }
    getRequest(id)
      .then((d) => {
        setDetail(d);
        setBlob(d.missingLines.map((l) => l.barcodeSubmitted).join(', '));
        setDamage(d.shipmentDamaged === 'NOT_ANSWERED' ? null : d.shipmentDamaged);
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [id, router]);

  const barcodes = useMemo(() => parseBarcodeBlob(blob), [blob]);

  function onNextFromEntry() {
    let valid = true;
    if (barcodes.length === 0) {
      setBarcodeError('Enter or upload the missing device barcodes');
      valid = false;
    } else {
      setBarcodeError(null);
    }
    if (damage === null) {
      setDamageError('Select an answer');
      valid = false;
    } else {
      setDamageError(null);
    }
    if (!valid) return;
    setReviewedBarcodes(barcodes);
    setStage('review');
  }

  function deleteReviewedRow(idx: number) {
    const next = reviewedBarcodes.filter((_, i) => i !== idx);
    setReviewedBarcodes(next);
    // Keep the textarea blob in sync so going back-and-forth between stages
    // is consistent.
    setBlob(next.join(', '));
  }

  async function onSubmitFromReview() {
    if (!detail) return;
    if (reviewedBarcodes.length === 0) {
      // Defensive — return user to entry stage to re-enter barcodes.
      setStage('enter');
      setBarcodeError('Enter or upload the missing device barcodes');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const response = await setMissingLines(detail.id, reviewedBarcodes);
      await updateDraft(detail.id, { shipmentDamaged: damage! });
      if (response.reconciliation.banner) {
        setReconciliationBanner(response.reconciliation.banner);
      }
      const next = detail.hasWrongDevice
        ? 'wrong'
        : detail.hasEncumberedDevice
          ? 'encumbered'
          : 'summary';
      router.push(`/wholesale/partial-credit/new/${next}?id=${detail.id}`);
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
      <h1 className={styles.heading}>Which devices are you missing?</h1>

      <StepIndicator
        current="missing"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      {stage === 'enter' ? (
        <>
          <BarcodeEntryCard
            value={blob}
            onChange={setBlob}
            textareaId="missing-barcodes"
            errorText={barcodeError}
            reconciliationBanner={reconciliationBanner}
          />

          <div className={styles.card}>
            <label className={styles.fieldLabel}>Was the shipment damaged?</label>
            <div className={styles.radioGroup}>
              <label>
                <input
                  type="radio"
                  name="damage"
                  checked={damage === 'YES'}
                  onChange={() => {
                    setDamage('YES');
                    setDamageError(null);
                  }}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="damage"
                  checked={damage === 'NO'}
                  onChange={() => {
                    setDamage('NO');
                    setDamageError(null);
                  }}
                />
                No
              </label>
            </div>
            {damage === 'YES' && (
              <p className={styles.helperText}>
                {/* TODO Phase 2: render Figma "Add photos of the damaged shipment"
                    dropzone + inline error when no photo attached. */}
                Photo uploads will be available in a future update. You can still proceed.
              </p>
            )}
            {damageError && <p className={styles.errorText}>{damageError}</p>}
          </div>
        </>
      ) : (
        <div className={styles.card}>
          {reconciliationBanner && (
            <div className={styles.warningBanner}>{reconciliationBanner}</div>
          )}
          <div className={styles.sectionHeaderRow}>
            <h2 className={styles.sectionHeading}>
              Missing Devices
              <span className={styles.countBadge}>({reviewedBarcodes.length})</span>
            </h2>
          </div>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th className={styles.thWithSort}>
                  Missing Device Barcode
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
              // TODO Phase 2: open "Add Missing Devices" modal per Figma
              // frame line 38947. For now, route back to the entry stage
              // so the buyer can paste more barcodes.
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
            router.push('/wholesale/partial-credit/new');
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
