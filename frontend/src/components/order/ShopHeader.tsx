'use client';

import { useEffect, useRef } from 'react';
import styles from '@/app/pws/order/pwsOrder.module.css';

interface Props {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  cartSkuCount: number;
  cartQtyTotal: number;
  cartPriceTotal: number;
  cartSummaryAnimate: boolean;
  onOpenCart: () => void;
  moreMenuOpen: boolean;
  onToggleMoreMenu: () => void;
  onCloseMoreMenu: () => void;
  onResetCart: () => void;
  onUploadExcel: () => void;
  onExportOrderData: () => void;
  onDownloadListing: () => void;
  onExportAllListing: () => void;
}

export function ShopHeader({
  searchTerm,
  onSearchChange,
  cartSkuCount,
  cartQtyTotal,
  cartPriceTotal,
  cartSummaryAnimate,
  onOpenCart,
  moreMenuOpen,
  onToggleMoreMenu,
  onCloseMoreMenu,
  onResetCart,
  onUploadExcel,
  onExportOrderData,
  onDownloadListing,
  onExportAllListing,
}: Props) {
  const moreMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (moreMenuRef.current && !moreMenuRef.current.contains(e.target as Node)) {
        onCloseMoreMenu();
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [onCloseMoreMenu]);

  return (
    <div className={styles.pageHeader}>
      <h2 className={styles.pageTitle}>Shop</h2>

      <div className={styles.searchBar}>
        <input
          type="text"
          placeholder="Search..."
          className={styles.searchInput}
          value={searchTerm}
          onChange={e => onSearchChange(e.target.value)}
        />
        <svg className={styles.searchIconSvg} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
      </div>
      <div className={`${styles.cartSummary} ${cartSummaryAnimate ? styles.animatePulse : ''}`}>
        <div className={styles.cartStat}>
          <span className={styles.cartStatLabel}>SKUs</span>
          <span className={styles.cartStatValue}>{cartSkuCount}</span>
        </div>
        <div className={styles.cartStat}>
          <span className={styles.cartStatLabel}>Qty</span>
          <span className={styles.cartStatValue}>{cartQtyTotal}</span>
        </div>
        <div className={styles.cartStat}>
          <span className={styles.cartStatLabel}>Total</span>
          <span className={styles.cartStatValue}>${cartPriceTotal.toLocaleString()}</span>
        </div>
        <button className={styles.cartBtn} onClick={onOpenCart}>Cart</button>
      </div>
      <div ref={moreMenuRef} style={{ position: 'relative' }}>
        <button className={styles.moreActionsBtn} onClick={onToggleMoreMenu}>&#8942;</button>
        {moreMenuOpen && (
          <div className={styles.moreActionsDropdown}>
            <button className={styles.moreActionsItem} onClick={onResetCart}>Reset Cart</button>
            <button className={styles.moreActionsItem} onClick={onUploadExcel}>Upload Offer Excel</button>
            <button className={styles.moreActionsItem} onClick={onExportOrderData}>Export Order Data</button>
            <button className={styles.moreActionsItem} onClick={onDownloadListing}>Download Listing</button>
            <button className={styles.moreActionsItem} onClick={onExportAllListing}>Export All Listing</button>
          </div>
        )}
      </div>
    </div>
  );
}
