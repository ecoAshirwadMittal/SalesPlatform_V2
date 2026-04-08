'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/feature-flags';

interface FeatureFlag {
  id: number;
  name: string;
  active: boolean;
  description: string | null;
}

export default function FeatureFlagsPage() {
  const [flags, setFlags] = useState<FeatureFlag[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Partial<FeatureFlag>>({});
  const [adding, setAdding] = useState(false);
  const [newFlag, setNewFlag] = useState<Partial<FeatureFlag>>({ name: '', active: false, description: '' });

  async function load() {
    try {
      const res = await apiFetch(API);
      const data = await res.json();
      setFlags(data);
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(editData),
      });
      setEditId(null);
      setBanner({ type: 'success', message: 'Feature flag updated.' });
      load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleCreate() {
    try {
      await apiFetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newFlag),
      });
      setAdding(false);
      setNewFlag({ name: '', active: false, description: '' });
      setBanner({ type: 'success', message: 'Feature flag created.' });
      load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this feature flag?')) return;
    try {
      await apiFetch(`${API}/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'Feature flag deleted.' });
      load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    }
  }

  function startEdit(flag: FeatureFlag) {
    setEditId(flag.id);
    setEditData({ name: flag.name, active: flag.active, description: flag.description || '' });
  }

  async function handleToggle(flag: FeatureFlag) {
    try {
      await apiFetch(`${API}/${flag.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: flag.name, active: !flag.active, description: flag.description }),
      });
      load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    }
  }

  if (loading) return <div className={s.loading}>Loading feature flags...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>PWS Feature Flags</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.card}>
        <div className={s.toolbar}>
          <button className={s.addBtn} onClick={() => setAdding(true)}>+ Add Flag</button>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Active</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {adding && (
              <tr>
                <td><input className={s.cellInput} value={newFlag.name || ''} onChange={e => setNewFlag({ ...newFlag, name: e.target.value })} placeholder="Flag name" /></td>
                <td>
                  <button className={`${s.toggle} ${newFlag.active ? s.toggleOn : ''}`} onClick={() => setNewFlag({ ...newFlag, active: !newFlag.active })} />
                </td>
                <td><input className={s.cellInput} value={newFlag.description || ''} onChange={e => setNewFlag({ ...newFlag, description: e.target.value })} placeholder="Description" /></td>
                <td>
                  <div className={s.actionsCell}>
                    <button className={s.saveRowBtn} onClick={handleCreate}>Save</button>
                    <button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button>
                  </div>
                </td>
              </tr>
            )}
            {flags.map(flag => (
              <tr key={flag.id}>
                <td>
                  {editId === flag.id
                    ? <input className={s.cellInput} value={editData.name || ''} onChange={e => setEditData({ ...editData, name: e.target.value })} />
                    : flag.name}
                </td>
                <td>
                  {editId === flag.id
                    ? <button className={`${s.toggle} ${editData.active ? s.toggleOn : ''}`} onClick={() => setEditData({ ...editData, active: !editData.active })} />
                    : <button className={`${s.toggle} ${flag.active ? s.toggleOn : ''}`} onClick={() => handleToggle(flag)} />}
                </td>
                <td>
                  {editId === flag.id
                    ? <input className={s.cellInput} value={editData.description || ''} onChange={e => setEditData({ ...editData, description: e.target.value })} />
                    : (flag.description || '')}
                </td>
                <td>
                  <div className={s.actionsCell}>
                    {editId === flag.id ? (
                      <>
                        <button className={s.saveRowBtn} onClick={() => handleSave(flag.id)}>Save</button>
                        <button className={s.actionBtn} onClick={() => setEditId(null)}>Cancel</button>
                      </>
                    ) : (
                      <>
                        <button className={s.actionBtn} onClick={() => startEdit(flag)}>Edit</button>
                        <button className={s.deleteBtn} onClick={() => handleDelete(flag.id)}>Delete</button>
                      </>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
