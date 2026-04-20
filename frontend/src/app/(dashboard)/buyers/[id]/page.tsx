'use client';

import { useParams, useRouter } from 'next/navigation';
import { useCallback, useEffect, useState } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';
import styles from './buyerEdit.module.css';

interface SalesRepSummary {
  id: number;
  firstName: string;
  lastName: string;
}

interface BuyerCodeDetail {
  id: number | null;
  code: string;
  buyerCodeType: string | null;
  budget: number | null;
  softDelete: boolean;
  codeUniqueValid: boolean;
}

interface BuyerPermissions {
  canEditSalesRep: boolean;
  canToggleStatus: boolean;
  canEditBuyerCodeType: boolean;
}

interface BuyerDetail {
  id: number;
  companyName: string;
  status: 'Active' | 'Disabled';
  isSpecialBuyer: boolean;
  salesReps: SalesRepSummary[];
  buyerCodes: BuyerCodeDetail[];
  permissions: BuyerPermissions;
}

const BUYER_CODE_TYPES = ['Wholesale', 'Data_Wipe', 'Purchasing_Order_Data_Wipe', 'Purchasing_Order'];

export default function BuyerEditPage() {
  const params = useParams();
  const router = useRouter();
  const buyerId = params.id as string;
  const isCreateMode = buyerId === 'new';

  const [buyer, setBuyer] = useState<BuyerDetail | null>(null);
  const [loading, setLoading] = useState(!isCreateMode);
  const [saving, setSaving] = useState(false);
  const [toggling, setToggling] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const [companyName, setCompanyName] = useState('');
  const [isSpecialBuyer, setIsSpecialBuyer] = useState(false);
  const [buyerCodes, setBuyerCodes] = useState<BuyerCodeDetail[]>([]);
  const [salesRepIds, setSalesRepIds] = useState<number[]>([]);
  const [allSalesReps, setAllSalesReps] = useState<SalesRepSummary[]>([]);

  const fetchBuyer = useCallback(async () => {
    if (isCreateMode) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch(`${API_BASE}/admin/buyers/${buyerId}`);
      if (!res.ok) {
        const body = await res.json().catch(() => null);
        throw new Error(body?.error || `HTTP ${res.status}`);
      }
      const data: BuyerDetail = await res.json();
      setBuyer(data);
      setCompanyName(data.companyName);
      setIsSpecialBuyer(data.isSpecialBuyer);
      setBuyerCodes(data.buyerCodes);
      setSalesRepIds(data.salesReps.map((sr) => sr.id));
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load buyer');
    } finally {
      setLoading(false);
    }
  }, [buyerId, isCreateMode]);

  const fetchSalesReps = useCallback(async () => {
    try {
      const res = await apiFetch(`${API_BASE}/admin/buyers/sales-representatives`);
      if (res.ok) {
        const data: SalesRepSummary[] = await res.json();
        setAllSalesReps(data);
      }
    } catch {
      // Non-critical — sales rep list fails gracefully
    }
  }, []);

  useEffect(() => {
    fetchBuyer();
    fetchSalesReps();
  }, [fetchBuyer, fetchSalesReps]);

  const handleSave = async () => {
    if (!isCreateMode && !buyer) return;
    setSaving(true);
    setError(null);
    setSuccessMsg(null);

    const payload = {
      companyName: companyName.trim(),
      status: buyer?.status ?? undefined,
      isSpecialBuyer,
      salesRepIds: isCreateMode || buyer?.permissions.canEditSalesRep ? salesRepIds : undefined,
      buyerCodes: buyerCodes.map((bc) => ({
        id: bc.id,
        code: bc.code,
        buyerCodeType: bc.buyerCodeType,
        budget: bc.budget,
        softDelete: bc.softDelete,
      })),
    };

    const url = isCreateMode
      ? `${API_BASE}/admin/buyers`
      : `${API_BASE}/admin/buyers/${buyerId}`;
    const method = isCreateMode ? 'POST' : 'PUT';

    try {
      const res = await apiFetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const errBody = await res.json().catch(() => null);
        throw new Error(errBody?.error || `HTTP ${res.status}`);
      }
      const saved: BuyerDetail = await res.json();
      if (isCreateMode) {
        router.push(`/buyers/${saved.id}`);
        return;
      }
      setBuyer(saved);
      setCompanyName(saved.companyName);
      setIsSpecialBuyer(saved.isSpecialBuyer);
      setBuyerCodes(saved.buyerCodes);
      setSalesRepIds(saved.salesReps.map((sr) => sr.id));
      setSuccessMsg('Buyer saved successfully.');
      setTimeout(() => setSuccessMsg(null), 3000);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  const handleToggleStatus = async (e?: React.ChangeEvent<HTMLSelectElement>) => {
    if (!buyer) return;
    if (e) {
      const newStatus = e.target.value;
      if (newStatus === buyer.status) return;
    }
    setToggling(true);
    setError(null);
    setSuccessMsg(null);
    try {
      const res = await apiFetch(`${API_BASE}/admin/buyers/${buyerId}/status`, {
        method: 'PATCH',
      });
      if (!res.ok) {
        const errBody = await res.json().catch(() => null);
        throw new Error(errBody?.error || `HTTP ${res.status}`);
      }
      const updated: BuyerDetail = await res.json();
      setBuyer(updated);
      setSuccessMsg(`Status changed to ${updated.status}.`);
      setTimeout(() => setSuccessMsg(null), 3000);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Status toggle failed');
    } finally {
      setToggling(false);
    }
  };

  const handleCancel = () => {
    router.push('/buyers');
  };

  const handleAddCode = () => {
    setBuyerCodes((prev) => [
      ...prev,
      { id: null, code: '', buyerCodeType: 'Wholesale', budget: null, softDelete: false, codeUniqueValid: true },
    ]);
  };

  const handleDeleteCode = (index: number) => {
    setBuyerCodes((prev) =>
      prev.map((bc, i) => {
        if (i !== index) return bc;
        if (bc.id === null) return { ...bc, softDelete: true };
        return { ...bc, softDelete: true };
      })
    );
  };

  const updateCode = (index: number, field: keyof BuyerCodeDetail, value: unknown) => {
    setBuyerCodes((prev) =>
      prev.map((bc, i) => (i === index ? { ...bc, [field]: value } : bc))
    );
  };

  const toggleSalesRep = (repId: number) => {
    setSalesRepIds((prev) =>
      prev.includes(repId) ? prev.filter((id) => id !== repId) : [...prev, repId]
    );
  };

  if (loading) {
    return (
      <div className={styles.pageContainer}>
        <div className={styles.loadingState}>Loading buyer...</div>
      </div>
    );
  }

  if (!isCreateMode && error && !buyer) {
    return (
      <div className={styles.pageContainer}>
        <div className={styles.errorState}>{error}</div>
        <button className={styles.cancelBtn} onClick={handleCancel}>Back to Buyers</button>
      </div>
    );
  }

  if (!isCreateMode && !buyer) return null;

  const visibleCodes = buyerCodes.filter((bc) => bc.id === null ? !bc.softDelete : true);
  const showSalesReps = isCreateMode || buyer?.permissions.canEditSalesRep;

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <div className={styles.headerLeft}>
          <h2 className={styles.pageTitle}>{isCreateMode ? 'New Buyer' : 'Edit Buyer'}</h2>
        </div>
        <button className={styles.closeBtn} onClick={handleCancel} title="Close">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>
      </div>

      {error && <div className={styles.errorBanner}>{error}</div>}
      {successMsg && <div className={styles.successBanner}>{successMsg}</div>}

      <div className={styles.formSection}>
        {!isCreateMode && buyer && (
          <div className={styles.topInfoRow}>
            <span className={styles.avatar}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 21h18M5 21V7l7-4 7 4v14M9 9h1M9 13h1M9 17h1M14 9h1M14 13h1M14 17h1" />
              </svg>
            </span>
            <div className={styles.addedDate}>
              <span className={styles.addedLabel}>Added</span>
              <span className={styles.addedValue}>—</span>
            </div>
            {buyer.permissions.canToggleStatus ? (
              <select
                className={styles.statusSelect}
                value={buyer.status}
                onChange={handleToggleStatus}
                disabled={toggling}
              >
                <option value="Active">Active</option>
                <option value="Disabled">Disabled</option>
              </select>
            ) : (
              <span className={`${styles.statusBadge} ${buyer.status === 'Active' ? styles.statusActive : styles.statusDisabled}`}>
                {buyer.status}
              </span>
            )}
          </div>
        )}

        <div className={styles.specialBuyerRow}>
          <span className={styles.specialBuyerLabel}>Apply special buyer treatment</span>
          <label className={styles.switchLabel}>
            <input
              type="checkbox"
              checked={isSpecialBuyer}
              onChange={(e) => setIsSpecialBuyer(e.target.checked)}
            />
            <span className={styles.switchSlider}></span>
          </label>
        </div>

        <div className={styles.fieldRow}>
          <label className={styles.fieldLabel}>Name</label>
          <input
            type="text"
            className={styles.fieldInput}
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            maxLength={200}
          />
        </div>

        {showSalesReps && (
          <div className={styles.fieldRow}>
            <label className={styles.fieldLabel}>ecoATM Sales Rep</label>
            <select
              className={styles.fieldInput}
              value={salesRepIds[0] ?? ''}
              onChange={(e) => {
                const val = e.target.value;
                setSalesRepIds(val ? [Number(val)] : []);
              }}
            >
              <option value=""></option>
              {allSalesReps.map((sr) => (
                <option key={sr.id} value={sr.id}>{sr.firstName} {sr.lastName}</option>
              ))}
            </select>
          </div>
        )}

        {!showSalesReps && buyer && buyer.salesReps.length > 0 && (
          <div className={styles.fieldRow}>
            <label className={styles.fieldLabel}>ecoATM Sales Rep</label>
            <span className={styles.readOnlyValue}>
              {buyer.salesReps.map((sr) => `${sr.firstName} ${sr.lastName}`).join(', ')}
            </span>
          </div>
        )}
      </div>

      <div className={styles.codesSection}>
        <div className={styles.gridWrapper}>
          <table className={styles.codesGrid}>
            <thead>
              <tr>
                <th>
                  <span className={styles.thContent}>Buyer Codes <span className={styles.sortArrows}>⇅</span></span>
                </th>
                <th>
                  <span className={styles.thContent}>Type <span className={styles.sortArrows}>⇅</span></span>
                </th>
                <th>
                  <span className={styles.thContent}>Budget ($) <span className={styles.sortArrows}>⇅</span></span>
                </th>
                <th style={{ width: 40 }}></th>
              </tr>
            </thead>
            <tbody>
              {visibleCodes.length === 0 ? (
                <tr>
                  <td colSpan={4} className={styles.emptyCell}>No buyer codes</td>
                </tr>
              ) : (
                visibleCodes.map((bc, idx) => {
                  const realIdx = buyerCodes.indexOf(bc);
                  const isSoftDeleted = bc.softDelete;
                  return (
                    <tr
                      key={bc.id ?? `new-${idx}`}
                      className={`${isSoftDeleted ? styles.deletedRow : ''} ${!bc.codeUniqueValid ? styles.notUniqueRow : ''}`}
                    >
                      <td>
                        <input
                          type="text"
                          className={styles.codeInput}
                          value={bc.code}
                          onChange={(e) => updateCode(realIdx, 'code', e.target.value)}
                          disabled={isSoftDeleted}
                        />
                      </td>
                      <td>
                        {isCreateMode || buyer?.permissions.canEditBuyerCodeType || bc.id === null ? (
                          <select
                            className={styles.codeSelect}
                            value={bc.buyerCodeType ?? ''}
                            onChange={(e) => updateCode(realIdx, 'buyerCodeType', e.target.value)}
                            disabled={isSoftDeleted}
                          >
                            <option value="">Select</option>
                            {BUYER_CODE_TYPES.map((t) => (
                              <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>
                            ))}
                          </select>
                        ) : (
                          <span className={styles.readOnlyValue}>
                            {bc.buyerCodeType?.replace(/_/g, ' ') ?? ''}
                          </span>
                        )}
                      </td>
                      <td>
                        <input
                          type="number"
                          className={styles.codeInput}
                          value={bc.budget ?? ''}
                          onChange={(e) => {
                            const val = e.target.value === '' ? null : parseInt(e.target.value, 10);
                            updateCode(realIdx, 'budget', val);
                          }}
                          min={0}
                          disabled={isSoftDeleted}
                        />
                      </td>
                      <td>
                        {!isSoftDeleted && (
                          <button
                            className={styles.deleteCodeBtn}
                            onClick={() => handleDeleteCode(realIdx)}
                            title="Soft delete"
                          >
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#D9534F" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <polyline points="3 6 5 6 21 6" />
                              <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
                            </svg>
                          </button>
                        )}
                        {isSoftDeleted && (
                          <span className={styles.deletedLabel}>Deleted</span>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
          <div className={styles.codesPagination}>
            <span>⏮</span> <span>⏪</span>{' '}
            <span className={styles.codesPaginationText}>{visibleCodes.filter(bc => !bc.softDelete).length > 0 ? `1 to ${visibleCodes.filter(bc => !bc.softDelete).length} of ${visibleCodes.filter(bc => !bc.softDelete).length}` : '0 to 0 of 0'}</span>{' '}
            <span>⏩</span> <span>⏭</span>
          </div>
        </div>
        <div className={styles.addCodeRow}>
          <button className={styles.addCodeBtn} onClick={handleAddCode}>
            Add Another
          </button>
        </div>
      </div>

      <div className={styles.footerActions}>
        <button className={styles.cancelBtn} onClick={handleCancel} disabled={saving}>
          Cancel
        </button>
        <button className={styles.saveBtn} onClick={handleSave} disabled={saving}>
          {saving ? 'Saving...' : 'Save'}
        </button>
      </div>
    </div>
  );
}
