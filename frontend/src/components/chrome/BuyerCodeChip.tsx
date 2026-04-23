'use client';

/**
 * BuyerCodeChip — displays the active buyer code in the chrome top-bar,
 * or as a clickable card in the buyer-code picker.
 *
 * Two variants:
 *   'framed'  — default, used in the chrome top-bar:
 *               transparent bg, 1px border, green circle + briefcase left,
 *               bold code on top, company name below. Non-interactive unless
 *               an onClick is provided.
 *   'filled'  — dark-teal pill, white text, right-anchored arrow.
 *               Used in the buyer-code picker card (replaces the inline pill
 *               styling in buyer-select/page.tsx for callers that migrate to
 *               this component).
 *
 * QA reference: qa-02-buyer-code-picker.png (filled), qa-03-bidder-dashboard-ad.png (framed).
 */

import BriefcaseIcon from '@/app/(dashboard)/buyer-select/BriefcaseIcon';
import styles from './chrome.module.css';

interface BuyerCodeChipProps {
  code: string;
  companyName: string;
  /**
   * 'framed' — top-bar chrome chip (default)
   * 'filled' — picker pill (dark background)
   */
  variant?: 'framed' | 'filled';
  onClick?: () => void;
}

export default function BuyerCodeChip({
  code,
  companyName,
  variant = 'framed',
  onClick,
}: BuyerCodeChipProps) {
  const isInteractive = Boolean(onClick);
  const chipClass =
    variant === 'filled' ? styles.chipFilled : styles.chipFramed;

  // Render as a <button> when interactive so it has the correct role and
  // keyboard behavior; <div> when purely presentational (framed, no handler).
  if (isInteractive) {
    return (
      <button
        type="button"
        className={`${styles.chip} ${chipClass}`}
        onClick={onClick}
      >
        <ChipInner code={code} companyName={companyName} variant={variant} />
      </button>
    );
  }

  return (
    <div className={`${styles.chip} ${chipClass}`}>
      <ChipInner code={code} companyName={companyName} variant={variant} />
    </div>
  );
}

interface ChipInnerProps {
  code: string;
  companyName: string;
  variant: 'framed' | 'filled';
}

function ChipInner({ code, companyName, variant }: ChipInnerProps) {
  return (
    <>
      {/* Green circular avatar with briefcase icon */}
      <span className={styles.chipAvatar} aria-hidden="true">
        <BriefcaseIcon />
      </span>

      {/* Code + company stacked vertically */}
      <span className={styles.chipInfo}>
        <span className={styles.chipCode}>{code}</span>
        <span className={styles.chipCompany}>{companyName}</span>
      </span>

      {/* Arrow only on the filled (picker) variant */}
      {variant === 'filled' && (
        <span className={styles.chipArrow} aria-hidden="true">→</span>
      )}
    </>
  );
}
