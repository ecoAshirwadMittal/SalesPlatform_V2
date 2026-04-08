'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/ranks-config';

interface Brand {
  id: number;
  name: string;
  display_name: string;
  is_enabled: boolean;
  sort_rank: number;
}

export default function RanksConfigPage() {
  const [brands, setBrands] = useState<Brand[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Record<string, unknown>>({});

  async function load() {
    try {
      const res = await apiFetch(API);
      const data = await res.json();
      setBrands(data);
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  function startEdit(b: Brand) {
    setEditId(b.id);
    setEditData({ name: b.name, displayName: b.display_name, isEnabled: b.is_enabled, sortRank: b.sort_rank });
  }

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(editData),
      });
      setEditId(null);
      setBanner({ type: 'success', message: 'Brand updated.' });
      load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    }
  }

  if (loading) return <div className={s.loading}>Loading ranks...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>PWS Ranks Configuration</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.card}>
        <table className={s.table}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Display Name</th>
              <th>Enabled</th>
              <th>Rank</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {brands.map(b => (
              <tr key={b.id}>
                <td>
                  {editId === b.id
                    ? <input className={s.cellInput} value={String(editData.name || '')} onChange={e => setEditData({ ...editData, name: e.target.value })} />
                    : b.name}
                </td>
                <td>
                  {editId === b.id
                    ? <input className={s.cellInput} value={String(editData.displayName || '')} onChange={e => setEditData({ ...editData, displayName: e.target.value })} />
                    : b.display_name}
                </td>
                <td>
                  {editId === b.id
                    ? <button className={`${s.toggle} ${editData.isEnabled ? s.toggleOn : ''}`} onClick={() => setEditData({ ...editData, isEnabled: !editData.isEnabled })} />
                    : <span>{b.is_enabled ? 'Yes' : 'No'}</span>}
                </td>
                <td>
                  {editId === b.id
                    ? <input className={s.cellInputShort} type="number" value={String(editData.sortRank || 0)} onChange={e => setEditData({ ...editData, sortRank: Number(e.target.value) })} />
                    : b.sort_rank}
                </td>
                <td>
                  <div className={s.actionsCell}>
                    {editId === b.id ? (
                      <>
                        <button className={s.saveRowBtn} onClick={() => handleSave(b.id)}>Save</button>
                        <button className={s.actionBtn} onClick={() => setEditId(null)}>Cancel</button>
                      </>
                    ) : (
                      <button className={s.actionBtn} onClick={() => startEdit(b)}>Edit</button>
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
