import { Suspense } from 'react';
import { AdminReviewClient } from './AdminReviewClient';

/**
 * Server-component shell for the admin partial-credit review detail.
 *
 * Why the Suspense boundary: the client child reads `useParams()` /
 * `useRouter()` from `next/navigation` — Next 16's static-prerender
 * pipeline bails with "missing-suspense-with-csr-bailout" without a
 * Suspense boundary above the client component. Same pattern as
 * `bidder/dashboard/page.tsx` (see Sprint 2 BidderDashboardGate).
 */
export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <AdminReviewClient />
    </Suspense>
  );
}
