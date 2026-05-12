'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  type CreditRequestDetail,
  CreditRequestValidationError,
  getRequest,
  listPhotos,
  type PhotoMetadata,
  submitRequest,
  type ValidationIssue,
} from '@/lib/partialCreditClient';
import { StepIndicator } from '../../StepIndicator';
import styles from '../../wizard.module.css';

/**
 * Inline SVG circle-check icon — matches the Figma "circle-check" Font
 * Awesome solid glyph at 32px in `#14AC36`. Inline avoids dragging the
 * @fortawesome dependency into the bundle just for one icon.
 */
function CircleCheckIcon() {
  return (
    <svg
      width={32}
      height={32}
      viewBox="0 0 512 512"
      aria-hidden="true"
      focusable="false"
      role="img"
    >
      <path
        fill="#14AC36"
        d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM369 209L241 337c-9.4 9.4-24.6 9.4-33.9 0l-64-64c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0l47 47L335 175c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9z"
      />
    </svg>
  );
}

/**
 * Inline chevron icon used by per-section Show/Hide Details toggle. The
 * caller flips the `aria-expanded` state; CSS rotates the glyph 180° when
 * the section is collapsed so a single inline SVG covers both directions.
 */
function ChevronIcon({ expanded }: { expanded: boolean }) {
  return (
    <svg
      viewBox="0 0 12 12"
      width="14"
      height="14"
      aria-hidden="true"
      focusable="false"
      fill="none"
      style={{
        transform: expanded ? 'rotate(0deg)' : 'rotate(180deg)',
        transition: 'transform 150ms ease',
      }}
    >
      <path
        d="M2.5 4.5L6 8L9.5 4.5"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/**
 * Inline pen icon used by the per-section Edit button. Figma uses the FA
 * solid `pen` glyph at 14px; we inline the equivalent stroke path so the
 * Summary page doesn't pull in the icon dependency.
 */
function PenIcon() {
  return (
    <svg
      viewBox="0 0 14 14"
      width="14"
      height="14"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M9.5 1.5L12.5 4.5L4.5 12.5L1.5 13L2 10L9.5 1.5Z"
        stroke="currentColor"
        strokeWidth="1.2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

type SectionKey = 'missing' | 'wrong' | 'encumbered';

/**
 * Wizard Step 5 — Summary + Submit. Figma frame "Request Credit -
 * Summary" with the confirmation modal overlay ("Request submitted!").
 *
 * Read-only review of every line entered in the prior steps. On Submit
 * the controller runs the validator; failed validations surface as a
 * banner with the failing issue codes.
 *
 * Group 6 rebuild (2026-05-12): introduces the canonical Figma structure
 * — H1 restored to "Submit a Credit Request", new metadata card, per-
 * reason tables (replacing the old `<ul>` lists), Show/Hide Details
 * chevron toggle, per-group Edit button, and a buttonless confirmation
 * modal that auto-routes back to the landing after a short delay.
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
  const [photos, setPhotos] = useState<readonly PhotoMetadata[]>([]);
  // Default each section expanded — matches Figma "expanded" variants.
  const [expanded, setExpanded] = useState<Record<SectionKey, boolean>>({
    missing: true,
    wrong: true,
    encumbered: true,
  });

  useEffect(() => {
    if (!Number.isFinite(id) || id <= 0) {
      router.replace('/wholesale/partial-credit/new');
      return;
    }
    let cancelled = false;
    Promise.resolve(getRequest(id))
      .then((d) => {
        if (!cancelled && d) setDetail(d);
      })
      .catch((e) => {
        if (!cancelled) setError(e instanceof Error ? e.message : 'Failed to load');
      });
    return () => {
      cancelled = true;
    };
  }, [id, router]);

  // Photo counts surface in the Wrong-devices table's "Photos (optional)"
  // column. Fetched once on mount; failures degrade gracefully to a zero
  // count so the Summary page still renders.
  useEffect(() => {
    if (!detail) return;
    let cancelled = false;
    Promise.resolve(listPhotos(detail.id))
      .then((p) => {
        if (!cancelled && p) setPhotos(p);
      })
      .catch(() => {
        if (!cancelled) setPhotos([]);
      });
    return () => {
      cancelled = true;
    };
  }, [detail]);

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

  /**
   * The Figma confirmation modal renders no explicit dismiss control
   * (the design assumes auto-dismiss). Pure auto-dismiss is hostile for
   * keyboard / screen-reader users, so we route back on Escape and on
   * scrim click in addition to the auto-redirect timer. See design notes §4.
   */
  const dismissConfirmation = useCallback(() => {
    router.push('/wholesale/partial-credit');
  }, [router]);

  useEffect(() => {
    if (!submitted) return;
    function onKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape') {
        dismissConfirmation();
      }
    }
    window.addEventListener('keydown', onKeyDown);
    // Auto-redirect after 3 s — matches the design-notes "transient confirmation".
    const timer = window.setTimeout(dismissConfirmation, 3000);
    return () => {
      window.removeEventListener('keydown', onKeyDown);
      window.clearTimeout(timer);
    };
  }, [submitted, dismissConfirmation]);

  function toggle(section: SectionKey) {
    setExpanded((prev) => ({ ...prev, [section]: !prev[section] }));
  }

  function editSection(section: SectionKey) {
    if (!detail) return;
    router.push(`/wholesale/partial-credit/new/${section}?id=${detail.id}`);
  }

  function editAll() {
    // Bottom-row Edit routes to the FIRST active reason step for
    // predictability per the Group 6 dispatch spec.
    if (!detail) return;
    if (detail.hasMissingDevice) {
      editSection('missing');
      return;
    }
    if (detail.hasWrongDevice) {
      editSection('wrong');
      return;
    }
    if (detail.hasEncumberedDevice) {
      editSection('encumbered');
    }
  }

  // Cross-line photo lookup — Wrong-line photos are keyed by `wrongDeviceLineId`.
  const photoCounts = useMemo<Map<number, number>>(() => {
    const map = new Map<number, number>();
    for (const p of photos) {
      if (p.wrongDeviceLineId !== null) {
        map.set(p.wrongDeviceLineId, (map.get(p.wrongDeviceLineId) ?? 0) + 1);
      }
    }
    return map;
  }, [photos]);

  if (!detail) {
    return <div className={`pg-partial-credit ${styles.page}`}>{error ?? 'Loading…'}</div>;
  }

  const totalDevices =
    detail.missingLines.length + detail.wrongLines.length + detail.encumberedLines.length;

  const activeReasonLabels: string[] = [];
  if (detail.hasMissingDevice) activeReasonLabels.push('Missing Device');
  if (detail.hasWrongDevice) activeReasonLabels.push('Wrong Device');
  if (detail.hasEncumberedDevice) activeReasonLabels.push('Encumbered Device');

  return (
    <div className={`pg-partial-credit ${styles.page}`}>
      <div className={styles.breadcrumb}>
        <Link href="/wholesale/partial-credit">All Credit Requests</Link> &nbsp;›&nbsp; Summary
      </div>
      <h1 className={styles.heading}>Submit a Credit Request</h1>

      <StepIndicator
        current="summary"
        hasMissing={detail.hasMissingDevice}
        hasWrong={detail.hasWrongDevice}
        hasEncumbered={detail.hasEncumberedDevice}
      />

      {/* Metadata card — three labelled rows (design notes §2.5.1). */}
      <section className={styles.summaryMetaCard} aria-label="Request summary">
        <dl className={styles.summaryMetaList}>
          <div className={styles.summaryMetaRow}>
            <dt className={styles.summaryMetaLabel}>Order Number</dt>
            <dd className={styles.summaryMetaValue}>{detail.orderNumber}</dd>
          </div>
          <div className={styles.summaryMetaRow}>
            <dt className={styles.summaryMetaLabel}>Request Reasons</dt>
            <dd className={styles.summaryMetaValue}>
              {activeReasonLabels.length > 0 ? activeReasonLabels.join(', ') : '—'}
            </dd>
          </div>
          <div className={styles.summaryMetaRow}>
            <dt className={styles.summaryMetaLabel}>Total Devices</dt>
            <dd className={styles.summaryMetaValue}>{totalDevices}</dd>
          </div>
        </dl>
      </section>

      {detail.hasMissingDevice && (
        <section className={styles.summarySection} aria-label="Missing devices">
          <header className={styles.summarySectionHeader}>
            <h2 className={styles.summarySectionHeading}>
              Missing Devices
              <span className={styles.countBadge}>({detail.missingLines.length})</span>
            </h2>
            <div className={styles.summarySectionActions}>
              <button
                type="button"
                className={styles.summaryEditButton}
                onClick={() => editSection('missing')}
                aria-label="Edit missing devices"
              >
                <PenIcon />
                <span>Edit</span>
              </button>
              <button
                type="button"
                className={styles.summaryToggleButton}
                onClick={() => toggle('missing')}
                aria-expanded={expanded.missing}
                aria-controls="summary-missing-body"
              >
                <span>{expanded.missing ? 'Hide Details' : 'Show Details'}</span>
                <ChevronIcon expanded={expanded.missing} />
              </button>
            </div>
          </header>
          {expanded.missing && (
            <div id="summary-missing-body">
              <table className={styles.summaryTable}>
                <thead>
                  <tr>
                    <th scope="col">Barcode</th>
                    <th scope="col">Device Description</th>
                    <th scope="col">Amount Paid</th>
                  </tr>
                </thead>
                <tbody>
                  {detail.missingLines.map((line) => {
                    const description = [line.brand, line.model].filter(Boolean).join(' ') || '—';
                    const amount =
                      line.amountPaid !== null
                        ? `$${line.amountPaid.toFixed(2)}`
                        : '—';
                    return (
                      <tr key={line.id}>
                        <td>{line.barcodeSubmitted}</td>
                        <td>{description}</td>
                        <td>{amount}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}

      {detail.hasWrongDevice && (
        <section className={styles.summarySection} aria-label="Wrong devices">
          <header className={styles.summarySectionHeader}>
            <h2 className={styles.summarySectionHeading}>
              Wrong Devices
              <span className={styles.countBadge}>({detail.wrongLines.length})</span>
            </h2>
            <div className={styles.summarySectionActions}>
              <button
                type="button"
                className={styles.summaryEditButton}
                onClick={() => editSection('wrong')}
                aria-label="Edit wrong devices"
              >
                <PenIcon />
                <span>Edit</span>
              </button>
              <button
                type="button"
                className={styles.summaryToggleButton}
                onClick={() => toggle('wrong')}
                aria-expanded={expanded.wrong}
                aria-controls="summary-wrong-body"
              >
                <span>{expanded.wrong ? 'Hide Details' : 'Show Details'}</span>
                <ChevronIcon expanded={expanded.wrong} />
              </button>
            </div>
          </header>
          {expanded.wrong && (
            <div id="summary-wrong-body">
              <table className={styles.summaryTable}>
                <thead>
                  <tr>
                    <th scope="col">Expected Device</th>
                    <th scope="col">
                      Received Device
                      <span className={styles.thMutedHelper}>(IMEI or model name)</span>
                    </th>
                    <th scope="col">
                      Photos
                      <span className={styles.thMutedHelper}>(optional)</span>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {detail.wrongLines.map((line) => {
                    const expected =
                      [line.expectedBarcode, line.expectedBrand, line.expectedModel]
                        .filter(Boolean)
                        .join(' — ') || line.expectedBarcode;
                    const received = line.actualImeiOrModel ?? '(not specified)';
                    const photoCount = photoCounts.get(line.id) ?? 0;
                    return (
                      <tr key={line.id}>
                        <td>{expected}</td>
                        <td>{received}</td>
                        <td>{photoCount === 0 ? '—' : `${photoCount} photo${photoCount === 1 ? '' : 's'}`}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}

      {detail.hasEncumberedDevice && (
        <section className={styles.summarySection} aria-label="Encumbered devices">
          <header className={styles.summarySectionHeader}>
            <h2 className={styles.summarySectionHeading}>
              Encumbered Devices
              <span className={styles.countBadge}>({detail.encumberedLines.length})</span>
            </h2>
            <div className={styles.summarySectionActions}>
              <button
                type="button"
                className={styles.summaryEditButton}
                onClick={() => editSection('encumbered')}
                aria-label="Edit encumbered devices"
              >
                <PenIcon />
                <span>Edit</span>
              </button>
              <button
                type="button"
                className={styles.summaryToggleButton}
                onClick={() => toggle('encumbered')}
                aria-expanded={expanded.encumbered}
                aria-controls="summary-encumbered-body"
              >
                <span>{expanded.encumbered ? 'Hide Details' : 'Show Details'}</span>
                <ChevronIcon expanded={expanded.encumbered} />
              </button>
            </div>
          </header>
          {expanded.encumbered && (
            <div id="summary-encumbered-body">
              <table className={styles.summaryTable}>
                <thead>
                  <tr>
                    <th scope="col">Barcode</th>
                    <th scope="col">Device Description</th>
                  </tr>
                </thead>
                <tbody>
                  {detail.encumberedLines.map((line) => {
                    const description = [line.brand, line.model].filter(Boolean).join(' ') || '—';
                    return (
                      <tr key={line.id}>
                        <td>{line.barcodeSubmitted}</td>
                        <td>{description}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
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

      <div className={styles.summaryBottomRow}>
        <button
          type="button"
          className={styles.buttonSecondary}
          onClick={editAll}
          aria-label="Edit credit request"
        >
          Edit
        </button>
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
          onClick={onSubmit}
          disabled={submitting}
        >
          {submitting ? 'Submitting…' : 'Submit Request'}
        </button>
      </div>

      {submitted && (
        <div
          className={styles.confirmModal}
          role="dialog"
          aria-modal="true"
          aria-labelledby="pc-confirm-heading"
          onClick={dismissConfirmation}
        >
          <div
            className={styles.confirmCard}
            onClick={(e) => e.stopPropagation()}
          >
            <CircleCheckIcon />
            <h2 id="pc-confirm-heading" className={styles.confirmHeading}>
              Request submitted!
            </h2>
          </div>
        </div>
      )}
    </div>
  );
}
