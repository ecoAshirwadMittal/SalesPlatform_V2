import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const CreateAuctionRequestSchema = z.object({
  weekId: z.number().int().positive(),
  customSuffix: z.string().optional(),
});
export type CreateAuctionRequest = z.infer<typeof CreateAuctionRequestSchema>;

export const CreateAuctionResponseSchema = z.object({
  id: z.number(),
  auctionTitle: z.string(),
  auctionStatus: z.string(),
  weekId: z.number(),
  weekDisplay: z.string(),
});
export type CreateAuctionResponse = z.infer<typeof CreateAuctionResponseSchema>;

export const RoundViewSchema = z.object({
  id: z.number(),
  round: z.number().int(),
  name: z.string(),
  startDatetime: z.string(),
  endDatetime: z.string(),
  roundStatus: z.string(),
  hasRound: z.boolean(),
});
export type RoundView = z.infer<typeof RoundViewSchema>;

export const AuctionDetailResponseSchema = z.object({
  id: z.number(),
  auctionTitle: z.string(),
  auctionStatus: z.string(),
  weekId: z.number(),
  weekDisplay: z.string(),
  rounds: z.array(RoundViewSchema),
});
export type AuctionDetailResponse = z.infer<typeof AuctionDetailResponseSchema>;

export const ScheduleDefaultsResponseSchema = z.object({
  round1Start: z.string(),
  round1End: z.string(),
  round2Start: z.string(),
  round2End: z.string(),
  round3Start: z.string(),
  round3End: z.string(),
  round2Active: z.boolean(),
  round3Active: z.boolean(),
  round2MinutesOffset: z.number().int(),
  round3MinutesOffset: z.number().int(),
});
export type ScheduleDefaultsResponse = z.infer<typeof ScheduleDefaultsResponseSchema>;

export interface ScheduleAuctionRequest {
  round1Start: string;
  round1End: string;
  round2Start: string;
  round2End: string;
  round2Active: boolean;
  round3Start: string;
  round3End: string;
  round3Active: boolean;
}

export class DuplicateAuctionTitleError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'DuplicateAuctionTitleError';
  }
}

export class AuctionAlreadyExistsError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuctionAlreadyExistsError';
  }
}

/**
 * Thrown when `PUT /schedule` rejects the payload because one or more active
 * rounds had `end <= start`. Backend returns a comma-joined message plus a
 * `details` array with one item per round — we expose both so the page can
 * pin inline per-round errors as well as render a banner fallback.
 */
export class RoundValidationError extends Error {
  readonly details: string[];
  constructor(message: string, details: string[]) {
    super(message);
    this.name = 'RoundValidationError';
    this.details = details;
  }
}

export class AuctionAlreadyStartedError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuctionAlreadyStartedError';
  }
}

export class AuctionHasBidsError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuctionHasBidsError';
  }
}

export class AuctionNotFoundError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuctionNotFoundError';
  }
}

interface ErrorEnvelope {
  message?: unknown;
  details?: Array<{ message?: unknown }>;
}

async function readErrorEnvelope(res: Response): Promise<{ message: string; details: string[] }> {
  const body = (await res.json().catch(() => ({}))) as ErrorEnvelope;
  const message = typeof body?.message === 'string' ? body.message : `HTTP ${res.status}`;
  const details = Array.isArray(body?.details)
    ? body.details
        .map((d) => (typeof d?.message === 'string' ? d.message : ''))
        .filter((s) => s.length > 0)
    : [];
  return { message, details };
}

/**
 * POST /api/v1/admin/auctions. Maps the backend's 409 responses to typed
 * errors so the UI can render field-level vs. banner-level messages.
 * Backend distinguishes duplicate-title (most common admin mistake) from
 * auction-already-exists-for-week (a race the UI also guards against via
 * the `hasAuction` helper flag).
 *
 * Per the 2026-04-20 ADR amendment, Create persists only the auction row;
 * the three scheduling rounds are written by the Schedule endpoint (Phase C).
 */
export async function createAuction(req: CreateAuctionRequest): Promise<CreateAuctionResponse> {
  const res = await apiFetch('/api/v1/admin/auctions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });

  if (res.status === 409) {
    const { message } = await readErrorEnvelope(res);
    if (message.toLowerCase().includes('name already exists')) {
      throw new DuplicateAuctionTitleError(message);
    }
    throw new AuctionAlreadyExistsError(message);
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return CreateAuctionResponseSchema.parse(await res.json());
}

/**
 * GET /api/v1/admin/auctions/{id}. Returns the auction plus rounds
 * (empty when Unscheduled, 3 when Scheduled/Started/Closed).
 */
export async function getAuctionDetail(id: number): Promise<AuctionDetailResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/${id}`);
  if (res.status === 404) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionNotFoundError(message);
  }
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return AuctionDetailResponseSchema.parse(await res.json());
}

/**
 * GET /api/v1/admin/auctions/{id}/schedule-defaults. Returns the
 * pre-populated form values for the scheduling page. When the auction
 * already has rounds, values come from the stored rows; otherwise they
 * are computed from the parent week + config minute offsets.
 */
export async function getScheduleDefaults(id: number): Promise<ScheduleDefaultsResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/${id}/schedule-defaults`);
  if (res.status === 404) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionNotFoundError(message);
  }
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return ScheduleDefaultsResponseSchema.parse(await res.json());
}

/**
 * PUT /api/v1/admin/auctions/{id}/schedule. Persists the three round rows
 * (delete-and-recreate) and flips `auction.status` to `Scheduled`.
 *
 * Error mapping:
 *   400 + RoundValidationException → RoundValidationError (comma message + per-round details)
 *   409 "started" body → AuctionAlreadyStartedError
 *   409 "bids" body → AuctionHasBidsError
 */
export async function saveSchedule(
  id: number,
  req: ScheduleAuctionRequest,
): Promise<AuctionDetailResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/${id}/schedule`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });

  if (res.status === 400) {
    const { message, details } = await readErrorEnvelope(res);
    throw new RoundValidationError(message, details);
  }

  if (res.status === 404) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionNotFoundError(message);
  }

  if (res.status === 409) {
    const { message } = await readErrorEnvelope(res);
    const lower = message.toLowerCase();
    if (lower.includes('bid')) {
      throw new AuctionHasBidsError(message);
    }
    throw new AuctionAlreadyStartedError(message);
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return AuctionDetailResponseSchema.parse(await res.json());
}

/**
 * POST /api/v1/admin/auctions/{id}/unschedule. Flips auction + all rounds
 * to `Unscheduled`. Rejected with 409 when any round is already `Started`.
 */
export async function unscheduleAuction(id: number): Promise<AuctionDetailResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/${id}/unschedule`, {
    method: 'POST',
  });

  if (res.status === 404) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionNotFoundError(message);
  }

  if (res.status === 409) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionAlreadyStartedError(message);
  }

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return AuctionDetailResponseSchema.parse(await res.json());
}

/**
 * DELETE /api/v1/admin/auctions/{id}. Administrator only; cascades to
 * bid rounds and scheduling rows. 409 when any round is `Started`.
 */
export async function deleteAuction(id: number): Promise<void> {
  const res = await apiFetch(`/api/v1/admin/auctions/${id}`, {
    method: 'DELETE',
  });

  if (res.status === 404) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionNotFoundError(message);
  }

  if (res.status === 409) {
    const { message } = await readErrorEnvelope(res);
    throw new AuctionAlreadyStartedError(message);
  }

  if (res.status !== 204 && !res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }
}

// ---------------------------------------------------------------------------
// List endpoints backing the admin Auctions + Scheduling Auctions grids.
// Ports Mendix Mx_Admin.Pages.Auctions + Mx_Admin.Pages.Scheduling_Auctions.
// ---------------------------------------------------------------------------

export const AuctionListRowSchema = z.object({
  id: z.number(),
  auctionTitle: z.string(),
  auctionStatus: z.string(),
  weekId: z.number().nullable(),
  weekDisplay: z.string().nullable(),
  createdDate: z.string().nullable(),
  changedDate: z.string().nullable(),
  createdBy: z.string().nullable(),
  updatedBy: z.string().nullable(),
  roundCount: z.number().int(),
});
export type AuctionListRow = z.infer<typeof AuctionListRowSchema>;

export const AuctionListPageResponseSchema = z.object({
  content: z.array(AuctionListRowSchema),
  page: z.number().int(),
  pageSize: z.number().int(),
  totalElements: z.number().int(),
  totalPages: z.number().int(),
});
export type AuctionListPageResponse = z.infer<typeof AuctionListPageResponseSchema>;

export interface ListAuctionsParams {
  title?: string;
  weekId?: number;
  status?: string;
  page?: number;
  pageSize?: number;
}

export async function listAuctions(
  params: ListAuctionsParams = {},
): Promise<AuctionListPageResponse> {
  const qs = new URLSearchParams();
  if (params.title) qs.set('title', params.title);
  if (typeof params.weekId === 'number') qs.set('weekId', String(params.weekId));
  if (params.status) qs.set('status', params.status);
  qs.set('page', String(params.page ?? 0));
  qs.set('pageSize', String(params.pageSize ?? 20));

  const res = await apiFetch(`/api/v1/admin/auctions?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return AuctionListPageResponseSchema.parse(await res.json());
}

export const SchedulingAuctionListRowSchema = z.object({
  id: z.number(),
  auctionId: z.number().nullable(),
  auctionTitle: z.string().nullable(),
  auctionWeekYear: z.string().nullable(),
  round: z.number().int(),
  name: z.string().nullable(),
  startDatetime: z.string().nullable(),
  endDatetime: z.string().nullable(),
  roundStatus: z.string(),
  hasRound: z.boolean(),
});
export type SchedulingAuctionListRow = z.infer<typeof SchedulingAuctionListRowSchema>;

export const SchedulingAuctionListPageResponseSchema = z.object({
  content: z.array(SchedulingAuctionListRowSchema),
  page: z.number().int(),
  pageSize: z.number().int(),
  totalElements: z.number().int(),
  totalPages: z.number().int(),
});
export type SchedulingAuctionListPageResponse = z.infer<
  typeof SchedulingAuctionListPageResponseSchema
>;

export interface ListSchedulingAuctionsParams {
  auctionId?: number;
  status?: string;
  weekDisplay?: string;
  page?: number;
  pageSize?: number;
}

export async function listSchedulingAuctions(
  params: ListSchedulingAuctionsParams = {},
): Promise<SchedulingAuctionListPageResponse> {
  const qs = new URLSearchParams();
  if (typeof params.auctionId === 'number') qs.set('auctionId', String(params.auctionId));
  if (params.status) qs.set('status', params.status);
  if (params.weekDisplay) qs.set('weekDisplay', params.weekDisplay);
  qs.set('page', String(params.page ?? 0));
  qs.set('pageSize', String(params.pageSize ?? 20));

  const res = await apiFetch(`/api/v1/admin/scheduling-auctions?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return SchedulingAuctionListPageResponseSchema.parse(await res.json());
}

// ---------------------------------------------------------------------------
// Round 2 / Round 3 Selection Rules (Phase D).
// Ports Mendix acc_RoundTwoCriteriaPage + PG_Round3Criteria.
// ---------------------------------------------------------------------------

export const RegularBuyerQualificationSchema = z.enum([
  'Only_Qualified',
  'All_Buyers',
]);
export type RegularBuyerQualification = z.infer<typeof RegularBuyerQualificationSchema>;

export const RegularBuyerInventoryOptionSchema = z.enum([
  'InventoryRound1QualifiedBids',
  'ShowAllInventory',
]);
export type RegularBuyerInventoryOption = z.infer<typeof RegularBuyerInventoryOptionSchema>;

/**
 * Backend emits numeric decimal columns (targetPercent, targetValue,
 * totalValueFloor) as JSON numbers, never as strings. Keeping them as
 * `number | null` here avoids a parse/format dance on every render.
 */
export const BidRoundSelectionFilterResponseSchema = z.object({
  id: z.number(),
  legacyId: z.number().nullable(),
  round: z.number().int(),
  targetPercent: z.number().nullable(),
  targetValue: z.number().nullable(),
  totalValueFloor: z.number().nullable(),
  mergedGrade1: z.string().nullable(),
  mergedGrade2: z.string().nullable(),
  mergedGrade3: z.string().nullable(),
  stbAllowAllBuyersOverride: z.boolean(),
  stbIncludeAllInventory: z.boolean(),
  regularBuyerQualification: RegularBuyerQualificationSchema,
  regularBuyerInventoryOptions: RegularBuyerInventoryOptionSchema,
  createdDate: z.string(),
  changedDate: z.string(),
});
export type BidRoundSelectionFilterResponse = z.infer<
  typeof BidRoundSelectionFilterResponseSchema
>;

export interface BidRoundSelectionFilterRequest {
  targetPercent: number | null;
  targetValue: number | null;
  totalValueFloor: number | null;
  mergedGrade1: string | null;
  mergedGrade2: string | null;
  mergedGrade3: string | null;
  stbAllowAllBuyersOverride: boolean;
  stbIncludeAllInventory: boolean;
  regularBuyerQualification: RegularBuyerQualification;
  regularBuyerInventoryOptions: RegularBuyerInventoryOption;
}

/**
 * GET /api/v1/admin/auctions/round-filters/{round}. Returns the single row
 * for round 2 or round 3. Any other `round` value is rejected server-side
 * with 400 (the service validates before hitting the repo).
 */
export async function getRoundFilter(round: 2 | 3): Promise<BidRoundSelectionFilterResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/round-filters/${round}`);
  if (!res.ok) {
    const { message } = await readErrorEnvelope(res);
    throw new Error(message || `HTTP ${res.status}`);
  }
  return BidRoundSelectionFilterResponseSchema.parse(await res.json());
}

/**
 * PUT /api/v1/admin/auctions/round-filters/{round}. Administrator only —
 * SalesOps is rejected by the filter-chain matcher before `@PreAuthorize`
 * runs, matching the 2026-04-19 ADR pattern for Admin-only writes on a
 * shared Admin/SalesOps surface.
 */
export async function updateRoundFilter(
  round: 2 | 3,
  req: BidRoundSelectionFilterRequest,
): Promise<BidRoundSelectionFilterResponse> {
  const res = await apiFetch(`/api/v1/admin/auctions/round-filters/${round}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });
  if (!res.ok) {
    const { message } = await readErrorEnvelope(res);
    throw new Error(message || `HTTP ${res.status}`);
  }
  return BidRoundSelectionFilterResponseSchema.parse(await res.json());
}
