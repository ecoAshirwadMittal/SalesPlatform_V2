'use client';

import { useCallback, useEffect, useState } from 'react';
import {
  listEmailTemplates,
  updateEmailTemplate,
  type EmailTemplateView,
  type EmailTemplateUpdate,
} from '@/lib/adminEmailTemplatesClient';
import { EmailTemplateEditor } from './EmailTemplateEditor';
import styles from './emailTemplates.module.css';

/**
 * SPKB-3664 (admin half) — admin email-templates editor.
 *
 * Renders the 3 seeded templates from
 * {@code partial_credit.email_templates}. Each row exposes an Edit
 * button that swaps the row in-place for the editor component
 * ({@link EmailTemplateEditor}). No "New Template" affordance: the
 * listener code references {@code template_key} directly, so adding a
 * key here without a matching code change would silently break the
 * listener.
 */
export default function EmailTemplatesPage() {
  const [rows, setRows] = useState<EmailTemplateView[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [toast, setToast] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      setRows(await listEmailTemplates());
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to load templates';
      setLoadError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const handleSave = useCallback(
    async (id: number, patch: EmailTemplateUpdate) => {
      const updated = await updateEmailTemplate(id, patch);
      setRows((prev) => prev.map((r) => (r.id === id ? updated : r)));
      setToast(`Saved ${updated.templateKey}`);
      setTimeout(() => setToast(null), 3000);
    },
    [],
  );

  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>Email templates</h1>
      <p className={styles.intro}>
        Subject + body for partial-credit notification emails. Variables use{' '}
        <code>{'{{varName}}'}</code> substitution; HTML body escapes values by default,
        and <code>{'{{!varName}}'}</code> opts out for admin-trusted raw HTML.
      </p>

      {loading && <div className={styles.notice}>Loading…</div>}
      {loadError && (
        <div className={styles.error} role="alert">
          {loadError}
        </div>
      )}
      {toast && (
        <div className={styles.toast} role="status">
          {toast}
        </div>
      )}

      {!loading && !loadError && (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Template key</th>
              <th>Subject</th>
              <th>Enabled</th>
              <th>Last changed</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.id} className={editingId === row.id ? styles.rowEditing : undefined}>
                {editingId === row.id ? (
                  <td colSpan={5}>
                    <div className={styles.rowKey}>{row.templateKey}</div>
                    <EmailTemplateEditor
                      row={row}
                      onSave={handleSave}
                      onClose={() => setEditingId(null)}
                    />
                  </td>
                ) : (
                  <>
                    <td className={styles.keyCell}>{row.templateKey}</td>
                    <td>{row.subject}</td>
                    <td>
                      <span className={row.enabled ? styles.pillOn : styles.pillOff}>
                        {row.enabled ? 'On' : 'Off'}
                      </span>
                    </td>
                    <td>{row.changedDate ?? '—'}</td>
                    <td>
                      <button
                        type="button"
                        className={styles.editButton}
                        onClick={() => setEditingId(row.id)}
                      >
                        Edit
                      </button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
