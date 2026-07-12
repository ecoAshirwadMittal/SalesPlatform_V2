'use client';

/**
 * SwitchBuyerCodeCard — in-content "Switch Buyer Code" block for the buyer
 * portal shells (BDD-P3 / ruling 4, 2026-07-12).
 *
 * Legacy layout (extracted from the bidder-dashboard capture):
 *   - a "Switch Buyer Code" label — "Switch" in brand green (#14AC36),
 *     "Buyer Code" in body dark (#3C3C3C). The label is the click target that
 *     opens the buyer-code picker.
 *   - a bordered card (#D0D0D0, ~300px, no fill) with an empty left cell +
 *     vertical divider, then the buyer name (small) stacked over the CODE
 *     (large, bold).
 *
 * Renders nothing when there is no active code (picker / logo-only state).
 */

import type { ActiveBuyerCode } from '@/lib/activeBuyerCode';
import styles from './chrome.module.css';

interface SwitchBuyerCodeCardProps {
  /** Currently active buyer code. null → the block is hidden. */
  activeBuyerCode: ActiveBuyerCode | null;
  /** Click handler for the label — typically router.push('/buyer-select'). */
  onSwitch: () => void;
}

export default function SwitchBuyerCodeCard({
  activeBuyerCode,
  onSwitch,
}: SwitchBuyerCodeCardProps) {
  if (activeBuyerCode === null) return null;

  return (
    <div className={styles.switchBlock}>
      <button type="button" className={styles.switchLabel} onClick={onSwitch}>
        <span className={styles.switchLabelAccent}>Switch</span> Buyer Code
      </button>

      <div className={styles.switchCard}>
        {/* Empty left cell + divider (legacy) */}
        <span className={styles.switchCardCell} aria-hidden="true" />
        <span className={styles.switchCardInfo}>
          <span className={styles.switchCardCompany}>{activeBuyerCode.buyerName}</span>
          <span className={styles.switchCardCode}>{activeBuyerCode.code}</span>
        </span>
      </div>
    </div>
  );
}
