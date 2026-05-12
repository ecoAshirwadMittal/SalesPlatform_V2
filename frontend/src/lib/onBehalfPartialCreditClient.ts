import { z } from 'zod';
import { apiFetch } from './apiFetch';
import {
  CreditRequestDetailSchema,
  type CreditRequestDetail,
} from './partialCreditClient';

/**
 * Typed client for the sales-rep on-behalf endpoints
 * ({@code /api/v1/salesrep/partial-credit/**}). Mirrors the convention
 * of the other admin/buyer clients: Zod schemas at the boundary, one
 * function per HTTP verb.
 */

export const BuyerCodeOptionSchema = z.object({
  id: z.number(),
  code: z.string(),
  buyerName: z.string().nullable(),
});
export type BuyerCodeOption = z.infer<typeof BuyerCodeOptionSchema>;

export const BuyerUserOptionSchema = z.object({
  userId: z.number(),
  displayName: z.string(),
  email: z.string().nullable(),
});
export type BuyerUserOption = z.infer<typeof BuyerUserOptionSchema>;

const BASE = '/api/v1/salesrep/partial-credit';

export async function listEligibleBuyerCodes(): Promise<BuyerCodeOption[]> {
  const res = await apiFetch(`${BASE}/buyer-codes`);
  if (!res.ok) {
    throw new Error(`listEligibleBuyerCodes failed: HTTP ${res.status}`);
  }
  return z.array(BuyerCodeOptionSchema).parse(await res.json());
}

export async function listBuyersForCode(
  buyerCodeId: number,
): Promise<BuyerUserOption[]> {
  const res = await apiFetch(`${BASE}/buyer-codes/${buyerCodeId}/users`);
  if (!res.ok) {
    throw new Error(`listBuyersForCode failed: HTTP ${res.status}`);
  }
  return z.array(BuyerUserOptionSchema).parse(await res.json());
}

export async function createDraftOnBehalf(
  orderNumber: string,
  buyerCodeId: number,
  onBehalfOfUserId: number,
): Promise<CreditRequestDetail> {
  const res = await apiFetch(`${BASE}/drafts`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ orderNumber, buyerCodeId, onBehalfOfUserId }),
  });
  if (!res.ok) {
    const errorBody = (await res.json().catch(() => null)) as
      | { message?: string }
      | null;
    throw new Error(
      errorBody?.message ?? `createDraftOnBehalf failed: HTTP ${res.status}`,
    );
  }
  return CreditRequestDetailSchema.parse(await res.json());
}
