import { z } from 'zod';
import { apiFetch } from './apiFetch';
import {
  CreditRequestDetailSchema,
  CreditRequestValidationError,
  type CreditRequestDetail,
  type ValidationIssue,
} from './partialCreditClient';

/**
 * Typed API client for the SalesOps-facing Partial Credit admin surface
 * (`AdminPartialCreditController` at `/api/v1/admin/partial-credit/**`).
 *
 * Why a separate module: the buyer-side {@link partialCreditClient}
 * already encodes the wizard's narrower contract — adding the admin
 * endpoints there would dilute its surface and force the buyer wizard to
 * compile against decision/section/global types it never calls. A small
 * dedicated client keeps the buyer and admin TypeScript dependency
 * graphs disjoint, which is the same split the backend made between
 * `BuyerPartialCreditController` and `AdminPartialCreditController`.
 *
 * Validation issues (e.g. `LINES_PENDING_DECISION` on completeReview)
 * are surfaced via the shared {@link CreditRequestValidationError} so
 * admin pages can branch on `issues[].code` identically to the wizard.
 */

const SystemStatusSchema = z.enum([
  'DRAFT',
  'PENDING_APPROVAL',
  'UNDER_REVIEW',
  'APPROVED',
  'DECLINED',
]);
export type AdminSystemStatus = z.infer<typeof SystemStatusSchema>;

const ReviewDecisionSchema = z.enum(['PENDING', 'ACCEPTED', 'DECLINED']);
export type ReviewDecision = z.infer<typeof ReviewDecisionSchema>;

const PrologResultSchema = z.enum(['ENCUMBERED', 'NOT_ENCUMBERED', 'PENDING']);
export type PrologResult = z.infer<typeof PrologResultSchema>;

const LineKindSchema = z.enum(['MISSING', 'WRONG', 'ENCUMBERED']);
export type LineKind = z.infer<typeof LineKindSchema>;

// ---------------------------------------------------------------------------
// Row + counter shapes (landing list)
// ---------------------------------------------------------------------------

export const AdminCreditRequestRowSchema = z.object({
  id: z.number(),
  requestNumber: z.string(),
  orderNumber: z.string(),
  buyerCodeId: z.number().nullable(),
  // Backend returns null when buyer-code resolution didn't pre-load this row.
  buyerCode: z.string().nullable(),
  partyName: z.string().nullable(),
  systemStatus: SystemStatusSchema,
  displayStatus: z.string(),
  // Per §11.Q5 — colour is live from DB so the SPKB-3664 status-config
  // page can swap it without a redeploy. Apply inline on the pill node.
  statusColorHex: z.string(),
  requestDate: z.string().nullable(),
  submittedDate: z.string().nullable(),
  hasMissingDevice: z.boolean(),
  hasWrongDevice: z.boolean(),
  hasEncumberedDevice: z.boolean(),
  totalDevices: z.number().nullable(),
  requestedTotal: z.number().nullable(),
});
export type AdminCreditRequestRow = z.infer<typeof AdminCreditRequestRowSchema>;

export const AdminStatusCountersSchema = z.object({
  pendingApproval: z.number(),
  underReview: z.number(),
  approved: z.number(),
  declined: z.number(),
});
export type AdminStatusCounters = z.infer<typeof AdminStatusCountersSchema>;

export const AdminLandingResponseSchema = z.object({
  rows: z.array(AdminCreditRequestRowSchema),
  counters: AdminStatusCountersSchema,
  total: z.number(),
});
export type AdminLandingResponse = z.infer<typeof AdminLandingResponseSchema>;

// ---------------------------------------------------------------------------
// Header summary + line projection (detail endpoints)
// ---------------------------------------------------------------------------

export const HeaderSummarySchema = z.object({
  requestedSkus: z.number(),
  requestedQty: z.number(),
  requestedTotal: z.number(),
  approvedSkus: z.number(),
  approvedQty: z.number(),
  approvedTotal: z.number(),
});
export type HeaderSummary = z.infer<typeof HeaderSummarySchema>;

// The three line-DTO shapes are 1:1 with the buyer-side detail; we re-use
// the loose `z.unknown` for the irrelevant cases and validate the active
// branch inline.
const MissingLineDtoSchema = z.object({
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

const WrongLineDtoSchema = z.object({
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

const EncumberedLineDtoSchema = z.object({
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

export const AdminLineProjectionSchema = z.object({
  kind: LineKindSchema,
  missingLine: MissingLineDtoSchema.nullable(),
  wrongLine: WrongLineDtoSchema.nullable(),
  encumberedLine: EncumberedLineDtoSchema.nullable(),
});
export type AdminLineProjection = z.infer<typeof AdminLineProjectionSchema>;

// ---------------------------------------------------------------------------
// Mutating-endpoint responses
// ---------------------------------------------------------------------------

export const LineDecisionResponseSchema = z.object({
  line: AdminLineProjectionSchema,
  summary: HeaderSummarySchema,
});
export type LineDecisionResponse = z.infer<typeof LineDecisionResponseSchema>;

export const SectionDecisionResponseSchema = z.object({
  kind: LineKindSchema,
  updatedCount: z.number(),
  summary: HeaderSummarySchema,
});
export type SectionDecisionResponse = z.infer<typeof SectionDecisionResponseSchema>;

export const GlobalDecisionResponseSchema = z.object({
  missingUpdated: z.number(),
  wrongUpdated: z.number(),
  encumberedUpdated: z.number(),
  summary: HeaderSummarySchema,
});
export type GlobalDecisionResponse = z.infer<typeof GlobalDecisionResponseSchema>;

export const CompleteReviewResponseSchema = z.object({
  id: z.number(),
  requestNumber: z.string(),
  outcome: SystemStatusSchema,
  reviewCompletedOn: z.string().nullable(),
  summary: HeaderSummarySchema,
});
export type CompleteReviewResponse = z.infer<typeof CompleteReviewResponseSchema>;

// ---------------------------------------------------------------------------
// Filter shape used by the landing page
// ---------------------------------------------------------------------------

export interface AdminListFilter {
  status?: AdminSystemStatus;
  buyerCodeId?: number;
  orderNumber?: string;
  reason?: LineKind;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
}

// Re-export the shared validation error so detail components can branch
// on `instanceof CreditRequestValidationError` without importing from two
// client modules.
export { CreditRequestValidationError };
export type { ValidationIssue };

// ---------------------------------------------------------------------------
// HTTP wrappers
// ---------------------------------------------------------------------------

const BASE = '/api/v1/admin/partial-credit';

export async function listAdmin(filter: AdminListFilter): Promise<AdminLandingResponse> {
  const params = new URLSearchParams();
  if (filter.status) params.set('status', filter.status);
  if (filter.buyerCodeId !== undefined) params.set('buyerCodeId', String(filter.buyerCodeId));
  if (filter.orderNumber) params.set('orderNumber', filter.orderNumber);
  if (filter.reason) params.set('reason', filter.reason);
  if (filter.dateFrom) params.set('dateFrom', filter.dateFrom);
  if (filter.dateTo) params.set('dateTo', filter.dateTo);
  if (filter.page !== undefined) params.set('page', String(filter.page));
  if (filter.size !== undefined) params.set('size', String(filter.size));

  const url = params.toString() ? `${BASE}?${params.toString()}` : BASE;
  const r = await apiFetch(url);
  if (!r.ok) throw new Error(`listAdmin failed: HTTP ${r.status}`);
  return AdminLandingResponseSchema.parse(await r.json());
}

export async function getAdminDetail(id: number): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/${id}`);
  if (!r.ok) throw new Error(`getAdminDetail failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

/**
 * Idempotent: backend flips PENDING_APPROVAL → UNDER_REVIEW on the first
 * call and is a no-op on subsequent calls. The frontend can safely re-
 * fire on every mount without guarding.
 */
export async function openForReview(id: number): Promise<CreditRequestDetail> {
  const r = await apiFetch(`${BASE}/${id}/open-for-review`, { method: 'POST' });
  if (!r.ok) throw new Error(`openForReview failed: HTTP ${r.status}`);
  return CreditRequestDetailSchema.parse(await r.json());
}

export async function setLineDecision(
  id: number,
  lineId: number,
  kind: LineKind,
  decision: ReviewDecision,
): Promise<LineDecisionResponse> {
  const r = await apiFetch(`${BASE}/${id}/lines/${lineId}/decision`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ kind, decision }),
  });
  if (!r.ok) throw new Error(`setLineDecision failed: HTTP ${r.status}`);
  return LineDecisionResponseSchema.parse(await r.json());
}

export async function setSectionDecision(
  id: number,
  kind: LineKind,
  decision: ReviewDecision,
): Promise<SectionDecisionResponse> {
  // Lower-cased path segment matches the controller — `@PathVariable LineKind`
  // is bound case-sensitively against the enum constant names.
  const r = await apiFetch(`${BASE}/${id}/sections/${kind}/decision`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ decision }),
  });
  if (!r.ok) throw new Error(`setSectionDecision failed: HTTP ${r.status}`);
  return SectionDecisionResponseSchema.parse(await r.json());
}

export async function setGlobalDecision(
  id: number,
  decision: ReviewDecision,
): Promise<GlobalDecisionResponse> {
  const r = await apiFetch(`${BASE}/${id}/decision`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ decision }),
  });
  if (!r.ok) throw new Error(`setGlobalDecision failed: HTTP ${r.status}`);
  return GlobalDecisionResponseSchema.parse(await r.json());
}

export async function setEncumberedFields(
  id: number,
  lineId: number,
  prologResult: PrologResult,
  actualValue: number | null,
): Promise<LineDecisionResponse> {
  const r = await apiFetch(`${BASE}/${id}/lines/${lineId}/encumbered`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prologResult, actualValue }),
  });
  if (!r.ok) throw new Error(`setEncumberedFields failed: HTTP ${r.status}`);
  return LineDecisionResponseSchema.parse(await r.json());
}

/**
 * Backend rejects with 400 + `issues[]` (e.g. `LINES_PENDING_DECISION`)
 * when any line is still PENDING. Surface the typed error so the modal
 * banner can render the first issue inline.
 */
export async function completeReview(
  id: number,
  outcome: 'APPROVED' | 'DECLINED',
): Promise<CompleteReviewResponse> {
  const r = await apiFetch(`${BASE}/${id}/complete-review`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ outcome }),
  });
  if (r.status === 400) {
    const body = (await r.json()) as { issues?: ValidationIssue[] };
    throw new CreditRequestValidationError(body.issues ?? []);
  }
  if (!r.ok) throw new Error(`completeReview failed: HTTP ${r.status}`);
  return CompleteReviewResponseSchema.parse(await r.json());
}
