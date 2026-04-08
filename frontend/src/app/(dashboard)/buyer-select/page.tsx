'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import styles from './buyerSelect.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * Buyer Code Select page — strict clone of legacy Mendix Buyer_Code_Select.
 * After login, the buyer sees their assigned buyer codes split into
 * PWS (Premium Wholesale) and Auction (Weekly Wholesale) categories.
 *
 * Selecting a code sets it as the active buyer context and redirects
 * to the corresponding landing page.
 */

const API_BASE = '/api/v1';

interface BuyerCode {
  id: number;
  code: string;
  buyerName: string;
  buyerCodeType: string; // Premium_Wholesale, Wholesale, Data_Wipe, etc.
}

interface UserInfo {
  userId: number;
  firstName: string;
  lastName: string;
}

function getAuthUser(): UserInfo | null {
  if (typeof window === 'undefined') return null;
  try {
    const stored = localStorage.getItem('auth_user');
    if (stored) return JSON.parse(stored);
  } catch { /* ignore */ }
  return null;
}

export default function BuyerSelectPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserInfo | null>(null);
  const [pwsCodes, setPwsCodes] = useState<BuyerCode[]>([]);
  const [auctionCodes, setAuctionCodes] = useState<BuyerCode[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadBuyerCodes();
  }, []);

  async function loadBuyerCodes() {
    try {
      const authUser = getAuthUser();
      if (!authUser) {
        router.push('/login');
        return;
      }
      setUser(authUser);

      const res = await apiFetch(`${API_BASE}/auth/buyer-codes?userId=${authUser.userId}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const codes: BuyerCode[] = await res.json();

      // Split into PWS and Auction categories based on buyer_code_type
      const pws = codes.filter(c =>
        c.buyerCodeType === 'Premium_Wholesale' || c.buyerCodeType === 'Wholesale'
      );
      const auction = codes.filter(c =>
        c.buyerCodeType !== 'Premium_Wholesale' && c.buyerCodeType !== 'Wholesale'
      );

      setPwsCodes(pws);
      setAuctionCodes(auction);
    } catch (err) {
      console.error('Failed to load buyer codes', err);
    } finally {
      setLoading(false);
    }
  }

  /**
   * Full navigation flow — mirrors the legacy Mendix nanoflow chain:
   *
   *   ACT_NavigateToBidderDashboard_WithTabId (nanoflow)
   *     └─ Sets BuyerCode on the session/tab helper
   *     └─ Calls ACT_NavigateToBidderDashboard (microflow)
   *          └─ Checks BuyerCodeType:
   *               • Non-PWS (Auction) → ACT_OpenBidderDashboard → /auction
   *               • PWS → ACT_Open_PWS_Order:
   *                    1. Clears previous buyer code sessions
   *                    2. Binds selected code to current session
   *                    3. SUB_NavigateToCounterOffers:
   *                         - If pending Buyer_Acceptance offers → /pws/counter-offers
   *                         - Else → /pws/order (PWSOrder_PE page)
   */
  async function handleCodeSelect(code: BuyerCode, category: 'pws' | 'auction') {
    // Step 1: Persist selected buyer code to session storage
    if (typeof window !== 'undefined') {
      sessionStorage.setItem('selectedBuyerCode', JSON.stringify(code));
      sessionStorage.setItem('buyerCategory', category);
    }

    // Step 2: Route based on buyer code type
    // (Legacy decision: BuyerCodeType != Premium_Wholesale → Auction)
    if (category === 'auction') {
      router.push('/auction');
      return;
    }

    // Step 3: PWS flow — ACT_Open_PWS_Order
    try {
      // 3a. Call backend to bind buyer code to session and check for counter-offers
      //     (Legacy: clears old BuyerCode_Session associations, sets new one,
      //      then calls SUB_NavigateToCounterOffers)
      const res = await apiFetch(`${API_BASE}/pws/session/activate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ buyerCode: code.code }),
      });

      if (res.ok) {
        const data = await res.json();
        // SUB_NavigateToCounterOffers: check if there are pending counter-offers
        if (data.pendingCounterOffers > 0) {
          router.push('/pws/counter-offers');
          return;
        }
      }
    } catch (err) {
      // Backend not yet implemented — continue to default PWS order page
      console.warn('Session activation endpoint not available yet:', err);
    }

    // 3b. Default: navigate to PWSOrder_PE (the inventory/order grid page)
    router.push('/pws/order');
  }

  if (loading) {
    return (
      <div className={styles.pageWrapper}>
        <header className={styles.header}>
          <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} className={styles.logo} />
        </header>
        <div className={styles.contentArea}>
          <p style={{ textAlign: 'center', color: '#112d32' }}>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.pageWrapper}>
      {/* Header */}
      <header className={styles.header}>
        <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} className={styles.logo} />
        <div className={styles.userIconWrapper}>
          <span className={styles.userIcon}>👤</span>
        </div>
      </header>

      {/* Content */}
      <main className={styles.contentArea}>
        <h1 className={styles.welcomeHeading}>
          Welcome {user?.firstName} {user?.lastName}!
        </h1>
        <p className={styles.subtitle}>Choose a buyer code to get started:</p>

        <div className={styles.cardGrid}>
          {/* PWS Card */}
          <div className={styles.card}>
            <Image
              src="/images/pws_card.png"
              alt="Premium Wholesale Devices"
              width={550}
              height={200}
              className={styles.cardImage}
            />
            <div className={styles.cardBody}>
              <h2 className={styles.cardTitle}>Premium Wholesale Devices</h2>
              <p className={styles.cardDescription}>
                Shop consumer-ready, fully functional devices.
              </p>
              <div className={styles.buyerCodeList}>
                {pwsCodes.map((bc) => (
                  <button
                    key={bc.id}
                    className={styles.buyerCodeButton}
                    onClick={() => handleCodeSelect(bc, 'pws')}
                  >
                    <span className={styles.codeLabel}>{bc.code}</span>
                    <span className={styles.buyerName}>{bc.buyerName}</span>
                    <span className={styles.arrowIcon}>→</span>
                  </button>
                ))}
                {pwsCodes.length === 0 && (
                  <p className={styles.noCodes}>No PWS buyer codes available.</p>
                )}
              </div>
            </div>
          </div>

          {/* Auction Card */}
          <div className={styles.card}>
            <Image
              src="/images/auction_card.png"
              alt="Weekly Wholesale Auction"
              width={550}
              height={200}
              className={styles.cardImage}
            />
            <div className={styles.cardBody}>
              <h2 className={styles.cardTitle}>Weekly Wholesale Auction</h2>
              <p className={styles.cardDescription}>
                Bid on devices ready for uplift, repair, &amp; resale.
              </p>
              <div className={styles.buyerCodeList}>
                {auctionCodes.map((bc) => (
                  <button
                    key={bc.id}
                    className={styles.buyerCodeButton}
                    onClick={() => handleCodeSelect(bc, 'auction')}
                  >
                    <span className={styles.codeLabel}>{bc.code}</span>
                    <span className={styles.buyerName}>{bc.buyerName}</span>
                    <span className={styles.arrowIcon}>→</span>
                  </button>
                ))}
                {auctionCodes.length === 0 && (
                  <p className={styles.noCodes}>No auction buyer codes available.</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
