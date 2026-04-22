'use client';
import type { BidDataTotals } from '@/lib/bidder';

interface SubmitBarProps {
  totals: BidDataTotals | null;
  canSubmit: boolean;
  onSubmit: () => void;
  submitting: boolean;
  errorMessage: string | null;
  lastSubmittedAt: Date | null;
}

// Module-scoped so the formatter is constructed once and reused on every
// render; cheaper than a per-call `Intl.NumberFormat(...)` and produces
// locale-correct grouping/decimal separators.
const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

// Same module-scoped pattern as `currencyFormatter` — readable
// medium/short stamp ("Apr 22, 2026, 3:14 PM") for the post-submit
// confirmation line.
const submittedAtFormatter = new Intl.DateTimeFormat('en-US', {
  dateStyle: 'medium',
  timeStyle: 'short',
});

function formatCurrency(value: number): string {
  return currencyFormatter.format(value);
}

function formatSubmittedAt(value: Date): string {
  return submittedAtFormatter.format(value);
}

export function SubmitBar({
  totals,
  canSubmit,
  onSubmit,
  submitting,
  errorMessage,
  lastSubmittedAt,
}: SubmitBarProps) {
  const disabled = !canSubmit || submitting;

  return (
    <div className="mt-4 border-t border-[#407874]/30 pt-3">
      {errorMessage ? (
        <div
          role="alert"
          className="mb-2 rounded bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          {errorMessage}
        </div>
      ) : null}
      {lastSubmittedAt ? (
        <div className="mb-2 text-xs text-gray-600">
          Submitted at {formatSubmittedAt(lastSubmittedAt)}
        </div>
      ) : null}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <dl className="flex flex-wrap gap-6 text-sm text-[#112d32]">
          <div>
            <dt className="text-xs uppercase text-gray-500">Rows</dt>
            <dd className="font-semibold">{totals?.rowCount ?? 0}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase text-gray-500">Total Qty</dt>
            <dd className="font-semibold">{totals?.totalBidQuantity ?? 0}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase text-gray-500">Total Bid</dt>
            <dd className="font-semibold">
              {formatCurrency(totals?.totalBidAmount ?? 0)}
            </dd>
          </div>
          <div>
            <dt className="text-xs uppercase text-gray-500">Total Payout</dt>
            <dd className="font-semibold">
              {formatCurrency(totals?.totalPayout ?? 0)}
            </dd>
          </div>
        </dl>
        <button
          type="button"
          onClick={onSubmit}
          disabled={disabled}
          className="rounded px-5 py-2 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
          style={{ backgroundColor: disabled ? '#9ca3af' : '#407874' }}
        >
          {submitting ? 'Submitting…' : 'Submit Round'}
        </button>
      </div>
    </div>
  );
}
