'use client';

import { useState, useEffect, useCallback, useMemo, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './rmaRequests.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const BASE = `${API_BASE}/pws/rma`;
const PAGE_SIZE = 20;

interface RmaListItem {
  rmaId: number;
  number: string | null;
  systemStatus: string | null;
  internalStatusText: string | null;
  externalStatusText: string | null;
  statusGroupedTo: string | null;
  buyerCodeId: number | null;
  buyerName: string | null;
  companyName: string | null;
  requestSkus: number;
  requestQty: number;
  requestSalesTotal: number;
  submittedDate: string | null;
  approvalDate: string | null;
  approvedCount: number;
  declinedCount: number;
  oracleNumber: string | null;
}

interface ColumnFilters {
  number: string;
  status: string;
  buyer: string;
  company: string;
  skus: string;
}

type SortField = 'number' | 'submittedDate' | 'buyer' | 'company' | 'status' | 'skus' | 'qty' | 'total';
type SortDir = 'asc' | 'desc';

const EMPTY_FILTERS: ColumnFilters = { number: '', status: '', buyer: '', company: '', skus: '' };

function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
}

function formatCurrency(val: number | null): string {
  if (val == null) return '$0';
  return '$' + val.toLocaleString('en-US');
}

function matchesText(value: string | null | undefined, filter: string): boolean {
  if (!filter) return true;
  return (value || '').toLowerCase().includes(filter.toLowerCase());
}

function getStatusClass(grouped: string | null): string {
  switch (grouped) {
    case 'Pending_Approval': return styles.statusSubmitted;
    case 'Open': return styles.statusOpen;
    case 'Declined': return styles.statusDeclined;
    case 'Closed': return styles.statusClosed;
    default: return styles.statusDefault;
  }
}

interface RmaReason {
  id: number;
  reason: string;
}

export default function RmaRequestsPage() {
  const router = useRouter();
  const [rmas, setRmas] = useState<RmaListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ColumnFilters>(EMPTY_FILTERS);
  const [currentPage, setCurrentPage] = useState(1);
  const [sortField, setSortField] = useState<SortField | null>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');
  const [showMoreMenu, setShowMoreMenu] = useState(false);
  const filterTimers = useRef<Record<string, ReturnType<typeof setTimeout>>>({});

  // Request RMA modal state
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [submitErrors, setSubmitErrors] = useState<string[]>([]);
  const [submitSuccess, setSubmitSuccess] = useState<string | null>(null);
  const [reasons, setReasons] = useState<RmaReason[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  function readBuyerCodeId(): string | null {
    if (typeof window === 'undefined') return null;
    try {
      const stored = sessionStorage.getItem('selectedBuyerCode');
      if (stored) return String(JSON.parse(stored).id);
    } catch { /* ignore */ }
    return null;
  }

  const [buyerCodeId, setBuyerCodeId] = useState<string | null>(readBuyerCodeId);

  // Re-read buyer code when it changes via the layout switcher
  useEffect(() => {
    function handleBuyerCodeChange() {
      setBuyerCodeId(readBuyerCodeId());
    }
    window.addEventListener('buyerCodeChanged', handleBuyerCodeChange);
    return () => window.removeEventListener('buyerCodeChanged', handleBuyerCodeChange);
  }, []);

  const loadData = useCallback(async () => {
    if (!buyerCodeId) { setLoading(false); return; }
    setLoading(true);
    try {
      const res = await apiFetch(`${BASE}?buyerCodeId=${buyerCodeId}`);
      if (res.ok) setRmas(await res.json());
    } catch (err) {
      console.error('Failed to load RMAs:', err);
    } finally {
      setLoading(false);
    }
  }, [buyerCodeId]);

  useEffect(() => { loadData(); }, [loadData]);

  // Load return reasons when modal opens
  useEffect(() => {
    if (!showRequestModal) return;
    async function loadReasons() {
      try {
        const res = await apiFetch(`${BASE}/reasons`);
        if (res.ok) setReasons(await res.json());
      } catch (err) {
        console.error('Failed to load RMA reasons:', err);
      }
    }
    loadReasons();
  }, [showRequestModal]);

  function openRequestModal() {
    setSelectedFile(null);
    setSubmitErrors([]);
    setSubmitSuccess(null);
    setSubmitting(false);
    setShowRequestModal(true);
  }

  function closeRequestModal() {
    setShowRequestModal(false);
    setSelectedFile(null);
    setSubmitErrors([]);
    setSubmitSuccess(null);
  }

  function handleFileSelect(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setSubmitErrors([]);
    }
  }

  function handleFileDrop(e: React.DragEvent) {
    e.preventDefault();
    const file = e.dataTransfer.files?.[0];
    if (file) {
      setSelectedFile(file);
      setSubmitErrors([]);
    }
  }

  async function handleDownloadTemplate() {
    try {
      const res = await apiFetch(`${BASE}/template`);
      if (res.ok) {
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'RMA_Template.csv';
        a.click();
        URL.revokeObjectURL(url);
      }
    } catch (err) {
      console.error('Failed to download template:', err);
    }
  }

  async function handleSubmitRma() {
    if (!selectedFile) return;
    if (!buyerCodeId) {
      setSubmitErrors(['No buyer code selected. Please select a buyer code first.']);
      return;
    }

    const authUser = localStorage.getItem('auth_user');
    let userId: string | null = null;
    if (authUser) {
      try { userId = String(JSON.parse(authUser).userId); } catch { /* ignore */ }
    }
    if (!userId) {
      setSubmitErrors(['Unable to determine user. Please log in again.']);
      return;
    }

    setSubmitting(true);
    setSubmitErrors([]);
    setSubmitSuccess(null);

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('buyerCodeId', buyerCodeId);
      formData.append('userId', userId);

      const res = await apiFetch(`${BASE}/submit`, {
        method: 'POST',
        body: formData,
      });
      const data = await res.json();
      if (data.success) {
        setSubmitSuccess(data.message);
        loadData(); // Refresh the list
      } else {
        setSubmitErrors(data.errors || [data.message]);
      }
    } catch (err) {
      setSubmitErrors(['Failed to submit RMA. Please try again.']);
    } finally {
      setSubmitting(false);
    }
  }

  function handleSort(field: SortField) {
    if (sortField === field) {
      setSortDir(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('asc');
    }
    setCurrentPage(1);
  }

  function handleFilterChange(field: keyof ColumnFilters, value: string) {
    if (filterTimers.current[field]) clearTimeout(filterTimers.current[field]);
    filterTimers.current[field] = setTimeout(() => {
      setFilters(prev => ({ ...prev, [field]: value }));
      setCurrentPage(1);
    }, 300);
  }

  function sortIcon(field: SortField): string {
    if (sortField !== field) return '↕';
    return sortDir === 'asc' ? '↑' : '↓';
  }

  const filteredRmas = useMemo(() => {
    const filtered = rmas.filter(r =>
      matchesText(r.number, filters.number) &&
      matchesText(r.externalStatusText, filters.status) &&
      matchesText(r.buyerName, filters.buyer) &&
      matchesText(r.companyName, filters.company) &&
      matchesText(String(r.requestSkus), filters.skus)
    );

    if (sortField) {
      filtered.sort((a, b) => {
        let cmp = 0;
        switch (sortField) {
          case 'number': cmp = (a.number || '').localeCompare(b.number || ''); break;
          case 'submittedDate': cmp = (a.submittedDate || '').localeCompare(b.submittedDate || ''); break;
          case 'buyer': cmp = (a.buyerName || '').localeCompare(b.buyerName || ''); break;
          case 'company': cmp = (a.companyName || '').localeCompare(b.companyName || ''); break;
          case 'status': cmp = (a.externalStatusText || '').localeCompare(b.externalStatusText || ''); break;
          case 'skus': cmp = a.requestSkus - b.requestSkus; break;
          case 'qty': cmp = a.requestQty - b.requestQty; break;
          case 'total': cmp = a.requestSalesTotal - b.requestSalesTotal; break;
        }
        return sortDir === 'desc' ? -cmp : cmp;
      });
    }

    return filtered;
  }, [rmas, filters, sortField, sortDir]);

  const totalPages = Math.max(1, Math.ceil(filteredRmas.length / PAGE_SIZE));
  const pageRmas = filteredRmas.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

  function handleExportCsv() {
    const headers = ['RMA Number', 'Submit Date', 'Buyer', 'Company', 'Status', 'SKUs', 'Qty', 'Original Total'];
    const rows = filteredRmas.map(r => [
      r.number || '', formatDate(r.submittedDate), r.buyerName || '', r.companyName || '',
      r.externalStatusText || '', r.requestSkus, r.requestQty, r.requestSalesTotal,
    ]);
    const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `rma-requests-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    setShowMoreMenu(false);
  }

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>RMA Requests</h1>
        <div className={styles.headerActions}>
          <input
            className={styles.searchInput}
            placeholder="Search RMA Number..."
            onChange={e => handleFilterChange('number', e.target.value)}
          />
          <button className={styles.requestRmaBtn} onClick={openRequestModal}>Request an RMA</button>
          <div className={styles.moreActionsContainer}>
            <button className={styles.moreActionsBtn} onClick={() => setShowMoreMenu(!showMoreMenu)}>
              &#8943;
            </button>
            {showMoreMenu && (
              <div className={styles.moreActionsMenu}>
                <button className={styles.menuItem} onClick={handleExportCsv}>Export</button>
                <button className={styles.menuItem} onClick={() => setShowMoreMenu(false)}>
                  RMA Instructions
                </button>
                <button className={styles.menuItem} onClick={() => setShowMoreMenu(false)}>
                  Return Policy
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {loading ? (
        <div className={styles.loadingOverlay}>Loading...</div>
      ) : filteredRmas.length === 0 ? (
        <div className={styles.emptyState}>There are currently no RMA requests</div>
      ) : (
        <div className={styles.tableContainer}>
          <table className={styles.dataGrid}>
            <thead>
              <tr>
                <th onClick={() => handleSort('number')}>
                  RMA Number <span className={`${styles.sortIcon} ${sortField === 'number' ? styles.sortActive : ''}`}>{sortIcon('number')}</span>
                </th>
                <th onClick={() => handleSort('submittedDate')}>
                  Submit Date <span className={`${styles.sortIcon} ${sortField === 'submittedDate' ? styles.sortActive : ''}`}>{sortIcon('submittedDate')}</span>
                </th>
                <th onClick={() => handleSort('buyer')}>
                  Buyer <span className={`${styles.sortIcon} ${sortField === 'buyer' ? styles.sortActive : ''}`}>{sortIcon('buyer')}</span>
                </th>
                <th onClick={() => handleSort('company')}>
                  Company <span className={`${styles.sortIcon} ${sortField === 'company' ? styles.sortActive : ''}`}>{sortIcon('company')}</span>
                </th>
                <th onClick={() => handleSort('status')}>
                  RMA Status <span className={`${styles.sortIcon} ${sortField === 'status' ? styles.sortActive : ''}`}>{sortIcon('status')}</span>
                </th>
                <th className={styles.alignCenter} onClick={() => handleSort('skus')}>
                  SKUs <span className={`${styles.sortIcon} ${sortField === 'skus' ? styles.sortActive : ''}`}>{sortIcon('skus')}</span>
                </th>
                <th className={styles.alignCenter} onClick={() => handleSort('qty')}>
                  Qty <span className={`${styles.sortIcon} ${sortField === 'qty' ? styles.sortActive : ''}`}>{sortIcon('qty')}</span>
                </th>
                <th className={styles.alignRight} onClick={() => handleSort('total')}>
                  Original Total <span className={`${styles.sortIcon} ${sortField === 'total' ? styles.sortActive : ''}`}>{sortIcon('total')}</span>
                </th>
              </tr>
              <tr>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('number', e.target.value)} /></th>
                <th></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('buyer', e.target.value)} /></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('company', e.target.value)} /></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('status', e.target.value)} /></th>
                <th><input className={`${styles.columnFilter} ${styles.alignCenter}`} placeholder="Filter..." onChange={e => handleFilterChange('skus', e.target.value)} /></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {pageRmas.map(r => (
                <tr key={r.rmaId} className={styles.clickableRow}
                    onClick={() => router.push(`/pws/rma-requests/${r.rmaId}`)}>
                  <td>{r.number || r.rmaId}</td>
                  <td>{formatDate(r.submittedDate)}</td>
                  <td>{r.buyerName || ''}</td>
                  <td>{r.companyName || ''}</td>
                  <td>
                    <span className={`${styles.statusBadge} ${getStatusClass(r.statusGroupedTo)}`}>
                      {r.externalStatusText || r.systemStatus || ''}
                    </span>
                  </td>
                  <td className={styles.alignCenter}>{r.requestSkus}</td>
                  <td className={styles.alignCenter}>{r.requestQty}</td>
                  <td className={styles.alignRight}>{formatCurrency(r.requestSalesTotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className={styles.pagination}>
            <button className={styles.pageBtn} disabled={currentPage <= 1}
                    onClick={() => setCurrentPage(p => p - 1)}>&lt;</button>
            {Array.from({ length: totalPages }, (_, i) => i + 1).slice(
              Math.max(0, currentPage - 3), Math.min(totalPages, currentPage + 2)
            ).map(p => (
              <button key={p}
                      className={`${styles.pageBtn} ${p === currentPage ? styles.pageBtnActive : ''}`}
                      onClick={() => setCurrentPage(p)}>
                {p}
              </button>
            ))}
            <button className={styles.pageBtn} disabled={currentPage >= totalPages}
                    onClick={() => setCurrentPage(p => p + 1)}>&gt;</button>
          </div>
        </div>
      )}

      {/* Request RMA Modal */}
      {showRequestModal && (
        <div className={styles.modalOverlay} onClick={(e) => {
          if (e.target === e.currentTarget) closeRequestModal();
        }}>
          <div className={styles.modal}>
            <div className={styles.modalHeader}>
              <h2 className={styles.modalTitle}>Request an RMA</h2>
              <button className={styles.modalCloseBtn} onClick={closeRequestModal}>&times;</button>
            </div>
            <div className={styles.modalBody}>
              {submitSuccess ? (
                <div className={styles.modalSuccess}>
                  <div className={styles.modalSuccessText}>{submitSuccess}</div>
                </div>
              ) : (
                <>
                  {submitErrors.length > 0 && (
                    <div className={styles.modalError}>
                      <div className={styles.modalErrorTitle}>Validation Errors</div>
                      <ul className={styles.modalErrorList}>
                        {submitErrors.map((err, i) => <li key={i}>{err}</li>)}
                      </ul>
                    </div>
                  )}

                  <div className={styles.modalStep}>
                    <div className={styles.modalStepLabel}>Step 1: Download Template</div>
                    <div className={styles.modalStepDescription}>
                      Download the CSV template, fill in the IMEI/Serial numbers and return reasons, then upload it below.
                    </div>
                    <button className={styles.downloadTemplateBtn} onClick={handleDownloadTemplate}>
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                        <polyline points="7 10 12 15 17 10"/>
                        <line x1="12" y1="15" x2="12" y2="3"/>
                      </svg>
                      Download RMA Template
                    </button>
                    {reasons.length > 0 && (
                      <div className={styles.modalStepDescription} style={{ marginTop: 12 }}>
                        <strong>Valid return reasons:</strong>{' '}
                        {reasons.map(r => r.reason).join(', ')}
                      </div>
                    )}
                  </div>

                  <div className={styles.modalStep}>
                    <div className={styles.modalStepLabel}>Step 2: Upload Completed File</div>
                    <div className={styles.modalStepDescription}>
                      Upload the filled CSV file with IMEI/Serial numbers and return reasons.
                    </div>
                    <div
                      className={`${styles.fileUploadArea} ${selectedFile ? styles.fileUploadAreaActive : ''}`}
                      onClick={() => fileInputRef.current?.click()}
                      onDragOver={(e) => e.preventDefault()}
                      onDrop={handleFileDrop}
                    >
                      <div className={styles.fileUploadIcon}>
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                          <polyline points="17 8 12 3 7 8"/>
                          <line x1="12" y1="3" x2="12" y2="15"/>
                        </svg>
                      </div>
                      <div className={styles.fileUploadText}>
                        Click to browse or drag and drop your file
                      </div>
                      <div className={styles.fileUploadHint}>CSV files only</div>
                    </div>
                    <input
                      ref={fileInputRef}
                      type="file"
                      accept=".csv"
                      style={{ display: 'none' }}
                      onChange={handleFileSelect}
                    />
                    {selectedFile && (
                      <div className={styles.selectedFile}>
                        <span className={styles.selectedFileName}>{selectedFile.name}</span>
                        <button className={styles.removeFileBtn} onClick={() => setSelectedFile(null)}>&times;</button>
                      </div>
                    )}
                  </div>
                </>
              )}

              {submitting && (
                <div className={styles.modalSubmitting}>Submitting RMA request...</div>
              )}
            </div>
            <div className={styles.modalFooter}>
              {submitSuccess ? (
                <button className={styles.submitRmaBtn} onClick={closeRequestModal}>Close</button>
              ) : (
                <>
                  <button className={styles.cancelBtn} onClick={closeRequestModal}>Cancel</button>
                  <button
                    className={styles.submitRmaBtn}
                    disabled={!selectedFile || submitting}
                    onClick={handleSubmitRma}
                  >
                    {submitting ? 'Submitting...' : 'Submit RMA'}
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
