import { z } from 'zod';
import { apiFetch } from './apiFetch';

/**
 * Typed API client for the bidder dashboard surface
 * (`BidderDashboardController` at `/api/v1/bidder/**`). Ports the Mendix
 * `ACT_OpenBidderDashboard` + `ACT_CreateBidData` flow.
 *
 * Shape conventions borrowed from `auctions.ts`:
 *   - Zod schemas validate every response body at the boundary.
 *   - Nullable decimals come back as JSON numbers (never strings).
 *   - Typed error classes surface 409/429 conditions so the UI can
 *     branch without inspecting raw HTTP status strings.
 */

// ---------------------------------------------------------------------------
// Enum + schema definitions
// ---------------------------------------------------------------------------

/**
 * Landing mode the backend assigns to a `GET /dashboard` call.
 * `GRID` — render the bid-data grid for the active round.
 * `DOWNLOAD` — Round 2 "download inventory" landing.
 * `ERROR_AUCTION_NOT_FOUND` — no scheduled auction for this user. Served
 *   with HTTP 404 + this enum in the body.
 * `ALL_ROUNDS_DONE` — every round for the active auction already closed.
 */
const LandingModeSchema = z.enum([
  'GRID',
  'DOWNLOAD',
  'ERROR_AUCTION_NOT_FOUND',
  'ALL_ROUNDS_DONE',
]);

export type LandingMode = z.infer<typeof LandingModeSchema>;

export const BidDataRowSchema = z.object({
  id: z.number(),
  bidRoundId: z.number(),
  ecoid: z.string(),
  // Phase 6B MDM display fields — populated on grid-load via the backend
  // join; null on save-response (the save path doesn't re-fetch MDM data,
  // the frontend retains the last grid-load values when a save arrives).
  brand: z.string().nullable(),
  model: z.string().nullable(),
  modelName: z.string().nullable(),
  carrier: z.string().nullable(),
  added: z.string().nullable(),   // ISO-8601 timestamp; grid renders as date
  mergedGrade: z.string(),
  buyerCodeType: z.string(),
  bidQuantity: z.number().nullable(),
  bidAmount: z.number(),
  targetPrice: z.number(),
  maximumQuantity: z.number().nullable(),
  payout: z.number().nullable(),
  submittedBidQuantity: z.number().nullable(),
  submittedBidAmount: z.number().nullable(),
  lastValidBidQuantity: z.number().nullable(),
  lastValidBidAmount: z.number().nullable(),
  submittedDatetime: z.string().nullable(),
  changedDate: z.string(),
});
export type BidDataRow = z.infer<typeof BidDataRowSchema>;

export const BidDataTotalsSchema = z.object({
  rowCount: z.number(),
  totalBidAmount: z.number(),
  totalPayout: z.number(),
  totalBidQuantity: z.number(),
});
export type BidDataTotals = z.infer<typeof BidDataTotalsSchema>;

export const BidRoundSummarySchema = z.object({
  id: z.number(),
  schedulingAuctionId: z.number(),
  round: z.number().int(),
  roundStatus: z.string(),
  startDatetime: z.string().nullable(),
  endDatetime: z.string().nullable(),
  submitted: z.boolean(),
  submittedDatetime: z.string().nullable(),
});
export type BidRoundSummary = z.infer<typeof BidRoundSummarySchema>;

export const SchedulingAuctionSummarySchema = z.object({
  id: z.number(),
  auctionId: z.number(),
  auctionTitle: z.string(),
  round: z.number().int(),
  roundName: z.string(),
  status: z.string(),
});
export type SchedulingAuctionSummary = z.infer<typeof SchedulingAuctionSummarySchema>;

export const RoundTimerStateSchema = z.object({
  now: z.string(),
  startsAt: z.string().nullable(),
  endsAt: z.string().nullable(),
  secondsUntilStart: z.number(),
  secondsUntilEnd: z.number(),
  active: z.boolean(),
});
export type RoundTimerState = z.infer<typeof RoundTimerStateSchema>;

export const BidderDashboardResponseSchema = z.object({
  mode: LandingModeSchema,
  auction: SchedulingAuctionSummarySchema.nullable(),
  bidRound: BidRoundSummarySchema.nullable(),
  rows: z.array(BidDataRowSchema),
  totals: BidDataTotalsSchema.nullable(),
  timer: RoundTimerStateSchema.nullable(),
});
export type BidderDashboardResponse = z.infer<typeof BidderDashboardResponseSchema>;

export interface SaveBidPayload {
  bidQuantity: number | null;
  bidAmount: number;
}

export const BidSubmissionResultSchema = z.object({
  bidRoundId: z.number(),
  rowCount: z.number(),
  submittedDatetime: z.string(),
  resubmit: z.boolean(),
});
export type BidSubmissionResult = z.infer<typeof BidSubmissionResultSchema>;

export const CarryoverResultSchema = z.object({
  copied: z.number(),
  notFound: z.number(),
  prevWeek: z.string().nullable(),
});
export type CarryoverResult = z.infer<typeof CarryoverResultSchema>;

// ---------------------------------------------------------------------------
// Typed error classes
// ---------------------------------------------------------------------------

/**
 * Thrown when `PUT /bid-data/{id}` hits the per-(user, round) rate-limit
 * bucket. Backend replies `429 Too Many Requests` with an empty body.
 */
export class RateLimitedError extends Error {
  constructor(message: string = 'Too many requests') {
    super(message);
    this.name = 'RateLimitedError';
  }
}

/**
 * Thrown when a submit arrives after the round has already closed server
 * side. Backend returns `409` with `{ code: "ROUND_CLOSED", message }`.
 */
export class RoundClosedError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'RoundClosedError';
  }
}

/**
 * Thrown when a submit collides with concurrent writes on the same bid
 * round. Backend returns `409` with `{ code: "VERSION_CONFLICT", message }`.
 */
export class VersionConflictError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'VersionConflictError';
  }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

interface ErrorEnvelope {
  code?: unknown;
  message?: unknown;
}

async function readErrorEnvelope(
  res: Response,
): Promise<{ code: string | null; message: string }> {
  const body = (await res.json().catch(() => ({}))) as ErrorEnvelope;
  const code = typeof body?.code === 'string' ? body.code : null;
  const message = typeof body?.message === 'string' ? body.message : `HTTP ${res.status}`;
  return { code, message };
}

// ---------------------------------------------------------------------------
// Endpoint wrappers
// ---------------------------------------------------------------------------

/**
 * GET /api/v1/bidder/dashboard?buyerCodeId={id}
 *
 * Returns the dashboard envelope for the given buyer code. When the
 * backend cannot find an active auction it replies with HTTP 404 whose
 * body is still a valid `BidderDashboardResponse` carrying
 * `mode: "ERROR_AUCTION_NOT_FOUND"`. We intentionally do NOT throw for
 * that shape — the page branches on `response.mode` instead of hunting
 * exception types. Any other non-OK status is surfaced as a generic
 * `Error("HTTP <status>")`.
 */
export async function loadDashboard(buyerCodeId: number): Promise<BidderDashboardResponse> {
  const res = await apiFetch(`/api/v1/bidder/dashboard?buyerCodeId=${buyerCodeId}`);

  if (res.ok || res.status === 404) {
    return BidderDashboardResponseSchema.parse(await res.json());
  }

  throw new Error(`HTTP ${res.status}`);
}

/**
 * PUT /api/v1/bidder/bid-data/{id}
 *
 * Single-row save. Server applies a per-(user, round) rate-limit of
 * 60 requests / minute; exhausting the bucket returns 429 with no body,
 * which we translate into {@link RateLimitedError} so the UI can pause
 * autosave + surface a toast.
 */
export async function saveBid(id: number, payload: SaveBidPayload): Promise<BidDataRow> {
  const res = await apiFetch(`/api/v1/bidder/bid-data/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (res.status === 429) {
    throw new RateLimitedError();
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return BidDataRowSchema.parse(await res.json());
}

/**
 * POST /api/v1/bidder/bid-rounds/{id}/submit?buyerCodeId={buyerCodeId}
 *
 * Re-callable submit endpoint — the backend requires `buyerCodeId` as a
 * query param so the UPDATE is scoped to this buyer's rows only
 * (otherwise a submit by buyer A would flip rows for buyer B).
 *
 * 409 responses carry a typed `code` in the body that we translate into
 * {@link RoundClosedError} (ROUND_CLOSED) or {@link VersionConflictError}
 * (VERSION_CONFLICT). Any other 409 code falls back to a generic Error so
 * unknown server responses are not swallowed into the wrong branch.
 */
export async function submitBidRound(
  id: number,
  buyerCodeId: number,
): Promise<BidSubmissionResult> {
  const res = await apiFetch(
    `/api/v1/bidder/bid-rounds/${id}/submit?buyerCodeId=${buyerCodeId}`,
    { method: 'POST' },
  );

  if (res.status === 409) {
    const { code, message } = await readErrorEnvelope(res);
    if (code === 'ROUND_CLOSED') throw new RoundClosedError(message);
    if (code === 'VERSION_CONFLICT') throw new VersionConflictError(message);
    throw new Error(`HTTP ${res.status}`);
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return BidSubmissionResultSchema.parse(await res.json());
}

/**
 * POST /api/v1/bidder/bid-rounds/{id}/carryover?buyerCodeId={buyerCodeId}
 *
 * Copies the previous week's submitted bid values into the current round
 * for the given buyer code. Idempotent — calling it twice produces the
 * same state.
 *
 * Returns `{ copied, notFound, prevWeek }`. When `copied === 0` the UI
 * shows the empty-state modal ("You don't have bids from last week to
 * carry over."); when `copied > 0` it shows the success copy with the
 * week label.
 */
export async function carryoverBidRound(
  id: number,
  buyerCodeId: number,
): Promise<CarryoverResult> {
  const res = await apiFetch(
    `/api/v1/bidder/bid-rounds/${id}/carryover?buyerCodeId=${buyerCodeId}`,
    { method: 'POST' },
  );

  if (res.status === 409) {
    const { code, message } = await readErrorEnvelope(res);
    if (code === 'ROUND_CLOSED') throw new RoundClosedError(message);
    throw new Error(`HTTP ${res.status}: ${message}`);
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return CarryoverResultSchema.parse(await res.json());
}

// ---------------------------------------------------------------------------
// Import / Export  (Phase 10)
// ---------------------------------------------------------------------------

export const BidImportRowErrorSchema = z.object({
  row: z.number().int(),
  message: z.string(),
});
export type BidImportRowError = z.infer<typeof BidImportRowErrorSchema>;

export const BidImportResultSchema = z.object({
  updated: z.number().int(),
  errors: z.array(BidImportRowErrorSchema),
});
export type BidImportResult = z.infer<typeof BidImportResultSchema>;

/**
 * GET /api/v1/bidder/bid-rounds/{id}/export?buyerCodeId={buyerCodeId}
 *
 * Triggers a browser file download of the bid slice as an xlsx file.
 * Opens a hidden anchor element with the `download` attribute so the
 * browser streams the file directly — no Blob allocation in the tab.
 * The anchor is removed from the DOM immediately after the click.
 */
export function exportBidRound(id: number, buyerCodeId: number): void {
  const url = `/api/v1/bidder/bid-rounds/${id}/export?buyerCodeId=${buyerCodeId}`;

  // WHY a programmatic anchor: fetch() with blob() would buffer the entire
  // file in JS memory. Using an anchor + download attribute lets the browser
  // handle the stream natively. The relative URL is resolved against the
  // Next.js rewrites so cookies are forwarded automatically.
  const a = document.createElement('a');
  a.href = url;
  a.setAttribute('download', '');
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

/**
 * POST /api/v1/bidder/bid-rounds/{id}/import?buyerCodeId={buyerCodeId}
 *
 * Uploads an xlsx file and returns the import result. On validation
 * failure the server returns 200 with a non-empty `errors` array. On
 * rate-limit the server returns 429 which this function translates to
 * {@link RateLimitedError}.
 */
export async function importBidRound(
  id: number,
  buyerCodeId: number,
  file: File,
): Promise<BidImportResult> {
  const form = new FormData();
  form.append('file', file);

  const res = await apiFetch(
    `/api/v1/bidder/bid-rounds/${id}/import?buyerCodeId=${buyerCodeId}`,
    { method: 'POST', body: form },
  );

  if (res.status === 429) {
    throw new RateLimitedError();
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }

  return BidImportResultSchema.parse(await res.json());
}
