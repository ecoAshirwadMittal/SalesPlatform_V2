'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/order-status';

interface OrderStatus {
  id: number;
  system_status: string;
  internal_status_text: string;
  external_status_text: string;
  internal_hex_code: string;
  external_hex_code: string;
  description: string;
}

export default function OrderStatusPage() {
  const [items, setItems] = useState<OrderStatus[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Record<string, unknown>>({});
  const [adding, setAdding] = useState(false);
  const [newItem, setNewItem] = useState<Record<string, string>>({
    systemStatus: '', internalStatusText: '', externalStatusText: '',
    internalHexCode: '', externalHexCode: '', description: '',
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

  function startEdit(item: OrderStatus) {
    setEditId(item.id);
    setEditData({
      systemStatus: item.system_status, internalStatusText: item.internal_status_text,
      externalStatusText: item.external_status_text, internalHexCode: item.internal_hex_code,
      externalHexCode: item.external_hex_code, description: item.description,
    });
  }

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editData) });
      setEditId(null);
      setBanner({ type: 'success', message: 'Order status updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleCreate() {
    try {
      await apiFetch(API, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newItem) });
      setAdding(false);
      setNewItem({ systemStatus: '', internalStatusText: '', externalStatusText: '', internalHexCode: '', externalHexCode: '', description: '' });
      setBanner({ type: 'success', message: 'Order status created.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this order status?')) return;
    try {
      await apiFetch(`${API}/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'Order status deleted.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  if (loading) return <div className={s.loading}>Loading order statuses...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Order Status Configuration</h2>
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
              <th>System Status</th>
              <th>Internal Text</th>
              <th>External Text</th>
              <th>Internal Color</th>
              <th>External Color</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {adding && (
              <tr>
                <td><input className={s.cellInput} value={newItem.systemStatus} onChange={e => setNewItem({ ...newItem, systemStatus: e.target.value })} /></td>
                <td><input className={s.cellInput} value={newItem.internalStatusText} onChange={e => setNewItem({ ...newItem, internalStatusText: e.target.value })} /></td>
                <td><input className={s.cellInput} value={newItem.externalStatusText} onChange={e => setNewItem({ ...newItem, externalStatusText: e.target.value })} /></td>
                <td><input className={s.cellInput} value={newItem.internalHexCode} onChange={e => setNewItem({ ...newItem, internalHexCode: e.target.value })} /></td>
                <td><input className={s.cellInput} value={newItem.externalHexCode} onChange={e => setNewItem({ ...newItem, externalHexCode: e.target.value })} /></td>
                <td><input className={s.cellInput} value={newItem.description} onChange={e => setNewItem({ ...newItem, description: e.target.value })} /></td>
                <td><div className={s.actionsCell}><button className={s.saveRowBtn} onClick={handleCreate}>Save</button><button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button></div></td>
              </tr>
            )}
            {items.map(item => (
              <tr key={item.id}>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.systemStatus)} onChange={e => setEditData({ ...editData, systemStatus: e.target.value })} /> : item.system_status}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.internalStatusText)} onChange={e => setEditData({ ...editData, internalStatusText: e.target.value })} /> : item.internal_status_text}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.externalStatusText)} onChange={e => setEditData({ ...editData, externalStatusText: e.target.value })} /> : item.external_status_text}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.internalHexCode)} onChange={e => setEditData({ ...editData, internalHexCode: e.target.value })} /> : item.internal_hex_code}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.externalHexCode)} onChange={e => setEditData({ ...editData, externalHexCode: e.target.value })} /> : item.external_hex_code}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.description)} onChange={e => setEditData({ ...editData, description: e.target.value })} /> : item.description}</td>
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
