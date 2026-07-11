import { apiFetch } from '@/lib/apiFetch';

/**
 * Typed client for the unified Email Admin surface
 * (`AdminEmailController` — Tasks 7-9): SMTP config, email-template CRUD +
 * preview/send-test, and the delivery-log list/detail/resend endpoints.
 *
 * Field names are camelCase end-to-end — there is no Jackson snake_case
 * naming strategy configured on the backend (see
 * `docs/tasks/email-management-design-2026-07-10.md`), so every interface
 * below mirrors the Java record field names verbatim. This is the single
 * source of truth for the wire shapes; the three tab components import
 * from here rather than declaring their own snake_case-drifted types.
 */

const BASE = '/api/v1/admin/email';

async function jsonOrThrow<T = unknown>(res: Response): Promise<T> {
  const text = await res.text();
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}${text ? ': ' + text.slice(0, 200) : ''}`);
  if (!text) return {} as T;
  try {
    return JSON.parse(text) as T;
  } catch {
    throw new Error(text.slice(0, 200));
  }
}

// ── SMTP (Task 7) ──────────────────────────────────────────────────────

/**
 * Mirrors `SmtpConfigView` (GET response). Design decision D2: the SMTP
 * password is env-only (`spring.mail.password`) — this shape has NO
 * password field because the backend never returns one.
 */
export interface SmtpConfigView {
  id: number;
  serverHost: string | null;
  serverPort: number;
  protocol: string;
  fromAddress: string | null;
  fromDisplayName: string | null;
  replyTo: string | null;
  useSsl: boolean;
  useTls: boolean;
  enabled: boolean;
  maxRetryAttempts: number;
  timeoutMs: number;
  changedDate: string | null;
}

/** Mirrors `SmtpConfigUpdate` (PUT body). No password field (D2). */
export interface SmtpConfigUpdate {
  serverHost: string;
  serverPort: number;
  protocol: string;
  fromAddress: string;
  fromDisplayName: string;
  replyTo: string;
  useSsl: boolean;
  useTls: boolean;
  enabled: boolean;
  maxRetryAttempts: number;
  timeoutMs: number;
}

/** `POST /smtp/test` response. */
export interface SmtpTestResult {
  success: boolean;
  message: string;
}

export async function getSmtpConfig(): Promise<SmtpConfigView> {
  return jsonOrThrow(await apiFetch(`${BASE}/smtp`));
}

export async function updateSmtpConfig(body: SmtpConfigUpdate): Promise<SmtpConfigView> {
  return jsonOrThrow(
    await apiFetch(`${BASE}/smtp`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }),
  );
}

export async function testSmtpConnection(): Promise<SmtpTestResult> {
  return jsonOrThrow(await apiFetch(`${BASE}/smtp/test`, { method: 'POST' }));
}

// ── Templates (Task 8) ────────────────────────────────────────────────

/** Mirrors `EmailTemplateView`. */
export interface EmailTemplateView {
  id: number;
  templateKey: string;
  templateName: string;
  subject: string | null;
  contentHtml: string | null;
  contentPlain: string | null;
  fromAddress: string | null;
  fromDisplayName: string | null;
  replyTo: string | null;
  toDefault: string | null;
  ccDefault: string | null;
  bccDefault: string | null;
  hasAttachment: boolean;
  enabled: boolean;
  description: string | null;
  createdDate: string;
  changedDate: string;
}

/**
 * Mirrors `EmailTemplateUpsert` — the full-representation body for both
 * create (POST) and update (PUT). `templateKey` is required on both but
 * immutable on update: the controller validates it, then silently ignores
 * it for an existing row.
 */
export interface EmailTemplateUpsert {
  templateKey: string;
  templateName: string;
  subject: string;
  contentHtml: string;
  contentPlain: string | null;
  fromAddress: string | null;
  fromDisplayName: string | null;
  replyTo: string | null;
  toDefault: string | null;
  ccDefault: string | null;
  bccDefault: string | null;
  hasAttachment: boolean;
  enabled: boolean;
  description: string | null;
}

/** `POST /templates/{id}/preview` response. */
export interface TemplatePreviewResult {
  subject: string;
  html: string;
  text: string | null;
}

/** `POST /templates/{id}/send-test` response. */
export interface SendTestResult {
  success: boolean;
  logId: number;
  status: string;
}

export async function listEmailTemplates(): Promise<EmailTemplateView[]> {
  return jsonOrThrow(await apiFetch(`${BASE}/templates`));
}

export async function createEmailTemplate(body: EmailTemplateUpsert): Promise<EmailTemplateView> {
  return jsonOrThrow(
    await apiFetch(`${BASE}/templates`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }),
  );
}

export async function updateEmailTemplate(id: number, body: EmailTemplateUpsert): Promise<EmailTemplateView> {
  return jsonOrThrow(
    await apiFetch(`${BASE}/templates/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }),
  );
}

export async function deleteEmailTemplate(id: number): Promise<void> {
  await jsonOrThrow(await apiFetch(`${BASE}/templates/${id}`, { method: 'DELETE' }));
}

export async function previewEmailTemplate(
  id: number,
  vars: Record<string, unknown>,
): Promise<TemplatePreviewResult> {
  return jsonOrThrow(
    await apiFetch(`${BASE}/templates/${id}/preview`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ vars }),
    }),
  );
}

export async function sendTestEmailTemplate(
  id: number,
  toAddress: string,
  vars: Record<string, unknown>,
): Promise<SendTestResult> {
  return jsonOrThrow(
    await apiFetch(`${BASE}/templates/${id}/send-test`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ toAddress, vars }),
    }),
  );
}

// ── Log (Task 9) ──────────────────────────────────────────────────────

export type EmailStatus = 'PENDING' | 'SENT' | 'FAILED';

/** Mirrors `EmailLogView` — the same shape backs list, detail, and resend. */
export interface EmailLogView {
  id: number;
  templateKey: string | null;
  fromAddress: string | null;
  toAddress: string;
  cc: string | null;
  bcc: string | null;
  subject: string | null;
  contentHtml: string | null;
  status: EmailStatus;
  errorMessage: string | null;
  retryCount: number;
  nextAttemptAt: string | null;
  sourceModule: string | null;
  sourceId: number | null;
  sentDate: string | null;
  createdDate: string;
}

/** Mirrors Spring's `Page<T>` JSON envelope. */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface EmailLogFilters {
  status?: string;
  from?: string;
  to?: string;
  templateKey?: string;
  page?: number;
  size?: number;
}

export async function listEmailLog(filters: EmailLogFilters): Promise<PageResponse<EmailLogView>> {
  const params = new URLSearchParams();
  if (filters.status) params.set('status', filters.status);
  if (filters.from) params.set('from', filters.from);
  if (filters.to) params.set('to', filters.to);
  if (filters.templateKey) params.set('templateKey', filters.templateKey);
  params.set('page', String(filters.page ?? 0));
  params.set('size', String(filters.size ?? 50));
  return jsonOrThrow(await apiFetch(`${BASE}/log?${params}`));
}

export async function getEmailLog(id: number): Promise<EmailLogView> {
  return jsonOrThrow(await apiFetch(`${BASE}/log/${id}`));
}

export async function resendEmailLog(id: number): Promise<EmailLogView> {
  return jsonOrThrow(await apiFetch(`${BASE}/log/${id}/resend`, { method: 'POST' }));
}
