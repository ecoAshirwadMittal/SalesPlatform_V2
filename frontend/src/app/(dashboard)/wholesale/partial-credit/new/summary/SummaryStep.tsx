'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import {
  type CreditRequestDetail,
  CreditRequestValidationError,
  getRequest,
  submitRequest,
  type ValidationIssue,
} from '@/lib/partialCreditClient';
import { StepIndicator } from '../../StepIndicator';
import styles from '../../wizard.module.css';

/**
 * Wizard Step 5 — Summary + Submit. Figma frame "Request Credit -
 * Summary" with the confirmation modal overlay ("Request submitted!").
 *
 * Read-only review of every line entered in the prior steps. On Submit
 * the controller runs the validator; failed validations surface as a
 * banner with the failing issue codes.
 */
export function SummaryStep() {
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id'));

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [issues, setIssues] = useState<readonly ValidationIssue[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(id) || id <= 0) {
      router.replace('/wholesale/partial-credit/new');
      return;
    }
    getRequest(id)
      .then((d) => setDetail(d))
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [id, router]);

  async function onSubmit() {
    if (!detail) return;
    setSubmitting(true);
    setError(null);
    setIssues([]);
    try {
      const updated = await submitRequest(detail.id);
      setDetail(updated);
      setSubmitted(true);
    } catch (e) {
      if (e instanceof CreditRequestValidationError) {
        setIssues(e.issues);
      } else {
        setError(e instanceof Error ? e.message : 'Failed to submit');
      }
      setSubmitting(false);
    }
  }

  if (!detail) {
    return <div className={styles.page}>{error ?? 'Loading…'}</div>;
  }

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; Summary
      </div>
      <h1 className={styles.heading}>Review and submit</h1>

      <StepIndicator
        current="summary"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      <div className={styles.summarySection}>
        <h3>Order</h3>
        <p style={{ margin: 0 }}>
          <strong>{detail.orderNumber}</strong>
          {detail.partyName ? ` · ${detail.partyName}` : ''}
        </p>
      </div>

      {detail.hasMissingDevice && (
        <div className={styles.summarySection}>
          <h3>Missing Devices ({detail.missingLines.length})</h3>
          <ul className={styles.list}>
            {detail.missingLines.map((line) => (
              <li key={line.id}>{line.barcodeSubmitted}</li>
            ))}
          </ul>
          <p className={styles.helperText}>
            Shipment damaged: <strong>{detail.shipmentDamaged}</strong>
          </p>
        </div>
      )}

      {detail.hasWrongDevice && (
        <div className={styles.summarySection}>
          <h3>Wrong Devices ({detail.wrongLines.length})</h3>
          <ul className={styles.list}>
            {detail.wrongLines.map((line) => (
              <li key={line.id}>
                Expected <strong>{line.expectedBarcode}</strong> · received{' '}
                <em>{line.actualImeiOrModel ?? '(not specified)'}</em>
              </li>
            ))}
          </ul>
        </div>
      )}

      {detail.hasEncumberedDevice && (
        <div className={styles.summarySection}>
          <h3>Encumbered Devices ({detail.encumberedLines.length})</h3>
          <ul className={styles.list}>
            {detail.encumberedLines.map((line) => (
              <li key={line.id}>{line.barcodeSubmitted}</li>
            ))}
          </ul>
        </div>
      )}

      {issues.length > 0 && (
        <div className={styles.warningBanner}>
          <strong>Please fix the following before submitting:</strong>
          <ul style={{ margin: '8px 0 0', paddingLeft: 18 }}>
            {issues.map((i) => (
              <li key={i.code}>{i.message}</li>
            ))}
          </ul>
        </div>
      )}
      {error && <div className={styles.warningBanner}>{error}</div>}

      <div className={styles.buttonRow}>
        <button type="button" className={styles.buttonSecondary} onClick={() => router.back()}>
          Back
        </button>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={onSubmit}
          disabled={submitting}
        >
          {submitting ? 'Submitting…' : 'Submit Request'}
        </button>
      </div>

      {submitted && (
        <div className={styles.confirmModal} role="dialog" aria-modal="true">
          <div className={styles.confirmCard}>
            <div
              style={{
                width: 56,
                height: 56,
                borderRadius: '50%',
                background: '#E6F6EB',
                color: '#0E7A26',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: 28,
                margin: '0 auto',
              }}
            >
              ✓
            </div>
            <h2>Request submitted!</h2>
            <p>
              We have received your credit request. You can track its status on the Credit
              Requests page.
            </p>
            <button
              type="button"
              className={styles.buttonPrimary}
              onClick={() => router.push('/wholesale/partial-credit')}
            >
              Back to Credit Requests
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
