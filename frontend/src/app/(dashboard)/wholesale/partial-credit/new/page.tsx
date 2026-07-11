import { Suspense } from 'react';
import { StartCreditRequestForm } from './StartCreditRequestForm';

// Server Component shell. The Suspense boundary must sit above the client
// component that calls useSearchParams() — otherwise Next 16 static prerender
// bails with "missing-suspense-with-csr-bailout". Same pattern as
// bidder/dashboard/page.tsx (see BidderDashboardGate).
export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <StartCreditRequestForm />
    </Suspense>
  );
}
