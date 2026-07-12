'use client';

/**
 * BuyerPortalChrome — shared top chrome for the buyer portal shells.
 *
 * Legacy parity (BDD-P3 / SHELL-P1, 2026-07-12): the chrome is a transparent
 * band (no white bar, no border) holding two regions:
 *   LEFT  — ecoATM DIRECT logo (content-area top-left)
 *   RIGHT — logged-in person's full name + initials avatar
 *
 * The "Switch Buyer Code" widget is NOT here — it moved to an in-content card
 * rendered below this band (see <SwitchBuyerCodeCard>), matching legacy.
 */

import Image from 'next/image';
import type { AuthUser } from '@/lib/session';
import UserAvatarPopover, { type AvatarMenuItem } from './UserAvatarPopover';
import styles from './chrome.module.css';

interface BuyerPortalChromeProps {
  /** Authenticated user — supplies full name + initials to the avatar. */
  user: AuthUser;
  /**
   * Items for the avatar popover. Auction shell passes:
   *   [{ label: 'Submit Feedback', onClick }, { label: 'Logout', onClick }]
   * PWS shell may inject additional items in Phase 4+.
   */
  avatarPopoverItems: AvatarMenuItem[];
}

export default function BuyerPortalChrome({
  user,
  avatarPopoverItems,
}: BuyerPortalChromeProps) {
  return (
    <header className={styles.chrome} role="banner">
      {/* LEFT — ecoATM DIRECT logo */}
      <div className={styles.chromeLogo}>
        <Image
          src="/images/ecoatm-direct-logo.png"
          alt="ecoATM DIRECT"
          width={119}
          height={46}
          priority
        />
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
