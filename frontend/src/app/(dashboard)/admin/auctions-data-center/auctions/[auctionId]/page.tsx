/**
 * Canonical auction-detail route (`/admin/auctions-data-center/auctions/{id}`).
 *
 * Today there is no dedicated auction summary view; the schedule editor
 * doubles as the detail page (status badge + rounds + activity). This
 * server component performs a server-side redirect so direct links to
 * the canonical URL (eg. shared via Slack, bookmarked from the auctions
 * list, or hit by accident from the View button before sub-project N+1
 * adds a real summary page) resolve instead of 404'ing.
 *
 * Carries any query string forward (Next.js `redirect` discards it
 * otherwise) — useful when callers stash a return-to or status flag
 * on the canonical URL.
 *
 * Tracked as gap-analysis C26; replace with a real summary page when
 * the dedicated detail surface lands.
 */
import { redirect } from 'next/navigation';

interface PageProps {
  params: Promise<{ auctionId: string }>;
}

export default async function AuctionDetailRedirect({ params }: PageProps) {
  const { auctionId } = await params;
  redirect(`/admin/auctions-data-center/auctions/${auctionId}/schedule`);
}
