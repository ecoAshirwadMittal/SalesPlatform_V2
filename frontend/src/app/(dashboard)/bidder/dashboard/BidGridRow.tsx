'use client';
import { useState } from 'react';
import type { BidDataRow } from '@/lib/bidder';
import { useAutoSaveBid } from '@/hooks/useAutoSaveBid';

interface BidGridRowProps {
  row: BidDataRow;
  striped: boolean;
  onSaved: (row: BidDataRow) => void;
}

export function BidGridRow({ row, striped, onSaved }: BidGridRowProps) {
  const [qty, setQty] = useState<string>(row.bidQuantity?.toString() ?? '');
  const [amount, setAmount] = useState<string>(row.bidAmount.toString());
  const { save } = useAutoSaveBid(row.id, onSaved);

  const emit = (nextQty: string, nextAmount: string): void => {
    const payload = {
      bidQuantity: nextQty === '' ? null : parseInt(nextQty, 10),
      bidAmount: parseFloat(nextAmount || '0'),
    };
    save(payload);
  };

  return (
    <tr className={striped ? 'bg-[#F7F7F7]' : ''}>
      <td className="px-3 py-2">{row.ecoid}</td>
      <td className="px-3 py-2">{row.mergedGrade}</td>
      <td className="px-3 py-2 text-right">${row.targetPrice.toFixed(2)}</td>
      <td className="px-3 py-2 text-right">{row.maximumQuantity ?? '—'}</td>
      <td className="px-3 py-2 text-right">
        <input
          aria-label={`Quantity for ${row.ecoid}`}
          type="number"
          min="0"
          value={qty}
          onChange={(e) => {
            setQty(e.target.value);
            emit(e.target.value, amount);
          }}
          className="w-20 border px-2 py-1 text-right"
        />
      </td>
      <td className="px-3 py-2 text-right">
        <input
          aria-label={`Amount for ${row.ecoid}`}
          type="number"
          min="0"
          step="0.01"
          value={amount}
          onChange={(e) => {
            setAmount(e.target.value);
            emit(qty, e.target.value);
          }}
          className="w-24 border px-2 py-1 text-right"
        />
      </td>
      <td className="px-3 py-2 text-right">${(row.payout ?? 0).toFixed(2)}</td>
    </tr>
  );
}
