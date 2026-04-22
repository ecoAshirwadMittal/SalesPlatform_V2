import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import {
  loadDashboard,
  saveBid,
  submitBidRound,
  RateLimitedError,
  RoundClosedError,
  VersionConflictError,
  type BidderDashboardResponse,
  type BidDataRow,
  type BidSubmissionResult,
} from './bidder';

/**
 * `apiFetch` is a thin wrapper around the global `fetch`. Rather than stub
 * the wrapper (which would force every caller to know the indirection),
 * we mock `global.fetch` directly — same surface the wrapper ultimately
 * exercises, and matches the apiFetch contract under test.
 */

type FetchMock = ReturnType<typeof vi.fn>;

function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

function emptyResponse(status: number): Response {
  return new Response(null, { status });
}

let fetchMock: FetchMock;

beforeEach(() => {
  fetchMock = vi.fn();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (globalThis as any).fetch = fetchMock;
});

afterEach(() => {
  vi.restoreAllMocks();
});

// ---------------------------------------------------------------------------
// loadDashboard
// ---------------------------------------------------------------------------

describe('loadDashboard', () => {
  const gridBody: BidderDashboardResponse = {
    mode: 'GRID',
    auction: {
      id: 10,
      auctionId: 1,
      auctionTitle: 'Auction 2026 / Wk17',
      round: 1,
      roundName: 'Round 1',
      status: 'Started',
    },
    bidRound: {
      id: 100,
      schedulingAuctionId: 10,
      round: 1,
      roundStatus: 'Started',
      startDatetime: '2026-04-21T16:00:00Z',
      endDatetime: '2026-04-25T07:00:00Z',
      submitted: false,
      submittedDatetime: null,
    },
    rows: [
      {
        id: 501,
        bidRoundId: 100,
        ecoid: 'ECO-001',
        mergedGrade: 'Good',
        buyerCodeType: 'Wholesale',
        bidQuantity: 5,
        bidAmount: 120.5,
        targetPrice: 100,
        maximumQuantity: 10,
        payout: 602.5,
        submittedBidQuantity: null,
        submittedBidAmount: null,
        lastValidBidQuantity: null,
        lastValidBidAmount: null,
        submittedDatetime: null,
        changedDate: '2026-04-21T16:05:00Z',
      },
    ],
    totals: { rowCount: 1, totalBidAmount: 120.5, totalPayout: 602.5, totalBidQuantity: 5 },
    timer: {
      now: '2026-04-21T16:00:00Z',
      startsAt: '2026-04-21T16:00:00Z',
      endsAt: '2026-04-25T07:00:00Z',
      secondsUntilStart: 0,
      secondsUntilEnd: 327600,
      active: true,
    },
  };

  it('parses the GRID response and hits the correct URL', async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse(200, gridBody));

    const result = await loadDashboard(42);

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/bidder/dashboard?buyerCodeId=42');
    expect(init).toMatchObject({ credentials: 'include' });
    expect(result.mode).toBe('GRID');
    expect(result.rows).toHaveLength(1);
    expect(result.rows[0].id).toBe(501);
  });

  it('returns the ERROR_AUCTION_NOT_FOUND body on 404 instead of throwing', async () => {
    const errorBody: BidderDashboardResponse = {
      mode: 'ERROR_AUCTION_NOT_FOUND',
      auction: null,
      bidRound: null,
      rows: [],
      totals: null,
      timer: null,
    };
    fetchMock.mockResolvedValueOnce(jsonResponse(404, errorBody));

    const result = await loadDashboard(42);

    expect(result.mode).toBe('ERROR_AUCTION_NOT_FOUND');
    expect(result.rows).toEqual([]);
  });
});

// ---------------------------------------------------------------------------
// saveBid
// ---------------------------------------------------------------------------

describe('saveBid', () => {
  const rowBody: BidDataRow = {
    id: 501,
    bidRoundId: 100,
    ecoid: 'ECO-001',
    mergedGrade: 'Good',
    buyerCodeType: 'Wholesale',
    bidQuantity: 5,
    bidAmount: 120.5,
    targetPrice: 100,
    maximumQuantity: 10,
    payout: 602.5,
    submittedBidQuantity: null,
    submittedBidAmount: null,
    lastValidBidQuantity: null,
    lastValidBidAmount: null,
    submittedDatetime: null,
    changedDate: '2026-04-21T16:05:00Z',
  };

  it('sends PUT with JSON body and parses the row on 200', async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse(200, rowBody));

    const result = await saveBid(501, { bidQuantity: 5, bidAmount: 120.5 });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/bidder/bid-data/501');
    expect(init.method).toBe('PUT');
    expect(init.body).toBe(JSON.stringify({ bidQuantity: 5, bidAmount: 120.5 }));
    expect(result.id).toBe(501);
  });

  it('throws RateLimitedError on 429', async () => {
    fetchMock.mockResolvedValueOnce(emptyResponse(429));

    await expect(saveBid(501, { bidQuantity: 5, bidAmount: 120.5 })).rejects.toBeInstanceOf(
      RateLimitedError,
    );
  });

  it('throws a generic Error on non-200/non-429 responses', async () => {
    fetchMock.mockResolvedValueOnce(emptyResponse(500));

    await expect(saveBid(501, { bidQuantity: 5, bidAmount: 120.5 })).rejects.toThrow('HTTP 500');
  });
});

// ---------------------------------------------------------------------------
// submitBidRound
// ---------------------------------------------------------------------------

describe('submitBidRound', () => {
  const submissionBody: BidSubmissionResult = {
    bidRoundId: 100,
    rowCount: 42,
    submittedDatetime: '2026-04-25T06:59:00Z',
    resubmit: false,
  };

  it('POSTs to the submit URL with buyerCodeId in the query string', async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse(200, submissionBody));

    const result = await submitBidRound(100, 777);

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/bidder/bid-rounds/100/submit?buyerCodeId=777');
    expect(init.method).toBe('POST');
    expect(result.rowCount).toBe(42);
    expect(result.resubmit).toBe(false);
  });

  it('throws RoundClosedError on 409 with code ROUND_CLOSED', async () => {
    fetchMock.mockResolvedValueOnce(
      jsonResponse(409, { code: 'ROUND_CLOSED', message: 'Round already closed' }),
    );

    await expect(submitBidRound(100, 777)).rejects.toBeInstanceOf(RoundClosedError);
  });

  it('throws VersionConflictError on 409 with code VERSION_CONFLICT', async () => {
    fetchMock.mockResolvedValueOnce(
      jsonResponse(409, { code: 'VERSION_CONFLICT', message: 'Concurrent modification' }),
    );

    await expect(submitBidRound(100, 777)).rejects.toBeInstanceOf(VersionConflictError);
  });

  it('throws a generic Error on 409 with an unknown code', async () => {
    fetchMock.mockResolvedValueOnce(
      jsonResponse(409, { code: 'SOME_FUTURE_CODE', message: 'Unknown conflict' }),
    );

    await expect(submitBidRound(100, 777)).rejects.toThrow('HTTP 409');
  });
});
