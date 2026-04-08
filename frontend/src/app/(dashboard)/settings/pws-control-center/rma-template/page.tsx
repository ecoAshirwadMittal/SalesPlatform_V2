'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin';

interface RmaTemplate {
  id: number;
  template_name: string;
  is_active: boolean;
  file_name: string | null;
}

interface RmaReason {
  id: number;
  valid_reason: string;
  is_active: boolean;
}

export default function RmaTemplatePage() {
  const [templates, setTemplates] = useState<RmaTemplate[]>([]);
  const [reasons, setReasons] = useState<RmaReason[]>([]);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const [editTemplateId, setEditTemplateId] = useState<number | null>(null);
  const [editTemplateData, setEditTemplateData] = useState<Record<string, unknown>>({});

  const [editReasonId, setEditReasonId] = useState<number | null>(null);
  const [editReasonData, setEditReasonData] = useState<Record<string, unknown>>({});
  const [addingReason, setAddingReason] = useState(false);
  const [newReason, setNewReason] = useState({ validReason: '', isActive: true });

  async function load() {
    try {
      const res = await apiFetch(`${API}/rma-templates`);
      const data = await res.json();
      setTemplates(data.templates || []);
      setReasons(data.reasons || []);
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function saveTemplate(id: number) {
    try {
      await apiFetch(`${API}/rma-templates/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editTemplateData) });
      setEditTemplateId(null);
      setBanner({ type: 'success', message: 'Template updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function saveReason(id: number) {
    try {
      await apiFetch(`${API}/rma-reasons/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(editReasonData) });
      setEditReasonId(null);
      setBanner({ type: 'success', message: 'Reason updated.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function createReason() {
    try {
      await apiFetch(`${API}/rma-reasons`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newReason) });
      setAddingReason(false);
      setNewReason({ validReason: '', isActive: true });
      setBanner({ type: 'success', message: 'Reason created.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  async function deleteReason(id: number) {
    if (!confirm('Delete this reason?')) return;
    try {
      await apiFetch(`${API}/rma-reasons/${id}`, { method: 'DELETE' });
      setBanner({ type: 'success', message: 'Reason deleted.' });
      load();
    } catch (err) { setBanner({ type: 'error', message: String(err) }); }
  }

  if (loading) return <div className={s.loading}>Loading RMA templates...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>RMA Template &amp; Reasons</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      {/* Templates section */}
      <div className={s.card} style={{ marginBottom: 24 }}>
        <h3 className={s.sectionTitle}>Templates</h3>
        <table className={s.table}>
          <thead>
            <tr>
              <th>Template Name</th>
              <th>Active</th>
              <th>File Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {templates.map(t => (
              <tr key={t.id}>
                <td>
                  {editTemplateId === t.id
                    ? <input className={s.cellInput} value={String(editTemplateData.templateName || '')} onChange={e => setEditTemplateData({ ...editTemplateData, templateName: e.target.value })} />
                    : t.template_name}
                </td>
                <td>
                  {editTemplateId === t.id
                    ? <button className={`${s.toggle} ${editTemplateData.isActive ? s.toggleOn : ''}`} onClick={() => setEditTemplateData({ ...editTemplateData, isActive: !editTemplateData.isActive })} />
                    : (t.is_active ? 'Yes' : 'No')}
                </td>
                <td>
                  {editTemplateId === t.id
                    ? <input className={s.cellInput} value={String(editTemplateData.fileName || '')} onChange={e => setEditTemplateData({ ...editTemplateData, fileName: e.target.value })} />
                    : (t.file_name || '')}
                </td>
                <td>
                  <div className={s.actionsCell}>
                    {editTemplateId === t.id ? (
                      <><button className={s.saveRowBtn} onClick={() => saveTemplate(t.id)}>Save</button><button className={s.actionBtn} onClick={() => setEditTemplateId(null)}>Cancel</button></>
                    ) : (
                      <button className={s.actionBtn} onClick={() => { setEditTemplateId(t.id); setEditTemplateData({ templateName: t.template_name, isActive: t.is_active, fileName: t.file_name || '' }); }}>Edit</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Reasons section */}
      <div className={s.card}>
        <div className={s.toolbar}>
          <h3 className={s.sectionTitle} style={{ margin: 0 }}>RMA Reasons</h3>
          <button className={s.addBtn} onClick={() => setAddingReason(true)}>+ Add Reason</button>
        </div>
        <table className={s.table}>
          <thead>
            <tr>
              <th>Reason</th>
              <th>Active</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {addingReason && (
              <tr>
                <td><input className={s.cellInput} value={newReason.validReason} onChange={e => setNewReason({ ...newReason, validReason: e.target.value })} placeholder="Reason text" /></td>
                <td><button className={`${s.toggle} ${newReason.isActive ? s.toggleOn : ''}`} onClick={() => setNewReason({ ...newReason, isActive: !newReason.isActive })} /></td>
                <td><div className={s.actionsCell}><button className={s.saveRowBtn} onClick={createReason}>Save</button><button className={s.actionBtn} onClick={() => setAddingReason(false)}>Cancel</button></div></td>
              </tr>
            )}
            {reasons.map(r => (
              <tr key={r.id}>
                <td>
                  {editReasonId === r.id
                    ? <input className={s.cellInput} value={String(editReasonData.validReason || '')} onChange={e => setEditReasonData({ ...editReasonData, validReason: e.target.value })} />
                    : r.valid_reason}
                </td>
                <td>
                  {editReasonId === r.id
                    ? <button className={`${s.toggle} ${editReasonData.isActive ? s.toggleOn : ''}`} onClick={() => setEditReasonData({ ...editReasonData, isActive: !editReasonData.isActive })} />
                    : (r.is_active ? 'Yes' : 'No')}
                </td>
                <td>
                  <div className={s.actionsCell}>
                    {editReasonId === r.id ? (
                      <><button className={s.saveRowBtn} onClick={() => saveReason(r.id)}>Save</button><button className={s.actionBtn} onClick={() => setEditReasonId(null)}>Cancel</button></>
                    ) : (
                      <><button className={s.actionBtn} onClick={() => { setEditReasonId(r.id); setEditReasonData({ validReason: r.valid_reason, isActive: r.is_active }); }}>Edit</button><button className={s.deleteBtn} onClick={() => deleteReason(r.id)}>Delete</button></>
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
