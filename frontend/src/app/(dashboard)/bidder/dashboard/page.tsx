import { loadDashboard } from '@/lib/bidder';
import { BidderDashboardClient } from './BidderDashboardClient';

// Next.js 16: `searchParams` is a Promise that must be awaited before
// use — previous synchronous access is a breaking-change removal.
export default async function Page({
  searchParams,
}: {
  searchParams: Promise<{ buyerCodeId?: string }>;
}) {
  const params = await searchParams;
  if (!params.buyerCodeId) return <div>Missing buyerCodeId</div>;
  const buyerCodeId = parseInt(params.buyerCodeId, 10);
  if (Number.isNaN(buyerCodeId)) return <div>Invalid buyerCodeId</div>;
  const initial = await loadDashboard(buyerCodeId);
  return <BidderDashboardClient initial={initial} buyerCodeId={buyerCodeId} />;
}
