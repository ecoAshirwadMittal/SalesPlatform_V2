import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

interface ReviewSummaryPanelProps {
  detail: CreditRequestDetail;
}

/**
 * Renders when {@code reviewCompletedOn} is non-null — the request is
 * APPROVED or DECLINED and the buyer can see the outcome. Sprint 4 ships
 * a slim panel: reviewer id + completion date + outcome label.
 *
 * <p>Phase 2 will resolve {@code reviewedById → display name + free-form
 * reviewer notes}; for now the buyer sees the user id (which is enough
 * to escalate to sales ops if something looks off). The plan name
 * "ReviewerNotesPanel" was renamed to ReviewSummaryPanel since notes
 * are not in V89's schema.
 */
export function ReviewSummaryPanel({ detail }: ReviewSummaryPanelProps) {
  if (!detail.reviewCompletedOn) return null;
  const outcomeLabel =
    detail.systemStatus === 'APPROVED'
      ? 'Approved'
      : detail.systemStatus === 'DECLINED'
        ? 'Declined'
        : detail.displayStatus;
  return (
    <section className={styles.reviewPanel} aria-label="Review summary">
      <h2 className={styles.reviewHeading}>Review summary</h2>
      <dl className={styles.reviewGrid}>
        <dt>Outcome</dt>
        <dd>{outcomeLabel}</dd>
        <dt>Reviewed on</dt>
        <dd>{formatDate(detail.reviewCompletedOn)}</dd>
        {detail.reviewedById !== null && (
          <>
            <dt>Reviewer</dt>
            <dd>User #{detail.reviewedById}</dd>
          </>
        )}
      </dl>
    </section>
  );
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  return Number.isNaN(d.getTime()) ? '—' : d.toLocaleString();
}
