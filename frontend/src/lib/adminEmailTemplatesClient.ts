import { z } from 'zod';
import { apiFetch } from './apiFetch';

/**
 * Typed client for the admin email-templates endpoints
 * (`AdminPartialCreditController#/email-templates/*`).
 *
 * Three endpoints, one per editor concern:
 *  - listEmailTemplates() — populates the table on page load
 *  - updateEmailTemplate() — commits an edit on Save
 *  - previewEmailTemplate() — renders the (possibly disabled) template
 *    for the Preview tab
 *
 * Validation issues surface as a thrown Error with the server's message
 * — the editor has only four editable fields and doesn't warrant the
 * typed-error overhead the wizard's CreditRequestValidationError needs.
 */

export const EmailTemplateViewSchema = z.object({
  id: z.number(),
  templateKey: z.string(),
  subject: z.string(),
  bodyHtml: z.string(),
  // V90 leaves body_text nullable so admins can omit it; renderer falls
  // back to stripped HTML at send time.
  bodyText: z.string().nullable(),
  enabled: z.boolean(),
  description: z.string().nullable(),
  changedDate: z.string().nullable(),
  changedById: z.number().nullable(),
});
export type EmailTemplateView = z.infer<typeof EmailTemplateViewSchema>;

export const EmailTemplateUpdateSchema = z.object({
  subject: z.string().nullable().optional(),
  bodyHtml: z.string().nullable().optional(),
  bodyText: z.string().nullable().optional(),
  enabled: z.boolean().nullable().optional(),
  description: z.string().nullable().optional(),
});
export type EmailTemplateUpdate = z.infer<typeof EmailTemplateUpdateSchema>;

export const RenderedEmailSchema = z.object({
  subject: z.string(),
  bodyHtml: z.string(),
  bodyText: z.string().nullable(),
});
export type RenderedEmail = z.infer<typeof RenderedEmailSchema>;

const BASE = '/api/v1/admin/partial-credit/email-templates';

export async function listEmailTemplates(): Promise<EmailTemplateView[]> {
  const res = await apiFetch(BASE);
  if (!res.ok) {
    throw new Error(`listEmailTemplates failed: HTTP ${res.status}`);
  }
  return z.array(EmailTemplateViewSchema).parse(await res.json());
}

export async function updateEmailTemplate(
  id: number,
  patch: EmailTemplateUpdate,
): Promise<EmailTemplateView> {
  const body = EmailTemplateUpdateSchema.parse(patch);
  const res = await apiFetch(`${BASE}/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const errorBody = (await res.json().catch(() => null)) as
      | { message?: string }
      | null;
    throw new Error(
      errorBody?.message ?? `updateEmailTemplate failed: HTTP ${res.status}`,
    );
  }
  return EmailTemplateViewSchema.parse(await res.json());
}

export async function previewEmailTemplate(
  id: number,
  variables: Record<string, unknown>,
): Promise<RenderedEmail> {
  const res = await apiFetch(`${BASE}/${id}/preview`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ variables }),
  });
  if (!res.ok) {
    throw new Error(`previewEmailTemplate failed: HTTP ${res.status}`);
  }
  return RenderedEmailSchema.parse(await res.json());
}
