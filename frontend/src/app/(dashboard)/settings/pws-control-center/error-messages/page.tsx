'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/error-messages';

interface ErrorMessage {
  id: number;
  source_system: string;
  source_error_code: string;
  source_error_type: string;
  user_error_code: string;
  user_error_message: string;
  bypass_for_user: boolean;
}

export default function ErrorMessagesPage() {
  const [items, setItems] = useState<ErrorMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Record<string, unknown>>({});
  const [adding, setAdding] = useState(false);
  const [newItem, setNewItem] = useState<Record<string, unknown>>({
    sourceSystem: '', sourceErrorCode: '', sourceErrorType: '',
    userErrorCode: '', userErrorMessage: '', bypassForUser: false,
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

  function startEdit(item: ErrorMessage) {
    setEditId(item.id);
    setEditData({
      sourceSystem: item.source_system, sourceErrorCode: item.source_error_code,
      sourceErrorType: item.source_error_type, userErrorCode: item.user_error_code,
      userErrorMessage: item.user_error_message, bypassForUser: item.bypass_for_user,
    });
  }

  async function handleSave(id: number) {
    try {
      await apiFetch(`${API}/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editData) });
      setEditId(null);
      setBanner({ type: 'success', message: 'Error message updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleCreate() {
    try {
      await apiFetch(API, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newItem) });
      setAdding(false);
      setNewItem({ sourceSystem: '', sourceErrorCode: '', sourceErrorType: '', userErrorCode: '', userErrorMessage: '', bypassForUser: false });
      setBanner({ type: 'success', message: 'Error message created.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this error message?')) return;
    try {
      await apiFetch(`${API}/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'Error message deleted.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  if (loading) return <div className={s.loading}>Loading error messages...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>PWS Error Messages</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.card}>
        <div className={s.toolbar}>
          <button className={s.addBtn} onClick={() => setAdding(true)}>+ Add Error Message</button>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Source System</th>
              <th>Source Code</th>
              <th>Source Type</th>
              <th>User Code</th>
              <th>User Message</th>
              <th>Bypass</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {adding && (
              <tr>
                <td><input className={s.cellInput} value={String(newItem.sourceSystem)} onChange={e => setNewItem({ ...newItem, sourceSystem: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.sourceErrorCode)} onChange={e => setNewItem({ ...newItem, sourceErrorCode: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.sourceErrorType)} onChange={e => setNewItem({ ...newItem, sourceErrorType: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.userErrorCode)} onChange={e => setNewItem({ ...newItem, userErrorCode: e.target.value })} /></td>
                <td><input className={s.cellInput} value={String(newItem.userErrorMessage)} onChange={e => setNewItem({ ...newItem, userErrorMessage: e.target.value })} /></td>
                <td><button className={`${s.toggle} ${newItem.bypassForUser ? s.toggleOn : ''}`} onClick={() => setNewItem({ ...newItem, bypassForUser: !newItem.bypassForUser })} /></td>
                <td><div className={s.actionsCell}><button className={s.saveRowBtn} onClick={handleCreate}>Save</button><button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button></div></td>
              </tr>
            )}
            {items.map(item => (
              <tr key={item.id}>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.sourceSystem)} onChange={e => setEditData({ ...editData, sourceSystem: e.target.value })} /> : item.source_system}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.sourceErrorCode)} onChange={e => setEditData({ ...editData, sourceErrorCode: e.target.value })} /> : item.source_error_code}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.sourceErrorType)} onChange={e => setEditData({ ...editData, sourceErrorType: e.target.value })} /> : item.source_error_type}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.userErrorCode)} onChange={e => setEditData({ ...editData, userErrorCode: e.target.value })} /> : item.user_error_code}</td>
                <td>{editId === item.id ? <input className={s.cellInput} value={String(editData.userErrorMessage)} onChange={e => setEditData({ ...editData, userErrorMessage: e.target.value })} /> : item.user_error_message}</td>
                <td>{editId === item.id ? <button className={`${s.toggle} ${editData.bypassForUser ? s.toggleOn : ''}`} onClick={() => setEditData({ ...editData, bypassForUser: !editData.bypassForUser })} /> : (item.bypass_for_user ? 'Yes' : 'No')}</td>
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
