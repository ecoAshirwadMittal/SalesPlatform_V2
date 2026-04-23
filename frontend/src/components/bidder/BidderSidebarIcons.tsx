'use client';

/**
 * Inline SVG icons for the bidder sidebar.
 * Kept as simple functional components so tree-shaking can drop unused ones.
 * All icons are 20×20, strokeWidth 2, matching the admin sidebar icon spec.
 */

interface IconProps {
  /** Accessible label — pass undefined when the parent provides one. */
  'aria-hidden'?: boolean | 'true' | 'false';
}

/** Gavel icon — used for the Auction nav item. */
export function GavelIcon({ 'aria-hidden': ariaHidden = true }: IconProps) {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden={ariaHidden}
      focusable="false"
    >
      {/* Gavel head */}
      <path d="M14.5 2.5l7 7-2 2-7-7 2-2z" />
      {/* Handle */}
      <path d="M14 14l-9 9" />
      {/* Second strike line */}
      <path d="M2 14l2 2" />
      {/* Block base */}
      <rect x="3" y="19" width="9" height="2" rx="1" />
    </svg>
  );
}

/** Open book icon — used for the Buyer User Guide nav item. */
export function BookIcon({ 'aria-hidden': ariaHidden = true }: IconProps) {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden={ariaHidden}
      focusable="false"
    >
      <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
      <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
    </svg>
  );
}
