'use client';
import { useEffect, useState, useMemo, useCallback, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './pwsOrder.module.css';
import { InventoryGrid, type DeviceItem, type ColumnFilters, type CartEntry } from '@/components/order/InventoryGrid';
import { CaseLotsGrid, type CaseLotItem, type CLColumnFilters, type CLCartEntry } from '@/components/order/CaseLotsGrid';
import { ShopHeader } from '@/components/order/ShopHeader';
import { pickAndUploadOfferExcel, exportOrderData, downloadFilteredListing, exportAllListing } from '@/components/order/orderExports';
import { useListing, matchesIntFilter, matchesFloatFilter, matchesContains, matchesMultiSelect } from '@/components/order/useListing';
import { apiFetch } from '@/lib/apiFetch';
import { getBuyerCodeId, getUserId } from '@/lib/session';
import { API_BASE } from '@/lib/apiRoutes';

const PAGE_SIZE = 50;
const DEBOUNCE_MS = 300;

const emptyFilters: ColumnFilters = {
  sku: '', category: [], brand: [], model: [],
  carrier: [], capacity: [], color: [], grade: [],
  availableQty: '', price: '',
};

const DEVICE_MULTI_FIELDS = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'] as const;
type MultiSelectField = typeof DEVICE_MULTI_FIELDS[number];

type SortField = keyof DeviceItem | null;
type SortDir = 'asc' | 'desc';

const emptyCLFilters: CLColumnFilters = {
  sku: '', model: [], carrier: [], capacity: [], color: [], grade: [],
  caseLotSize: '', caseLotAtpQty: '', unitPrice: '', caseLotPrice: '',
};

const CL_MULTI_FIELDS = ['model', 'carrier', 'capacity', 'color', 'grade'] as const;
type CLMultiField = typeof CL_MULTI_FIELDS[number];

type CLSortField = keyof CaseLotItem | null;

/** Build query string with buyerCodeId and optional userId for authorization */
function authParams(): string {
  const buyerCodeId = getBuyerCodeId();
  const userId = getUserId();
  let params = `buyerCodeId=${buyerCodeId}`;
  if (userId) params += `&userId=${userId}`;
  return params;
}

export default function PwsOrderPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('functional');
  const [devices, setDevices] = useState<DeviceItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  const [filters, setFilters] = useState<ColumnFilters>(emptyFilters);
  const [sortField, setSortField] = useState<SortField>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');
  const [page, setPage] = useState(1);

  const [cartItems, setCartItems] = useState<Record<string, CartEntry>>({});

  // Case lots state
  const [caseLots, setCaseLots] = useState<CaseLotItem[]>([]);
  const [clFilters, setCLFilters] = useState<CLColumnFilters>(emptyCLFilters);
  const [clSortField, setCLSortField] = useState<CLSortField>(null);
  const [clSortDir, setCLSortDir] = useState<SortDir>('asc');
  const [clPage, setCLPage] = useState(1);
  const [clCartItems, setCLCartItems] = useState<Record<number, CLCartEntry>>({});
  const clDebounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Animation pulse tracking (pws-font-animation equivalent)
  const [animatingSkus, setAnimatingSkus] = useState<Set<string>>(new Set());
  const [cartSummaryAnimate, setCartSummaryAnimate] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  const [moreMenuOpen, setMoreMenuOpen] = useState(false);
  const [showResetConfirm, setShowResetConfirm] = useState(false);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const loadInventory = useCallback(async () => {
    try {
      setSaveError(null);
      const res = await apiFetch(`${API_BASE}/inventory/devices?itemType=PWS&minAtpQty=1&excludeGrade=FMIP`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: Record<string, unknown>[] = await res.json();

      const mapped: DeviceItem[] = data.map((d) => ({
        id: d.id as number,
        sku: (d.sku as string) || 'Unknown',
        category: (d.categoryName as string) || '-',
        brand: (d.brandName as string) || '-',
        model: (d.modelName as string) || '-',
        carrier: (d.carrierName as string) || '-',
        capacity: (d.capacityName as string) || '-',
        color: (d.colorName as string) || '-',
        grade: (d.gradeName as string) || '-',
        availableQty: (d.availableQty as number) || 0,
        listPrice: (d.listPrice as number) || 0,
      }));
      setDevices(mapped);
    } catch {
      setSaveError('Failed to load inventory. Please refresh the page.');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadCaseLots = useCallback(async () => {
    try {
      const res = await apiFetch(`${API_BASE}/inventory/case-lots`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: Record<string, unknown>[] = await res.json();
      const mapped: CaseLotItem[] = data.map((cl) => ({
        id: cl.id as number,
        deviceId: cl.deviceId as number,
        sku: (cl.sku as string) || 'Unknown',
        model: (cl.modelName as string) || '-',
        carrier: (cl.carrierName as string) || '-',
        capacity: (cl.capacityName as string) || '-',
        color: (cl.colorName as string) || '-',
        grade: (cl.gradeName as string) || '-',
        caseLotSize: (cl.caseLotSize as number) || 0,
        caseLotAtpQty: (cl.caseLotAtpQty as number) || 0,
        unitPrice: (cl.unitPrice as number) || 0,
        caseLotPrice: (cl.caseLotPrice as number) || 0,
      }));
      setCaseLots(mapped);
    } catch {
      setSaveError('Failed to load case lots.');
    }
  }, []);

  const loadCart = useCallback(async () => {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart?${authParams()}`);
      if (!res.ok) return;
      const data = await res.json();
      if (data.items && data.items.length > 0) {
        const hydrated: Record<string, CartEntry> = {};
        for (const item of data.items) {
          if (item.quantity > 0) {
            hydrated[item.sku] = { offerPrice: item.price, cartQty: item.quantity };
          }
        }
        setCartItems(hydrated);
      }
    } catch {
      setSaveError('Failed to load cart.');
    }
  }, []);

  // Load inventory, cart, and case lots on mount
  useEffect(() => {
    loadInventory();
    loadCaseLots();
    loadCart();
  }, [loadInventory, loadCaseLots, loadCart]);

  // Refresh cart when buyer code is switched from the top bar
  useEffect(() => {
    function handleBuyerCodeChanged() {
      setCartItems({});
      setCLCartItems({});
      loadCart();
    }
    window.addEventListener('buyerCodeChanged', handleBuyerCodeChanged);
    return () => window.removeEventListener('buyerCodeChanged', handleBuyerCodeChanged);
  }, [loadCart]);

  function saveItemToApi(sku: string, deviceId: number, offerPrice: number, quantity: number) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    setSaveError(null);
    apiFetch(`${API_BASE}/pws/offers/cart/items?${authParams()}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sku, deviceId, offerPrice, quantity }),
    }).catch(() => setSaveError('Failed to save cart item. Your changes may not be persisted.'));
  }

  function saveCaseLotItemToApi(sku: string, deviceId: number, caseLotId: number, offerPrice: number, quantity: number) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    setSaveError(null);
    apiFetch(`${API_BASE}/pws/offers/cart/items?${authParams()}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sku, deviceId, caseLotId, offerPrice, quantity }),
    }).catch(() => setSaveError('Failed to save case lot item. Your changes may not be persisted.'));
  }

  const clPasses = useCallback((cl: CaseLotItem, exclude: CLMultiField | null): boolean => {
    if (cl.caseLotAtpQty <= 0) return false;
    if (!matchesContains(cl.sku, clFilters.sku)) return false;
    for (const f of CL_MULTI_FIELDS) {
      if (f === exclude) continue;
      if (!matchesMultiSelect(cl[f], clFilters[f])) return false;
    }
    if (!matchesIntFilter(cl.caseLotSize, clFilters.caseLotSize)) return false;
    if (!matchesIntFilter(cl.caseLotAtpQty, clFilters.caseLotAtpQty)) return false;
    if (!matchesFloatFilter(cl.unitPrice, clFilters.unitPrice)) return false;
    if (!matchesFloatFilter(cl.caseLotPrice, clFilters.caseLotPrice)) return false;
    return true;
  }, [clFilters]);

  const {
    distinctValues: clDistinctValues,
    filteredRows: filteredCaseLots,
    pageRows: pageCaseLots,
    totalPages: clTotalPages,
    rangeStart: clRangeStart,
    rangeEnd: clRangeEnd,
  } = useListing<CaseLotItem, CLMultiField>({
    rows: caseLots,
    multiFields: CL_MULTI_FIELDS,
    passes: clPasses,
    sortField: clSortField,
    sortDir: clSortDir,
    page: clPage,
    pageSize: PAGE_SIZE,
  });

  const updateCLFilter = useCallback((field: 'sku' | 'caseLotSize' | 'caseLotAtpQty' | 'unitPrice' | 'caseLotPrice', value: string) => {
    setCLFilters(prev => ({ ...prev, [field]: value }));
    setCLPage(1);
  }, []);

  const updateCLMultiFilter = useCallback((field: CLMultiField, value: string[]) => {
    setCLFilters(prev => ({ ...prev, [field]: value }));
    setCLPage(1);
  }, []);

  const handleCLSort = useCallback((field: keyof CaseLotItem) => {
    if (clSortField === field) {
      setCLSortDir(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setCLSortField(field);
      setCLSortDir('asc');
    }
  }, [clSortField]);

  const handleCLCartChange = (cl: CaseLotItem, field: 'numCases' | 'unitOffer', value: string) => {
    let numVal: number;
    if (field === 'numCases') {
      numVal = parseInt(value, 10);
      numVal = isNaN(numVal) ? 0 : Math.max(0, numVal);
    } else {
      numVal = parseFloat(value);
      numVal = isNaN(numVal) ? 0 : Math.max(0, numVal);
    }

    setCLCartItems(prev => {
      const existing = prev[cl.id] || { numCases: 0, unitOffer: cl.unitPrice };
      const updated = { ...existing, [field]: numVal };

      if (field === 'numCases' && numVal > 0 && (updated.unitOffer === 0 || !prev[cl.id])) {
        updated.unitOffer = cl.unitPrice;
      }

      const next = { ...prev, [cl.id]: updated };

      // Trigger animation
      setAnimatingSkus(prev => new Set(prev).add(`cl-${cl.id}`));
      setCartSummaryAnimate(true);
      setTimeout(() => {
        setAnimatingSkus(prev => { const n = new Set(prev); n.delete(`cl-${cl.id}`); return n; });
        setCartSummaryAnimate(false);
      }, 350);

      if (clDebounceRef.current) clearTimeout(clDebounceRef.current);
      clDebounceRef.current = setTimeout(() => {
        saveCaseLotItemToApi(cl.sku, cl.deviceId, cl.id, updated.unitOffer, updated.numCases);
      }, DEBOUNCE_MS);

      return next;
    });
  };


  const devicePasses = useCallback((d: DeviceItem, exclude: MultiSelectField | null): boolean => {
    if (d.availableQty <= 0) return false;
    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      const searchable = [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade].join(' ').toLowerCase();
      if (!searchable.includes(term)) return false;
    }
    if (!matchesContains(d.sku, filters.sku)) return false;
    for (const f of DEVICE_MULTI_FIELDS) {
      if (f === exclude) continue;
      if (!matchesMultiSelect(d[f], filters[f])) return false;
    }
    if (!matchesIntFilter(d.availableQty, filters.availableQty)) return false;
    if (!matchesFloatFilter(d.listPrice, filters.price)) return false;
    return true;
  }, [filters, searchTerm]);

  const {
    distinctValues,
    filteredRows: filteredDevices,
    pageRows: pageDevices,
    totalPages,
    rangeStart,
    rangeEnd,
  } = useListing<DeviceItem, MultiSelectField>({
    rows: devices,
    multiFields: DEVICE_MULTI_FIELDS,
    passes: devicePasses,
    sortField,
    sortDir,
    page,
    pageSize: PAGE_SIZE,
  });

  const updateFilter = useCallback((field: 'sku' | 'availableQty' | 'price', value: string) => {
    setFilters(prev => ({ ...prev, [field]: value }));
    setPage(1);
  }, []);

  const updateMultiFilter = useCallback((field: MultiSelectField, value: string[]) => {
    setFilters(prev => ({ ...prev, [field]: value }));
    setPage(1);
  }, []);

  const handleSort = useCallback((field: keyof DeviceItem) => {
    if (sortField === field) {
      setSortDir(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  }, [sortField]);

  const handleCartChange = (device: DeviceItem, field: 'offerPrice' | 'cartQty', value: string) => {
    let numVal: number;
    if (field === 'cartQty') {
      numVal = parseInt(value, 10);
      numVal = isNaN(numVal) ? 0 : Math.max(0, numVal);
    } else {
      numVal = parseFloat(value);
      numVal = isNaN(numVal) ? 0 : Math.max(0, numVal);
    }

    setCartItems(prev => {
      const existing = prev[device.sku] || { offerPrice: device.listPrice, cartQty: 0 };
      const updated = { ...existing, [field]: numVal };

      // Re-default offerPrice to listPrice when price is 0/empty or first setting qty
      if (field === 'cartQty' && numVal > 0 && (updated.offerPrice === 0 || !prev[device.sku])) {
        updated.offerPrice = device.listPrice;
      }
      if (field === 'offerPrice' && numVal === 0 && updated.cartQty > 0) {
        updated.offerPrice = device.listPrice;
      }

      const next = { ...prev, [device.sku]: updated };

      // Trigger animation pulse
      setAnimatingSkus(prev => new Set(prev).add(device.sku));
      setCartSummaryAnimate(true);
      setTimeout(() => {
        setAnimatingSkus(prev => { const n = new Set(prev); n.delete(device.sku); return n; });
        setCartSummaryAnimate(false);
      }, 350);

      if (debounceRef.current) clearTimeout(debounceRef.current);
      debounceRef.current = setTimeout(() => {
        saveItemToApi(device.sku, device.id, updated.offerPrice, updated.cartQty);
      }, DEBOUNCE_MS);

      return next;
    });
  };

  // Cart totals
  const cartSkuCount = Object.values(cartItems).filter(c => c.cartQty > 0).length;
  const cartQtyTotal = Object.values(cartItems).reduce((sum, c) => sum + c.cartQty, 0);
  const cartPriceTotal = Object.entries(cartItems).reduce((sum, [, c]) => sum + (c.offerPrice * c.cartQty), 0);

  // ── Three-dots "More Actions" menu ──
  // Matches Mendix PWSOrder_PE: Reset, Upload Offer Excel, Export Order Data, Download Listing, Export All Listing

  // 1. Reset Cart → confirmation dialog → DELETE /cart
  async function handleResetCart() {
    setShowResetConfirm(false);
    setMoreMenuOpen(false);
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      await apiFetch(`${API_BASE}/pws/offers/cart?${authParams()}`, { method: 'DELETE' });
      setCartItems({});
      setCLCartItems({});
    } catch (err) {
      console.error('Reset failed', err);
    }
  }

  function handleUploadExcel() {
    setMoreMenuOpen(false);
    pickAndUploadOfferExcel(authParams(), loadCart);
  }

  function handleExportOrderData() {
    setMoreMenuOpen(false);
    if (!getBuyerCodeId()) return;
    void exportOrderData(authParams());
  }

  function handleDownloadListing() {
    setMoreMenuOpen(false);
    downloadFilteredListing(activeTab, filteredDevices, filteredCaseLots);
  }

  function handleExportAllListing() {
    setMoreMenuOpen(false);
    exportAllListing(devices);
  }

  if (loading) {
    return <div className={styles.pageWrapper}>Loading inventory...</div>;
  }

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.contentArea}>
        {saveError && (
          <div style={{ padding: '8px 16px', background: '#fef2f2', color: '#991b1b', borderBottom: '1px solid #fecaca', fontSize: '14px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{saveError}</span>
            <button type="button" onClick={() => setSaveError(null)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#991b1b', fontWeight: 'bold' }}>×</button>
          </div>
        )}
        <ShopHeader
          searchTerm={searchTerm}
          onSearchChange={v => { setSearchTerm(v); setPage(1); }}
          cartSkuCount={cartSkuCount}
          cartQtyTotal={cartQtyTotal}
          cartPriceTotal={cartPriceTotal}
          cartSummaryAnimate={cartSummaryAnimate}
          onOpenCart={() => router.push('/pws/cart')}
          moreMenuOpen={moreMenuOpen}
          onToggleMoreMenu={() => setMoreMenuOpen(p => !p)}
          onCloseMoreMenu={() => setMoreMenuOpen(false)}
          onResetCart={() => { setMoreMenuOpen(false); setShowResetConfirm(true); }}
          onUploadExcel={handleUploadExcel}
          onExportOrderData={handleExportOrderData}
          onDownloadListing={handleDownloadListing}
          onExportAllListing={handleExportAllListing}
        />

        {/* Tabs */}
        <div className={styles.tabsContainer}>
          <div className={styles.tabList}>
            <div className={`${styles.tabItem} ${activeTab === 'functional' ? styles.active : ''}`} onClick={() => setActiveTab('functional')}>
              Functional Devices
            </div>
            <div className={`${styles.tabItem} ${activeTab === 'caselots' ? styles.active : ''}`} onClick={() => setActiveTab('caselots')}>
              Functional Case Lots
            </div>
          </div>

          <div className={styles.gridWrapper}>
          <div className={styles.gridContainer}>
            {activeTab === 'functional' && (
              <InventoryGrid
                pageRows={pageDevices}
                filters={filters}
                distinctValues={distinctValues}
                sortField={sortField}
                sortDir={sortDir}
                cartItems={cartItems}
                animatingSkus={animatingSkus}
                page={page}
                totalPages={totalPages}
                rangeStart={rangeStart}
                rangeEnd={rangeEnd}
                totalRows={filteredDevices.length}
                onSort={handleSort}
                onFilterChange={updateFilter}
                onMultiFilterChange={updateMultiFilter}
                onCartChange={handleCartChange}
                onPageChange={setPage}
              />
            )}

            {activeTab === 'caselots' && (
              <CaseLotsGrid
                pageRows={pageCaseLots}
                filters={clFilters}
                distinctValues={clDistinctValues}
                sortField={clSortField}
                sortDir={clSortDir}
                cartItems={clCartItems}
                animatingSkus={animatingSkus}
                page={clPage}
                totalPages={clTotalPages}
                rangeStart={clRangeStart}
                rangeEnd={clRangeEnd}
                totalRows={filteredCaseLots.length}
                onSort={handleCLSort}
                onFilterChange={updateCLFilter}
                onMultiFilterChange={updateCLMultiFilter}
                onCartChange={handleCLCartChange}
                onPageChange={setCLPage}
              />
            )}
          </div>
          </div>
        </div>
      </div>

      {/* Reset Cart Confirmation */}
      {showResetConfirm && (
        <div className={styles.confirmOverlay}>
          <div className={styles.confirmDialog}>
            <h3>Reset Cart</h3>
            <p>Are you sure you want to clear all items from your cart?</p>
            <div className={styles.confirmActions}>
              <button className={styles.confirmCancel} onClick={() => setShowResetConfirm(false)}>Cancel</button>
              <button className={styles.confirmOk} onClick={handleResetCart}>Reset</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
