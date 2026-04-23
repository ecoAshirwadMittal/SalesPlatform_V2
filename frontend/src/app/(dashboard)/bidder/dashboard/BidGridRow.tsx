'use client';
import type { BidDataRow } from '@/lib/bidder';
import { useAutoSaveBid } from '@/hooks/useAutoSaveBid';
import { PriceCell } from './PriceCell';
import { QtyCapCell } from './QtyCapCell';

interface BidGridRowProps {
  row: BidDataRow;
  striped: boolean;
  disabled?: boolean;
  onSaved: (row: BidDataRow) => void;
  onError?: (err: unknown) => void;
}

export function BidGridRow({ row, striped, disabled = false, onSaved, onError }: BidGridRowProps) {
  // WHY: useAutoSaveBid already implements 500ms debouncing via setTimeout.
  // The cell components call `save` on every user edit; the hook coalesces
  // rapid edits into a single PUT before hitting the network.
  const { save } = useAutoSaveBid(row.id, onSaved, onError);

  const handlePriceSave = (_rowId: number, value: number): void => {
    save({ bidQuantity: row.bidQuantity, bidAmount: value });
  };

  const handleQtySave = (_rowId: number, value: number | null): void => {
    save({ bidQuantity: value, bidAmount: row.bidAmount });
  };

  return (
    <tr className={striped ? 'bg-[#F7F7F7]' : ''}>
      <td className="px-3 py-2">{row.ecoid}</td>
      <td className="px-3 py-2">{row.mergedGrade}</td>
      <td className="px-3 py-2 text-right">${row.targetPrice.toFixed(2)}</td>
      <td className="px-3 py-2 text-right">{row.maximumQuantity ?? '—'}</td>
      <td className="px-3 py-2 text-right">
        <QtyCapCell
          rowId={row.id}
          initialValue={row.bidQuantity}
          onSave={handleQtySave}
          disabled={disabled}
        />
      </td>
      <td className="px-3 py-2 text-right">
        <PriceCell
          rowId={row.id}
          initialValue={row.bidAmount}
          onSave={handlePriceSave}
          disabled={disabled}
        />
      </td>
      <td className="px-3 py-2 text-right">${(row.payout ?? 0).toFixed(2)}</td>
    </tr>
  );
}
