import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const BidReportRowSchema = z.object({
  id: z.number(),
  bidRoundId: z.number(),
  auctionId: z.number().nullable(),
  schedulingAuctionId: z.number(),
  buyerCodeId: z.number(),
  ecoid: z.string().nullable(),
  mergedGrade: z.string().nullable(),
  buyerCodeType: z.string().nullable(),
  bidQuantity: z.number().nullable(),
  bidAmount: z.number().nullable(),
  submittedBidQuantity: z.number().nullable(),
  submittedBidAmount: z.number().nullable(),
  targetPrice: z.number().nullable(),
  maximumQuantity: z.number().nullable(),
  submittedDatetime: z.string().nullable(),
  changedDate: z.string().nullable(),
});
export type BidReportRow = z.infer<typeof BidReportRowSchema>;

export const BidReportPageResponseSchema = z.object({
  content: z.array(BidReportRowSchema),
  page: z.number().int(),
  pageSize: z.number().int(),
  totalElements: z.number().int(),
  totalPages: z.number().int(),
});
export type BidReportPageResponse = z.infer<typeof BidReportPageResponseSchema>;

export interface BidReportParams {
  auctionId?: number;
  page?: number;
  pageSize?: number;
}

export async function getBidReportR3(params: BidReportParams = {}): Promise<BidReportPageResponse> {
  const qs = new URLSearchParams();
  if (typeof params.auctionId === 'number') qs.set('auctionId', String(params.auctionId));
  qs.set('page', String(params.page ?? 0));
  qs.set('pageSize', String(params.pageSize ?? 20));

  const res = await apiFetch(`/api/v1/admin/bid-reports/r3?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return BidReportPageResponseSchema.parse(await res.json());
}
