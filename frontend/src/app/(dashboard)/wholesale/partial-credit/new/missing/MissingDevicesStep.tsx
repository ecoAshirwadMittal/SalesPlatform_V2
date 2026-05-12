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
import styles from '../../wizard.module.css';

/**
 * Wizard Step 2 — Missing Devices. Figma frames "Missing Device - Start"
 * → "Barcodes Entered" → "Yes/No Damage" → "Photo Added".
 *
 * Captures: pasted barcodes (parsed on Next), damage Yes/No.
 * Photo uploads are Sprint 4 scope — the radio is the Yes-path gate
 * but no photos can be attached yet.
 */
export function MissingDevicesStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reconciliationBanner, setReconciliationBanner] = useState<string | null>(null);
  const [blob, setBlob] = useState('');
  const [damage, setDamage] = useState<'YES' | 'NO' | null>(null);
  const [submitting, setSubmitting] = useState(false);

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
  const canNext = barcodes.length > 0 && damage !== null;

  async function onNext() {
    if (!detail || !canNext) return;
    setSubmitting(true);
    setError(null);
    try {
      const response = await setMissingLines(detail.id, barcodes);
      await updateDraft(detail.id, { shipmentDamaged: damage! });

      // Surface the reconciliation banner before navigating away — when
      // the server dropped duplicates or non-manifest barcodes the wizard
      // should signal that to the buyer (Figma "Removed N duplicate and
      // M not in order"). The next-step navigation happens immediately
      // after; the banner is preserved in component state so a back-nav
      // returns to it.
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
    return <div className={styles.page}>{error ?? 'Loading…'}</div>;
  }

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; Missing
        Devices
      </div>
      <h1 className={styles.heading}>Which devices are you missing?</h1>

      <StepIndicator
        current="missing"
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
        <label className={styles.fieldLabel} htmlFor="missing-barcodes">
          Barcodes
        </label>
        <textarea
          id="missing-barcodes"
          className={styles.textarea}
          placeholder="Enter barcodes (one per line or comma-separated)"
          value={blob}
          onChange={(e) => setBlob(e.target.value)}
        />
        <p className={styles.helperText}>{barcodes.length} barcode(s) entered</p>
      </div>

      <div className={styles.card}>
        <label className={styles.fieldLabel}>Was the shipment damaged?</label>
        <div className={styles.radioGroup}>
          <label>
            <input
              type="radio"
              name="damage"
              checked={damage === 'YES'}
              onChange={() => setDamage('YES')}
            />
            Yes
          </label>
          <label>
            <input
              type="radio"
              name="damage"
              checked={damage === 'NO'}
              onChange={() => setDamage('NO')}
            />
            No
          </label>
        </div>
        {damage === 'YES' && (
          <p className={styles.helperText}>
            Photo uploads will be available in a future update. You can still proceed.
          </p>
        )}
      </div>

      {error && <div className={styles.warningBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button
          type="button"
          className={styles.buttonSecondary}
          onClick={() => router.push('/wholesale/partial-credit/new')}
        >
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
