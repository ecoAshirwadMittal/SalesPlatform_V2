import { Suspense } from 'react';
import { BidderDashboardGate } from './BidderDashboardGate';

// Server Component shell. The Suspense boundary must sit above the client
// component that calls useSearchParams() — otherwise Next 16 static prerender
// bails with "missing-suspense-with-csr-bailout".
export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <BidderDashboardGate />
    </Suspense>
  );
}
