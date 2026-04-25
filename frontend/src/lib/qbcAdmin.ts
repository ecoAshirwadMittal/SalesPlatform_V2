import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const QualifiedBuyerCodeRowSchema = z.object({
  id: z.number(),
  schedulingAuctionId: z.number(),
  buyerCodeId: z.number(),
  qualificationType: z.string(),
  included: z.boolean(),
  submitted: z.boolean(),
  submittedDatetime: z.string().nullable(),
  specialTreatment: z.boolean(),
  createdDate: z.string().nullable(),
  changedDate: z.string().nullable(),
});
export type QualifiedBuyerCodeRow = z.infer<typeof QualifiedBuyerCodeRowSchema>;

export const QualifiedBuyerCodePageResponseSchema = z.object({
  content: z.array(QualifiedBuyerCodeRowSchema),
  page: z.number().int(),
  pageSize: z.number().int(),
  totalElements: z.number().int(),
  totalPages: z.number().int(),
});
export type QualifiedBuyerCodePageResponse = z.infer<typeof QualifiedBuyerCodePageResponseSchema>;

export interface ListQbcParams {
  schedulingAuctionId?: number;
  buyerCodeId?: number;
  page?: number;
  pageSize?: number;
}

export async function listQualifiedBuyerCodes(
  params: ListQbcParams = {},
): Promise<QualifiedBuyerCodePageResponse> {
  const qs = new URLSearchParams();
  if (typeof params.schedulingAuctionId === 'number')
    qs.set('schedulingAuctionId', String(params.schedulingAuctionId));
  if (typeof params.buyerCodeId === 'number')
    qs.set('buyerCodeId', String(params.buyerCodeId));
  qs.set('page', String(params.page ?? 0));
  qs.set('pageSize', String(params.pageSize ?? 20));

  const res = await apiFetch(`/api/v1/admin/qualified-buyer-codes?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return QualifiedBuyerCodePageResponseSchema.parse(await res.json());
}

/**
 * PATCH /api/v1/admin/qualified-buyer-codes/{id}/qualify.
 * Sets qualification_type = 'Manual' on the given QBC row.
 */
export async function qualifyBuyerCode(id: number): Promise<QualifiedBuyerCodeRow> {
  const res = await apiFetch(`/api/v1/admin/qualified-buyer-codes/${id}/qualify`, {
    method: 'PATCH',
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return QualifiedBuyerCodeRowSchema.parse(await res.json());
}
