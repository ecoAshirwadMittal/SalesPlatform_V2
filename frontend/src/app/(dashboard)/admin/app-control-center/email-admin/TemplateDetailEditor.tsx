'use client';

import { useState } from 'react';
import DOMPurify from 'dompurify';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import {
  previewEmailTemplate,
  sendTestEmailTemplate,
  type EmailTemplateView,
  type TemplatePreviewResult,
} from '@/lib/adminEmailClient';
import type { Banner } from './shared';

/**
 * Task 10 — create/edit form for a single email template, plus the two
 * render-only admin actions (`preview` bypasses `enabled`; `send-test` is
 * a real, rate-limited outbound send). Both actions require a saved
 * `id`, so they're hidden while creating a new (unsaved) template.
 */
export function TemplateDetailEditor({
  data, isCreate, onChange, onSave, onCancel, onBanner,
}: {
  data: Partial<EmailTemplateView>;
  isCreate: boolean;
  onChange: (d: Partial<EmailTemplateView>) => void;
  onSave: () => void;
  onCancel: () => void;
  onBanner: (b: Banner) => void;
}) {
  const [contentTab, setContentTab] = useState<'html' | 'plain'>('html');
  const [preview, setPreview] = useState<TemplatePreviewResult | null>(null);
  const [previewing, setPreviewing] = useState(false);
  const [testAddress, setTestAddress] = useState('');
  const [sendingTest, setSendingTest] = useState(false);

  function set<K extends keyof EmailTemplateView>(field: K, value: EmailTemplateView[K]) {
    onChange({ ...data, [field]: value });
  }

  async function handlePreview() {
    if (!data.id) return;
    setPreviewing(true);
    try {
      setPreview(await previewEmailTemplate(data.id, {}));
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setPreviewing(false);
    }
  }

  async function handleSendTest() {
    if (!data.id || !testAddress) return;
    setSendingTest(true);
    try {
      const result = await sendTestEmailTemplate(data.id, testAddress, {});
      onBanner({
        type: result.success ? 'success' : 'error',
        message: result.success
          ? `Test email sent to ${testAddress} (log #${result.logId}).`
          : `Test email did not send — status: ${result.status}.`,
      });
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setSendingTest(false);
    }
  }

  return (
    <div className={e.detailEditor}>
      <h3 className={s.sectionTitle}>{isCreate ? 'New Template' : `Edit Template: ${data.templateName || ''}`}</h3>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-template-key">Template Key</label>
          <input
            id="tpl-template-key"
            className={s.formInput}
            value={data.templateKey || ''}
            onChange={(ev) => set('templateKey', ev.target.value)}
            placeholder="WELCOME_EMAIL"
            disabled={!isCreate}
          />
          {isCreate && (
            <div className={s.formHint}>Letters, digits, underscores only. Cannot be changed after creation.</div>
          )}
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-template-name">Template Name</label>
          <input
            id="tpl-template-name"
            className={s.formInput}
            value={data.templateName || ''}
            onChange={(ev) => set('templateName', ev.target.value)}
          />
        </div>
      </div>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-from-address">From Address</label>
          <input
            id="tpl-from-address"
            className={s.formInput}
            value={data.fromAddress || ''}
            onChange={(ev) => set('fromAddress', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-from-display-name">From Display Name</label>
          <input
            id="tpl-from-display-name"
            className={s.formInput}
            value={data.fromDisplayName || ''}
            onChange={(ev) => set('fromDisplayName', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-reply-to">Reply-To</label>
          <input
            id="tpl-reply-to"
            className={s.formInput}
            value={data.replyTo || ''}
            onChange={(ev) => set('replyTo', ev.target.value)}
          />
        </div>
      </div>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-to-default">To (default)</label>
          <input
            id="tpl-to-default"
            className={s.formInput}
            value={data.toDefault || ''}
            onChange={(ev) => set('toDefault', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-cc-default">CC (default)</label>
          <input
            id="tpl-cc-default"
            className={s.formInput}
            value={data.ccDefault || ''}
            onChange={(ev) => set('ccDefault', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-bcc-default">BCC (default)</label>
          <input
            id="tpl-bcc-default"
            className={s.formInput}
            value={data.bccDefault || ''}
            onChange={(ev) => set('bccDefault', ev.target.value)}
          />
        </div>
      </div>

      <div className={s.formGroup}>
        <label className={s.formLabel} htmlFor="tpl-subject">Subject</label>
        <input
          id="tpl-subject"
          className={s.formInput}
          value={data.subject || ''}
          onChange={(ev) => set('subject', ev.target.value)}
        />
        <div className={s.formHint}>Use {'{'} placeholders {'}'} like {'{offerNumber}'}, {'{buyerCode}'}, etc.</div>
      </div>

      <div className={s.formGroup}>
        <label className={s.formLabel} htmlFor="tpl-description">Description</label>
        <input
          id="tpl-description"
          className={s.formInput}
          value={data.description || ''}
          onChange={(ev) => set('description', ev.target.value)}
        />
      </div>

      <div className={e.toggleGrid}>
        <div className={s.toggleRow}>
          <button
            type="button"
            aria-label="Enabled"
            className={`${s.toggleSwitch} ${data.enabled ? s.toggleSwitchOn : ''}`}
            onClick={() => set('enabled', !data.enabled)}
          />
          <span className={s.toggleLabel}>Enabled</span>
        </div>
        <div className={s.toggleRow}>
          <button
            type="button"
            aria-label="Has Attachment"
            className={`${s.toggleSwitch} ${data.hasAttachment ? s.toggleSwitchOn : ''}`}
            onClick={() => set('hasAttachment', !data.hasAttachment)}
          />
          <span className={s.toggleLabel}>Has Attachment</span>
        </div>
      </div>

      <div className={e.contentTabs}>
        <button className={`${e.contentTab} ${contentTab === 'html' ? e.contentTabActive : ''}`} onClick={() => setContentTab('html')}>HTML Content</button>
        <button className={`${e.contentTab} ${contentTab === 'plain' ? e.contentTabActive : ''}`} onClick={() => setContentTab('plain')}>Plain Text</button>
      </div>

      {contentTab === 'html' ? (
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-content-html" style={{ position: 'absolute', width: 1, height: 1, overflow: 'hidden', clip: 'rect(0,0,0,0)' }}>HTML Content</label>
          <textarea
            id="tpl-content-html"
            className={s.formTextarea}
            style={{ minHeight: 200 }}
            value={data.contentHtml || ''}
            onChange={(ev) => set('contentHtml', ev.target.value)}
            placeholder="HTML email body..."
          />
        </div>
      ) : (
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="tpl-content-plain" style={{ position: 'absolute', width: 1, height: 1, overflow: 'hidden', clip: 'rect(0,0,0,0)' }}>Plain Text</label>
          <textarea
            id="tpl-content-plain"
            className={s.formTextarea}
            style={{ minHeight: 200 }}
            value={data.contentPlain || ''}
            onChange={(ev) => set('contentPlain', ev.target.value)}
            placeholder="Plain text email body..."
          />
        </div>
      )}

      <div className={s.actionsRow}>
        <button className={s.saveBtn} onClick={onSave}>{isCreate ? 'Create Template' : 'Save Template'}</button>
        <button className={s.actionBtn} onClick={onCancel}>Cancel</button>
      </div>

      {!isCreate && data.id && (
        <div className={e.detailEditor}>
          <h3 className={s.sectionTitle}>Preview &amp; Test</h3>
          <div className={s.actionsRow} style={{ marginTop: 0 }}>
            <button className={s.actionBtn} disabled={previewing} onClick={handlePreview}>
              {previewing ? 'Rendering...' : 'Preview'}
            </button>
          </div>
          {preview && (
            <div style={{ marginTop: 12 }}>
              <div className={e.detailRow}><strong>Subject:</strong> {preview.subject}</div>
              {/* M-3: real sanitization via DOMPurify before injecting rendered HTML. */}
              <div
                className={e.htmlPreview}
                dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(preview.html) }}
              />
            </div>
          )}

          <div className={e.formGrid} style={{ marginTop: 16 }}>
            <div className={s.formGroup}>
              <label className={s.formLabel} htmlFor="tpl-send-test-address">Send test to</label>
              <input
                id="tpl-send-test-address"
                className={s.formInput}
                type="email"
                value={testAddress}
                onChange={(ev) => setTestAddress(ev.target.value)}
                placeholder="you@example.com"
              />
            </div>
          </div>
          <div className={s.actionsRow} style={{ marginTop: 12 }}>
            <button className={`${s.actionBtn} ${e.testBtn}`} disabled={sendingTest || !testAddress} onClick={handleSendTest}>
              {sendingTest ? 'Sending...' : 'Send Test'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
