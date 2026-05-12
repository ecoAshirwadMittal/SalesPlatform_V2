'use client';

import Link from 'next/link';
import { useParams } from 'next/navigation';
import { useCallback, useEffect, useRef, useState } from 'react';
import {
  getRequest,
  listPhotos,
  type CreditRequestDetail,
  type PhotoMetadata,
} from '@/lib/partialCreditClient';
import { getUserId } from '@/lib/session';
import { ApprovedDeclinedToggle, type DecisionFilter } from './_components/ApprovedDeclinedToggle';
import { BuyerHeaderStrip } from './_components/BuyerHeaderStrip';
import { BuyerLineSection } from './_components/BuyerLineSection';
import { BuyerSummaryPanels } from './_components/BuyerSummaryPanels';
import { MultiReasonTabs, type ReasonTabKey } from './_components/MultiReasonTabs';
import { PhotoGallery } from './_components/PhotoGallery';
import { PhotoUploadDropzone } from './_components/PhotoUploadDropzone';
import { ReviewSummaryPanel } from './_components/ReviewSummaryPanel';
import styles from './detail.module.css';

/**
 * Buyer detail page (Sprint 4 chunk 5 — SPKB-3669). Read-only mirror of
 * the admin review with the post-submit photo flow attached.
 *
 * <p><b>Behavioural gates:</b>
 * <ul>
 *   <li>Per-line decision pills appear only when status is APPROVED or
 *       DECLINED (Sprint 4 §11.Q2).</li>
 *   <li>Photo upload + delete are blocked once status is APPROVED or
 *       DECLINED (Sprint 4 §11.Q1 — both edits frozen at completion).</li>
 *   <li>Approved-credit summary panel is hidden until
 *       {@code reviewCompletedOn} is set.</li>
 *   <li>Review summary panel renders only when {@code reviewCompletedOn}
 *       is non-null.</li>
 * </ul>
 *
 * <p><b>Group 4 (Figma parity 2026-05-12):</b>
 * <ul>
 *   <li>Multi-reason variant (≥2 active reasons) renders a Tabs control
 *       and a per-tab Approved/Declined toggle. Single-reason layout is
 *       unchanged.</li>
 *   <li>H1 reads "Credit Request Details"; request number sits under
 *       the Order Number field on HeaderStrip.</li>
 *   <li>Status pill colour is server-driven via {@code statusColorHex}.
 *       The fallback helper was removed; see DTO blocker note in
 *       Group 4 dispatch output.</li>
 * </ul>
 */
export default function BuyerPartialCreditDetailPage() {
  const params = useParams<{ id: string }>();
  const requestId = Number(params?.id);

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [photos, setPhotos] = useState<PhotoMetadata[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [viewerUserId, setViewerUserId] = useState<number | null>(null);

  // Multi-reason variant state: active tab + per-tab decision filter.
  // The toggle defaults to APPROVED per Figma `656:3480` / `656:3735`.
  const [activeTab, setActiveTab] = useState<ReasonTabKey | null>(null);
  const [decisionFilter, setDecisionFilter] = useState<DecisionFilter>('APPROVED');
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [d, p] = await Promise.all([getRequest(requestId), listPhotos(requestId)]);
      setDetail(d);
      setPhotos(p);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load request');
    } finally {
      setLoading(false);
    }
  }, [requestId]);

  useEffect(() => {
    setViewerUserId(getUserId());
  }, []);

  useEffect(() => {
    if (Number.isNaN(requestId)) {
      setError('Invalid request id');
      setLoading(false);
      return;
    }
    void load();
  }, [requestId, load]);

  // Seed the active tab once the detail loads. Picking the first
  // available reason avoids rendering an empty tablist body.
  useEffect(() => {
    if (!detail || activeTab !== null) return;
    if (detail.hasMissingDevice) setActiveTab('MISSING');
    else if (detail.hasWrongDevice) setActiveTab('WRONG');
    else if (detail.hasEncumberedDevice) setActiveTab('ENCUMBERED');
  }, [detail, activeTab]);

  const handleUploaded = useCallback((photo: PhotoMetadata) => {
    setPhotos((prev) => [...prev, photo]);
  }, []);

  const handleDeleted = useCallback((photoId: number) => {
    setPhotos((prev) => prev.filter((p) => p.id !== photoId));
  }, []);

  if (loading) {
    return <div className={`pg-partial-credit ${styles.page}`}>Loading…</div>;
  }
  if (error || !detail) {
    return (
      <div className={`pg-partial-credit ${styles.page}`}>
        <BreadcrumbBack />
        <div className={styles.errorBanner} role="alert">
          {error ?? 'Request not found'}
        </div>
      </div>
    );
  }

  const finalised =
    detail.systemStatus === 'APPROVED' || detail.systemStatus === 'DECLINED';

  // BLOCKER: GET /api/v1/buyer/partial-credit/{id} does NOT include
  // `colorHex` on CreditRequestDetail today (server only exposes
  // displayStatus + systemStatus). Until the backend joins
  // credit_request_statuses.colorHex into the detail response, we ship
  // a transparent string — the pill renders the displayStatus text
  // styled against the surrounding card background and the parity-
  // sensitive bg colour is left to follow once the DTO ships the hex.
  // Recorded in Group 4 output.
  const statusColorHex = '';

  const activeReasons: ReasonTabKey[] = [];
  if (detail.hasMissingDevice && detail.missingLines.length > 0) activeReasons.push('MISSING');
  if (detail.hasWrongDevice && detail.wrongLines.length > 0) activeReasons.push('WRONG');
  if (detail.hasEncumberedDevice && detail.encumberedLines.length > 0)
    activeReasons.push('ENCUMBERED');
  const multiReason = activeReasons.length >= 2;

  return (
    <div className={`pg-partial-credit ${styles.page}`}>
      <BreadcrumbBack />
      <h1 className={styles.pageHeading}>Credit Request Details</h1>

      <BuyerHeaderStrip detail={detail} statusColorHex={statusColorHex} />

      <BuyerSummaryPanels
        requestedQty={countRequestedQty(detail)}
        requestedTotal={detail.requestedTotal ?? 0}
        approvedQty={detail.reviewCompletedOn ? countApprovedQty(detail) : null}
        approvedTotal={detail.approvedTotal}
      />

      <ReviewSummaryPanel detail={detail} />

      {multiReason ? (
        <MultiReasonReasonSections
          detail={detail}
          activeReasons={activeReasons}
          activeTab={activeTab}
          onTabChange={setActiveTab}
          decisionFilter={decisionFilter}
          onDecisionFilterChange={setDecisionFilter}
        />
      ) : (
        <>
          <BuyerLineSection kind="MISSING" detail={detail} />
          <BuyerLineSection kind="WRONG" detail={detail} />
          <BuyerLineSection kind="ENCUMBERED" detail={detail} />
        </>
      )}

      <section className={styles.photosSection} aria-label="Photos">
        <h2 className={styles.photosHeading}>Photos</h2>
        {!finalised && (
          <>
            {/* Figma "Add Photos" primary button (`+` icon). Functionality
                stays in PhotoUploadDropzone but the visual is swapped for
                a CTA that opens a native file picker via the hidden
                input. TODO(backend): no buyer-export endpoint exists, so
                the Figma "Download" CTA next to this row is not shipped. */}
            <button
              type="button"
              className={styles.addPhotosButton}
              onClick={() => fileInputRef.current?.click()}
            >
              <span aria-hidden>+</span> Add Photos
            </button>
            <div className={styles.hiddenDropzone}>
              <PhotoUploadDropzone
                requestId={detail.id}
                onUploaded={handleUploaded}
                inputRef={fileInputRef}
              />
            </div>
          </>
        )}
        <PhotoGallery
          photos={photos}
          viewerUserId={viewerUserId ?? -1}
          readOnly={finalised}
          onDeleted={handleDeleted}
        />
      </section>
    </div>
  );
}

function BreadcrumbBack() {
  return (
    <Link href="/wholesale/partial-credit" className={styles.backLink}>
      <ChevronLeftIcon /> All Credit Requests
    </Link>
  );
}

function ChevronLeftIcon() {
  return (
    <svg
      aria-hidden
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <polyline points="15 18 9 12 15 6" />
    </svg>
  );
}

interface MultiReasonReasonSectionsProps {
  detail: CreditRequestDetail;
  activeReasons: readonly ReasonTabKey[];
  activeTab: ReasonTabKey | null;
  onTabChange: (next: ReasonTabKey) => void;
  decisionFilter: DecisionFilter;
  onDecisionFilterChange: (next: DecisionFilter) => void;
}

function MultiReasonReasonSections({
  detail,
  activeReasons,
  activeTab,
  onTabChange,
  decisionFilter,
  onDecisionFilterChange,
}: MultiReasonReasonSectionsProps) {
  if (activeTab === null) return null;
  const tabs = activeReasons.map((key) => ({
    key,
    label: tabLabel(key),
    count: reasonCount(key, detail),
  }));
  const decisionCounts = countDecisions(activeTab, detail);
  return (
    <>
      <MultiReasonTabs active={activeTab} onChange={onTabChange} tabs={tabs} />
      <ApprovedDeclinedToggle
        value={decisionFilter ?? 'APPROVED'}
        onChange={onDecisionFilterChange}
        approvedCount={decisionCounts.approved}
        declinedCount={decisionCounts.declined}
      />
      <BuyerLineSection
        kind={activeTab}
        detail={detail}
        decisionFilter={decisionFilter}
      />
    </>
  );
}

function tabLabel(kind: ReasonTabKey): string {
  switch (kind) {
    case 'MISSING':
      return 'Missing Devices';
    case 'WRONG':
      return 'Wrong Devices';
    case 'ENCUMBERED':
      return 'Encumbered Devices';
  }
}

function reasonCount(kind: ReasonTabKey, d: CreditRequestDetail): number {
  switch (kind) {
    case 'MISSING':
      return d.missingLines.length;
    case 'WRONG':
      return d.wrongLines.length;
    case 'ENCUMBERED':
      return d.encumberedLines.length;
  }
}

function countDecisions(
  kind: ReasonTabKey,
  d: CreditRequestDetail,
): { approved: number; declined: number } {
  const lines =
    kind === 'MISSING'
      ? d.missingLines
      : kind === 'WRONG'
        ? d.wrongLines
        : d.encumberedLines;
  let approved = 0;
  let declined = 0;
  for (const l of lines) {
    if (l.reviewDecision === 'ACCEPTED') approved += 1;
    else if (l.reviewDecision === 'DECLINED') declined += 1;
  }
  return { approved, declined };
}

// ---------------------------------------------------------------------------
// Derived counters — recomputed client-side because the detail DTO does
// not (yet) include them. These map to what the admin's HeaderSummary
// computes server-side; once Chunk 8 unifies the contract we can pull
// these from the response directly.
// ---------------------------------------------------------------------------

function countRequestedQty(d: CreditRequestDetail): number {
  return d.missingLines.length + d.wrongLines.length + d.encumberedLines.length;
}

function countApprovedQty(d: CreditRequestDetail): number {
  return (
    d.missingLines.filter((l) => l.reviewDecision === 'ACCEPTED').length +
    d.wrongLines.filter((l) => l.reviewDecision === 'ACCEPTED').length +
    d.encumberedLines.filter((l) => l.reviewDecision === 'ACCEPTED').length
  );
}
