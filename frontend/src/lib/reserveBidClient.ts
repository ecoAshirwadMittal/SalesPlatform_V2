import type {
  ReserveBidListResponse,
  ReserveBidRow,
  ReserveBidRequest,
  ReserveBidUploadResult,
  ReserveBidAuditResponse,
  ReserveBidSyncStatus,
} from "./reserveBidTypes";

const BASE = "/api/v1/admin/reserve-bids";

async function req<T>(method: string, path: string, body?: unknown, init: RequestInit = {}): Promise<T> {
  const res = await fetch(path, {
    method,
    credentials: "include",
    headers: body instanceof FormData ? {} : { "Content-Type": "application/json" },
    body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
    ...init,
  });
  if (!res.ok) {
    const errJson = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(errJson.message || `HTTP ${res.status}`);
  }
  return (res.status === 204 ? undefined : await res.json()) as T;
}

export const reserveBidClient = {
  list: (params: { productId?: string; grade?: string; page?: number; size?: number } = {}) => {
    const q = new URLSearchParams(
      Object.entries(params).filter(([, v]) => v != null && v !== "").map(([k, v]) => [k, String(v)])
    );
    return req<ReserveBidListResponse>("GET", `${BASE}?${q}`);
  },
  get: (id: number) => req<ReserveBidRow>("GET", `${BASE}/${id}`),
  create: (body: ReserveBidRequest) => req<ReserveBidRow>("POST", BASE, body),
  update: (id: number, body: ReserveBidRequest) => req<ReserveBidRow>("PUT", `${BASE}/${id}`, body),
  remove: (id: number) => req<void>("DELETE", `${BASE}/${id}`),
  upload: (file: File) => {
    const fd = new FormData();
    fd.append("file", file);
    return req<ReserveBidUploadResult>("POST", `${BASE}/upload`, fd);
  },
  download: () =>
    fetch(`${BASE}/download`, { credentials: "include" }).then((r) => {
      if (!r.ok) throw new Error(`Download failed: ${r.status}`);
      return r.blob();
    }),
  audit: (id: number, page = 0, size = 20) =>
    req<ReserveBidAuditResponse>("GET", `${BASE}/${id}/audit?page=${page}&size=${size}`),
  syncStatus: () => req<ReserveBidSyncStatus>("GET", `${BASE}/sync`),
  triggerSync: () => req<{ rowsFetched: number }>("POST", `${BASE}/sync`),
};
