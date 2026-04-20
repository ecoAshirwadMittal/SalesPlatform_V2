import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const CreateAuctionRequestSchema = z.object({
  weekId: z.number().int().positive(),
  customSuffix: z.string().optional(),
});
export type CreateAuctionRequest = z.infer<typeof CreateAuctionRequestSchema>;

export const AuctionRoundSchema = z.object({
  id: z.number(),
  round: z.number().int(),
  startDatetime: z.string(),
  endDatetime: z.string(),
  status: z.string(),
});
export type AuctionRound = z.infer<typeof AuctionRoundSchema>;

export const CreateAuctionResponseSchema = z.object({
  id: z.number(),
  auctionTitle: z.string(),
  auctionStatus: z.string(),
  weekId: z.number(),
  weekDisplay: z.string(),
  rounds: z.array(AuctionRoundSchema),
});
export type CreateAuctionResponse = z.infer<typeof CreateAuctionResponseSchema>;

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
 * POST /api/v1/admin/auctions. Maps the backend's 409 responses to typed
 * errors so the UI can render field-level vs. banner-level messages.
 * Backend distinguishes duplicate-title (most common admin mistake) from
 * auction-already-exists-for-week (a race the UI also guards against via
 * the `hasAuction` helper flag).
 */
export async function createAuction(req: CreateAuctionRequest): Promise<CreateAuctionResponse> {
  const res = await apiFetch('/api/v1/admin/auctions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });

  if (res.status === 409) {
    const body = await res.json().catch(() => ({}));
    const message = typeof body?.message === 'string' ? body.message : 'Conflict';
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
