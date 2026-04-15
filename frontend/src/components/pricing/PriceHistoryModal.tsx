'use client';

import s from '@/app/pws/pricing/pricing.module.css';

interface PricingDeviceLite {
  id: number;
  sku: string;
}

export interface PriceHistoryEntry {
  id: number;
  listPrice: number | null;
  minPrice: number | null;
  previousListPrice: number | null;
  previousMinPrice: number | null;
  expirationDate: string | null;
  createdDate: string | null;
}

interface PriceHistoryModalProps {
  device: PricingDeviceLite | null;
  data: PriceHistoryEntry[];
  loading: boolean;
  onClose: () => void;
}

export function PriceHistoryModal({ device, data, loading, onClose }: PriceHistoryModalProps) {
  return (
    <div className={s.modalOverlay} onClick={onClose}>
      <div className={`${s.modal} ${s.historyModal}`} onClick={(e) => e.stopPropagation()}>
        <div className={s.modalHeader}>
          <h2 className={s.modalTitle}>
            Price History{device ? ` — ${device.sku}` : ''}
          </h2>
          <button className={s.modalClose} onClick={onClose}>×</button>
        </div>
        <div className={s.modalBody}>
          {loading ? (
            <p className={s.modalStatus}>Loading history…</p>
          ) : data.length === 0 ? (
            <p>No price history found for this device.</p>
          ) : (
            <table className={s.historyTable}>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>List Price</th>
                  <th>Prev List Price</th>
                  <th>Min Price</th>
                  <th>Prev Min Price</th>
                </tr>
              </thead>
              <tbody>
                {data.map((h) => (
                  <tr key={h.id}>
                    <td>{h.createdDate ? new Date(h.createdDate).toLocaleDateString() : '—'}</td>
                    <td>{h.listPrice != null ? `$${h.listPrice.toFixed(2)}` : '—'}</td>
                    <td>{h.previousListPrice != null ? `$${h.previousListPrice.toFixed(2)}` : '—'}</td>
                    <td>{h.minPrice != null ? `$${h.minPrice.toFixed(2)}` : '—'}</td>
                    <td>{h.previousMinPrice != null ? `$${h.previousMinPrice.toFixed(2)}` : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
