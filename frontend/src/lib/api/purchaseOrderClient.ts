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
