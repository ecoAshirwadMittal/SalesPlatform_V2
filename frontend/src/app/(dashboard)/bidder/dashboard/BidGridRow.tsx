'use client';
import type { BidDataRow } from '@/lib/bidder';
import { useAutoSaveBid } from '@/hooks/useAutoSaveBid';
import { PriceCell } from './PriceCell';
import { QtyCapCell } from './QtyCapCell';
import styles from './bidGrid.module.css';

interface BidGridRowProps {
  row: BidDataRow;
  striped: boolean;
  disabled?: boolean;
  onSaved: (row: BidDataRow) => void;
  onError?: (err: unknown) => void;
}

/** `M/D/YYYY` formatter — matches QA's short date format for the Added column. */
function formatAdded(iso: string | null | undefined): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return '';
  return `${d.getMonth() + 1}/${d.getDate()}/${d.getFullYear()}`;
}

/**
 * Phase 6B: row renders 11 QA-parity columns — Product Id, Brand, Model,
 * Model Name (hidden on narrow viewports via `.colModelName`), Grade,
 * Carrier, Added, Avail Qty, Target Price, Price (editable), Qty Cap
 * (editable). MDM display fields (brand/model/modelName/carrier/added)
 * are null on save-response — fallback to em-dash for display.
 */
export function BidGridRow({ row, striped, disabled = false, onSaved, onError }: BidGridRowProps) {
  const { save } = useAutoSaveBid(row.id, onSaved, onError);

  const handlePriceSave = (_rowId: number, value: number): void => {
    save({ bidQuantity: row.bidQuantity, bidAmount: value });
  };

  const handleQtySave = (_rowId: number, value: number | null): void => {
    save({ bidQuantity: value, bidAmount: row.bidAmount });
  };

  return (
    <tr className={striped ? 'bg-[#F7F7F7]' : ''}>
      <td className="auction-productid px-3 py-2">{row.ecoid}</td>
      <td className="auction-brand px-3 py-2">{row.brand ?? '—'}</td>
      <td className="auction-model px-3 py-2">{row.model ?? '—'}</td>
      <td className={`auction-modelname px-3 py-2 ${styles.colModelName}`}>{row.modelName ?? '—'}</td>
      <td className="auction-grade px-3 py-2">{row.mergedGrade}</td>
      <td className="auction-carrier px-3 py-2">{row.carrier ?? '—'}</td>
      <td className="auction-added px-3 py-2">{formatAdded(row.added)}</td>
      <td className="auction-availqt px-3 py-2 text-right">{row.maximumQuantity ?? '—'}</td>
      <td className="auction-targetp px-3 py-2 text-right">${row.targetPrice.toFixed(2)}</td>
      <td className="auction-price px-3 py-2 text-right">
        <PriceCell
          rowId={row.id}
          initialValue={row.bidAmount}
          onSave={handlePriceSave}
          disabled={disabled}
        />
      </td>
      <td className="auction-qtycap px-3 py-2 text-right">
        <QtyCapCell
          rowId={row.id}
          initialValue={row.bidQuantity}
          onSave={handleQtySave}
          disabled={disabled}
        />
      </td>
    </tr>
  );
}
