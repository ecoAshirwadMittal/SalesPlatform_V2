/**
 * Inline briefcase SVG used in the buyer-code pill avatar.
 * Pure presentational component — no state, no side effects.
 * Dimensions sized for the 32px green circular avatar container.
 */
export default function BriefcaseIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="14"
      height="14"
      fill="white"
      aria-hidden="true"
      focusable="false"
    >
      <path d="M20 7h-4V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2H4a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2ZM10 5h4v2h-4V5Zm10 14H4V9h16v10Z" />
    </svg>
  );
}
