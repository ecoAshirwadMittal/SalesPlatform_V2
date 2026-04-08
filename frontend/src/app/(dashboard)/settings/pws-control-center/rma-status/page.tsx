'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/rma-status';

interface RmaStatus {
  id: number;
  sort_order: number;
  system_status: string;
  internal_status_text: string;
  external_status_text: string;
  status_group: string;
  bidder_message: string | null;
  description: string;
  is_default: boolean;
}

export default function RmaStatusPage() {
  const [items, setItems] = useState<RmaStatus[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Record<string, unknown>>({});
  const [adding, setAdding] = useState(false);
  const [newItem, setNewItem] = useState<Record<string, unknown>>({
    sortOrder: 0, systemStatus: '', internalStatusText: '', externalStatusText: '',
    statusGroup: '', bidderMessage: '', description: '', isDefault: false,
  });

  async function load() {
    try {
      const res = await apiFetch(API);
      setItems(await res.json());
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  function startEdit(item: RmaStatus) {
    setEditId(item.id);
    setEditData({
      sortOrder: item.sort_order, systemStatus: item.system_status,
      internalStatusText: item.internal_status_text, externalStatusText: item.external_status_text,
      statusGroup: item.status_group, bidderMessage: item.bidder_message || '',
      description: item.description, isDefault: item.is_default,
    });
  }

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editData) });
      setEditId(null);
      setBanner({ type: 'success', message: 'RMA status updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleCreate() {
    try {
      await apiFetch(API, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newItem) });
      setAdding(false);
      setNewItem({ sortOrder: 0, systemStatus: '', internalStatusText: '', externalStatusText: '', statusGroup: '', bidderMessage: '', description: '', isDefault: false });
      setBanner({ type: 'success', message: 'RMA status created.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this RMA status?')) return;
    try {
      await apiFetch(`${API}/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'RMA status deleted.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  if (loading) return <div className={s.loading}>Loading RMA statuses...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>RMA Status Configuration</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.card}>
        <div className={s.toolbar}>
          <button className={s.addBtn} onClick={() => setAdding(true)}>+ Add Status</button>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Order</th>
              <th>System Status</th>
              <th>Internal Text</th>
              <th>External Text</th>
              <th>Group</th>
              <th>Description</th>
              <th>Default</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {adding && (
              <tr>
                <td><input className={s.cellInputShort} type="number" value={String(newItem.sortOrder)} onChange={e => setNewItem({ ...newItem, sortOrder: Number(e.target.value) })} /></td>
                <td><input className={s.cellInput} value={String(newItem.systemStatus)} onChange={e => setNewItem({ ...newItem, systemStatus: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.internalStatusText)} onChange={e => setNewItem({ ...newItem, internalStatusText: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.externalStatusText)} onChange={e => setNewItem({ ...newItem, externalStatusText: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.statusGroup)} onChange={e => setNewItem({ ...newItem, statusGroup: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.description)} onChange={e => setNewItem({ ...newItem, description: e.target.value })} /></td>
                <td><button className={`${s.toggle} ${newItem.isDefault ? s.toggleOn : ''}`} onClick={() => setNewItem({ ...newItem, isDefault: !newItem.isDefault })} /></td>
                <td><div className={s.actionsCell}><button className={s.saveRowBtn} onClick={handleCreate}>Save</button><button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button></div></td>
              </tr>
            )}
            {items.map(item => (
              <tr key={item.id}>
                <td>{editId === item.id ? <input className={s.cellInputShort} type="number" value={String(editData.sortOrder)} onChange={e => setEditData({ ...editData, sortOrder: Number(e.target.value) })} /> : item.sort_order}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.systemStatus)} onChange={e => setEditData({ ...editData, systemStatus: e.target.value })} /> : item.system_status}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.internalStatusText)} onChange={e => setEditData({ ...editData, internalStatusText: e.target.value })} /> : item.internal_status_text}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.externalStatusText)} onChange={e => setEditData({ ...editData, externalStatusText: e.target.value })} /> : item.external_status_text}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.statusGroup)} onChange={e => setEditData({ ...editData, statusGroup: e.target.value })} /> : item.status_group}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.description)} onChange={e => setEditData({ ...editData, description: e.target.value })} /> : item.description}</td>
                <td>{editId === item.id ? <button className={`${s.toggle} ${editData.isDefault ? s.toggleOn : ''}`} onClick={() => setEditData({ ...editData, isDefault: !editData.isDefault })} /> : (item.is_default ? 'Yes' : 'No')}</td>
                <td>
                  <div className={s.actionsCell}>
                    {editId === item.id ? (
                      <><button className={s.saveRowBtn} onClick={() => handleSave(item.id)}>Save</button><button className={s.actionBtn} onClick={() => setEditId(null)}>Cancel</button></>
                    ) : (
                      <><button className={s.actionBtn} onClick={() => startEdit(item)}>Edit</button><button className={s.deleteBtn} onClick={() => handleDelete(item.id)}>Delete</button></>
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
