/**
 * Pure helper functions extracted from OrderHistoryPage.
 * Isolated here so they can be unit-tested without a DOM or React environment.
 */

export interface OrderHistoryResponse {
  id: number;
  orderNumber: string | null;
  offerDate: string | null;
  orderDate: string | null;
  orderStatus: string | null;
  shipDate: string | null;
  shipMethod: string | null;
  skuCount: number;
  totalQuantity: number;
  totalPrice: number;
  buyer: string | null;
  company: string | null;
  lastUpdateDate: string | null;
  offerOrderType: string | null;
  offerId: number;
}

export type SortField = keyof OrderHistoryResponse;
export type SortDir = 'asc' | 'desc';

// ── Formatting helpers ──

/**
 * Format an ISO date string as M/D/YYYY (US locale, no padding).
 * Returns empty string for null/undefined/empty input.
 */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', {
    month: 'numeric',
    day: 'numeric',
    year: 'numeric',
  });
}

/**
 * Format a numeric value as a USD currency string (e.g. "$1,234.56").
 * Returns "$0.00" for null/undefined.
 */
export function formatCurrency(val: number | null | undefined): string {
  if (val == null) return '$0.00';
  return (
    '$' +
    val.toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })
  );
}

/**
 * Known external status values returned by the view's CASE mapping.
 * These are already user-friendly — no further transformation needed.
 */
const KNOWN_EXTERNAL_STATUSES = new Set([
  'In Process',
  'Offer Pending',
  'Awaiting Carrier Pickup',
  'Shipped',
  'Order Cancelled',
  'Offer Declined',
  'Draft',
  'Submitted',
]);

/**
 * Convert an order status value to the external-facing display label.
 * The view now returns Mendix-compatible external status text directly
 * (e.g. "In Process", "Shipped"), so known values pass through unchanged.
 * Unknown values get underscores replaced with spaces as a fallback.
 * Returns empty string for null/undefined.
 */
export function formatStatus(status: string | null | undefined): string {
  if (!status) return '';
  if (KNOWN_EXTERNAL_STATUSES.has(status)) return status;
  return status.replace(/_/g, ' ');
}

/**
 * Map an order status string to a CSS module class key.
 * Only 3 statuses get colored badges (matching QA exactly):
 * - Offer Pending → darkorange
 * - Shipped → seagreen
 * - Order Cancelled → red
 * All other statuses render as plain text (no badge).
 */
export type StatusKey =
  | 'statusOfferPending'
  | 'statusShipped'
  | 'statusCancelled'
  | 'statusNone';

export function statusClassKey(status: string | null | undefined): StatusKey {
  if (!status) return 'statusNone';
  switch (status) {
    case 'Offer Pending':
      return 'statusOfferPending';
    case 'Shipped':
      return 'statusShipped';
    case 'Order Cancelled':
      return 'statusCancelled';
    default:
      return 'statusNone';
  }
}

// ── Sorting helper ──

/**
 * Sort a copy of an orders array by the given field and direction.
 * Null values always sort to the end regardless of direction.
 * Returns an independent copy — the original array is not mutated.
 */
export function sortOrders(
  orders: OrderHistoryResponse[],
  sortField: SortField | null,
  sortDir: SortDir,
): OrderHistoryResponse[] {
  if (!sortField) return [...orders];

  return [...orders].sort((a, b) => {
    const aVal = a[sortField];
    const bVal = b[sortField];

    // Nulls always go to the end
    if (aVal == null && bVal == null) return 0;
    if (aVal == null) return 1;
    if (bVal == null) return -1;

    if (typeof aVal === 'number' && typeof bVal === 'number') {
      return sortDir === 'asc' ? aVal - bVal : bVal - aVal;
    }

    const cmp = String(aVal).localeCompare(String(bVal));
    return sortDir === 'asc' ? cmp : -cmp;
  });
}

// ── Pagination helper ──

/**
 * Compute the human-readable "X to Y of Z" range label for a page.
 * Returns "0 of 0" when totalElements is 0.
 */
export function paginationLabel(
  page: number,
  pageSize: number,
  totalElements: number,
): string {
  if (totalElements === 0) return '0 of 0';
  const startRow = page * pageSize + 1;
  const endRow = Math.min((page + 1) * pageSize, totalElements);
  return `${startRow} to ${endRow} of ${totalElements}`;
}

// ── userId extraction helper ──

/**
 * Parse a userId from the raw JSON string stored in localStorage under 'auth_user'.
 * Returns null if the string is absent, malformed, or the userId field is missing.
 */
export function parseUserId(stored: string | null): number | null {
  if (!stored) return null;
  try {
    const parsed = JSON.parse(stored);
    return typeof parsed?.userId === 'number' ? parsed.userId : null;
  } catch {
    return null;
  }
}
