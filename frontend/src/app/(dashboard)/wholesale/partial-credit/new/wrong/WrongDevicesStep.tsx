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
import styles from '../../wizard.module.css';

interface WrongRow {
  expectedBarcode: string;
  actualImeiOrModel: string;
}

/**
 * Wizard Step 3 — Wrong Devices. Figma frames "Wrong Device" → "Barcodes
 * Entered" → "Actual Device Details".
 *
 * Two-stage: enter expected barcodes, then identify each received device.
 * Photo uploads + the photo modal are Sprint 4 scope.
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
    if (barcodes.length === 0) return;
    setRows(barcodes.map((b) => ({ expectedBarcode: b, actualImeiOrModel: '' })));
    setStage('details');
  }

  async function onNextFromDetails() {
    if (!detail) return;
    setSubmitting(true);
    setError(null);
    try {
      const response = await setWrongLines(detail.id, rows);
      // Surface the reconciliation banner so the buyer sees when the
      // server dropped duplicates or non-manifest expected barcodes
      // (Figma "Removed N duplicate and M not in order").
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
    return <div className={styles.page}>{error ?? 'Loading…'}</div>;
  }

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; Wrong
        Devices
      </div>
      <h1 className={styles.heading}>
        {stage === 'enter' ? 'Which devices were you expecting?' : 'What did you receive instead?'}
      </h1>

      <StepIndicator
        current="wrong"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      {stage === 'enter' ? (
        <div className={styles.card}>
          <p className={styles.cardSubheading}>
            Copy and paste the expected barcodes into the text field below.
          </p>
          {reconciliationBanner && (
            <div className={styles.warningBanner}>{reconciliationBanner}</div>
          )}
          <label className={styles.fieldLabel} htmlFor="wrong-barcodes">
            Barcodes
          </label>
          <textarea
            id="wrong-barcodes"
            className={styles.textarea}
            placeholder="Enter barcodes (one per line or comma-separated)"
            value={blob}
            onChange={(e) => setBlob(e.target.value)}
          />
          <p className={styles.helperText}>{barcodes.length} barcode(s) entered</p>
        </div>
      ) : (
        <div className={styles.card} style={{ padding: 0 }}>
          {reconciliationBanner && (
            <div className={styles.warningBanner}>{reconciliationBanner}</div>
          )}
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th>Expected Device</th>
                <th>Received Device (IMEI or model name)</th>
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
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {error && <div className={styles.warningBanner}>{error}</div>}

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
          disabled={stage === 'enter' ? barcodes.length === 0 : submitting}
        >
          {submitting ? 'Saving…' : 'Next'}
        </button>
      </div>
    </div>
  );
}
