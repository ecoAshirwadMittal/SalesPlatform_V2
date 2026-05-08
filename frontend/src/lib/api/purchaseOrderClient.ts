import type {
  PODetailListResponse,
  PODetailUploadResult,
  PurchaseOrderListResponse,
  PurchaseOrderRequest,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

const BASE = "/api/v1/admin/purchase-orders";

async function jsonOrThrow<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let body: unknown = null;
    try { body = await res.json(); } catch { /* ignore */ }
    const message = (body as { message?: string })?.message ?? res.statusText;
    throw Object.assign(new Error(message), { status: res.status, body });
  }
  return res.status === 204 ? (undefined as T) : res.json();
}

export async function listPurchaseOrders(params: {
  page?: number; size?: number;
  weekFromId?: number; weekToId?: number;
  yearFrom?: number; yearTo?: number;
  /**
   * Spring Data sort spec, e.g. "id,desc" or "weekFrom.id,asc". Multiple
   * may be passed (joined here into one comma-separated value, which Spring
   * splits back out). Caller is responsible for using property paths the
   * underlying entity actually exposes — derived columns like `state`
   * cannot be sorted server-side.
   */
  sort?: string | string[];
}): Promise<PurchaseOrderListResponse> {
  const qs = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v === undefined || v === null) continue;
    if (k === "sort" && Array.isArray(v)) {
      for (const s of v) qs.append("sort", s);
    } else {
      qs.set(k, String(v));
    }
  }
  return jsonOrThrow(await fetch(`${BASE}?${qs}`, { credentials: "include" }));
}

export async function getPurchaseOrder(id: number): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, { credentials: "include" }));
}

/**
 * Lookup-by-exact-range for the new landing. Returns every PO whose
 * (weekFromId, weekToId) matches; caller branches on cardinality:
 * 0 → empty state; 1 → render; 2+ → config error.
 */
export async function findPosByRange(
  weekFromId: number, weekToId: number,
): Promise<{ matches: PurchaseOrderRow[] }> {
  const qs = new URLSearchParams({
    weekFromId: String(weekFromId),
    weekToId: String(weekToId),
  });
  return jsonOrThrow(await fetch(`${BASE}/by-range?${qs}`, { credentials: "include" }));
}

/**
 * Most recent PO globally, used as the landing's default selection.
 * Sorted by changedDate desc by the backend.
 */
export async function findMostRecentPurchaseOrder(): Promise<PurchaseOrderRow | null> {
  const r = await listPurchaseOrders({ page: 0, size: 1, sort: "changedDate,desc" });
  return r.items[0] ?? null;
}

/**
 * All POs for the landing's PO picker dropdown. Caps at 500 — well above
 * the historical PO volume per the migration data (V21 imported a few
 * dozen) but high enough that future usage stays comfortably bounded.
 * Sorted newest-changed first so the dropdown opens with the relevant
 * ones at the top.
 */
export async function listAllPurchaseOrders(): Promise<PurchaseOrderRow[]> {
  const r = await listPurchaseOrders({ page: 0, size: 500, sort: "changedDate,desc" });
  return r.items;
}

export async function createPurchaseOrder(req: PurchaseOrderRequest): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(BASE, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  }));
}

export async function updatePurchaseOrder(id: number, req: PurchaseOrderRequest): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  }));
}

export async function deletePurchaseOrder(id: number): Promise<void> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, {
    method: "DELETE",
    credentials: "include",
  }));
}

export async function listPoDetails(id: number, page = 0, size = 50): Promise<PODetailListResponse> {
  return jsonOrThrow(await fetch(`${BASE}/${id}/details?page=${page}&size=${size}`,
      { credentials: "include" }));
}

export async function uploadPoDetails(id: number, file: File): Promise<PODetailUploadResult> {
  const fd = new FormData();
  fd.append("file", file);
  return jsonOrThrow(await fetch(`${BASE}/${id}/details/upload`, {
    method: "POST", credentials: "include", body: fd,
  }));
}

export function downloadPoDetailsUrl(id: number): string {
  return `${BASE}/${id}/details/download`;
}
