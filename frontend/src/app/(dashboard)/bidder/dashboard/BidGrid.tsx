'use client';
import type { BidDataRow } from '@/lib/bidder';
import { BidGridRow } from './BidGridRow';

interface BidGridProps {
  rows: BidDataRow[];
  onRowSaved: (row: BidDataRow) => void;
}

export function BidGrid({ rows, onRowSaved }: BidGridProps) {
  return (
    <table className="w-full text-sm">
      <thead className="bg-[#407874] text-white">
        <tr>
          <th className="px-3 py-2 text-left">Device</th>
          <th className="px-3 py-2 text-left">Grade</th>
          <th className="px-3 py-2 text-right">Target Price</th>
          <th className="px-3 py-2 text-right">Max Qty</th>
          <th className="px-3 py-2 text-right">Bid Qty</th>
          <th className="px-3 py-2 text-right">Bid Amount</th>
          <th className="px-3 py-2 text-right">Payout</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row, i) => (
          <BidGridRow
            key={row.id}
            row={row}
            striped={i % 2 === 1}
            onSaved={onRowSaved}
          />
        ))}
      </tbody>
    </table>
  );
}
