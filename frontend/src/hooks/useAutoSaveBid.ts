import { useCallback, useRef, useState } from 'react';
import { saveBid } from '../lib/bidder';
import type { SaveBidPayload, BidDataRow } from '../lib/bidder';

export function useAutoSaveBid(
  rowId: number,
  onSaved: (row: BidDataRow) => void,
): { dirty: boolean; save: (payload: SaveBidPayload) => void } {
  const [dirty, setDirty] = useState(false);
  const pending = useRef<SaveBidPayload | null>(null);
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const flush = useCallback(async (): Promise<void> => {
    if (!pending.current) return;
    const payload = pending.current;
    pending.current = null;
    try {
      const row = await saveBid(rowId, payload);
      onSaved(row);
    } finally {
      setDirty(false);
    }
  }, [rowId, onSaved]);

  const save = useCallback((payload: SaveBidPayload): void => {
    pending.current = payload;
    setDirty(true);
    if (timer.current) clearTimeout(timer.current);
    timer.current = setTimeout(() => flush(), 500);
  }, [flush]);

  return { dirty, save };
}
