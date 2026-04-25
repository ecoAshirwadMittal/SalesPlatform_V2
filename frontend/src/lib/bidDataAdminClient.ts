/**
 * Thin client for the P8 Lane 3A admin Bid Data endpoints.
 *
 * Contract:
 *   GET    /api/v1/admin/bid-data?bidRoundId=&buyerCodeId=&submittedBidAmountGt=
 *   DELETE /api/v1/admin/bid-data/{id}
 *
 * Both endpoints require Administrator or SalesOps; the client just forwards
 * cookies (`credentials: 'include'`) — auth lives in the backend filter.
 */

export interface BidDataAdminRow {
  id: number;
  bidRoundId: number;
  buyerCodeId: number;
  ecoid: string | null;
  mergedGrade: string | null;
  bidQuantity: number | null;
  bidAmount: string | null;
  submittedBidQuantity: number | null;
  submittedBidAmount: string | null;
  submittedDatetime: string | null;
  changedDate: string | null;
  deprecated: boolean;
}

export interface BidDataAdminListResponse {
  rows: BidDataAdminRow[];
  total: number;
}

const BASE = '/api/v1/admin/bid-data';

async function request<T>(method: string, path: string, init: RequestInit = {}): Promise<T> {
  const res = await fetch(path, {
    method,
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    ...init,
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(body.message || `HTTP ${res.status}`);
  }
  return (res.status === 204 ? undefined : await res.json()) as T;
}

export interface BidDataListParams {
  bidRoundId?: number;
  buyerCodeId?: number;
  submittedBidAmountGt0?: boolean;
}

export const bidDataAdminClient = {
  list: (params: BidDataListParams = {}): Promise<BidDataAdminListResponse> => {
    const q = new URLSearchParams();
    if (params.bidRoundId != null) q.set('bidRoundId', String(params.bidRoundId));
    if (params.buyerCodeId != null) q.set('buyerCodeId', String(params.buyerCodeId));
    if (params.submittedBidAmountGt0) q.set('submittedBidAmountGt', '1');
    const qs = q.toString();
    return request<BidDataAdminListResponse>('GET', qs ? `${BASE}?${qs}` : BASE);
  },
  remove: (id: number): Promise<void> => request<void>('DELETE', `${BASE}/${id}`),
};
