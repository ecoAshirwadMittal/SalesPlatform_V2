'use client';
import { useSearchParams } from 'next/navigation';
import { BidderDashboardClient } from './BidderDashboardClient';

// Client-side page: `loadDashboard` uses a relative URL which only resolves
// in the browser. Reading `searchParams` via the client hook avoids the Next
// 16 server-component Promise dance entirely.
export default function Page() {
  const params = useSearchParams();
  const raw = params.get('buyerCodeId');
  if (!raw) return <div>Missing buyerCodeId</div>;
  const buyerCodeId = parseInt(raw, 10);
  if (Number.isNaN(buyerCodeId)) return <div>Invalid buyerCodeId</div>;
  return <BidderDashboardClient buyerCodeId={buyerCodeId} />;
}
