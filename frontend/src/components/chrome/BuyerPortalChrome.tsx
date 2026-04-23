'use client';

/**
 * BuyerPortalChrome — shared top-bar for all buyer portal shells.
 *
 * Three regions (horizontal flex):
 *   LEFT  — ecoATM DIRECT logo
 *   MID   — "Switch Buyer Code" link + <BuyerCodeChip> (hidden when no active code)
 *   RIGHT — user full name + <UserAvatarPopover>
 *
 * When activeBuyerCode is null (e.g. during the picker flow), the middle
 * region is hidden. This keeps the chrome usable in a "logo-only" state
 * before a code is selected.
 *
 * QA reference: qa-03-bidder-dashboard-ad.png top bar.
 */

import Image from 'next/image';
import type { ActiveBuyerCode } from '@/lib/activeBuyerCode';
import type { AuthUser } from '@/lib/session';
import BuyerCodeChip from './BuyerCodeChip';
import UserAvatarPopover, { type AvatarMenuItem } from './UserAvatarPopover';
import styles from './chrome.module.css';

interface BuyerPortalChromeProps {
  /** Currently active buyer code. null → middle region hidden (picker/logo-only state). */
  activeBuyerCode: ActiveBuyerCode | null;
  /** Authenticated user — supplies full name + initials to the avatar. */
  user: AuthUser;
  /** Click handler for the "Switch Buyer Code" link — typically router.push('/buyer-select'). */
  onSwitchBuyerCode: () => void;
  /**
   * Items for the avatar popover. Auction shell passes:
   *   [{ label: 'Submit Feedback', onClick }, { label: 'Logout', onClick }]
   * PWS shell may inject additional items in Phase 4+.
   */
  avatarPopoverItems: AvatarMenuItem[];
}

export default function BuyerPortalChrome({
  activeBuyerCode,
  user,
  onSwitchBuyerCode,
  avatarPopoverItems,
}: BuyerPortalChromeProps) {
  return (
    <header className={styles.chrome} role="banner">
      {/* LEFT — ecoATM DIRECT logo */}
      <div className={styles.chromeLogo}>
        <Image
          src="/images/ecoatm_logo.svg"
          alt="ecoATM DIRECT"
          width={120}
          height={28}
          priority
        />
      </div>

      {/* MID — Switch Buyer Code link + active code chip (hidden when no active code) */}
      <div className={styles.chromeMid}>
        {activeBuyerCode !== null && (
          <>
            <button
              type="button"
              className={styles.switchLink}
              onClick={onSwitchBuyerCode}
            >
              Switch Buyer Code
            </button>
            <BuyerCodeChip
              code={activeBuyerCode.code}
              companyName={activeBuyerCode.buyerName}
              variant="framed"
            />
          </>
        )}
      </div>

      {/* RIGHT — user name + avatar popover */}
      <div className={styles.chromeRight}>
        <UserAvatarPopover
          user={{ fullName: user.fullName, initials: user.initials }}
          items={avatarPopoverItems}
        />
      </div>
    </header>
  );
}
