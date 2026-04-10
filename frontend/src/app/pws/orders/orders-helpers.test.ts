import { describe, it, expect } from 'vitest';
import {
  formatDate,
  formatCurrency,
  formatStatus,
  statusClassKey,
  sortOrders,
  paginationLabel,
  parseUserId,
  type OrderHistoryResponse,
} from './orders-helpers';

// ─────────────────────────────────────────────
// formatDate
// ─────────────────────────────────────────────

describe('formatDate', () => {
  it('returns empty string for null', () => {
    expect(formatDate(null)).toBe('');
  });

  it('returns empty string for undefined', () => {
    expect(formatDate(undefined)).toBe('');
  });

  it('returns empty string for empty string', () => {
    expect(formatDate('')).toBe('');
  });

  it('formats a full ISO datetime string to M/D/YYYY', () => {
    // Use a UTC noon timestamp to avoid timezone-boundary flips on any host
    const result = formatDate('2024-03-15T12:00:00Z');
    // Accept "3/15/2024" regardless of locale separator style
    expect(result).toMatch(/3.15.2024/);
  });

  it('formats a date-only string (UTC noon to avoid timezone boundary)', () => {
    // Use T12:00:00Z so the rendered date is Jan 5 in every UTC offset zone
    const result = formatDate('2024-01-05T12:00:00Z');
    expect(result).toMatch(/1.5.2024/);
  });

  it('includes numeric month and day without leading zeros', () => {
    const result = formatDate('2025-06-09T12:00:00Z');
    // Should be "6/9/2025", not "06/09/2025"
    expect(result).not.toMatch(/^0/);
    expect(result).toMatch(/6.9.2025/);
  });
});

// ─────────────────────────────────────────────
// formatCurrency
// ─────────────────────────────────────────────

describe('formatCurrency', () => {
  it('returns "$0.00" for null', () => {
    expect(formatCurrency(null)).toBe('$0.00');
  });

  it('returns "$0.00" for undefined', () => {
    expect(formatCurrency(undefined)).toBe('$0.00');
  });

  it('returns "$0.00" for zero', () => {
    expect(formatCurrency(0)).toBe('$0.00');
  });

  it('formats a whole number with two decimal places', () => {
    expect(formatCurrency(100)).toBe('$100.00');
  });

  it('formats a decimal value', () => {
    expect(formatCurrency(99.9)).toBe('$99.90');
  });

  it('formats a value already at two decimal places', () => {
    expect(formatCurrency(1234.56)).toBe('$1,234.56');
  });

  it('includes thousands separator', () => {
    expect(formatCurrency(10000)).toBe('$10,000.00');
  });

  it('handles large values', () => {
    expect(formatCurrency(1_000_000)).toBe('$1,000,000.00');
  });

  it('rounds to two decimal places', () => {
    // 1.005 rounds to $1.01 in most locales due to float representation;
    // assert that the result always has exactly two decimals
    const result = formatCurrency(1.005);
    expect(result).toMatch(/^\$\d+\.\d{2}$/);
  });
});

// ─────────────────────────────────────────────
// formatStatus
// ─────────────────────────────────────────────

describe('formatStatus', () => {
  it('returns empty string for null', () => {
    expect(formatStatus(null)).toBe('');
  });

  it('returns empty string for undefined', () => {
    expect(formatStatus(undefined)).toBe('');
  });

  it('returns empty string for empty string', () => {
    expect(formatStatus('')).toBe('');
  });

  it('passes through known external statuses unchanged', () => {
    expect(formatStatus('In Process')).toBe('In Process');
    expect(formatStatus('Offer Pending')).toBe('Offer Pending');
    expect(formatStatus('Shipped')).toBe('Shipped');
    expect(formatStatus('Order Cancelled')).toBe('Order Cancelled');
    expect(formatStatus('Offer Declined')).toBe('Offer Declined');
    expect(formatStatus('Draft')).toBe('Draft');
    expect(formatStatus('Submitted')).toBe('Submitted');
    expect(formatStatus('Awaiting Carrier Pickup')).toBe('Awaiting Carrier Pickup');
  });

  it('replaces underscores with spaces for unknown statuses', () => {
    expect(formatStatus('Some_Custom_Status')).toBe('Some Custom Status');
  });

  it('returns the original string when no underscores and not known', () => {
    expect(formatStatus('Complete')).toBe('Complete');
  });
});

// ─────────────────────────────────────────────
// statusClassKey
// ─────────────────────────────────────────────

describe('statusClassKey', () => {
  it('returns "statusNone" for null', () => {
    expect(statusClassKey(null)).toBe('statusNone');
  });

  it('returns "statusNone" for undefined', () => {
    expect(statusClassKey(undefined)).toBe('statusNone');
  });

  it('returns "statusNone" for empty string', () => {
    expect(statusClassKey('')).toBe('statusNone');
  });

  it('maps "Offer Pending" to statusOfferPending', () => {
    expect(statusClassKey('Offer Pending')).toBe('statusOfferPending');
  });

  it('maps "Shipped" to statusShipped', () => {
    expect(statusClassKey('Shipped')).toBe('statusShipped');
  });

  it('maps "Order Cancelled" to statusCancelled', () => {
    expect(statusClassKey('Order Cancelled')).toBe('statusCancelled');
  });

  it('returns "statusNone" for "In Process"', () => {
    expect(statusClassKey('In Process')).toBe('statusNone');
  });

  it('returns "statusNone" for "Awaiting Carrier Pickup"', () => {
    expect(statusClassKey('Awaiting Carrier Pickup')).toBe('statusNone');
  });

  it('returns "statusNone" for "Draft"', () => {
    expect(statusClassKey('Draft')).toBe('statusNone');
  });

  it('returns "statusNone" for "Submitted"', () => {
    expect(statusClassKey('Submitted')).toBe('statusNone');
  });

  it('returns "statusNone" for "Offer Declined"', () => {
    expect(statusClassKey('Offer Declined')).toBe('statusNone');
  });

  it('returns "statusNone" for an unrecognised status', () => {
    expect(statusClassKey('Unknown_Status')).toBe('statusNone');
  });
});

// ─────────────────────────────────────────────
// sortOrders
// ─────────────────────────────────────────────

function makeOrder(overrides: Partial<OrderHistoryResponse> = {}): OrderHistoryResponse {
  return {
    id: 1,
    orderNumber: null,
    offerDate: null,
    orderDate: null,
    orderStatus: null,
    shipDate: null,
    shipMethod: null,
    skuCount: 0,
    totalQuantity: 0,
    totalPrice: 0,
    buyer: null,
    company: null,
    lastUpdateDate: null,
    offerOrderType: null,
    offerId: 1,
    ...overrides,
  };
}

describe('sortOrders', () => {
  it('returns an independent copy when sortField is null', () => {
    const orders = [makeOrder({ id: 1 }), makeOrder({ id: 2 })];
    const result = sortOrders(orders, null, 'asc');
    expect(result).toHaveLength(2);
    expect(result).not.toBe(orders); // new array reference
  });

  it('does not mutate the original array', () => {
    const orders = [
      makeOrder({ id: 1, totalPrice: 300 }),
      makeOrder({ id: 2, totalPrice: 100 }),
    ];
    const original = [...orders];
    sortOrders(orders, 'totalPrice', 'asc');
    expect(orders[0].totalPrice).toBe(original[0].totalPrice);
  });

  it('sorts numbers ascending', () => {
    const orders = [
      makeOrder({ id: 3, totalPrice: 300 }),
      makeOrder({ id: 1, totalPrice: 100 }),
      makeOrder({ id: 2, totalPrice: 200 }),
    ];
    const result = sortOrders(orders, 'totalPrice', 'asc');
    expect(result.map((o) => o.totalPrice)).toEqual([100, 200, 300]);
  });

  it('sorts numbers descending', () => {
    const orders = [
      makeOrder({ id: 1, totalPrice: 100 }),
      makeOrder({ id: 3, totalPrice: 300 }),
      makeOrder({ id: 2, totalPrice: 200 }),
    ];
    const result = sortOrders(orders, 'totalPrice', 'desc');
    expect(result.map((o) => o.totalPrice)).toEqual([300, 200, 100]);
  });

  it('sorts strings ascending (localeCompare)', () => {
    const orders = [
      makeOrder({ id: 1, buyer: 'Charlie' }),
      makeOrder({ id: 2, buyer: 'Alice' }),
      makeOrder({ id: 3, buyer: 'Bob' }),
    ];
    const result = sortOrders(orders, 'buyer', 'asc');
    expect(result.map((o) => o.buyer)).toEqual(['Alice', 'Bob', 'Charlie']);
  });

  it('sorts strings descending', () => {
    const orders = [
      makeOrder({ id: 1, buyer: 'Alice' }),
      makeOrder({ id: 2, buyer: 'Charlie' }),
      makeOrder({ id: 3, buyer: 'Bob' }),
    ];
    const result = sortOrders(orders, 'buyer', 'desc');
    expect(result.map((o) => o.buyer)).toEqual(['Charlie', 'Bob', 'Alice']);
  });

  it('sorts null values to the end ascending', () => {
    const orders = [
      makeOrder({ id: 1, buyer: null }),
      makeOrder({ id: 2, buyer: 'Alice' }),
      makeOrder({ id: 3, buyer: null }),
      makeOrder({ id: 4, buyer: 'Bob' }),
    ];
    const result = sortOrders(orders, 'buyer', 'asc');
    const buyers = result.map((o) => o.buyer);
    expect(buyers.slice(0, 2)).toEqual(expect.arrayContaining(['Alice', 'Bob']));
    expect(buyers[2]).toBeNull();
    expect(buyers[3]).toBeNull();
  });

  it('sorts null values to the end descending', () => {
    const orders = [
      makeOrder({ id: 1, buyer: 'Alice' }),
      makeOrder({ id: 2, buyer: null }),
    ];
    const result = sortOrders(orders, 'buyer', 'desc');
    expect(result[0].buyer).toBe('Alice');
    expect(result[1].buyer).toBeNull();
  });

  it('handles two nulls as equal (stable relative order)', () => {
    const orders = [
      makeOrder({ id: 1, buyer: null }),
      makeOrder({ id: 2, buyer: null }),
    ];
    const result = sortOrders(orders, 'buyer', 'asc');
    expect(result).toHaveLength(2);
    // Both are null — no crash, just stable
    expect(result.every((o) => o.buyer === null)).toBe(true);
  });

  it('returns empty array when given empty input', () => {
    expect(sortOrders([], 'totalPrice', 'asc')).toEqual([]);
  });

  it('sorts by date string field ascending', () => {
    const orders = [
      makeOrder({ id: 1, orderDate: '2024-03-15' }),
      makeOrder({ id: 2, orderDate: '2023-01-01' }),
      makeOrder({ id: 3, orderDate: '2024-12-31' }),
    ];
    const result = sortOrders(orders, 'orderDate', 'asc');
    expect(result.map((o) => o.orderDate)).toEqual([
      '2023-01-01',
      '2024-03-15',
      '2024-12-31',
    ]);
  });

  it('sorts skuCount (integer) ascending', () => {
    const orders = [
      makeOrder({ id: 1, skuCount: 5 }),
      makeOrder({ id: 2, skuCount: 1 }),
      makeOrder({ id: 3, skuCount: 10 }),
    ];
    const result = sortOrders(orders, 'skuCount', 'asc');
    expect(result.map((o) => o.skuCount)).toEqual([1, 5, 10]);
  });
});

// ─────────────────────────────────────────────
// paginationLabel
// ─────────────────────────────────────────────

describe('paginationLabel', () => {
  it('returns "0 of 0" when totalElements is 0', () => {
    expect(paginationLabel(0, 20, 0)).toBe('0 of 0');
  });

  it('returns correct range for first page (page=0)', () => {
    expect(paginationLabel(0, 20, 100)).toBe('1 to 20 of 100');
  });

  it('returns correct range for second page (page=1)', () => {
    expect(paginationLabel(1, 20, 100)).toBe('21 to 40 of 100');
  });

  it('returns correct range for the last partial page', () => {
    // 45 total, page size 20: last page is page=2, rows 41–45
    expect(paginationLabel(2, 20, 45)).toBe('41 to 45 of 45');
  });

  it('returns correct range when total exactly fills the last page', () => {
    // 40 total, page size 20, page=1 → rows 21–40
    expect(paginationLabel(1, 20, 40)).toBe('21 to 40 of 40');
  });

  it('handles a single-item result set', () => {
    expect(paginationLabel(0, 20, 1)).toBe('1 to 1 of 1');
  });

  it('handles page size of 1', () => {
    expect(paginationLabel(4, 1, 10)).toBe('5 to 5 of 10');
  });

  it('handles large totals', () => {
    expect(paginationLabel(0, 20, 378755)).toBe('1 to 20 of 378755');
  });
});

// ─────────────────────────────────────────────
// parseUserId
// ─────────────────────────────────────────────

describe('parseUserId', () => {
  it('returns null for null input', () => {
    expect(parseUserId(null)).toBeNull();
  });

  it('returns null for empty string', () => {
    expect(parseUserId('')).toBeNull();
  });

  it('returns null for malformed JSON', () => {
    expect(parseUserId('not-json')).toBeNull();
  });

  it('returns null when JSON is valid but userId is missing', () => {
    expect(parseUserId('{"email":"test@example.com"}')).toBeNull();
  });

  it('returns null when userId is a string, not a number', () => {
    expect(parseUserId('{"userId":"42"}')).toBeNull();
  });

  it('returns null when userId is null in the JSON', () => {
    expect(parseUserId('{"userId":null}')).toBeNull();
  });

  it('returns the userId when present as a number', () => {
    expect(parseUserId('{"userId":123}')).toBe(123);
  });

  it('returns userId 0 (falsy number) correctly', () => {
    // 0 is a valid number — must not be treated as absent
    expect(parseUserId('{"userId":0}')).toBe(0);
  });

  it('handles JSON with extra fields', () => {
    expect(parseUserId('{"userId":7,"email":"a@b.com","role":"ADMIN"}')).toBe(7);
  });

  it('returns null when JSON is a bare array', () => {
    expect(parseUserId('[1,2,3]')).toBeNull();
  });

  it('returns null when JSON is a bare number', () => {
    expect(parseUserId('42')).toBeNull();
  });
});
