export interface ReserveBidRow {
  id: number;
  productId: string;
  grade: string;
  brand: string | null;
  model: string | null;
  bid: string;                  // BigDecimal serialized as string
  lastUpdateDatetime: string;
  lastAwardedMinPrice: string | null;
  lastAwardedWeek: string | null;
  bidValidWeekDate: string | null;
  changedDate: string;
}

export interface ReserveBidRequest {
  productId: string;
  grade: string;
  brand?: string | null;
  model?: string | null;
  bid: string;
  lastAwardedMinPrice?: string | null;
  lastAwardedWeek?: string | null;
  bidValidWeekDate?: string | null;
}

export interface ReserveBidListResponse {
  rows: ReserveBidRow[];
  total: number;
  page: number;
  size: number;
}

export interface UploadError {
  rowNumber: number;
  productId: string | null;
  grade: string | null;
  reason: string;
}

export interface ReserveBidUploadResult {
  created: number;
  updated: number;
  unchanged: number;
  auditsGenerated: number;
  errors: UploadError[];
}

export interface ReserveBidAuditRow {
  id: number;
  reserveBidId: number;
  productId: string;
  grade: string;
  oldPrice: string;
  newPrice: string;
  createdDate: string;
  changedByUsername: string | null;
}

export interface ReserveBidAuditResponse {
  rows: ReserveBidAuditRow[];
  total: number;
  page: number;
  size: number;
}

export interface ReserveBidSyncStatus {
  lastSyncDatetime: string | null;
  sourceMaxDatetime: string | null;
  drift: string | null;
  state: "IN_SYNC" | "BEHIND_SOURCE" | "NEVER_SYNCED";
}
