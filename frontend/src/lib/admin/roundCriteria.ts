import { z } from 'zod';
import { apiFetch } from '../apiFetch';

/**
 * Lane 4 admin R2-criteria client. Targets the simplified surface at
 * {@code /api/v1/admin/round-criteria/{round}} — three settings, two
 * radio groups + one toggle. The broader admin form (target_percent,
 * merged_grade*, etc.) lives at {@code /admin/auctions-data-center/
 * auctions/round-filters/[round]} (Phase D) and is not in scope here.
 */

export const RoundCriteriaQualificationSchema = z.enum([
  'All_Buyers',
  'Bid_Buyers_Only',
]);
export type RoundCriteriaQualification = z.infer<typeof RoundCriteriaQualificationSchema>;

export const RoundCriteriaInventorySchema = z.enum([
  'Full_Inventory',
  'Inventory_With_Bids',
]);
export type RoundCriteriaInventory = z.infer<typeof RoundCriteriaInventorySchema>;

export const RoundCriteriaResponseSchema = z.object({
  round: z.number().int(),
  regularBuyerQualification: RoundCriteriaQualificationSchema,
  regularBuyerInventoryOptions: RoundCriteriaInventorySchema,
  stbAllowAllBuyersOverride: z.boolean(),
});
export type RoundCriteriaResponse = z.infer<typeof RoundCriteriaResponseSchema>;

export interface RoundCriteriaUpdateRequest {
  regularBuyerQualification: RoundCriteriaQualification;
  regularBuyerInventoryOptions: RoundCriteriaInventory;
  stbAllowAllBuyersOverride: boolean;
}

/**
 * Sentinel returned by {@link getRoundCriteria} when the API responds 404
 * (no row persisted yet). Distinguishes "missing" from "loaded with these
 * defaults" so the page can show "(unsaved defaults)" affordances.
 */
export const ROUND_CRITERIA_NOT_FOUND = Symbol('ROUND_CRITERIA_NOT_FOUND');

export const ROUND_CRITERIA_DEFAULTS: Omit<RoundCriteriaResponse, 'round'> = {
  regularBuyerQualification: 'Bid_Buyers_Only',
  regularBuyerInventoryOptions: 'Inventory_With_Bids',
  stbAllowAllBuyersOverride: false,
};

interface ErrorEnvelope {
  message?: unknown;
}

async function readMessage(res: Response): Promise<string> {
  const body = (await res.json().catch(() => ({}))) as ErrorEnvelope;
  return typeof body?.message === 'string' ? body.message : `HTTP ${res.status}`;
}

/**
 * GET /api/v1/admin/round-criteria/{round}. Returns the persisted criteria
 * row, or the {@link ROUND_CRITERIA_NOT_FOUND} sentinel when no row exists.
 * Throws for any other non-2xx status so the caller can render a banner.
 */
export async function getRoundCriteria(
  round: 2 | 3,
): Promise<RoundCriteriaResponse | typeof ROUND_CRITERIA_NOT_FOUND> {
  const res = await apiFetch(`/api/v1/admin/round-criteria/${round}`);
  if (res.status === 404) {
    return ROUND_CRITERIA_NOT_FOUND;
  }
  if (!res.ok) {
    throw new Error(await readMessage(res));
  }
  return RoundCriteriaResponseSchema.parse(await res.json());
}

/**
 * PUT /api/v1/admin/round-criteria/{round}. Upserts the row and returns
 * the persisted projection. Throws on non-2xx so the page can render a
 * banner — 400 typically means an unknown enum string slipped through.
 */
export async function updateRoundCriteria(
  round: 2 | 3,
  req: RoundCriteriaUpdateRequest,
): Promise<RoundCriteriaResponse> {
  const res = await apiFetch(`/api/v1/admin/round-criteria/${round}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });
  if (!res.ok) {
    throw new Error(await readMessage(res));
  }
  return RoundCriteriaResponseSchema.parse(await res.json());
}
