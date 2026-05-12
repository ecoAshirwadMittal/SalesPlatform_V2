'use client';

import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  CreditRequestValidationError,
  type HeaderSummary,
  type LineKind,
  type PrologResult,
  type ReviewDecision,
  completeReview,
  openForReview,
  setEncumberedFields as apiSetEncumberedFields,
  setGlobalDecision,
  setLineDecision as apiSetLineDecision,
  setSectionDecision,
} from '@/lib/adminPartialCreditClient';
import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../admin.module.css';
import { HeaderStrip } from './_components/HeaderStrip';
import { ReasonSection } from './_components/ReasonSection';
import { SummaryPanels } from './_components/SummaryPanels';
import {
  CompleteReviewModal,
  type CompleteReviewOutcome,
} from './_components/CompleteReviewModal';

/**
 * Stateful client for the admin review detail page. On mount calls
 * `openForReview` (idempotent — backend short-circuits if status is
 * already `UNDER_REVIEW`) and keeps a single `detail` shape in state.
 * Every mutating decision endpoint returns a fresh {@link HeaderSummary}
 * which we plug into the summary panels without re-fetching the
 * detail. We DO re-fetch (via `openForReview` again) after
 * section / global bulk operations because those don't return the new
 * line projections — only counts and the summary.
 */
export function AdminReviewClient() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const idRaw = params?.id;
  const id = typeof idRaw === 'string' ? Number.parseInt(idRaw, 10) : Number.NaN;

  const [detail, setDetail] = useState<CreditRequestDetail | null>(null);
  const [summary, setSummary] = useState<HeaderSummary | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  // Complete-review modal state. `submitting` blocks both buttons while
  // the network call is in flight; `submitError` surfaces 400 validation
  // issues (e.g. LINES_PENDING_DECISION) inline.
  const [modalOpen, setModalOpen] = useState(false);
  const [modalSubmitting, setModalSubmitting] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);

  // Per §11.Q4 — default Approved if ≥1 line is ACCEPTED, else Declined.
  const defaultOutcome = useMemo<CompleteReviewOutcome>(() => {
    if (!detail) return 'APPROVED';
    const any = (lines: { reviewDecision: string | null }[]): boolean =>
      lines.some((l) => l.reviewDecision === 'ACCEPTED');
    if (any(detail.missingLines) || any(detail.wrongLines) || any(detail.encumberedLines)) {
      return 'APPROVED';
    }
    return 'DECLINED';
  }, [detail]);

  const refreshDetail = useCallback(async (): Promise<void> => {
    if (Number.isNaN(id)) return;
    // openForReview is idempotent — re-running it gives us the latest
    // detail + has the side effect of holding the review lock if
    // another reviewer hasn't taken it yet. It's the simplest "read
    // the detail and project the latest header summary" entrypoint
    // because the backend recomputes everything on each call.
    const fresh = await openForReview(id);
    setDetail(fresh);
    setSummary(headerSummaryFromDetail(fresh));
  }, [id]);

  useEffect(() => {
    if (Number.isNaN(id)) {
      setError('Invalid request id');
      return;
    }
    refreshDetail().catch((e: unknown) => {
      setError(e instanceof Error ? e.message : 'Failed to open review');
    });
  }, [id, refreshDetail]);

  const wrapBusy = useCallback(
    async (fn: () => Promise<void>): Promise<void> => {
      setBusy(true);
      setError(null);
      try {
        await fn();
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : 'Request failed');
      } finally {
        setBusy(false);
      }
    },
    [],
  );

  // ----- mutating handlers -----------------------------------------------

  const handleLineDecision = useCallback(
    (kind: LineKind) =>
      (lineId: number, decision: ReviewDecision) => {
        if (!detail) return;
        void wrapBusy(async () => {
          const res = await apiSetLineDecision(id, lineId, kind, decision);
          setDetail((cur) => (cur ? applyLineUpdate(cur, kind, lineId, res.line) : cur));
          setSummary(res.summary);
        });
      },
    [detail, id, wrapBusy],
  );

  const handleSectionBulk = useCallback(
    (kind: LineKind, decision: ReviewDecision) => {
      void wrapBusy(async () => {
        const res = await setSectionDecision(id, kind, decision);
        setSummary(res.summary);
        // Section bulk doesn't return the per-line projections — re-pull
        // detail to refresh each line's reviewDecision + amountToCredit.
        await refreshDetail();
      });
    },
    [id, wrapBusy, refreshDetail],
  );

  const handleGlobalBulk = useCallback(
    (decision: ReviewDecision) => {
      void wrapBusy(async () => {
        const res = await setGlobalDecision(id, decision);
        setSummary(res.summary);
        await refreshDetail();
      });
    },
    [id, wrapBusy, refreshDetail],
  );

  const handleEncumberedFields = useCallback(
    (lineId: number, prologResult: PrologResult, actualValue: number | null) => {
      void wrapBusy(async () => {
        const res = await apiSetEncumberedFields(id, lineId, prologResult, actualValue);
        setDetail((cur) =>
          cur ? applyLineUpdate(cur, 'ENCUMBERED', lineId, res.line) : cur,
        );
        setSummary(res.summary);
      });
    },
    [id, wrapBusy],
  );

  const handleConfirmComplete = useCallback(
    (outcome: CompleteReviewOutcome) => {
      setModalSubmitting(true);
      setModalError(null);
      completeReview(id, outcome)
        .then(() => {
          setModalOpen(false);
          router.push('/admin/auctions-data-center/partial-credit');
        })
        .catch((e: unknown) => {
          if (e instanceof CreditRequestValidationError) {
            const codes = e.issues.map((i) => i.code);
            if (codes.includes('LINES_PENDING_DECISION')) {
              setModalError(
                'Every line needs an Accept or Decline before you can complete review.',
              );
            } else {
              setModalError(e.issues[0]?.message ?? 'Validation failed');
            }
            return;
          }
          setModalError(e instanceof Error ? e.message : 'Failed to complete review');
        })
        .finally(() => setModalSubmitting(false));
    },
    [id, router],
  );

  // ----- render ----------------------------------------------------------

  if (Number.isNaN(id)) {
    return <div className={styles.detailPage}>Invalid request id.</div>;
  }

  if (!detail || !summary) {
    return (
      <div className={styles.detailPage}>
        {error ? <div className={styles.errorBanner}>{error}</div> : <p>Loading review…</p>}
      </div>
    );
  }

  // Status pill colour: detail endpoint doesn't carry the hex, so we
  // fall back to neutral grey. The landing list ships the hex; once
  // SPKB-3664 wires status-config, we can either add it to the detail
  // response or fetch it once on mount.
  const fallbackPillColor = '#6F6F6F';

  return (
    <div className={styles.detailPage}>
      <div className={styles.breadcrumb}>
        <Link href="/admin/auctions-data-center/partial-credit">All Partial Credit Requests</Link>
        &nbsp;›&nbsp; {detail.requestNumber}
      </div>

      <HeaderStrip
        detail={detail}
        statusColorHex={fallbackPillColor}
        onCompleteReview={() => {
          setModalError(null);
          setModalOpen(true);
        }}
        completeDisabled={busy || detail.systemStatus === 'APPROVED' || detail.systemStatus === 'DECLINED'}
      />

      <SummaryPanels summary={summary} />

      {error && <div className={styles.errorBanner}>{error}</div>}

      <div className={styles.bulkBanner}>
        <span className={styles.bulkBannerLabel}>Bulk apply across all sections:</span>
        <button
          type="button"
          className={styles.buttonAcceptSmall}
          disabled={busy}
          onClick={() => handleGlobalBulk('ACCEPTED')}
        >
          Accept All
        </button>
        <button
          type="button"
          className={styles.buttonDanger}
          disabled={busy}
          onClick={() => handleGlobalBulk('DECLINED')}
        >
          Decline All
        </button>
      </div>

      {detail.hasMissingDevice && (
        <ReasonSection
          title="Missing Devices"
          kind="MISSING"
          lines={detail.missingLines}
          busy={busy}
          onLineDecision={handleLineDecision('MISSING')}
          onSectionBulk={handleSectionBulk}
        />
      )}

      {detail.hasWrongDevice && (
        <ReasonSection
          title="Wrong Devices"
          kind="WRONG"
          lines={detail.wrongLines}
          busy={busy}
          onLineDecision={handleLineDecision('WRONG')}
          onSectionBulk={handleSectionBulk}
        />
      )}

      {detail.hasEncumberedDevice && (
        <ReasonSection
          title="Encumbered Devices"
          kind="ENCUMBERED"
          lines={detail.encumberedLines}
          busy={busy}
          onLineDecision={handleLineDecision('ENCUMBERED')}
          onSectionBulk={handleSectionBulk}
          onEncumberedFields={handleEncumberedFields}
        />
      )}

      {modalOpen && (
        <CompleteReviewModal
          defaultOutcome={defaultOutcome}
          errorMessage={modalError}
          submitting={modalSubmitting}
          onCancel={() => {
            if (modalSubmitting) return;
            setModalOpen(false);
            setModalError(null);
          }}
          onConfirm={handleConfirmComplete}
        />
      )}
    </div>
  );
}

/**
 * Reconcile the response's discriminated-union line projection back
 * into the parent's three-array detail shape. Only the one matching
 * (kind, lineId) row is replaced; the rest are left alone so React's
 * keyed reconciliation keeps row state (e.g. the encumbered Actual
 * Value local draft) for unrelated rows.
 */
function applyLineUpdate(
  detail: CreditRequestDetail,
  kind: LineKind,
  lineId: number,
  projection: {
    kind: LineKind;
    missingLine: CreditRequestDetail['missingLines'][number] | null;
    wrongLine: CreditRequestDetail['wrongLines'][number] | null;
    encumberedLine: CreditRequestDetail['encumberedLines'][number] | null;
  },
): CreditRequestDetail {
  if (kind === 'MISSING' && projection.missingLine) {
    return {
      ...detail,
      missingLines: detail.missingLines.map((l) =>
        l.id === lineId ? projection.missingLine! : l,
      ),
    };
  }
  if (kind === 'WRONG' && projection.wrongLine) {
    return {
      ...detail,
      wrongLines: detail.wrongLines.map((l) =>
        l.id === lineId ? projection.wrongLine! : l,
      ),
    };
  }
  if (kind === 'ENCUMBERED' && projection.encumberedLine) {
    return {
      ...detail,
      encumberedLines: detail.encumberedLines.map((l) =>
        l.id === lineId ? projection.encumberedLine! : l,
      ),
    };
  }
  return detail;
}

/**
 * Best-effort projection of the header summary from the detail payload
 * (the backend doesn't ship a summary on the detail endpoint today —
 * only on mutating responses). On first render we approximate from the
 * line arrays + total. The first decision call replaces this with the
 * authoritative summary from the API.
 */
function headerSummaryFromDetail(detail: CreditRequestDetail): HeaderSummary {
  const requestedQty =
    detail.missingLines.length + detail.wrongLines.length + detail.encumberedLines.length;
  const accepted = (lines: { reviewDecision: string | null; amountToCredit: number | null }[]) =>
    lines.filter((l) => l.reviewDecision === 'ACCEPTED');
  const approvedLines = [
    ...accepted(detail.missingLines),
    ...accepted(detail.wrongLines),
    ...accepted(detail.encumberedLines),
  ];
  const approvedTotal = approvedLines.reduce((acc, l) => acc + (l.amountToCredit ?? 0), 0);
  return {
    requestedSkus: requestedQty,
    requestedQty,
    requestedTotal: detail.requestedTotal ?? 0,
    approvedSkus: approvedLines.length,
    approvedQty: approvedLines.length,
    approvedTotal,
  };
}
