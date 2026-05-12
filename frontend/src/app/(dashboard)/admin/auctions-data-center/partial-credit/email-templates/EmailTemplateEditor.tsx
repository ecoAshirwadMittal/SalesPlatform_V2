'use client';

import { useState } from 'react';
import {
  previewEmailTemplate,
  type EmailTemplateView,
  type EmailTemplateUpdate,
  type RenderedEmail,
} from '@/lib/adminEmailTemplatesClient';
import styles from './emailTemplates.module.css';

/**
 * Inline editor for one template row. Two tabs:
 *  - Edit: subject + body_html + body_text + enabled toggle. Save
 *    button is enabled iff at least one field changed.
 *  - Preview: renders the current draft (server-side via the preview
 *    endpoint) so the admin sees exactly what the listener would build
 *    at send time.
 *
 * The component is presentational + holds local draft state only; the
 * parent owns the actual PATCH call so the page can refresh the list
 * after a successful save.
 *
 * Variable stub map used by Preview matches the listener's contract
 * (requestNumber / orderNumber / approvedTotalDisplay /
 * wrongDeviceLineDescription / photoUploadDeadline). Sprint 4 §6.1
 * documents the full set.
 */
const PREVIEW_VARIABLES: Record<string, string> = {
  requestNumber: 'PCR-2026-PREVIEW',
  orderNumber: 'SO-12345',
  approvedTotalDisplay: '$125.50',
  buyerName: 'Sample Buyer Co.',
  reviewerName: 'Sales Ops',
  reviewCompletedDate: '2026-05-12',
  detailUrl: 'https://buy-qa.ecoatmdirect.com/wholesale/partial-credit/123',
  wrongDeviceLineDescription: 'Apple iPhone 12, 64GB, Grade A',
  photoUploadDeadline: 'by Friday',
};

/**
 * Per-template variable contract. Static map sourced from Sprint 4 §6.1.
 * Surfaced as a collapsible hint above the body-html textarea so admins
 * don't have to flip between code and the editor to remember which
 * tokens each template accepts.
 */
const TEMPLATE_VARIABLES: Record<string, string[]> = {
  ReviewCompleted_Approved: [
    'requestNumber',
    'orderNumber',
    'approvedTotalDisplay',
    'buyerName',
    'reviewerName',
    'reviewCompletedDate',
    'detailUrl',
  ],
  ReviewCompleted_Declined: [
    'requestNumber',
    'orderNumber',
    'buyerName',
    'reviewerName',
    'reviewCompletedDate',
    'detailUrl',
  ],
  PhotosRequired_WrongDevices: [
    'requestNumber',
    'orderNumber',
    'buyerName',
    'wrongDeviceLineDescription',
    'photoUploadDeadline',
    'detailUrl',
  ],
};

function variablesForTemplate(templateKey: string): string[] {
  return TEMPLATE_VARIABLES[templateKey] ?? Object.keys(PREVIEW_VARIABLES);
}

interface EditorDraft {
  subject: string;
  bodyHtml: string;
  bodyText: string;
  enabled: boolean;
}

function toDraft(row: EmailTemplateView): EditorDraft {
  return {
    subject: row.subject,
    bodyHtml: row.bodyHtml,
    bodyText: row.bodyText ?? '',
    enabled: row.enabled,
  };
}

function diff(row: EmailTemplateView, draft: EditorDraft): EmailTemplateUpdate {
  const patch: EmailTemplateUpdate = {};
  if (draft.subject !== row.subject) patch.subject = draft.subject;
  if (draft.bodyHtml !== row.bodyHtml) patch.bodyHtml = draft.bodyHtml;
  // null-vs-empty-string nuance: server treats null as "no change", so
  // only send body_text when it actually changed.
  const incomingText = row.bodyText ?? '';
  if (draft.bodyText !== incomingText) {
    // Empty input means "clear" — patch with empty string (not null).
    patch.bodyText = draft.bodyText;
  }
  if (draft.enabled !== row.enabled) patch.enabled = draft.enabled;
  return patch;
}

export interface EmailTemplateEditorProps {
  row: EmailTemplateView;
  onSave: (id: number, patch: EmailTemplateUpdate) => Promise<void>;
  onClose: () => void;
}

export function EmailTemplateEditor({ row, onSave, onClose }: EmailTemplateEditorProps) {
  const [draft, setDraft] = useState<EditorDraft>(() => toDraft(row));
  const [activeTab, setActiveTab] = useState<'edit' | 'preview'>('edit');
  const [preview, setPreview] = useState<RenderedEmail | null>(null);
  const [previewError, setPreviewError] = useState<string | null>(null);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  const patch = diff(row, draft);
  const hasChanges = Object.keys(patch).length > 0;

  async function loadPreview() {
    setPreviewLoading(true);
    setPreviewError(null);
    try {
      // Preview the *current draft*, not the persisted row — admins need
      // to see edits before saving. The server's renderPreview accepts
      // the (id, vars) shape; to preview the draft text without saving,
      // we'd need a separate "render arbitrary template" endpoint. For
      // now Preview reflects the persisted state, and we surface a hint
      // when the draft is dirty.
      const rendered = await previewEmailTemplate(row.id, PREVIEW_VARIABLES);
      setPreview(rendered);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to preview';
      setPreviewError(message);
    } finally {
      setPreviewLoading(false);
    }
  }

  function switchTab(tab: 'edit' | 'preview') {
    setActiveTab(tab);
    if (tab === 'preview' && preview === null && !previewLoading) {
      void loadPreview();
    }
  }

  async function handleSave() {
    if (!hasChanges) return;
    setSaving(true);
    setSaveError(null);
    try {
      await onSave(row.id, patch);
      onClose();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Save failed';
      setSaveError(message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={styles.editor} aria-label={`Edit ${row.templateKey}`}>
      <div className={styles.tabs} role="tablist">
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'edit'}
          className={activeTab === 'edit' ? styles.tabActive : styles.tab}
          onClick={() => switchTab('edit')}
        >
          Edit
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'preview'}
          className={activeTab === 'preview' ? styles.tabActive : styles.tab}
          onClick={() => switchTab('preview')}
        >
          Preview
        </button>
      </div>

      {activeTab === 'edit' && (
        <div className={styles.editPanel} role="tabpanel">
          <label className={styles.field}>
            <span className={styles.fieldLabel}>Subject</span>
            <input
              type="text"
              className={styles.textInput}
              value={draft.subject}
              onChange={(e) => setDraft({ ...draft, subject: e.target.value })}
              maxLength={255}
            />
          </label>
          <details className={styles.variablesHint}>
            <summary>Available variables</summary>
            <ul>
              {variablesForTemplate(row.templateKey).map((name) => (
                <li key={name}>
                  <code>{`{{${name}}}`}</code>
                </li>
              ))}
            </ul>
          </details>
          <label className={styles.field}>
            <span className={styles.fieldLabel}>Body (HTML)</span>
            <textarea
              className={styles.textArea}
              value={draft.bodyHtml}
              onChange={(e) => setDraft({ ...draft, bodyHtml: e.target.value })}
              rows={10}
            />
          </label>
          <label className={styles.field}>
            <span className={styles.fieldLabel}>Body (plain text)</span>
            <textarea
              className={styles.textArea}
              value={draft.bodyText}
              onChange={(e) => setDraft({ ...draft, bodyText: e.target.value })}
              rows={6}
              placeholder="Optional — falls back to HTML body if blank."
            />
          </label>
          <label className={styles.toggle}>
            <input
              type="checkbox"
              checked={draft.enabled}
              onChange={(e) => setDraft({ ...draft, enabled: e.target.checked })}
            />
            <span>Enabled (listener uses this template when on)</span>
          </label>

          {saveError && <div className={styles.error} role="alert">{saveError}</div>}

          <div className={styles.actions}>
            <button type="button" className={styles.buttonGhost} onClick={onClose} disabled={saving}>
              Cancel
            </button>
            <button
              type="button"
              className={styles.buttonPrimary}
              onClick={handleSave}
              disabled={!hasChanges || saving}
            >
              {saving ? 'Saving…' : 'Save'}
            </button>
          </div>
        </div>
      )}

      {activeTab === 'preview' && (
        <div className={styles.previewPanel} role="tabpanel">
          {hasChanges && (
            <div className={styles.previewHint}>
              Preview reflects the saved template — unsaved edits are not shown.
            </div>
          )}
          {previewLoading && <div>Loading preview…</div>}
          {previewError && <div className={styles.error} role="alert">{previewError}</div>}
          {preview && (
            <>
              <div className={styles.previewField}>
                <span className={styles.fieldLabel}>Subject</span>
                <div className={styles.previewSubject}>{preview.subject}</div>
              </div>
              <div className={styles.previewField}>
                <span className={styles.fieldLabel}>HTML body</span>
                <div
                  className={styles.previewHtml}
                  // Admin-trusted content rendered by the same server-side
                  // renderer the listener uses. Variables are stub values
                  // baked into PREVIEW_VARIABLES — no buyer input flows
                  // through here.
                  dangerouslySetInnerHTML={{ __html: preview.bodyHtml }}
                />
              </div>
              {preview.bodyText && (
                <div className={styles.previewField}>
                  <span className={styles.fieldLabel}>Plain-text body</span>
                  <pre className={styles.previewText}>{preview.bodyText}</pre>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}
