import { z } from 'zod';
import { apiFetch } from './apiFetch';

/**
 * Typed client for the SPKB-3664 status-config admin endpoints.
 *
 * Shape mirrors {@code partialCreditClient.ts}: Zod schemas at the
 * boundary, named functions per HTTP verb. Server-side validation
 * (color hex, text length) surfaces as a 400 + error body; this client
 * lets that bubble as a thrown Error so the UI can render an inline
 * "fix your input" message without a special exception class — the
 * five editable fields don't warrant the typed-error overhead the
 * wizard's CreditRequestValidationError needs.
 */

export const StatusConfigRowSchema = z.object({
  id: z.number(),
  systemStatus: z.string(),
  internalStatusText: z.string(),
  externalStatusText: z.string(),
  colorHex: z.string(),
  sortOrder: z.number(),
  showInUserCounters: z.boolean(),
  isDefault: z.boolean(),
  statusGroupedTo: z.string().nullable(),
});
export type StatusConfigRow = z.infer<typeof StatusConfigRowSchema>;

export const StatusConfigPatchSchema = z.object({
  internalStatusText: z.string().nullable().optional(),
  externalStatusText: z.string().nullable().optional(),
  // Six-hex color, e.g. #FFAA00. Validated client-side before PATCH to
  // avoid round-tripping the obvious typos.
  colorHex: z
    .string()
    .regex(/^#[0-9A-Fa-f]{6}$/, 'colorHex must match #RRGGBB')
    .nullable()
    .optional(),
  sortOrder: z.number().int().nullable().optional(),
  showInUserCounters: z.boolean().nullable().optional(),
});
export type StatusConfigPatch = z.infer<typeof StatusConfigPatchSchema>;

const BASE = '/api/v1/admin/partial-credit/statuses';

export async function listStatuses(): Promise<StatusConfigRow[]> {
  const res = await apiFetch(BASE);
  if (!res.ok) {
    throw new Error(`listStatuses failed: HTTP ${res.status}`);
  }
  return z.array(StatusConfigRowSchema).parse(await res.json());
}

export async function updateStatus(
  id: number,
  patch: StatusConfigPatch,
): Promise<StatusConfigRow> {
  // Validate client-side so an invalid color hex never crosses the wire.
  const body = StatusConfigPatchSchema.parse(patch);
  const res = await apiFetch(`${BASE}/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    // Surface the server's message verbatim so the UI inline-error
    // matches the validation that ran on the backend.
    const errorBody = (await res.json().catch(() => null)) as
      | { message?: string }
      | null;
    throw new Error(
      errorBody?.message ?? `updateStatus failed: HTTP ${res.status}`,
    );
  }
  return StatusConfigRowSchema.parse(await res.json());
}
