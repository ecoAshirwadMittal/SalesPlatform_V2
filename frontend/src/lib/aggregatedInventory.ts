import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const WeekOptionSchema = z.object({
  id: z.number(),
  weekDisplay: z.string(),
  weekStartDateTime: z.string(),
  weekEndDateTime: z.string(),
});
export type WeekOption = z.infer<typeof WeekOptionSchema>;

export const InventoryRowSchema = z.object({
  id: z.number(),
  ecoid2: z.string(),
  mergedGrade: z.string().nullable(),
  brand: z.string().nullable(),
  model: z.string().nullable(),
  name: z.string().nullable(),
  carrier: z.string().nullable(),
  dwTotalQuantity: z.number(),
  dwAvgTargetPrice: z.number(),
  totalQuantity: z.number(),
  avgTargetPrice: z.number(),
});
export type InventoryRow = z.infer<typeof InventoryRowSchema>;

export const InventoryPageResponseSchema = z.object({
  content: z.array(InventoryRowSchema),
  page: z.number(),
  pageSize: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});
export type InventoryPageResponse = z.infer<typeof InventoryPageResponseSchema>;

export const InventoryTotalsSchema = z.object({
  totalQuantity: z.number(),
  totalPayout: z.number(),
  averageTargetPrice: z.number(),
  dwTotalQuantity: z.number(),
  dwTotalPayout: z.number(),
  dwAverageTargetPrice: z.number(),
  lastSyncedAt: z.string().nullable(),
});
export type InventoryTotals = z.infer<typeof InventoryTotalsSchema>;

export async function fetchWeeks(): Promise<WeekOption[]> {
  const res = await apiFetch('/api/v1/admin/inventory/weeks');
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return z.array(WeekOptionSchema).parse(await res.json());
}

export interface InventorySearchParams {
  weekId: number;
  productId?: string;
  grades?: string;
  brand?: string;
  model?: string;
  modelName?: string;
  carrier?: string;
  page: number;
  pageSize: number;
}

export async function fetchInventoryPage(p: InventorySearchParams): Promise<InventoryPageResponse> {
  const qs = new URLSearchParams();
  qs.set('weekId', String(p.weekId));
  qs.set('page', String(p.page));
  qs.set('pageSize', String(p.pageSize));
  if (p.productId)  qs.set('productId', p.productId);
  if (p.grades)     qs.set('grades', p.grades);
  if (p.brand)      qs.set('brand', p.brand);
  if (p.model)      qs.set('model', p.model);
  if (p.modelName)  qs.set('modelName', p.modelName);
  if (p.carrier)    qs.set('carrier', p.carrier);

  const res = await apiFetch(`/api/v1/admin/inventory?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryPageResponseSchema.parse(await res.json());
}

export async function fetchInventoryTotals(weekId: number): Promise<InventoryTotals> {
  const res = await apiFetch(`/api/v1/admin/inventory/totals?weekId=${weekId}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryTotalsSchema.parse(await res.json());
}

export async function updateInventoryRow(
  id: number,
  body: { mergedGrade: string; datawipe: boolean; totalQuantity: number; dwTotalQuantity: number; }
): Promise<InventoryRow> {
  const res = await apiFetch(`/api/v1/admin/inventory/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryRowSchema.parse(await res.json());
}
