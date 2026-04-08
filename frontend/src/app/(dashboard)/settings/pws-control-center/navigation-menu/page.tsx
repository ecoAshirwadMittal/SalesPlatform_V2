'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/navigation-menu';

interface NavMenuItem {
  id: number;
  name: string;
  is_active: boolean;
  sort_order: number;
  loading_message: string | null;
  microflow_name: string | null;
  icon_css_class: string | null;
  user_group: string | null;
}

export default function NavigationMenuPage() {
  const [items, setItems] = useState<NavMenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Record<string, unknown>>({});
  const [adding, setAdding] = useState(false);
  const [newItem, setNewItem] = useState<Record<string, unknown>>({
    name: '', isActive: true, sortOrder: 0, loadingMessage: '', microflowName: '', iconCssClass: '', userGroup: '',
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

  function startEdit(item: NavMenuItem) {
    setEditId(item.id);
    setEditData({
      name: item.name, isActive: item.is_active, sortOrder: item.sort_order,
      loadingMessage: item.loading_message || '', microflowName: item.microflow_name || '',
      iconCssClass: item.icon_css_class || '', userGroup: item.user_group || '',
    });
  }

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editData) });
      setEditId(null);
      setBanner({ type: 'success', message: 'Menu item updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleCreate() {
    try {
      await apiFetch(API, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newItem) });
      setAdding(false);
      setNewItem({ name: '', isActive: true, sortOrder: 0, loadingMessage: '', microflowName: '', iconCssClass: '', userGroup: '' });
      setBanner({ type: 'success', message: 'Menu item created.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this menu item?')) return;
    try {
      await apiFetch(`${API}/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'Menu item deleted.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  if (loading) return <div className={s.loading}>Loading navigation menu...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>PWS Navigation Menu</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.card}>
        <div className={s.toolbar}>
          <button className={s.addBtn} onClick={() => setAdding(true)}>+ Add Menu Item</button>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Active</th>
              <th>Order</th>
              <th>Loading Message</th>
              <th>Icon Class</th>
              <th>User Group</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {adding && (
              <tr>
                <td><input className={s.cellInput} value={String(newItem.name)} onChange={e => setNewItem({ ...newItem, name: e.target.value })} /></td>
                <td><button className={`${s.toggle} ${newItem.isActive ? s.toggleOn : ''}`} onClick={() => setNewItem({ ...newItem, isActive: !newItem.isActive })} /></td>
                <td><input className={s.cellInputShort} type="number" value={String(newItem.sortOrder)} onChange={e => setNewItem({ ...newItem, sortOrder: Number(e.target.value) })} /></td>
                <td><input className={s.cellInput} value={String(newItem.loadingMessage)} onChange={e => setNewItem({ ...newItem, loadingMessage: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.iconCssClass)} onChange={e => setNewItem({ ...newItem, iconCssClass: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.userGroup)} onChange={e => setNewItem({ ...newItem, userGroup: e.target.value })} /></td>
                <td><div className={s.actionsCell}><button className={s.saveRowBtn} onClick={handleCreate}>Save</button><button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button></div></td>
              </tr>
            )}
            {items.map(item => (
              <tr key={item.id}>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.name)} onChange={e => setEditData({ ...editData, name: e.target.value })} /> : item.name}</td>
                <td>{editId === item.id ? <button className={`${s.toggle} ${editData.isActive ? s.toggleOn : ''}`} onClick={() => setEditData({ ...editData, isActive: !editData.isActive })} /> : (item.is_active ? 'Yes' : 'No')}</td>
                <td>{editId === item.id ? <input className={s.cellInputShort} type="number" value={String(editData.sortOrder)} onChange={e => setEditData({ ...editData, sortOrder: Number(e.target.value) })} /> : item.sort_order}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.loadingMessage)} onChange={e => setEditData({ ...editData, loadingMessage: e.target.value })} /> : (item.loading_message || '')}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.iconCssClass)} onChange={e => setEditData({ ...editData, iconCssClass: e.target.value })} /> : (item.icon_css_class || '')}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.userGroup)} onChange={e => setEditData({ ...editData, userGroup: e.target.value })} /> : (item.user_group || '')}</td>
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
