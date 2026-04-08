'use client';

import { useEffect, useState, useCallback } from 'react';
import styles from './UserFormModal.module.css';
import { apiFetch } from '@/lib/apiFetch';

interface Role { id: number; name: string; }
interface Buyer { id: number; companyName: string; }

interface UserDetail {
  userId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  overallUserStatus: string;
  userStatus: string;
  inactive: boolean;
  localUser: boolean;
  buyerRole: boolean;
  landingPagePreference: string;
  invitedDate: string | null;
  lastInviteSent: string | null;
  activationDate: string | null;
  lastLogin: string | null;
  roleIds: number[];
  buyerIds: number[];
}

interface Props {
  mode: 'create' | 'edit';
  userId?: number | null;
  onClose: () => void;
  onSaved: () => void;
}

const LANDING_OPTIONS = [
  { value: 'Wholesale_Auction', label: 'Wholesale Auction' },
  { value: 'Premium_Wholesale', label: 'Premium Wholesale' },
];

const STATUS_OPTIONS = [
  { value: 'Active', label: 'Active' },
  { value: 'Disabled', label: 'Disabled' },
];

function formatDateTime(val: string | null): string {
  if (!val) return '—';
  const d = new Date(val);
  if (isNaN(d.getTime())) return '—';
  return d.toLocaleDateString('en-US') + ' ' + d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

export default function UserFormModal({ mode, userId, onClose, onSaved }: Props) {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [roles, setRoles] = useState<Role[]>([]);
  const [buyers, setBuyers] = useState<Buyer[]>([]);
  const [buyerSearch, setBuyerSearch] = useState('');

  // Form state
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [userStatus, setUserStatus] = useState('Active');
  const [inactive, setInactive] = useState(false);
  const [landingPage, setLandingPage] = useState('Wholesale_Auction');
  const [selectedRoleIds, setSelectedRoleIds] = useState<number[]>([]);
  const [selectedBuyerIds, setSelectedBuyerIds] = useState<number[]>([]);

  // Readonly dates (edit mode)
  const [invitedDate, setInvitedDate] = useState<string | null>(null);
  const [lastInviteSent, setLastInviteSent] = useState<string | null>(null);
  const [activationDate, setActivationDate] = useState<string | null>(null);
  const [lastLogin, setLastLogin] = useState<string | null>(null);
  const [isLocalUser, setIsLocalUser] = useState(false);

  const [error, setError] = useState<string | null>(null);

  // Has Bidder role selected? Show buyer section if so.
  const hasBidderRole = roles.some(r =>
    r.name.toLowerCase() === 'bidder' && selectedRoleIds.includes(r.id)
  );

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [rolesRes, buyersRes] = await Promise.all([
       apiFetch('/api/v1/users/roles'),
       apiFetch('/api/v1/users/buyers'),
      ]);
      if (!rolesRes.ok) throw new Error(`Failed to load roles (HTTP ${rolesRes.status})`);
      if (!buyersRes.ok) throw new Error(`Failed to load buyers (HTTP ${buyersRes.status})`);
      const rolesData: Role[] = await rolesRes.json();
      const buyersData: Buyer[] = await buyersRes.json();
      setRoles(rolesData);
      setBuyers(buyersData);

      if (mode === 'edit' && userId) {
        const detailRes = await apiFetch(`/api/v1/users/direct-users/${userId}`);
        if (!detailRes.ok) throw new Error(`Failed to load user (HTTP ${detailRes.status})`);
        const user: UserDetail = await detailRes.json();
        setFirstName(user.firstName || '');
        setLastName(user.lastName || '');
        setEmail(user.email || '');
        setUserStatus(user.userStatus || 'Active');
        setInactive(user.inactive);
        setLandingPage(user.landingPagePreference || 'Wholesale_Auction');
        setSelectedRoleIds(user.roleIds || []);
        setSelectedBuyerIds(user.buyerIds || []);
        setInvitedDate(user.invitedDate);
        setLastInviteSent(user.lastInviteSent);
        setActivationDate(user.activationDate);
        setLastLogin(user.lastLogin);
        setIsLocalUser(user.localUser);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load data');
    } finally {
      setLoading(false);
    }
  }, [mode, userId]);

  useEffect(() => { loadData(); }, [loadData]);

  const toggleRole = (roleId: number) => {
    setSelectedRoleIds(prev =>
      prev.includes(roleId) ? prev.filter(id => id !== roleId) : [...prev, roleId]
    );
  };

  const toggleBuyer = (buyerId: number) => {
    setSelectedBuyerIds(prev =>
      prev.includes(buyerId) ? prev.filter(id => id !== buyerId) : [...prev, buyerId]
    );
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      const body = {
        firstName,
        lastName,
        email,
        userStatus,
        inactive,
        landingPagePreference: landingPage,
        roleIds: selectedRoleIds,
        buyerIds: hasBidderRole ? selectedBuyerIds : [],
      };

      const url = mode === 'create'
        ? '/api/v1/users/direct-users'
        : `/api/v1/users/direct-users/${userId}`;

      const res = await apiFetch(url, {
        method: mode === 'create' ? 'POST' : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `HTTP ${res.status}`);
      }

      onSaved();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  const filteredBuyers = buyerSearch
    ? buyers.filter(b => b.companyName.toLowerCase().includes(buyerSearch.toLowerCase()))
    : buyers;

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        {/* Header */}
        <div className={styles.header}>
          <h3 className={styles.title}>
            {mode === 'create' ? 'Create New User' : 'Edit User'}
          </h3>
          <button className={styles.closeBtn} onClick={onClose} title="Close">&times;</button>
        </div>

        {loading ? (
          <div className={styles.loadingBody}>Loading...</div>
        ) : (
          <>
            <div className={styles.body}>
              {error && <div className={styles.errorBanner}>{error}</div>}

              {/* Readonly dates — edit mode only */}
              {mode === 'edit' && (
                <div className={styles.readonlyDates}>
                  <div className={styles.dateItem}>
                    <span className={styles.dateLabel}>Last Invite Sent</span>
                    <span className={styles.dateValue}>{formatDateTime(lastInviteSent)}</span>
                  </div>
                  <div className={styles.dateItem}>
                    <span className={styles.dateLabel}>Activation Date</span>
                    <span className={styles.dateValue}>{formatDateTime(activationDate)}</span>
                  </div>
                  <div className={styles.dateItem}>
                    <span className={styles.dateLabel}>Last Login</span>
                    <span className={styles.dateValue}>{formatDateTime(lastLogin)}</span>
                  </div>
                </div>
              )}

              {/* Email */}
              <div className={styles.formGroup}>
                <label className={styles.label}>Email</label>
                <input
                  type="email"
                  className={styles.input}
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  readOnly={mode === 'edit'}
                  placeholder="user@example.com"
                />
              </div>

              {/* First / Last name */}
              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label className={styles.label}>First Name</label>
                  <input
                    type="text"
                    className={styles.input}
                    value={firstName}
                    onChange={e => setFirstName(e.target.value)}
                  />
                </div>
                <div className={styles.formGroup}>
                  <label className={styles.label}>Last Name</label>
                  <input
                    type="text"
                    className={styles.input}
                    value={lastName}
                    onChange={e => setLastName(e.target.value)}
                  />
                </div>
              </div>

              {/* Status + Inactive — edit only */}
              {mode === 'edit' && (
                <div className={styles.formRow}>
                  <div className={styles.formGroup}>
                    <label className={styles.label}>User Status</label>
                    <select
                      className={styles.select}
                      value={userStatus}
                      onChange={e => setUserStatus(e.target.value)}
                    >
                      {STATUS_OPTIONS.map(o => (
                        <option key={o.value} value={o.value}>{o.label}</option>
                      ))}
                    </select>
                  </div>
                  <div className={styles.formGroup}>
                    <label className={styles.checkboxLabel}>
                      <input
                        type="checkbox"
                        checked={inactive}
                        onChange={e => setInactive(e.target.checked)}
                      />
                      Inactive
                    </label>
                  </div>
                </div>
              )}

              {/* Landing page */}
              <div className={styles.formGroup}>
                <label className={styles.label}>Landing Page Preference</label>
                <select
                  className={styles.select}
                  value={landingPage}
                  onChange={e => setLandingPage(e.target.value)}
                >
                  {LANDING_OPTIONS.map(o => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              </div>

              {/* Roles — checkbox set */}
              <div className={styles.formGroup}>
                <label className={styles.label}>Roles</label>
                <div className={styles.checkboxSet}>
                  {roles.map(role => (
                    <label key={role.id} className={styles.checkboxItem}>
                      <input
                        type="checkbox"
                        checked={selectedRoleIds.includes(role.id)}
                        onChange={() => toggleRole(role.id)}
                      />
                      {role.name}
                    </label>
                  ))}
                </div>
              </div>

              {/* Buyers — shown when Bidder role selected */}
              {hasBidderRole && (
                <div className={styles.formGroup}>
                  <label className={styles.label}>Buyers</label>
                  <input
                    type="text"
                    className={styles.input}
                    placeholder="Search buyers..."
                    value={buyerSearch}
                    onChange={e => setBuyerSearch(e.target.value)}
                  />
                  <div className={styles.buyerList}>
                    {filteredBuyers.map(buyer => (
                      <label key={buyer.id} className={styles.checkboxItem}>
                        <input
                          type="checkbox"
                          checked={selectedBuyerIds.includes(buyer.id)}
                          onChange={() => toggleBuyer(buyer.id)}
                        />
                        {buyer.companyName}
                      </label>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Footer buttons */}
            <div className={styles.footer}>
              <button className={styles.cancelBtn} onClick={onClose} disabled={saving}>
                Cancel
              </button>
              <button className={styles.saveBtn} onClick={handleSave} disabled={saving}>
                {saving ? 'Saving...' : 'Save'}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
