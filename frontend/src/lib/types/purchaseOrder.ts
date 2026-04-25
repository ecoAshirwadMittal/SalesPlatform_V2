export type PurchaseOrderLifecycleState = "DRAFT" | "ACTIVE" | "CLOSED";

export interface PurchaseOrderRow {
  id: number;
  weekFromId: number;
  weekFromLabel: string;
  weekToId: number;
  weekToLabel: string;
  weekRangeLabel: string;
  state: PurchaseOrderLifecycleState;
  totalRecords: number;
  poRefreshTimestamp: string | null;
  changedDate: string;
  changedByUsername: string | null;
}

export interface PurchaseOrderListResponse {
  items: PurchaseOrderRow[];
  total: number;
  page: number;
  size: number;
}

export interface PurchaseOrderRequest {
  weekFromId: number;
  weekToId: number;
}

export interface PODetailRow {
  id: number;
  purchaseOrderId: number;
  buyerCodeId: number;
  buyerCode: string;
  productId: string;
  grade: string;
  modelName: string | null;
  price: string;          // BigDecimal serialized as string
  qtyCap: number | null;
  priceFulfilled: string | null;
  qtyFulfilled: number | null;
}

export interface PODetailListResponse {
  items: PODetailRow[];
  total: number;
  page: number;
  size: number;
}

export interface PODetailUploadResult {
  createdCount: number;
  deletedCount: number;
  skippedCount: number;
  errors: Array<{
    rowNumber: number;
    productId: string;
    grade: string;
    buyerCode: string;
    reason: string;
  }>;
}

export interface ApiError {
  code: string;
  message: string;
  details: string[];
}
