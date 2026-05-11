'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { getActiveBuyerCode } from '@/lib/activeBuyerCode';
import { createDraft, updateDraft } from '@/lib/partialCreditClient';
import { StepIndicator } from '../StepIndicator';
import styles from '../wizard.module.css';

/**
 * Wizard Step 1 — Overview. Figma frame "Start Credit Request".
 *
 * Buyer enters an order number and selects at least one reason, then the
 * page creates the draft and routes to the first per-reason step.
 */
export default function StartCreditRequestPage() {
  const router = useRouter();
  const [buyerCodeId, setBuyerCodeId] = useState<number | null>(null);
  const [orderNumber, setOrderNumber] = useState('');
  const [hasMissing, setHasMissing] = useState(false);
  const [hasWrong, setHasWrong] = useState(false);
  const [hasEncumbered, setHasEncumbered] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const active = getActiveBuyerCode();
    if (!active) {
      router.replace('/buyer-select');
      return;
    }
    setBuyerCodeId(active.id);
  }, [router]);

  const anyReason = hasMissing || hasWrong || hasEncumbered;
  const canNext = orderNumber.trim().length > 0 && anyReason && buyerCodeId !== null;

  async function onNext() {
    if (!canNext || buyerCodeId === null) return;
    setSubmitting(true);
    setError(null);
    try {
      const draft = await createDraft({
        orderNumber: orderNumber.trim(),
        buyerCodeId,
      });
      await updateDraft(draft.id, {
        hasMissingDevice: hasMissing,
        hasWrongDevice: hasWrong,
        hasEncumberedDevice: hasEncumbered,
      });
      const next = hasMissing ? 'missing' : hasWrong ? 'wrong' : 'encumbered';
      router.push(`/wholesale/partial-credit/new/${next}?id=${draft.id}`);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to create draft');
      setSubmitting(false);
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; New
      </div>
      <h1 className={styles.heading}>Submit a Credit Request</h1>

      <StepIndicator
        current="overview"
        hasMissing={hasMissing}
        hasWrong={hasWrong}
        hasEncumbered={hasEncumbered}
      />

      <div className={styles.card}>
        <h2 className={styles.cardHeading}>What order is the request for?</h2>
        <label className={styles.fieldLabel} htmlFor="orderNumber">
          Order Number
        </label>
        <input
          id="orderNumber"
          className={styles.input}
          placeholder="XX-XXXX"
          value={orderNumber}
          onChange={(e) => setOrderNumber(e.target.value)}
        />
        <p className={styles.helperText}>
          Partial credit requests must be made within 30 days of the order shipment date.
        </p>
      </div>

      <div className={styles.card}>
        <h2 className={styles.cardHeading}>Why are you requesting credit?</h2>
        <p className={styles.cardSubheading}>Select all that apply</p>

        <label className={styles.checkboxRow}>
          <input
            type="checkbox"
            checked={hasMissing}
            onChange={(e) => setHasMissing(e.target.checked)}
          />
          <span className={styles.checkboxLabel}>Missing Device</span>
        </label>

        <label className={styles.checkboxRow}>
          <input
            type="checkbox"
            checked={hasWrong}
            onChange={(e) => setHasWrong(e.target.checked)}
          />
          <span className={styles.checkboxLabel}>Wrong Device (model, carrier, or capacity)</span>
        </label>

        <label className={styles.checkboxRow}>
          <input
            type="checkbox"
            checked={hasEncumbered}
            onChange={(e) => setHasEncumbered(e.target.checked)}
          />
          <span className={styles.checkboxLabel}>
            Encumbered (iCloud locked, MDM locked, or blocklisted)
          </span>
        </label>
      </div>

      {error && <div className={styles.warningBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button
          type="button"
          className={styles.buttonSecondary}
          onClick={() => router.push('/wholesale/partial-credit')}
        >
          Cancel
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
