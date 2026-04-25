import { apiFetch } from '../apiFetch';

/**
 * Buyer-code list returned by `GET /api/v1/auth/buyer-codes`.
 *
 * Reuses the existing endpoint rather than introducing a new admin variant —
 * `BuyerCodeService.getBuyerCodesForUser` already returns Premium_Wholesale
 * + Wholesale codes for non-Bidder users (Admins, SalesOps), which is the
 * exact set BidAsBidder needs to impersonate from. Codes are filtered to
 * AUCTION-side on the consumer for the impersonation picker.
 */
export interface BuyerCodeSummary {
  id: number;
  code: string;
  buyerName: string;
  buyerCodeType: string;
  /** Derived: 'PWS' for Premium_Wholesale, 'AUCTION' for everything else. */
  codeType: 'PWS' | 'AUCTION';
}

/**
 * Fetch all buyer codes the current user is authorised to see.
 * Admin/SalesOps see the full Premium_Wholesale + Wholesale set; bidders
 * see only the codes linked to their own buyers.
 */
export async function listBuyerCodes(): Promise<BuyerCodeSummary[]> {
  const res = await apiFetch('/api/v1/auth/buyer-codes');
  if (!res.ok) {
    throw new Error(`Failed to load buyer codes: HTTP ${res.status}`);
  }
  return (await res.json()) as BuyerCodeSummary[];
}

/**
 * Convenience filter — returns auction-side codes only (Wholesale,
 * Data_Wipe, etc.) excluding PWS. BidAsBidder routes only into the
 * auction-side bidder dashboard so PWS codes are out of scope.
 */
export function filterAuctionCodes(codes: BuyerCodeSummary[]): BuyerCodeSummary[] {
  return codes.filter((c) => c.codeType === 'AUCTION');
}
