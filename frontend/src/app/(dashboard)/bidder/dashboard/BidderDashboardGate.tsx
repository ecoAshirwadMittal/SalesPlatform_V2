'use client';

import { useSearchParams } from 'next/navigation';
import { BidderDashboardClient } from './BidderDashboardClient';

export function BidderDashboardGate() {
  const params = useSearchParams();
  const raw = params.get('buyerCodeId');
  if (!raw) return <div>Missing buyerCodeId</div>;
  const buyerCodeId = parseInt(raw, 10);
  if (Number.isNaN(buyerCodeId)) return <div>Invalid buyerCodeId</div>;
  return <BidderDashboardClient buyerCodeId={buyerCodeId} />;
}
