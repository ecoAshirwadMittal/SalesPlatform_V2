'use client';

import { useEffect, useState, useCallback, useRef } from 'react';
import styles from './users.module.css';
import UserFormModal from './UserFormModal';
import { apiFetch } from '@/lib/apiFetch';

interface DirectUser {
  userId: number;
  fullName: string;
  email: string;
  overallUserStatus: string;
  submissionId: number | null;
  buyers: string;
  roles: string;
  changedDate: string | null;
}

interface PageResponse {
  content: DirectUser[];
  page: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

const STATUS_OPTIONS = ['', 'Active', 'Inactive', 'Disabled'];

const FILTER_DELAY = 500; // ms debounce matching Mendix config

/** Extract initials from full name (e.g. "Elango Ponnusamy" → "EP") */
function getInitials(name: string): string {
  const parts = name.trim().split(/\s+/);
  if (parts.length >= 2) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  return name.slice(0, 2).toUpperCase();
}

/** Format ISO date to MM/DD/YYYY matching Mendix display */
function formatDate(dateStr: string | null): string {
  if (!dateStr) return '';
  const d = new Date(dateStr + 'T00:00:00');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  const yyyy = d.getFullYear();
  return `${mm}/${dd}/${yyyy}`;
}

type SortField = 'fullName' | 'buyers' | 'roles' | 'email' | 'overallUserStatus';
type SortDir = 'asc' | 'desc' | null;

export default function UsersOverviewPage() {
  const [data, setData] = useState<PageResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const pageSize = 20;

  // Sort state
  const [sortField, setSortField] = useState<SortField | null>(null);
  const [sortDir, setSortDir] = useState<SortDir>(null);

  // Filter state
  const [nameFilter, setNameFilter] = useState('');
  const [buyerFilter, setBuyerFilter] = useState('');
  const [rolesFilter, setRolesFilter] = useState('');
  const [emailFilter, setEmailFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // Modal state
  const [modalMode, setModalMode] = useState<'create' | 'edit' | null>(null);
  const [editUserId, setEditUserId] = useState<number | null>(null);

  // Debounced values
  const [debouncedFilters, setDebouncedFilters] = useState({
    name: '', buyer: '', roles: '', email: '', status: ''
  });

  const debounceTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Debounce text filters
  useEffect(() => {
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      setDebouncedFilters({
        name: nameFilter,
        buyer: buyerFilter,
        roles: rolesFilter,
        email: emailFilter,
        status: statusFilter,
      });
      setPage(0);
    }, FILTER_DELAY);
    return () => { if (debounceTimer.current) clearTimeout(debounceTimer.current); };
  }, [nameFilter, buyerFilter, rolesFilter, emailFilter, statusFilter]);

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    const params = new URLSearchParams();
    params.set('page', String(page));
    params.set('pageSize', String(pageSize));
    if (debouncedFilters.name) params.set('name', debouncedFilters.name);
    if (debouncedFilters.buyer) params.set('buyer', debouncedFilters.buyer);
    if (debouncedFilters.roles) params.set('roles', debouncedFilters.roles);
    if (debouncedFilters.email) params.set('email', debouncedFilters.email);
    if (debouncedFilters.status) params.set('status', debouncedFilters.status);

    try {
      const res = await apiFetch(`/api/v1/users/direct-users?${params}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json: PageResponse = await res.json();
      setData(json);
    } catch (err) {
      console.error('Failed to fetch users', err);
    } finally {
      setLoading(false);
    }
  }, [page, debouncedFilters]);

  useEffect(() => { fetchUsers(); }, [fetchUsers]);

  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortDir(prev => prev === 'asc' ? 'desc' : prev === 'desc' ? null : 'asc');
      if (sortDir === 'desc') setSortField(null);
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  };

  // Client-side sort (server doesn't support sort param yet)
  const sortedContent = (() => {
    if (!data?.content || !sortField || !sortDir) return data?.content ?? [];
    return [...data.content].sort((a, b) => {
      const aVal = (a[sortField] ?? '').toLowerCase();
      const bVal = (b[sortField] ?? '').toLowerCase();
      const cmp = aVal.localeCompare(bVal);
      return sortDir === 'asc' ? cmp : -cmp;
    });
  })();

  const renderSortIcon = (field: SortField) => {
    const active = sortField === field;
    return (
      <span className={styles.sortIcon}>
        <span className={active && sortDir === 'asc' ? styles.sortActive : styles.sortArrow}>&#9650;</span>
        <span className={active && sortDir === 'desc' ? styles.sortActive : styles.sortArrow}>&#9660;</span>
      </span>
    );
  };

  const renderStatusIcon = (status: string | null) => {
    switch (status) {
      case 'Active':
        return (
          <span className={`${styles.statusIcon} ${styles.statusActive}`} title="Active">
            <svg width="20" height="20" viewBox="0 0 20 20"><circle cx="10" cy="10" r="9" fill="#14AC36" stroke="#0e8f2b" strokeWidth="1"/><path d="M6 10l3 3 5-5" stroke="#fff" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round"/></svg>
          </span>
        );
      case 'Inactive':
        return (
          <span className={`${styles.statusIcon} ${styles.statusInactive}`} title="Inactive">
            <svg width="20" height="20" viewBox="0 0 20 20"><circle cx="10" cy="10" r="9" fill="#999" stroke="#777" strokeWidth="1"/><path d="M7 7l6 6M13 7l-6 6" stroke="#fff" strokeWidth="2" strokeLinecap="round"/></svg>
          </span>
        );
      case 'Disabled':
        return (
          <span className={`${styles.statusIcon} ${styles.statusDisabled}`} title="Disabled">
            <svg width="20" height="20" viewBox="0 0 20 20"><circle cx="10" cy="10" r="9" fill="#D9534F" stroke="#c23531" strokeWidth="1"/><path d="M7 7l6 6M13 7l-6 6" stroke="#fff" strokeWidth="2" strokeLinecap="round"/></svg>
          </span>
        );
      default:
        return <span className={styles.statusIcon} title="Unknown" />;
    }
  };

  const totalPages = data?.totalPages ?? 0;
  const startItem = page * pageSize + 1;
  const endItem = Math.min((page + 1) * pageSize, data?.totalElements ?? 0);

  return (
    <div className={styles.pageContainer}>
      {/* Page header with Create button */}
      <div className={styles.pageHeader}>
        <h2 className={styles.pageTitle}>User Management</h2>
        <button className={styles.createBtn} onClick={() => { setModalMode('create'); setEditUserId(null); }}>
          Create
          <svg className={styles.createIcon} width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <line x1="19" y1="8" x2="19" y2="14"/>
            <line x1="22" y1="11" x2="16" y2="11"/>
          </svg>
        </button>
      </div>

      {/* Data Grid */}
      <div className={styles.gridWrapper}>
        <table className={styles.datagrid}>
          <colgroup>
            <col style={{ width: '18%' }} />
            <col style={{ width: '16%' }} />
            <col style={{ width: '18%' }} />
            <col style={{ width: '24%' }} />
            <col style={{ width: '20%' }} />
            <col style={{ width: '4%' }} />
          </colgroup>
          <thead>
            <tr>
              <th>
                <div className={styles.columnHeaderRow}>
                  <button className={styles.sortBtn} onClick={() => handleSort('fullName')}>
                    <span className={styles.columnHeader}>Name</span>
                    {renderSortIcon('fullName')}
                  </button>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={nameFilter}
                  onChange={(e) => setNameFilter(e.target.value)}
                />
              </th>
              <th>
                <div className={styles.columnHeaderRow}>
                  <button className={styles.sortBtn} onClick={() => handleSort('buyers')}>
                    <span className={styles.columnHeader}>Buyer</span>
                    {renderSortIcon('buyers')}
                  </button>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={buyerFilter}
                  onChange={(e) => setBuyerFilter(e.target.value)}
                />
              </th>
              <th>
                <div className={styles.columnHeaderRow}>
                  <button className={styles.sortBtn} onClick={() => handleSort('roles')}>
                    <span className={styles.columnHeader}>Roles</span>
                    {renderSortIcon('roles')}
                  </button>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={rolesFilter}
                  onChange={(e) => setRolesFilter(e.target.value)}
                />
              </th>
              <th className={styles.emailCol}>
                <div className={styles.columnHeaderRow}>
                  <button className={styles.sortBtn} onClick={() => handleSort('email')}>
                    <span className={styles.columnHeader}>Email</span>
                    {renderSortIcon('email')}
                  </button>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={emailFilter}
                  onChange={(e) => setEmailFilter(e.target.value)}
                />
              </th>
              <th>
                <div className={styles.columnHeaderRow}>
                  <button className={styles.sortBtn} onClick={() => handleSort('overallUserStatus')}>
                    <span className={styles.columnHeader}>Status</span>
                    {renderSortIcon('overallUserStatus')}
                  </button>
                </div>
                <select
                  className={styles.filterSelect}
                  value={statusFilter}
                  onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
                >
                  <option value="">Select</option>
                  {STATUS_OPTIONS.filter(Boolean).map(opt => (
                    <option key={opt} value={opt}>{opt}</option>
                  ))}
                </select>
              </th>
              <th className={styles.actionsHeader}>
                <button className={styles.columnPickerBtn} title="Column selector">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                </button>
              </th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6} className={styles.loadingCell}>Loading...</td></tr>
            ) : sortedContent.length === 0 ? (
              <tr><td colSpan={6} className={styles.loadingCell}>No users found</td></tr>
            ) : sortedContent.map(user => (
              <tr key={user.userId}>
                <td>
                  <div className={styles.nameCell}>
                    <span className={styles.avatar}>{getInitials(user.fullName)}</span>
                    <span>{user.fullName}</span>
                  </div>
                </td>
                <td>{user.buyers}</td>
                <td>{user.roles}</td>
                <td className={styles.emailCol}>{user.email}</td>
                <td className={styles.statusCell}>
                  {renderStatusIcon(user.overallUserStatus)}
                  <span className={styles.statusDate}>{formatDate(user.changedDate)}</span>
                </td>
                <td className={styles.actionsCell}>
                  <button className={styles.editBtn} title="Edit user" onClick={(e) => { e.stopPropagation(); setEditUserId(user.userId); setModalMode('edit'); }}>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination — matches Mendix: "Currently showing 1 to 20 of N" */}
      {totalPages > 0 && (
        <div className={styles.pagination}>
          <button
            className={styles.pageBtn}
            disabled={page === 0}
            onClick={() => setPage(0)}
            title="Go to first page"
          >
            &laquo;
          </button>
          <button
            className={styles.pageBtn}
            disabled={page === 0}
            onClick={() => setPage(p => Math.max(0, p - 1))}
            title="Go to previous page"
          >
            &lsaquo;
          </button>
          <span className={styles.pageInfo}>
            Currently showing {startItem} to {endItem} of {data?.totalElements}
          </span>
          <button
            className={styles.pageBtn}
            disabled={page >= totalPages - 1}
            onClick={() => setPage(p => p + 1)}
            title="Go to next page"
          >
            &rsaquo;
          </button>
          <button
            className={styles.pageBtn}
            disabled={page >= totalPages - 1}
            onClick={() => setPage(totalPages - 1)}
            title="Go to last page"
          >
            &raquo;
          </button>
        </div>
      )}

      {/* User Create/Edit Modal */}
      {modalMode && (
        <UserFormModal
          mode={modalMode}
          userId={editUserId}
          onClose={() => { setModalMode(null); setEditUserId(null); }}
          onSaved={() => { setModalMode(null); setEditUserId(null); fetchUsers(); }}
        />
      )}
    </div>
  );
}
