'use client';

import { useState, useEffect, useCallback } from 'react';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import {
  listEmailTemplates,
  createEmailTemplate,
  updateEmailTemplate,
  deleteEmailTemplate,
  type EmailTemplateView,
  type EmailTemplateUpsert,
} from '@/lib/adminEmailClient';
import { formatDate, type Banner } from './shared';
import { TemplateDetailEditor } from './TemplateDetailEditor';

const EMPTY_TEMPLATE: Partial<EmailTemplateView> = {
  templateKey: '',
  templateName: '',
  subject: '',
  contentHtml: '',
  contentPlain: '',
  hasAttachment: false,
  enabled: true,
};

/** Converts the (possibly-partial) editor state into a full `EmailTemplateUpsert` body. */
function toUpsertBody(d: Partial<EmailTemplateView>): EmailTemplateUpsert {
  return {
    templateKey: d.templateKey || '',
    templateName: d.templateName || '',
    subject: d.subject || '',
    contentHtml: d.contentHtml || '',
    contentPlain: d.contentPlain || null,
    fromAddress: d.fromAddress || null,
    fromDisplayName: d.fromDisplayName || null,
    replyTo: d.replyTo || null,
    toDefault: d.toDefault || null,
    ccDefault: d.ccDefault || null,
    bccDefault: d.bccDefault || null,
    hasAttachment: d.hasAttachment ?? false,
    enabled: d.enabled ?? true,
    description: d.description || null,
  };
}

export function TemplatesTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [templates, setTemplates] = useState<EmailTemplateView[]>([]);
  const [loading, setLoading] = useState(true);
  const [editId, setEditId] = useState<number | null>(null);
  const [creating, setCreating] = useState(false);
  const [formData, setFormData] = useState<Partial<EmailTemplateView>>(EMPTY_TEMPLATE);

  const load = useCallback(async () => {
    try {
      setTemplates(await listEmailTemplates());
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }, [onBanner]);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    setFormData(EMPTY_TEMPLATE);
    setCreating(true);
    setEditId(null);
  }

  function openEdit(tpl: EmailTemplateView) {
    setFormData({ ...tpl });
    setEditId(tpl.id);
    setCreating(false);
  }

  function closeForm() {
    setCreating(false);
    setEditId(null);
  }

  async function handleCreate() {
    try {
      await createEmailTemplate(toUpsertBody(formData));
      closeForm();
      onBanner({ type: 'success', message: 'Template created.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleSave() {
    if (editId === null) return;
    try {
      await updateEmailTemplate(editId, toUpsertBody(formData));
      closeForm();
      onBanner({ type: 'success', message: 'Template updated.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this email template?')) return;
    try {
      await deleteEmailTemplate(id);
      onBanner({ type: 'success', message: 'Template deleted.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  if (loading) return <div className={s.loading}>Loading templates...</div>;

  return (
    <>
      <div className={s.toolbar}>
        <button className={s.addBtn} onClick={openCreate}>+ New Template</button>
      </div>

      <table className={s.table}>
        <thead>
          <tr>
            <th style={{ width: '14%' }}>Template Key</th>
            <th style={{ width: '16%' }}>Template Name</th>
            <th style={{ width: '32%' }}>Subject</th>
            <th style={{ width: '10%' }}>Enabled</th>
            <th style={{ width: '14%' }}>Created</th>
            <th style={{ width: '14%' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {templates.map((tpl) => (
            <tr key={tpl.id}>
              <td>
                <button className={e.templateLink} onClick={() => openEdit(tpl)}>
                  {tpl.templateKey}
                </button>
              </td>
              <td>{tpl.templateName}</td>
              <td>{tpl.subject || '—'}</td>
              <td>{tpl.enabled ? 'Yes' : 'No'}</td>
              <td>{formatDate(tpl.createdDate)}</td>
              <td className={s.actionsCell}>
                <button className={s.actionBtn} onClick={() => openEdit(tpl)}>Edit</button>
                <button className={s.deleteBtn} onClick={() => handleDelete(tpl.id)}>Delete</button>
              </td>
            </tr>
          ))}
          {templates.length === 0 && (
            <tr><td colSpan={6} className={s.emptyState}>No email templates found.</td></tr>
          )}
        </tbody>
      </table>

      {(creating || editId !== null) && (
        <TemplateDetailEditor
          data={formData}
          isCreate={creating}
          onChange={setFormData}
          onSave={creating ? handleCreate : handleSave}
          onCancel={closeForm}
          onBanner={onBanner}
        />
      )}
    </>
  );
}
