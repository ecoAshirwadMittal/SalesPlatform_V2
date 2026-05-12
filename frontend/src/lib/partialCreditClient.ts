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
  expectedBrand: z.string().nullable(),
  expectedModel: z.string().nullable(),
  expectedGrade: z.string().nullable(),
  expectedAmountPaid: z.number().nullable(),
  actualImeiOrModel: z.string().nullable(),
  actualBrand: z.string().nullable(),
  actualModel: z.string().nullable(),
  actualGrade: z.string().nullable(),
  latestPrice: z.number().nullable(),
  actionRecommendation: z.string().nullable(),
  lineStatus: z.string().nullable(),
  reviewDecision: z.string().nullable(),
  amountToCredit: z.number().nullable(),
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
  partyName: z.string().nullable(),
  orderCreatedDate: z.string().nullable(),
  orderShippedDate: z.string().nullable(),
  systemStatus: SystemStatusSchema,
  displayStatus: z.string(),
  shipmentDamaged: ShipmentDamagedSchema,
  hasMissingDevice: z.boolean(),
  hasWrongDevice: z.boolean(),
  hasEncumberedDevice: z.boolean(),
  totalDevices: z.number().nullable(),
  requestedTotal: z.number().nullable(),
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
 * Splits a buyer-pasted blob into trimmed barcodes. Accepts the comma-
 * and newline-separated formats the Figma textarea shows.
 */
export function parseBarcodeBlob(blob: string): string[] {
  return blob
    .split(/[\s,]+/)
    .map((b) => b.trim())
    .filter((b) => b.length > 0);
}
