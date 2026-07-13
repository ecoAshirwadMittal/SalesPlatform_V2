// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import type { CreditRequestSummary } from '@/lib/partialCreditClient';

const pushMock = vi.fn();
const replaceMock = vi.fn();

// Return a STABLE router object (like the real useRouter) so the page's
// useEffect(..., [router]) fires exactly once — a fresh object each call would
// re-run the effect on every render and refetch the list spuriously. The cache
// defers pushMock/replaceMock access to first call (vi.mock hoisting-safe).
vi.mock('next/navigation', () => {
  let cached: { push: typeof pushMock; replace: typeof replaceMock } | null = null;
  return {
    useRouter: () => {
      if (!cached) cached = { push: pushMock, replace: replaceMock };
      return cached;
    },
  };
});

vi.mock('@/lib/activeBuyerCode', () => ({
  getActiveBuyerCode: vi.fn(),
}));

vi.mock('@/lib/session', () => ({
  getAuthUser: vi.fn(() => ({ roles: [] })),
}));

vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return { ...actual, listRequests: vi.fn(), deleteRequest: vi.fn() };
});

import { getActiveBuyerCode } from '@/lib/activeBuyerCode';
import { deleteRequest, listRequests } from '@/lib/partialCreditClient';
import PartialCreditLandingPage from './page';

const mockGetActive = getActiveBuyerCode as unknown as ReturnType<typeof vi.fn>;
const mockList = listRequests as unknown as ReturnType<typeof vi.fn>;
const mockDelete = deleteRequest as unknown as ReturnType<typeof vi.fn>;

function row(overrides: Partial<CreditRequestSummary>): CreditRequestSummary {
  return {
    id: 1,
    requestNumber: 'PCR-1',
    orderNumber: 'SO-1',
    systemStatus: 'DRAFT',
    displayStatus: 'Draft',
    requestDate: '2026-05-01',
    submittedDate: null,
    hasMissingDevice: true,
    hasWrongDevice: false,
    hasEncumberedDevice: false,
    totalDevices: 0,
    requestedTotal: null,
    ...overrides,
  };
}

const draftRow = row({
  id: 10,
  requestNumber: 'PCR-10',
  orderNumber: 'SO-DRAFT',
  systemStatus: 'DRAFT',
  displayStatus: 'Draft',
});
const submittedRow = row({
  id: 11,
  requestNumber: 'PCR-11',
  orderNumber: 'SO-SUBMITTED',
  systemStatus: 'PENDING_APPROVAL',
  displayStatus: 'Pending Approval',
  submittedDate: '2026-05-02',
});

describe('PartialCreditLandingPage — draft delete affordance (gap 2.5)', () => {
  beforeEach(() => {
    pushMock.mockReset();
    replaceMock.mockReset();
    mockGetActive.mockReset();
    mockList.mockReset();
    mockDelete.mockReset();
    mockGetActive.mockReturnValue({ id: 7, code: '20399', buyerName: 'Acme' });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders a delete control ONLY on DRAFT rows', async () => {
    mockList.mockResolvedValueOnce([draftRow, submittedRow]);

    render(<PartialCreditLandingPage />);

    await waitFor(() => expect(screen.getByText('SO-DRAFT')).toBeInTheDocument());
    expect(screen.getByText('SO-SUBMITTED')).toBeInTheDocument();

    // DRAFT row → delete affordance present; submitted row → absent.
    expect(
      screen.getByRole('button', { name: /Delete draft credit request PCR-10/i }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole('button', { name: /Delete draft credit request PCR-11/i }),
    ).not.toBeInTheDocument();
  });

  it('confirms, calls deleteRequest, then reloads the list', async () => {
    mockList
      .mockResolvedValueOnce([draftRow, submittedRow])
      .mockResolvedValueOnce([submittedRow]);
    mockDelete.mockResolvedValueOnce(undefined);
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);

    render(<PartialCreditLandingPage />);

    await waitFor(() => expect(screen.getByText('SO-DRAFT')).toBeInTheDocument());
    fireEvent.click(
      screen.getByRole('button', { name: /Delete draft credit request PCR-10/i }),
    );

    await waitFor(() => {
      expect(confirmSpy).toHaveBeenCalled();
      expect(mockDelete).toHaveBeenCalledWith(10);
      // Re-fetched the list after the delete (initial load + post-delete reload).
      expect(mockList).toHaveBeenCalledTimes(2);
    });
    await waitFor(() => expect(screen.queryByText('SO-DRAFT')).not.toBeInTheDocument());
  });

  it('does nothing when the confirm dialog is dismissed', async () => {
    mockList.mockResolvedValueOnce([draftRow, submittedRow]);
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(false);

    render(<PartialCreditLandingPage />);

    await waitFor(() => expect(screen.getByText('SO-DRAFT')).toBeInTheDocument());
    fireEvent.click(
      screen.getByRole('button', { name: /Delete draft credit request PCR-10/i }),
    );

    expect(confirmSpy).toHaveBeenCalled();
    expect(mockDelete).not.toHaveBeenCalled();
    expect(mockList).toHaveBeenCalledTimes(1);
  });
});
