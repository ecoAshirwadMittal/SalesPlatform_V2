'use client';

import Link from 'next/link';
import { useParams } from 'next/navigation';
import { useCallback, useEffect, useState } from 'react';
import {
  getRequest,
  listPhotos,
  type CreditRequestDetail,
  type PhotoMetadata,
} from '@/lib/partialCreditClient';
import { getUserId } from '@/lib/session';
import { BuyerHeaderStrip } from './_components/BuyerHeaderStrip';
import { BuyerLineSection } from './_components/BuyerLineSection';
import { BuyerSummaryPanels } from './_components/BuyerSummaryPanels';
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
 */
export default function BuyerPartialCreditDetailPage() {
  const params = useParams<{ id: string }>();
  const requestId = Number(params?.id);

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [photos, setPhotos] = useState<PhotoMetadata[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [viewerUserId, setViewerUserId] = useState<number | null>(null);

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

  const handleUploaded = useCallback((photo: PhotoMetadata) => {
    setPhotos((prev) => [...prev, photo]);
  }, []);

  const handleDeleted = useCallback((photoId: number) => {
    setPhotos((prev) => prev.filter((p) => p.id !== photoId));
  }, []);

  if (loading) {
    return <div className={styles.page}>Loading…</div>;
  }
  if (error || !detail) {
    return (
      <div className={styles.page}>
        <Link href="/wholesale/partial-credit" className={styles.backLink}>
          ← Back to my requests
        </Link>
        <div className={styles.errorBanner} role="alert">
          {error ?? 'Request not found'}
        </div>
      </div>
    );
  }

  const finalised =
    detail.systemStatus === 'APPROVED' || detail.systemStatus === 'DECLINED';
  // Buyer landing colour is server-driven; the detail GET does not
  // include it today, so render a fallback that matches the systemStatus
  // family. Phase 2 will route this through credit_request_statuses.
  const statusColorHex = fallbackStatusColor(detail.systemStatus);

  return (
    <div className={styles.page}>
      <Link href="/wholesale/partial-credit" className={styles.backLink}>
        ← Back to my requests
      </Link>
      <h1 className={styles.pageHeading}>{detail.requestNumber}</h1>

      <BuyerHeaderStrip detail={detail} statusColorHex={statusColorHex} />

      <BuyerSummaryPanels
        requestedSkus={countRequestedSkus(detail)}
        requestedQty={countRequestedQty(detail)}
        requestedTotal={detail.requestedTotal ?? 0}
        approvedSkus={detail.reviewCompletedOn ? countApprovedSkus(detail) : null}
        approvedQty={detail.reviewCompletedOn ? countApprovedQty(detail) : null}
        approvedTotal={detail.approvedTotal}
      />

      <ReviewSummaryPanel detail={detail} />

      <BuyerLineSection kind="MISSING" detail={detail} />
      <BuyerLineSection kind="WRONG" detail={detail} />
      <BuyerLineSection kind="ENCUMBERED" detail={detail} />

      <section className={styles.photosSection} aria-label="Photos">
        <h2 className={styles.photosHeading}>Photos</h2>
        {!finalised && (
          <PhotoUploadDropzone
            requestId={detail.id}
            onUploaded={handleUploaded}
          />
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

// ---------------------------------------------------------------------------
// Derived counters — recomputed client-side because the detail DTO does
// not (yet) include them. These map to what the admin's HeaderSummary
// computes server-side; once Chunk 8 unifies the contract we can pull
// these from the response directly.
// ---------------------------------------------------------------------------

function countRequestedSkus(d: CreditRequestDetail): number {
  return d.missingLines.length + d.wrongLines.length + d.encumberedLines.length;
}

function countRequestedQty(d: CreditRequestDetail): number {
  return countRequestedSkus(d);
}

function countApprovedSkus(d: CreditRequestDetail): number {
  return (
    d.missingLines.filter((l) => l.reviewDecision === 'ACCEPTED').length +
    d.wrongLines.filter((l) => l.reviewDecision === 'ACCEPTED').length +
    d.encumberedLines.filter((l) => l.reviewDecision === 'ACCEPTED').length
  );
}

function countApprovedQty(d: CreditRequestDetail): number {
  return countApprovedSkus(d);
}

function fallbackStatusColor(status: CreditRequestDetail['systemStatus']): string {
  switch (status) {
    case 'PENDING_APPROVAL':
      return '#D08214';
    case 'UNDER_REVIEW':
      return '#407874';
    case 'APPROVED':
      return '#14AC36';
    case 'DECLINED':
      return '#B3261E';
    case 'DRAFT':
    default:
      return '#888888';
  }
}
