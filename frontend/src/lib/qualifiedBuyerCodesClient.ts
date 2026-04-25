/**
 * Thin client for the P8 Lane 3B admin Qualified Buyer Codes endpoints.
 *
 * Contract:
 *   GET   /api/v1/admin/qualified-buyer-codes?schedulingAuctionId=
 *   PATCH /api/v1/admin/qualified-buyer-codes/{id}  body: { included: bool }
 *
 * The PATCH side-effect is described in the P8 plan: setting `included` via
 * this endpoint also flips `qualification_type='Manual'`. The client treats
 * the response as the new server-side truth and propagates it back to the
 * caller for optimistic-update reconciliation.
 */

export interface QualifiedBuyerCodeAdminRow {
  id: number;
  schedulingAuctionId: number;
  buyerCodeId: number;
  buyerCode: string | null;
  qualificationType: string;
  included: boolean;
  specialTreatment: boolean;
}

export interface QualifiedBuyerCodeAdminListResponse {
  rows: QualifiedBuyerCodeAdminRow[];
  total: number;
}

const BASE = '/api/v1/admin/qualified-buyer-codes';

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const res = await fetch(path, {
    method,
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) {
    const errBody = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(errBody.message || `HTTP ${res.status}`);
  }
  return (res.status === 204 ? undefined : await res.json()) as T;
}

export const qualifiedBuyerCodesClient = {
  list: (schedulingAuctionId: number): Promise<QualifiedBuyerCodeAdminListResponse> =>
    request<QualifiedBuyerCodeAdminListResponse>(
      'GET',
      `${BASE}?schedulingAuctionId=${schedulingAuctionId}`,
    ),
  updateIncluded: (id: number, included: boolean): Promise<QualifiedBuyerCodeAdminRow> =>
    request<QualifiedBuyerCodeAdminRow>('PATCH', `${BASE}/${id}`, { included }),
};
