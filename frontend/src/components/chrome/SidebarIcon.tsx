/**
 * SidebarIcon — renders a bespoke legacy sidebar glyph from /public/icons/sidebar.
 *
 * The glyphs are the real legacy Mendix nav assets (ICON-1): the ringed items
 * (Users, Buyers, Inventory, PO, RB, Auction, Credit Requests, Reports) carry a
 * thin dim ~34px ring baked into the SVG exactly as legacy renders it; the plain
 * items (Bid as Bidder, Settings, Admin, Buyer User Guide) have no ring. All are
 * a fixed 34×34 white viewBox — the box the shell CSS centres on the legacy icon
 * column (glyph/ring centre x≈26.5). Rendered as a plain <img> because every
 * glyph is monochrome white (no active-state colour change), so currentColor is
 * unnecessary and the asset is byte-for-byte the legacy art.
 *
 * Decorative: the adjacent nav label is the accessible name.
 */

interface SidebarIconProps {
  /** Asset basename in /public/icons/sidebar (e.g. "users", "credit-requests"). */
  name: string;
}

export default function SidebarIcon({ name }: SidebarIconProps) {
  return (
    // eslint-disable-next-line @next/next/no-img-element -- static monochrome SVG asset, no optimization needed
    <img
      src={`/icons/sidebar/${name}.svg`}
      alt=""
      aria-hidden="true"
      width={34}
      height={34}
      draggable={false}
    />
  );
}
