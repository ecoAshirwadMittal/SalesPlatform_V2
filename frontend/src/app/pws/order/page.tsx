'use client';
import { useEffect, useState, useMemo, useCallback, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './pwsOrder.module.css';
import MultiSelectFilter from './MultiSelectFilter';
import { apiFetch } from '@/lib/apiFetch';

const API_BASE = '/api/v1';
const PAGE_SIZE = 50;
const DEBOUNCE_MS = 300;

interface DeviceItem {
  id: number;
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  availableQty: number;
  listPrice: number;
}

interface ColumnFilters {
  sku: string;
  category: string[];
  brand: string[];
  model: string[];
  carrier: string[];
  capacity: string[];
  color: string[];
  grade: string[];
  availableQty: string;
  price: string;
}

const emptyFilters: ColumnFilters = {
  sku: '', category: [], brand: [], model: [],
  carrier: [], capacity: [], color: [], grade: [],
  availableQty: '', price: '',
};

type MultiSelectField = 'category' | 'brand' | 'model' | 'carrier' | 'capacity' | 'color' | 'grade';

type SortField = keyof DeviceItem | null;
type SortDir = 'asc' | 'desc';

interface CaseLotItem {
  id: number;
  deviceId: number;
  sku: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  caseLotSize: number;
  caseLotAtpQty: number;
  unitPrice: number;
  caseLotPrice: number;
}

interface CLColumnFilters {
  sku: string;
  model: string[];
  carrier: string[];
  capacity: string[];
  color: string[];
  grade: string[];
  caseLotSize: string;
  caseLotAtpQty: string;
  unitPrice: string;
  caseLotPrice: string;
}

const emptyCLFilters: CLColumnFilters = {
  sku: '', model: [], carrier: [], capacity: [], color: [], grade: [],
  caseLotSize: '', caseLotAtpQty: '', unitPrice: '', caseLotPrice: '',
};

type CLSortField = keyof CaseLotItem | null;

interface CLCartEntry {
  numCases: number;
  unitOffer: number;
}

interface CartEntry {
  offerPrice: number;
  cartQty: number;
}

interface BuyerCode {
  id: number;
  code: string;
  buyerName: string;
  buyerCodeType: string;
}

function getBuyerCodeId(): number | null {
  if (typeof window === 'undefined') return null;
  try {
    const stored = sessionStorage.getItem('selectedBuyerCode');
    if (stored) return JSON.parse(stored).id;
  } catch { /* ignore */ }
  return null;
}

function getUserId(): number | null {
  if (typeof window === 'undefined') return null;
  try {
    const stored = localStorage.getItem('auth_user');
    if (stored) return JSON.parse(stored).userId;
  } catch { /* ignore */ }
  return null;
}

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

  const [moreMenuOpen, setMoreMenuOpen] = useState(false);
  const [showResetConfirm, setShowResetConfirm] = useState(false);
  const [buyerCodes, setBuyerCodes] = useState<BuyerCode[]>([]);
  const [selectedBuyerCode, setSelectedBuyerCode] = useState<BuyerCode | null>(null);
  const [buyerDropdownOpen, setBuyerDropdownOpen] = useState(false);
  const [buyerSearchTerm, setBuyerSearchTerm] = useState('');
  const moreMenuRef = useRef<HTMLDivElement>(null);
  const buyerDropdownRef = useRef<HTMLDivElement>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Load inventory, cart, case lots, and buyer codes on mount
  useEffect(() => {
    loadInventory();
    loadCaseLots();
    loadCart();
    loadBuyerCodes();
  }, []);

  // Close dropdowns on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (moreMenuRef.current && !moreMenuRef.current.contains(e.target as Node)) {
        setMoreMenuOpen(false);
      }
      if (buyerDropdownRef.current && !buyerDropdownRef.current.contains(e.target as Node)) {
        setBuyerDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  async function loadInventory() {
    try {
      const res = await apiFetch(`${API_BASE}/inventory/devices?itemType=PWS&minAtpQty=1&excludeGrade=FMIP`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();

      const mapped: DeviceItem[] = data.map((d: any) => ({
        id: d.id,
        sku: d.sku || 'Unknown',
        category: d.categoryName || '-',
        brand: d.brandName || '-',
        model: d.modelName || '-',
        carrier: d.carrierName || '-',
        capacity: d.capacityName || '-',
        color: d.colorName || '-',
        grade: d.gradeName || '-',
        availableQty: d.availableQty || 0,
        listPrice: d.listPrice || 0,
      }));
      setDevices(mapped);
    } catch (err) {
      console.error('Failed to load inventory', err);
    } finally {
      setLoading(false);
    }
  }

  async function loadCaseLots() {
    try {
      const res = await apiFetch(`${API_BASE}/inventory/case-lots`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const mapped: CaseLotItem[] = data.map((cl: any) => ({
        id: cl.id,
        deviceId: cl.deviceId,
        sku: cl.sku || 'Unknown',
        model: cl.modelName || '-',
        carrier: cl.carrierName || '-',
        capacity: cl.capacityName || '-',
        color: cl.colorName || '-',
        grade: cl.gradeName || '-',
        caseLotSize: cl.caseLotSize || 0,
        caseLotAtpQty: cl.caseLotAtpQty || 0,
        unitPrice: cl.unitPrice || 0,
        caseLotPrice: cl.caseLotPrice || 0,
      }));
      setCaseLots(mapped);
    } catch (err) {
      console.error('Failed to load case lots', err);
    }
  }

  async function loadCart() {
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
    } catch (err) {
      console.error('Failed to load cart', err);
    }
  }

  async function loadBuyerCodes() {
    const userId = getUserId();
    if (!userId) return;
    try {
      const res = await apiFetch(`${API_BASE}/auth/buyer-codes?userId=${userId}`);
      if (!res.ok) return;
      const codes: BuyerCode[] = await res.json();
      // Only PWS/Wholesale codes (matching buyer-select page logic)
      const pwsCodes = codes.filter(c =>
        c.buyerCodeType === 'Premium_Wholesale' || c.buyerCodeType === 'Wholesale'
      );
      setBuyerCodes(pwsCodes);

      // Set current selection from sessionStorage, or auto-select first code
      const stored = sessionStorage.getItem('selectedBuyerCode');
      if (stored) {
        const parsed = JSON.parse(stored);
        const match = pwsCodes.find(c => c.id === parsed.id);
        if (match) setSelectedBuyerCode(match);
        else if (pwsCodes.length > 0) {
          setSelectedBuyerCode(pwsCodes[0]);
          sessionStorage.setItem('selectedBuyerCode', JSON.stringify({ id: pwsCodes[0].id, code: pwsCodes[0].code }));
        }
      } else if (pwsCodes.length > 0) {
        setSelectedBuyerCode(pwsCodes[0]);
        sessionStorage.setItem('selectedBuyerCode', JSON.stringify({ id: pwsCodes[0].id, code: pwsCodes[0].code }));
      }
    } catch (err) {
      console.error('Failed to load buyer codes', err);
    }
  }

  function handleBuyerCodeSwitch(bc: BuyerCode) {
    setBuyerDropdownOpen(false);
    setBuyerSearchTerm('');
    setSelectedBuyerCode(bc);
    // Update sessionStorage so authParams() picks up the new buyerCodeId
    sessionStorage.setItem('selectedBuyerCode', JSON.stringify({ id: bc.id, code: bc.code }));
    // Notify layout top bar to update the buyer code badge
    window.dispatchEvent(new Event('buyerCodeChanged'));
    // Clear current cart and reload for the new buyer code
    setCartItems({});
    loadCart();
  }

  function saveItemToApi(sku: string, deviceId: number, offerPrice: number, quantity: number) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    apiFetch(`${API_BASE}/pws/offers/cart/items?${authParams()}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sku, deviceId, offerPrice, quantity }),
    }).catch(err => console.error('Failed to save cart item', err));
  }

  function saveCaseLotItemToApi(sku: string, deviceId: number, caseLotId: number, offerPrice: number, quantity: number) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    apiFetch(`${API_BASE}/pws/offers/cart/items?${authParams()}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sku, deviceId, caseLotId, offerPrice, quantity }),
    }).catch(err => console.error('Failed to save case lot cart item', err));
  }

  // ── Case lots: distinct values, filtering, sorting, pagination ──
  type CLMultiField = 'model' | 'carrier' | 'capacity' | 'color' | 'grade';
  const clDistinctValues = useMemo(() => {
    const fields: CLMultiField[] = ['model', 'carrier', 'capacity', 'color', 'grade'];
    const result: Record<string, string[]> = {};
    for (const field of fields) {
      const filtered = caseLots.filter(cl => {
        if (cl.caseLotAtpQty <= 0) return false;
        for (const f of fields) {
          if (f === field) continue;
          if (clFilters[f].length > 0 && !clFilters[f].includes(cl[f])) return false;
        }
        if (clFilters.sku && !cl.sku.toLowerCase().includes(clFilters.sku.toLowerCase())) return false;
        return true;
      });
      const vals = new Set<string>();
      filtered.forEach(cl => { if (cl[field] && cl[field] !== '-') vals.add(cl[field]); });
      result[field] = Array.from(vals).sort();
    }
    return result;
  }, [caseLots, clFilters]);

  const filteredCaseLots = useMemo(() => {
    let result = caseLots.filter(cl => {
      if (cl.caseLotAtpQty <= 0) return false;
      if (clFilters.sku && !cl.sku.toLowerCase().includes(clFilters.sku.toLowerCase())) return false;
      if (clFilters.model.length > 0 && !clFilters.model.includes(cl.model)) return false;
      if (clFilters.carrier.length > 0 && !clFilters.carrier.includes(cl.carrier)) return false;
      if (clFilters.capacity.length > 0 && !clFilters.capacity.includes(cl.capacity)) return false;
      if (clFilters.color.length > 0 && !clFilters.color.includes(cl.color)) return false;
      if (clFilters.grade.length > 0 && !clFilters.grade.includes(cl.grade)) return false;
      if (clFilters.caseLotSize) {
        const num = parseInt(clFilters.caseLotSize, 10);
        if (!isNaN(num) && cl.caseLotSize !== num) return false;
      }
      if (clFilters.caseLotAtpQty) {
        const num = parseInt(clFilters.caseLotAtpQty, 10);
        if (!isNaN(num) && cl.caseLotAtpQty !== num) return false;
      }
      if (clFilters.unitPrice) {
        const num = parseFloat(clFilters.unitPrice);
        if (!isNaN(num) && cl.unitPrice !== num) return false;
      }
      if (clFilters.caseLotPrice) {
        const num = parseFloat(clFilters.caseLotPrice);
        if (!isNaN(num) && cl.caseLotPrice !== num) return false;
      }
      return true;
    });

    if (clSortField) {
      result = [...result].sort((a, b) => {
        const aVal = a[clSortField];
        const bVal = b[clSortField];
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return clSortDir === 'asc' ? aVal - bVal : bVal - aVal;
        }
        const cmp = String(aVal).toLowerCase().localeCompare(String(bVal).toLowerCase());
        return clSortDir === 'asc' ? cmp : -cmp;
      });
    }
    return result;
  }, [caseLots, clFilters, clSortField, clSortDir]);

  const clTotalPages = Math.max(1, Math.ceil(filteredCaseLots.length / PAGE_SIZE));
  const pageCaseLots = useMemo(() => {
    const start = (clPage - 1) * PAGE_SIZE;
    return filteredCaseLots.slice(start, start + PAGE_SIZE);
  }, [filteredCaseLots, clPage]);

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

  const clRangeStart = filteredCaseLots.length === 0 ? 0 : (clPage - 1) * PAGE_SIZE + 1;
  const clRangeEnd = Math.min(clPage * PAGE_SIZE, filteredCaseLots.length);

  function clSortIcon(field: keyof CaseLotItem) {
    return (
      <span className={styles.sortIcon}>
        <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
          <path d="M5 0L9.33 5H0.67L5 0Z" fill={clSortField === field && clSortDir === 'asc' ? '#1a2e35' : '#bbb'} />
          <path d="M5 14L0.67 9H9.33L5 14Z" fill={clSortField === field && clSortDir === 'desc' ? '#1a2e35' : '#bbb'} />
        </svg>
      </span>
    );
  }

  // Distinct values for dropdown filters — cascading: each dropdown shows only
  // values that exist in the dataset after applying all OTHER active filters.
  const distinctValues = useMemo(() => {
    const fields: MultiSelectField[] = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'];
    const result: Record<string, string[]> = {};
    for (const field of fields) {
      const filtered = devices.filter(d => {
        if (d.availableQty <= 0) return false;
        if (searchTerm) {
          const term = searchTerm.toLowerCase();
          const searchable = [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade].join(' ').toLowerCase();
          if (!searchable.includes(term)) return false;
        }
        for (const f of fields) {
          if (f === field) continue;
          if (filters[f].length > 0 && !filters[f].includes(d[f])) return false;
        }
        if (filters.sku && !d.sku.toLowerCase().includes(filters.sku.toLowerCase())) return false;
        if (filters.availableQty) {
          const num = parseInt(filters.availableQty, 10);
          if (!isNaN(num) && d.availableQty !== num) return false;
        }
        if (filters.price) {
          const num = parseFloat(filters.price);
          if (!isNaN(num) && d.listPrice !== num) return false;
        }
        return true;
      });
      const vals = new Set<string>();
      filtered.forEach(d => { if (d[field] && d[field] !== '-') vals.add(d[field]); });
      result[field] = Array.from(vals).sort();
    }
    return result;
  }, [devices, filters, searchTerm]);

  // Filter + sort
  const filteredDevices = useMemo(() => {
    let result = devices.filter(d => {
      if (d.availableQty <= 0) return false;

      // Global search across all text columns
      if (searchTerm) {
        const term = searchTerm.toLowerCase();
        const searchable = [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade].join(' ').toLowerCase();
        if (!searchable.includes(term)) return false;
      }

      if (filters.sku && !d.sku.toLowerCase().includes(filters.sku.toLowerCase())) return false;
      if (filters.category.length > 0 && !filters.category.includes(d.category)) return false;
      if (filters.brand.length > 0 && !filters.brand.includes(d.brand)) return false;
      if (filters.model.length > 0 && !filters.model.includes(d.model)) return false;
      if (filters.carrier.length > 0 && !filters.carrier.includes(d.carrier)) return false;
      if (filters.capacity.length > 0 && !filters.capacity.includes(d.capacity)) return false;
      if (filters.color.length > 0 && !filters.color.includes(d.color)) return false;
      if (filters.grade.length > 0 && !filters.grade.includes(d.grade)) return false;
      if (filters.availableQty) {
        const num = parseInt(filters.availableQty, 10);
        if (!isNaN(num) && d.availableQty !== num) return false;
      }
      if (filters.price) {
        const num = parseFloat(filters.price);
        if (!isNaN(num) && d.listPrice !== num) return false;
      }
      return true;
    });

    if (sortField) {
      result = [...result].sort((a, b) => {
        const aVal = a[sortField];
        const bVal = b[sortField];
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return sortDir === 'asc' ? aVal - bVal : bVal - aVal;
        }
        const aStr = String(aVal).toLowerCase();
        const bStr = String(bVal).toLowerCase();
        const cmp = aStr.localeCompare(bStr);
        return sortDir === 'asc' ? cmp : -cmp;
      });
    }

    return result;
  }, [devices, filters, sortField, sortDir, searchTerm]);

  const totalPages = Math.max(1, Math.ceil(filteredDevices.length / PAGE_SIZE));
  const pageDevices = useMemo(() => {
    const start = (page - 1) * PAGE_SIZE;
    return filteredDevices.slice(start, start + PAGE_SIZE);
  }, [filteredDevices, page]);

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

  const calculateTotal = (sku: string) => {
    const item = cartItems[sku];
    if (!item) return 0;
    return item.offerPrice * item.cartQty;
  };

  // Cart totals
  const cartSkuCount = Object.values(cartItems).filter(c => c.cartQty > 0).length;
  const cartQtyTotal = Object.values(cartItems).reduce((sum, c) => sum + c.cartQty, 0);
  const cartPriceTotal = Object.entries(cartItems).reduce((sum, [, c]) => sum + (c.offerPrice * c.cartQty), 0);

  const rangeStart = filteredDevices.length === 0 ? 0 : (page - 1) * PAGE_SIZE + 1;
  const rangeEnd = Math.min(page * PAGE_SIZE, filteredDevices.length);

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

  // 2. Upload Offer Excel → file picker → POST upload (stubbed for now)
  function handleUploadExcel() {
    setMoreMenuOpen(false);
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xlsx,.xls,.csv';
    input.onchange = async (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      try {
        const res = await apiFetch(`${API_BASE}/pws/offers/cart/import?${authParams()}`, {
          method: 'POST',
          body: formData,
        });
        if (res.ok) {
          loadCart();
        } else {
          console.error('Upload failed:', res.status);
        }
      } catch (err) {
        console.error('Upload failed', err);
      }
    };
    input.click();
  }

  // 3. Export Order Data → exports cart items to Excel/CSV (Mendix ACT_OrderDataExport_ExportExcel)
  async function handleExportOrderData() {
    setMoreMenuOpen(false);
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart/export?${authParams()}`);
      if (!res.ok) throw new Error('Export failed');
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${selectedBuyerCode?.code || 'order'}_${new Date().toISOString().slice(0, 10).replace(/-/g, '')}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Export failed', err);
    }
  }

  // 4. Download Listing → exports current datagrid view (filtered) as CSV
  function handleDownloadListing() {
    setMoreMenuOpen(false);
    const source = activeTab === 'caselots' ? filteredCaseLots : filteredDevices;
    let csv = '';

    if (activeTab === 'caselots') {
      csv = 'SKU,Model Family,Carrier,Capacity,Color,Grade,Case Pack Qty,Avl. Cases,Unit Price,Case Price\n';
      for (const cl of source as CaseLotItem[]) {
        csv += [cl.sku, cl.model, cl.carrier, cl.capacity, cl.color, cl.grade,
                cl.caseLotSize, cl.caseLotAtpQty, cl.unitPrice, cl.caseLotPrice].join(',') + '\n';
      }
    } else {
      csv = 'SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Avl. Qty,Price\n';
      for (const d of source as DeviceItem[]) {
        csv += [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade,
                d.availableQty, d.listPrice].join(',') + '\n';
      }
    }

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `listing_${activeTab}_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

  // 5. Export All Listing → exports ALL active devices (unfiltered) as CSV
  function handleExportAllListing() {
    setMoreMenuOpen(false);
    let csv = 'SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Avl. Qty,Price\n';
    for (const d of devices) {
      if (d.availableQty <= 0) continue;
      csv += [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade,
              d.availableQty, d.listPrice].join(',') + '\n';
    }

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${selectedBuyerCode?.code || 'all'}_listing_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

  function sortIcon(field: keyof DeviceItem) {
    return (
      <span className={styles.sortIcon}>
        <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
          <path d="M5 0L9.33 5H0.67L5 0Z" fill={sortField === field && sortDir === 'asc' ? '#1a2e35' : '#bbb'} />
          <path d="M5 14L0.67 9H9.33L5 14Z" fill={sortField === field && sortDir === 'desc' ? '#1a2e35' : '#bbb'} />
        </svg>
      </span>
    );
  }

  if (loading) {
    return <div className={styles.pageWrapper}>Loading inventory...</div>;
  }

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.contentArea}>
        {/* Shop header */}
        <div className={styles.pageHeader}>
          <h2 className={styles.pageTitle}>Shop</h2>

          {/* Buyer code switcher — always shown so user sees active buyer code */}
          {buyerCodes.length >= 1 && (
            <div className={styles.buyerSwitcher} ref={buyerDropdownRef}>
              <button
                className={styles.buyerSwitcherBtn}
                onClick={() => setBuyerDropdownOpen(p => !p)}
              >
                <span className={styles.buyerSwitcherCode}>{selectedBuyerCode?.code || '—'}</span>
                <span className={styles.buyerSwitcherName}>{selectedBuyerCode?.buyerName || ''}</span>
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="6 9 12 15 18 9"/></svg>
              </button>
              {buyerDropdownOpen && (
                <div className={styles.buyerDropdown}>
                  <input
                    type="text"
                    className={styles.buyerDropdownSearch}
                    placeholder="Search buyer codes..."
                    value={buyerSearchTerm}
                    onChange={e => setBuyerSearchTerm(e.target.value)}
                    autoFocus
                  />
                  <div className={styles.buyerDropdownList}>
                    {buyerCodes
                      .filter(bc => {
                        if (!buyerSearchTerm) return true;
                        const term = buyerSearchTerm.toLowerCase();
                        return bc.code.toLowerCase().includes(term) || bc.buyerName.toLowerCase().includes(term);
                      })
                      .map(bc => (
                        <button
                          key={bc.id}
                          className={`${styles.buyerDropdownItem} ${bc.id === selectedBuyerCode?.id ? styles.buyerDropdownItemActive : ''}`}
                          onClick={() => handleBuyerCodeSwitch(bc)}
                        >
                          <span className={styles.buyerDropdownCode}>{bc.code}</span>
                          <span className={styles.buyerDropdownName}>{bc.buyerName}</span>
                        </button>
                      ))
                    }
                  </div>
                </div>
              )}
            </div>
          )}

          <div className={styles.searchBar}>
            <input
              type="text"
              placeholder="Search..."
              className={styles.searchInput}
              value={searchTerm}
              onChange={e => { setSearchTerm(e.target.value); setPage(1); }}
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
            <button className={styles.cartBtn} onClick={() => router.push('/pws/cart')}>Cart</button>
          </div>
          <div ref={moreMenuRef} style={{ position: 'relative' }}>
            <button className={styles.moreActionsBtn} onClick={() => setMoreMenuOpen(p => !p)}>&#8942;</button>
            {moreMenuOpen && (
              <div className={styles.moreActionsDropdown}>
                <button className={styles.moreActionsItem} onClick={() => { setMoreMenuOpen(false); setShowResetConfirm(true); }}>Reset Cart</button>
                <button className={styles.moreActionsItem} onClick={handleUploadExcel}>Upload Offer Excel</button>
                <button className={styles.moreActionsItem} onClick={handleExportOrderData}>Export Order Data</button>
                <button className={styles.moreActionsItem} onClick={handleDownloadListing}>Download Listing</button>
                <button className={styles.moreActionsItem} onClick={handleExportAllListing}>Export All Listing</button>
              </div>
            )}
          </div>
        </div>

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
              <>
                <table className={styles.datagrid}>
                  <thead>
                    <tr>
                      <th className={styles.colSku} onClick={() => handleSort('sku')}>SKU {sortIcon('sku')}</th>
                      <th onClick={() => handleSort('category')}>Category {sortIcon('category')}</th>
                      <th onClick={() => handleSort('brand')}>Brand {sortIcon('brand')}</th>
                      <th className={styles.colModel} onClick={() => handleSort('model')}>Model Family {sortIcon('model')}</th>
                      <th onClick={() => handleSort('carrier')}>Carrier {sortIcon('carrier')}</th>
                      <th onClick={() => handleSort('capacity')}>Capacity {sortIcon('capacity')}</th>
                      <th onClick={() => handleSort('color')}>Color {sortIcon('color')}</th>
                      <th onClick={() => handleSort('grade')}>Grade {sortIcon('grade')}</th>
                      <th onClick={() => handleSort('availableQty')}>Avl. Qty {sortIcon('availableQty')}</th>
                      <th onClick={() => handleSort('listPrice')}>Price {sortIcon('listPrice')}</th>
                      <th className={styles.colOfferPrice}>Offer Price</th>
                      <th className={styles.colCartQty}>Cart Qty</th>
                      <th className={styles.colTotal}>Total</th>
                    </tr>
                    <tr className={styles.filterRow}>
                      <th><input className={styles.filterInput} type="text" value={filters.sku} onChange={e => updateFilter('sku', e.target.value)} /></th>
                      <th><MultiSelectFilter options={distinctValues.category || []} selected={filters.category} onChange={v => updateMultiFilter('category', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.brand || []} selected={filters.brand} onChange={v => updateMultiFilter('brand', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.model || []} selected={filters.model} onChange={v => updateMultiFilter('model', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.carrier || []} selected={filters.carrier} onChange={v => updateMultiFilter('carrier', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.capacity || []} selected={filters.capacity} onChange={v => updateMultiFilter('capacity', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.color || []} selected={filters.color} onChange={v => updateMultiFilter('color', v)} /></th>
                      <th><MultiSelectFilter options={distinctValues.grade || []} selected={filters.grade} onChange={v => updateMultiFilter('grade', v)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.availableQty} onChange={e => updateFilter('availableQty', e.target.value)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.price} onChange={e => updateFilter('price', e.target.value)} /></th>
                      <th></th>
                      <th></th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {pageDevices.map(device => {
                      const cart = cartItems[device.sku];
                      const hasQty = cart && cart.cartQty > 0;
                      const hasPrice = cart && cart.offerPrice > 0;
                      const isExceedQty = hasQty && device.availableQty <= 100 && cart.cartQty > device.availableQty;
                      const isAnimating = animatingSkus.has(device.sku);

                      // CAL_BuyerOfferItem_CSSStyle: color-coding based on offer vs list price
                      let priceClass = '';
                      if (hasPrice && hasQty && device.listPrice > 0) {
                        priceClass = cart.offerPrice < device.listPrice
                          ? styles.offerPriceOrange
                          : styles.offerPriceGreen;
                      }

                      return (
                        <tr key={device.sku} className={isAnimating ? styles.animatePulse : ''}>
                          <td>{device.sku}</td>
                          <td>{device.category}</td>
                          <td>{device.brand}</td>
                          <td>{device.model}</td>
                          <td>{device.carrier}</td>
                          <td>{device.capacity}</td>
                          <td>{device.color}</td>
                          <td>{device.grade}</td>
                          <td>{device.availableQty > 100 ? '100+' : device.availableQty}</td>
                          <td>${device.listPrice.toLocaleString()}</td>
                          <td className={`${styles.userDataCell} ${priceClass}`}>
                            {hasQty ? (
                              <div className={styles.textDollar}>
                                <input
                                  type="number"
                                  className={styles.numberInput}
                                  value={cart?.offerPrice || ''}
                                  onChange={e => handleCartChange(device, 'offerPrice', e.target.value)}
                                  placeholder=""
                                  min={0}
                                  step="0.01"
                                />
                              </div>
                            ) : (
                              <span className={styles.offerPriceDefault}>${device.listPrice.toLocaleString()}</span>
                            )}
                          </td>
                          <td className={`${styles.userDataCell} ${hasQty ? styles.cartQtyActive : ''}`}>
                            <input
                              type="number"
                              className={`${styles.numberInput} ${isExceedQty ? styles.exceedQtyInput : ''}`}
                              value={cart?.cartQty || ''}
                              onChange={e => handleCartChange(device, 'cartQty', e.target.value)}
                              placeholder=""
                              min={0}
                              step="1"
                              max={device.availableQty}
                            />
                            {isExceedQty && <div className={styles.exceedQtyHint}>Exceeds available</div>}
                          </td>
                          <td className={styles.colTotal}>{hasQty ? `$${calculateTotal(device.sku).toLocaleString()}` : ''}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>

                <div className={styles.pagination}>
                  <button className={styles.pageBtn} disabled={page <= 1} onClick={() => setPage(1)} title="First">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7"/><polyline points="18 17 13 12 18 7"/></svg>
                  </button>
                  <button className={styles.pageBtn} disabled={page <= 1} onClick={() => setPage(p => p - 1)} title="Previous">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6"/></svg>
                  </button>
                  <span className={styles.pageInfo}>{rangeStart} to {rangeEnd} of {filteredDevices.length}</span>
                  <button className={styles.pageBtn} disabled={page >= totalPages} onClick={() => setPage(p => p + 1)} title="Next">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                  </button>
                  <button className={styles.pageBtn} disabled={page >= totalPages} onClick={() => setPage(totalPages)} title="Last">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7"/><polyline points="6 17 11 12 6 7"/></svg>
                  </button>
                </div>
              </>
            )}

            {activeTab === 'caselots' && (
              <>
                <div className={styles.caseLotDisclaimer}>
                  Case lots are sold AS IS. All sales are final.
                </div>
                <table className={styles.datagrid}>
                  <thead>
                    <tr>
                      <th className={styles.colSku} onClick={() => handleCLSort('sku')}>SKU {clSortIcon('sku')}</th>
                      <th className={styles.colModel} onClick={() => handleCLSort('model')}>Model Family {clSortIcon('model')}</th>
                      <th onClick={() => handleCLSort('carrier')}>Carrier {clSortIcon('carrier')}</th>
                      <th onClick={() => handleCLSort('capacity')}>Capacity {clSortIcon('capacity')}</th>
                      <th onClick={() => handleCLSort('color')}>Color {clSortIcon('color')}</th>
                      <th onClick={() => handleCLSort('grade')}>Grade {clSortIcon('grade')}</th>
                      <th onClick={() => handleCLSort('caseLotSize')}>Case Pack Qty {clSortIcon('caseLotSize')}</th>
                      <th onClick={() => handleCLSort('caseLotAtpQty')}>Avl. Cases {clSortIcon('caseLotAtpQty')}</th>
                      <th onClick={() => handleCLSort('unitPrice')}>Unit Price {clSortIcon('unitPrice')}</th>
                      <th onClick={() => handleCLSort('caseLotPrice')}>Case Price {clSortIcon('caseLotPrice')}</th>
                      <th>No. Cases</th>
                      <th>Unit Offer</th>
                      <th>Case Offer</th>
                      <th>Total</th>
                    </tr>
                    <tr className={styles.filterRow}>
                      <th><input className={styles.filterInput} type="text" value={clFilters.sku} onChange={e => updateCLFilter('sku', e.target.value)} /></th>
                      <th><MultiSelectFilter options={clDistinctValues.model || []} selected={clFilters.model} onChange={v => updateCLMultiFilter('model', v)} /></th>
                      <th><MultiSelectFilter options={clDistinctValues.carrier || []} selected={clFilters.carrier} onChange={v => updateCLMultiFilter('carrier', v)} /></th>
                      <th><MultiSelectFilter options={clDistinctValues.capacity || []} selected={clFilters.capacity} onChange={v => updateCLMultiFilter('capacity', v)} /></th>
                      <th><MultiSelectFilter options={clDistinctValues.color || []} selected={clFilters.color} onChange={v => updateCLMultiFilter('color', v)} /></th>
                      <th><MultiSelectFilter options={clDistinctValues.grade || []} selected={clFilters.grade} onChange={v => updateCLMultiFilter('grade', v)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={clFilters.caseLotSize} onChange={e => updateCLFilter('caseLotSize', e.target.value)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={clFilters.caseLotAtpQty} onChange={e => updateCLFilter('caseLotAtpQty', e.target.value)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={clFilters.unitPrice} onChange={e => updateCLFilter('unitPrice', e.target.value)} /></th>
                      <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={clFilters.caseLotPrice} onChange={e => updateCLFilter('caseLotPrice', e.target.value)} /></th>
                      <th></th>
                      <th></th>
                      <th></th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {pageCaseLots.map(cl => {
                      const entry = clCartItems[cl.id];
                      const caseOffer = entry ? entry.unitOffer * cl.caseLotSize : 0;
                      const totalOffer = entry ? entry.unitOffer * cl.caseLotSize * entry.numCases : 0;
                      const hasNumCases = entry && entry.numCases > 0;
                      const hasUnitOffer = entry && entry.unitOffer > 0;
                      const isExceedCL = hasNumCases && cl.caseLotAtpQty <= 100 && entry.numCases > cl.caseLotAtpQty;
                      const isAnimatingCL = animatingSkus.has(`cl-${cl.id}`);

                      let clPriceClass = '';
                      if (hasUnitOffer && hasNumCases && cl.unitPrice > 0) {
                        clPriceClass = entry.unitOffer < cl.unitPrice
                          ? styles.offerPriceOrange
                          : styles.offerPriceGreen;
                      }

                      return (
                        <tr key={cl.id} className={isAnimatingCL ? styles.animatePulse : ''}>
                          <td>{cl.sku}</td>
                          <td>{cl.model}</td>
                          <td>{cl.carrier}</td>
                          <td>{cl.capacity}</td>
                          <td>{cl.color}</td>
                          <td>{cl.grade}</td>
                          <td>{cl.caseLotSize}</td>
                          <td>{cl.caseLotAtpQty}</td>
                          <td>${cl.unitPrice.toLocaleString()}</td>
                          <td>${cl.caseLotPrice.toLocaleString()}</td>
                          <td className={`${styles.userDataCell} ${hasNumCases ? styles.cartQtyActive : ''}`}>
                            <input
                              type="number"
                              className={`${styles.numberInput} ${isExceedCL ? styles.exceedQtyInput : ''}`}
                              value={entry?.numCases || ''}
                              onChange={e => handleCLCartChange(cl, 'numCases', e.target.value)}
                              placeholder=""
                              min={0}
                              step="1"
                              max={cl.caseLotAtpQty}
                            />
                            {isExceedCL && <div className={styles.exceedQtyHint}>Exceeds available</div>}
                          </td>
                          <td className={`${styles.userDataCell} ${clPriceClass}`}>
                            {hasNumCases ? (
                              <div className={styles.textDollar}>
                                <input
                                  type="number"
                                  className={styles.numberInput}
                                  value={entry?.unitOffer || ''}
                                  onChange={e => handleCLCartChange(cl, 'unitOffer', e.target.value)}
                                  placeholder=""
                                  min={0}
                                  step="0.01"
                                />
                              </div>
                            ) : (
                              <span className={styles.offerPriceDefault}>${cl.unitPrice.toLocaleString()}</span>
                            )}
                          </td>
                          <td className={styles.userDataCell}>{hasNumCases ? `$${caseOffer.toLocaleString()}` : ''}</td>
                          <td className={`${styles.userDataCell} ${styles.colTotal}`}>{hasNumCases ? `$${totalOffer.toLocaleString()}` : ''}</td>
                        </tr>
                      );
                    })}
                    {pageCaseLots.length === 0 && (
                      <tr><td colSpan={14} style={{ textAlign: 'center', padding: '40px', fontWeight: 500 }}>There are currently no case lots to purchase</td></tr>
                    )}
                  </tbody>
                </table>

                <div className={styles.pagination}>
                  <button className={styles.pageBtn} disabled={clPage <= 1} onClick={() => setCLPage(1)} title="First">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7"/><polyline points="18 17 13 12 18 7"/></svg>
                  </button>
                  <button className={styles.pageBtn} disabled={clPage <= 1} onClick={() => setCLPage(p => p - 1)} title="Previous">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6"/></svg>
                  </button>
                  <span className={styles.pageInfo}>{clRangeStart} to {clRangeEnd} of {filteredCaseLots.length}</span>
                  <button className={styles.pageBtn} disabled={clPage >= clTotalPages} onClick={() => setCLPage(p => p + 1)} title="Next">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                  </button>
                  <button className={styles.pageBtn} disabled={clPage >= clTotalPages} onClick={() => setCLPage(clTotalPages)} title="Last">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7"/><polyline points="6 17 11 12 6 7"/></svg>
                  </button>
                </div>
              </>
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
