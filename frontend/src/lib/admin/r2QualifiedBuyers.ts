import { apiFetch } from '../apiFetch';

/**
 * One row in the R2 qualified-buyer-codes admin grid (gap H10).
 * Shape mirrors {@code R2QualifiedBuyerRow.java}.
 */
export interface R2QualifiedBuyerRow {
  buyerCodeId: number;
  code: string;
  buyerCodeType: string;
  companyName: string;
  qualificationType: string;
  included: boolean;
  isSpecialTreatment: boolean;
}

export interface R2QualifiedBuyersResponse {
  auctionId: number;
  auctionTitle: string;
  r2SchedulingAuctionId: number;
  r2InitStatus: string | null;
  totalRows: number;
  qualifiedCount: number;
  specialTreatmentCount: number;
  notQualifiedCount: number;
  rows: R2QualifiedBuyerRow[];
}

export async function fetchR2QualifiedBuyers(
  auctionId: number,
): Promise<R2QualifiedBuyersResponse> {
  const res = await apiFetch(
    `/api/v1/admin/auctions/${auctionId}/r2-qualified-buyers`,
  );
  if (!res.ok) {
    throw new Error(`Failed to load R2 qualified buyers: HTTP ${res.status}`);
  }
  return (await res.json()) as R2QualifiedBuyersResponse;
}
