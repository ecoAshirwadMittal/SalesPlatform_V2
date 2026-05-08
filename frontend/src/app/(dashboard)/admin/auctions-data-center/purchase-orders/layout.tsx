/*
 * PO-11: server-component layout owning the metadata for the
 * /admin/auctions-data-center/purchase-orders/** route segment.
 *
 * Why this exists: the pages themselves are client components
 * ("use client") so they can't export `metadata`. They tried setting
 * document.title from useEffect, but Next.js's metadata reconciler
 * runs after every render and overwrites that. The supported pattern
 * is to put the title on a server-component layout, so this is the
 * minimal layout file that does just that.
 *
 * Trade-off: every PO sub-page (list, /new, /[id]) shows the same
 * "Purchase Order" title. Per-page differentiation (e.g. "PO #5",
 * "New Purchase Order") would need a layout.tsx in each sub-folder
 * with its own metadata or generateMetadata. Worth doing if multi-tab
 * use cases come up; not a regression today since the previous title
 * was the entirely-generic shell title.
 */
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Purchase Order — ecoATM Sales Platform",
};

export default function PurchaseOrdersLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
