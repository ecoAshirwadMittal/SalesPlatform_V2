import { z } from 'zod';
import { apiFetch } from './apiFetch';

/**
 * Typed API client for the buyer-side Partial Credit Requests surface
 * (`BuyerPartialCreditController` at `/api/v1/buyer/partial-credit/**`).
 *
 * Shape conventions mirror `bidder.ts` / `reserveBidClient.ts`:
 *   - Zod schemas validate every response body at the boundary
 *   - Validation issues from submit() get their own typed error
 *     (CreditRequestValidationError) so wizard pages can branch on
 *     specific failure codes
 */

const SystemStatusSchema = z.enum([
  'DRAFT',
  'PENDING_APPROVAL',
  'UNDER_REVIEW',
  'APPROVED',
  'DECLINED',
]);
export type SystemStatus = z.infer<typeof SystemStatusSchema>;

const ShipmentDamagedSchema = z.enum(['YES', 'NO', 'NOT_ANSWERED']);
export type ShipmentDamaged = z.infer<typeof ShipmentDamagedSchema>;

const MissingLineSchema = z.object({
  id: z.number(),
  barcodeSubmitted: z.string(),
  brand: z.string().nullable(),
  model: z.string().nullable(),
  grade: z.string().nullable(),
  boxNumber: z.string().nullable(),
  amountPaid: z.number().nullable(),
  shipStatus: z.string().nullable(),
  lineStatus: z.string().nullable(),
  reviewDecision: z.string().nullable(),
  amountToCredit: z.number().nullable(),
});

const WrongLineSchema = z.object({
  id: z.number(),
  expectedBarcode: z.string(),
  // V91 (Figma parity fix #5) — Box No. column on admin Wrong table.
  expectedBoxNumber: z.string().nullable(),
  expectedBrand: z.string().nullable(),
  expectedModel: z.string().nullable(),
  expectedGrade: z.string().nullable(),
  expectedAmountPaid: z.number().nullable(),
  actualImeiOrModel: z.string().nullable(),
  // V91 (Figma parity fix #6a) — alias of actualImeiOrModel for the
  // buyer detail "Received Device IMEI/Serial" column. Same underlying
  // value today; Phase 2 may split.
  receivedImei: z.string().nullable(),
  actualBrand: z.string().nullable(),
  actualModel: z.string().nullable(),
  actualGrade: z.string().nullable(),
  latestPrice: z.number().nullable(),
  actionRecommendation: z.string().nullable(),
  lineStatus: z.string().nullable(),
  reviewDecision: z.string().nullable(),
  amountToCredit: z.number().nullable(),
  // V91 (Figma parity fix #6b) — per-line photo count for the buyer
  // detail Wrong table's Photos column. Pre-resolved by the controller.
  photoCount: z.number().nullable(),
});

const EncumberedLineSchema = z.object({
  id: z.number(),
  barcodeSubmitted: z.string(),
  brand: z.string().nullable(),
  model: z.string().nullable(),
  grade: z.string().nullable(),
  boxNumber: z.string().nullable(),
  amountPaid: z.number().nullable(),
  prologResult: z.string().nullable(),
  actualValue: z.number().nullable(),
  lineStatus: z.string().nullable(),
  reviewDecision: z.string().nullable(),
  amountToCredit: z.number().nullable(),
});

export const CreditRequestDetailSchema = z.object({
  id: z.number(),
  requestNumber: z.string(),
  orderNumber: z.string(),
  // V91 (Figma parity fix #2): contact name distinct from partyName.
  buyerName: z.string().nullable(),
  partyName: z.string().nullable(),
  orderCreatedDate: z.string().nullable(),
  orderShippedDate: z.string().nullable(),
  systemStatus: SystemStatusSchema,
  displayStatus: z.string(),
  // V91 — admin detail shows the internal text; buyer detail keeps
  // displayStatus. statusColorHex flows live from credit_request_statuses.
  internalStatusText: z.string().nullable(),
  statusColorHex: z.string().nullable(),
  shipmentDamaged: ShipmentDamagedSchema,
  hasMissingDevice: z.boolean(),
  hasWrongDevice: z.boolean(),
  hasEncumberedDevice: z.boolean(),
  totalDevices: z.number().nullable(),
  requestedTotal: z.number().nullable(),
  // Sprint 4 chunk 5 — buyer detail page renders the approved total
  // alongside the requested total once the review is final.
  approvedTotal: z.number().nullable(),
  // Both null until the admin clicks Complete Review; consumed by the
  // buyer detail page's ReviewSummaryPanel.
  reviewedById: z.number().nullable(),
  reviewCompletedOn: z.string().nullable(),
  missingLines: z.array(MissingLineSchema),
  wrongLines: z.array(WrongLineSchema),
  encumberedLines: z.array(EncumberedLineSchema),
});
export type CreditRequestDetail = z.infer<typeof CreditRequestDetailSchema>;

export const CreditRequestSummarySchema = z.object({
  id: z.number(),
  requestNumber: z.string(),
  orderNumber: z.string(),
  systemStatus: SystemStatusSchema,
  displayStatus: z.string(),
  requestDate: z.string(),
  submittedDate: z.string().nullable(),
  hasMissingDevice: z.boolean(),
  hasWrongDevice: z.boolean(),
  hasEncumberedDevice: z.boolean(),
  totalDevices: z.number().nullable(),
  requestedTotal: z.number().nullable(),
});
export type CreditRequestSummary = z.infer<typeof CreditRequestSummarySchema>;

const ValidationIssueSchema = z.object({ code: z.string(), message: z.string() });
export type ValidationIssue = z.infer<typeof ValidationIssueSchema>;

/**
 * Mirror of the backend `BarcodeReconciliationResult` record. The wizard
 * surfaces {@link banner} verbatim above the textarea (Figma "Removed N
 * duplicate and M not in order"). The dropped buckets are kept in case
 * the wizard wants to render them per-barcode in a future iteration.
 */
export const BarcodeReconciliationSchema = z.object({
  validLines: z.array(z.unknown()),
  duplicates: z.array(z.string()),
  notInOrder: z.array(z.string()),
  banner: z.string(),
});
export type BarcodeReconciliation = z.infer<typeof BarcodeReconciliationSchema>;

/**
 * Response shape for the three line-replace endpoints: the up-to-date
 * detail plus the reconciliation block so the wizard can render the
 * Figma dedup/not-in-order banner without a second request.
 */
export const LineReplacementResponseSchema = z.object({
  detail: CreditRequestDetailSchema,
  reconciliation: BarcodeReconciliationSchema,
});
export type LineReplacementResponse = z.infer<typeof LineReplacementResponseSchema>;

/**
 * Thrown when the submit endpoint returns 400 with a list of validation
 * issues. Wizard pages can switch on {@code issues[i].code} to highlight
 * the matching field.
 */
export class CreditRequestValidationError extends Error {
  constructor(public readonly issues: readonly ValidationIssue[]) {
    super(issues.map((i) => i.code).join('; '));
    this.name = 'CreditRequestValidationError';
  }
}

interface CreateDraftBody {
  orderNumber: string;
  buyerCodeId: number;
}

interface UpdateDraftBody {
  hasMissingDevice?: boolean;
  hasWrongDevice?: boolean;
  hasEncumberedDevice?: boolean;
  shipmentDamaged?: ShipmentDamaged;
}

const BASE = '/api/v1/buyer/partial-credit';

export async function createDraft(body: CreateDraftBody): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/draft`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  // Step 1 pre-validate (chunk 3): the backend now throws
  // CreditRequestValidationException on createDraft when the order is
  // not on the manifest or is shipped > 30 days ago. Surface the same
  // typed error the submit endpoint uses so the wizard can render the
  // first issue message inline (Step 1's existing error banner).
  if (r.status === 400) {
    const errorBody = (await r.json()) as { issues?: ValidationIssue[] };
    throw new CreditRequestValidationError(errorBody.issues ?? []);
  }
  if (!r.ok) throw new Error(`createDraft failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

export async function updateDraft(
  id: number,
  patch: UpdateDraftBody,
): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(patch),
  });
  if (!r.ok) throw new Error(`updateDraft failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

export async function setMissingLines(
  id: number,
  barcodes: string[],
): Promise<LineReplacementResponse> {
  return postLines(`${BASE}/${id}/missing-lines`, { barcodes });
}

export async function setEncumberedLines(
  id: number,
  barcodes: string[],
): Promise<LineReplacementResponse> {
  return postLines(`${BASE}/${id}/encumbered-lines`, { barcodes });
}

export async function setWrongLines(
  id: number,
  wrongLines: { expectedBarcode: string; actualImeiOrModel: string }[],
): Promise<LineReplacementResponse> {
  return postLines(`${BASE}/${id}/wrong-lines`, { wrongLines });
}

async function postLines(url: string, body: unknown): Promise<LineReplacementResponse> {
  const r = await apiFetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!r.ok) throw new Error(`postLines failed: HTTP ${r.status}`);
  return LineReplacementResponseSchema.parse(await r.json());
}

export async function submitRequest(id: number): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/${id}/submit`, { method: 'POST' });
  if (r.status === 400) {
    const body = (await r.json()) as { issues?: ValidationIssue[] };
    throw new CreditRequestValidationError(body.issues ?? []);
  }
  if (!r.ok) throw new Error(`submit failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

export async function getRequest(id: number): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/${id}`);
  if (!r.ok) throw new Error(`getRequest failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

export async function listRequests(
  buyerCodeId: number,
  status?: SystemStatus,
): Promise<CreditRequestSummary[]> {
  const params = new URLSearchParams({ buyerCodeId: String(buyerCodeId) });
  if (status) params.set('status', status);
  const r = await apiFetch(`${BASE}?${params.toString()}`);
  if (!r.ok) throw new Error(`listRequests failed: HTTP ${r.status}`);
  return z.array(CreditRequestSummarySchema).parse(await r.json());
}

/**
 * Hard-deletes a DRAFT credit request (gap 2.5). The server enforces
 * ownership (403 on a foreign request) and DRAFT-only (409 once submitted);
 * the UI only ever offers this on DRAFT rows the buyer owns. Mirrors
 * {@link deletePhoto}: 204 is the success shape.
 */
export async function deleteRequest(id: number): Promise<void> {
  const r = await apiFetch(`${BASE}/${id}`, { method: 'DELETE' });
  if (!r.ok && r.status !== 204) {
    throw new Error(`deleteRequest failed: HTTP ${r.status}`);
  }
}

/**
 * Splits a buyer-pasted blob into trimmed barcodes. Accepts the comma-
 * and newline-separated formats the Figma textarea shows.
 */
export function parseBarcodeBlob(blob: string): string[] {
  return blob
    .split(/[\s,]+/)
    .map((b) => b.trim())
    .filter((b) => b.length > 0);
}

// ---------------------------------------------------------------------------
// Photo endpoints (Sprint 4 chunk 4) — buyer detail page consumes all four
// ---------------------------------------------------------------------------

const PhotoKindSchema = z.enum(['DAMAGE', 'WRONG_DEVICE']);
export type PhotoKind = z.infer<typeof PhotoKindSchema>;

export const PhotoMetadataSchema = z.object({
  id: z.number(),
  creditRequestId: z.number(),
  wrongDeviceLineId: z.number().nullable(),
  kind: PhotoKindSchema,
  originalFilename: z.string(),
  contentType: z.string(),
  sizeBytes: z.number(),
  uploadedDate: z.string().nullable(),
  uploadedByUserId: z.number().nullable(),
});
export type PhotoMetadata = z.infer<typeof PhotoMetadataSchema>;

/**
 * Server returns {error, message} where `error` is one of TOO_LARGE,
 * UNSUPPORTED_TYPE, TOO_MANY_PER_LINE, REQUEST_FINALIZED with the
 * matching HTTP status (413/415/409). We surface the reason verbatim
 * so the dropzone can render inline guidance.
 */
export class PhotoUploadError extends Error {
  readonly reason: string;
  readonly status: number;

  constructor(reason: string, message: string, status: number) {
    super(message);
    this.reason = reason;
    this.status = status;
  }
}

export async function listPhotos(requestId: number): Promise<PhotoMetadata[]> {
  const r = await apiFetch(`${BASE}/${requestId}/photos`);
  if (!r.ok) throw new Error(`listPhotos failed: HTTP ${r.status}`);
  return z.array(PhotoMetadataSchema).parse(await r.json());
}

export async function uploadPhoto(
  requestId: number,
  file: File,
  wrongDeviceLineId: number | null,
): Promise<PhotoMetadata> {
  const formData = new FormData();
  formData.append('file', file);
  if (wrongDeviceLineId !== null) {
    formData.append('wrongDeviceLineId', String(wrongDeviceLineId));
  }
  // Note: do NOT set Content-Type — the browser appends the multipart
  // boundary automatically when the body is a FormData instance.
  const r = await apiFetch(`${BASE}/${requestId}/photos`, {
    method: 'POST',
    body: formData,
  });
  if (!r.ok) {
    const body = (await r.json().catch(() => null)) as
      | { error?: string; message?: string }
      | null;
    throw new PhotoUploadError(
      body?.error ?? 'UPLOAD_FAILED',
      body?.message ?? `uploadPhoto failed: HTTP ${r.status}`,
      r.status,
    );
  }
  return PhotoMetadataSchema.parse(await r.json());
}

export async function deletePhoto(photoId: number): Promise<void> {
  const r = await apiFetch(`${BASE}/photos/${photoId}`, { method: 'DELETE' });
  if (!r.ok && r.status !== 204) {
    throw new Error(`deletePhoto failed: HTTP ${r.status}`);
  }
}

/**
 * URL for an <img src> — the backend streams the blob with
 * Content-Disposition: inline. Browsers honour the auth cookie
 * automatically for same-origin GETs so no extra header juggling needed.
 */
export function photoBlobUrl(photoId: number): string {
  return `${BASE}/photos/${photoId}/blob`;
}

// ---------------------------------------------------------------------------
// File-drop parser (Sprint 4 chunk 8) — wizard Step 2 hybrid upload
// ---------------------------------------------------------------------------

export const ParsedBarcodesSchema = z.object({
  barcodes: z.array(z.string()),
  warnings: z.array(z.string()),
});
export type ParsedBarcodes = z.infer<typeof ParsedBarcodesSchema>;

export class FileDropError extends Error {
  readonly status: number;
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

/**
 * Posts a single file (xlsx / csv / docx) to the backend file-drop
 * parser. The wizard merges the returned barcodes into the Step 2
 * textarea; warnings are surfaced inline so the buyer sees what was
 * skipped (short digit runs, duplicates, unknown cells).
 */
export async function parseBarcodesFromFile(file: File): Promise<ParsedBarcodes> {
  const formData = new FormData();
  formData.append('file', file);
  const r = await apiFetch(`${BASE}/parse-barcodes`, {
    method: 'POST',
    body: formData,
  });
  if (!r.ok) {
    const body = (await r.json().catch(() => null)) as
      | { error?: string; message?: string }
      | null;
    throw new FileDropError(
      body?.message ?? `parseBarcodesFromFile failed: HTTP ${r.status}`,
      r.status,
    );
  }
  return ParsedBarcodesSchema.parse(await r.json());
}
