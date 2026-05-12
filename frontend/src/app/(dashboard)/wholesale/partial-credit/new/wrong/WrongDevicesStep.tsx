'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import {
  type CreditRequestDetail,
  getRequest,
  parseBarcodeBlob,
  setWrongLines,
} from '@/lib/partialCreditClient';
import { StepIndicator } from '../../StepIndicator';
import { BarcodeEntryCard } from '../_components/BarcodeEntryCard';
import styles from '../../wizard.module.css';

interface WrongRow {
  expectedBarcode: string;
  actualImeiOrModel: string;
}

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
 * Wizard Step 3 — Wrong Devices. Figma frames "Wrong Device" → "Barcodes
 * Entered" → "Actual Device Details".
 *
 * Two-stage: enter expected barcodes (BarcodeEntryCard), then identify
 * each received device. Photo modals (Add Photos / Edit Photos) deferred
 * to Phase 2 — the Photos column is rendered as a placeholder.
 */
export function WrongDevicesStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reconciliationBanner, setReconciliationBanner] = useState<string | null>(null);
  const [stage, setStage] = useState<'enter' | 'details'>('enter');
  const [blob, setBlob] = useState('');
  const [rows, setRows] = useState<WrongRow[]>([]);
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
        if (d.wrongLines.length > 0) {
          setRows(
            d.wrongLines.map((l) => ({
              expectedBarcode: l.expectedBarcode,
              actualImeiOrModel: l.actualImeiOrModel ?? '',
            })),
          );
          setStage('details');
          setBlob(d.wrongLines.map((l) => l.expectedBarcode).join(', '));
        }
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [id, router]);

  const barcodes = useMemo(() => parseBarcodeBlob(blob), [blob]);

  function onNextFromEntry() {
    if (barcodes.length === 0) {
      setBarcodeError('Enter or upload the expected device barcodes');
      return;
    }
    setBarcodeError(null);
    setRows(barcodes.map((b) => ({ expectedBarcode: b, actualImeiOrModel: '' })));
    setStage('details');
  }

  function deleteRow(idx: number) {
    setRows(rows.filter((_, i) => i !== idx));
  }

  async function onNextFromDetails() {
    if (!detail) return;
    setSubmitting(true);
    setError(null);
    try {
      const response = await setWrongLines(detail.id, rows);
      if (response.reconciliation.banner) {
        setReconciliationBanner(response.reconciliation.banner);
      }
      const next = detail.hasEncumberedDevice ? 'encumbered' : 'summary';
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
      <h1 className={styles.heading}>Which devices were you expecting?</h1>

      <StepIndicator
        current="wrong"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      {stage === 'enter' ? (
        <BarcodeEntryCard
          value={blob}
          onChange={setBlob}
          textareaId="wrong-barcodes"
          errorText={barcodeError}
          reconciliationBanner={reconciliationBanner}
        />
      ) : (
        <div className={styles.card} style={{ padding: 0 }}>
          {reconciliationBanner && (
            <div className={styles.warningBanner}>{reconciliationBanner}</div>
          )}
          <div
            className={styles.sectionHeaderRow}
            style={{ padding: '16px 24px 0 24px', margin: 0 }}
          >
            <h2 className={styles.sectionHeading}>
              Wrong Devices
              <span className={styles.countBadge}>({rows.length})</span>
            </h2>
          </div>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th className={styles.thWithSort}>
                  Expected Device
                  <SortIcon />
                </th>
                <th className={styles.thWithSort}>
                  Received Device
                  <span className={styles.thMutedHelper}>(IMEI or model name)</span>
                  <SortIcon />
                </th>
                <th className={styles.thWithSort}>
                  Photos
                  <span className={styles.thMutedHelper}>(optional)</span>
                  <SortIcon />
                </th>
                <th aria-label="row actions" style={{ width: 32 }} />
              </tr>
            </thead>
            <tbody>
              {rows.map((row, idx) => (
                <tr key={`${row.expectedBarcode}-${idx}`}>
                  <td>{row.expectedBarcode}</td>
                  <td>
                    <input
                      className={styles.input}
                      value={row.actualImeiOrModel}
                      placeholder="Enter IMEI or model name"
                      onChange={(e) => {
                        const next = [...rows];
                        next[idx] = { ...next[idx], actualImeiOrModel: e.target.value };
                        setRows(next);
                      }}
                    />
                  </td>
                  <td>
                    {/* TODO Phase 2: open Figma "Add Photos" / "Edit Photos"
                        modal (frames 27068 / 29122). Photo upload is wired
                        on the detail page; the wizard placeholder ships
                        without the modal for now. */}
                    <button
                      type="button"
                      className={styles.addMoreButton}
                      disabled
                      aria-label={`Add photos for ${row.expectedBarcode}`}
                    >
                      + Add Photos
                    </button>
                  </td>
                  <td>
                    <button
                      type="button"
                      className={styles.rowDelete}
                      aria-label={`Remove row for ${row.expectedBarcode}`}
                      onClick={() => deleteRow(idx)}
                    >
                      <XmarkIcon />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {error && <div className={styles.errorBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button
          type="button"
          className={styles.buttonSecondary}
          onClick={() => {
            if (stage === 'details') setStage('enter');
            else router.back();
          }}
        >
          Back
        </button>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={stage === 'enter' ? onNextFromEntry : onNextFromDetails}
          disabled={stage === 'enter' ? false : submitting}
        >
          {submitting ? 'Saving…' : 'Next'}
        </button>
      </div>
    </div>
  );
}
