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
}): Promise<PurchaseOrderListResponse> {
  const qs = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null) qs.set(k, String(v));
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
