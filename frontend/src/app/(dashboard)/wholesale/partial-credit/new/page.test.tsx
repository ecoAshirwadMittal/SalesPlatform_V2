// @vitest-environment jsdom
import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import type { CreditRequestDetail } from '@/lib/partialCreditClient';

const pushMock = vi.fn();
const replaceMock = vi.fn();
const searchParamsGet = vi.fn<(key: string) => string | null>(() => null);

vi.mock('next/navigation', () => ({
  useRouter: () => ({ push: pushMock, replace: replaceMock, back: vi.fn() }),
  useSearchParams: () => ({ get: searchParamsGet }),
}));

vi.mock('@/lib/activeBuyerCode', () => ({
  getActiveBuyerCode: vi.fn(),
}));

vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return {
    ...actual,
    getRequest: vi.fn(),
    createDraft: vi.fn(),
    updateDraft: vi.fn(),
  };
});

import { getActiveBuyerCode } from '@/lib/activeBuyerCode';
import { getRequest } from '@/lib/partialCreditClient';
import StartCreditRequestPage from './page';

const mockGetActive = getActiveBuyerCode as unknown as ReturnType<typeof vi.fn>;
const mockGetRequest = getRequest as unknown as ReturnType<typeof vi.fn>;

function buildResumedDraft(overrides: Partial<CreditRequestDetail> = {}): CreditRequestDetail {
  return {
    id: 99,
    requestNumber: 'PCR-99',
    orderNumber: 'SO-321',
    partyName: 'Acme Buyer Co.',
    orderCreatedDate: '2026-05-01',
    orderShippedDate: '2026-05-02',
    systemStatus: 'DRAFT',
    displayStatus: 'Draft',
    shipmentDamaged: 'NOT_ANSWERED',
    hasMissingDevice: false,
    hasWrongDevice: false,
    hasEncumberedDevice: false,
    totalDevices: 0,
    requestedTotal: null,
    approvedTotal: null,
    reviewedById: null,
    reviewCompletedOn: null,
    missingLines: [],
    wrongLines: [],
    encumberedLines: [],
    ...overrides,
  };
}

describe('StartCreditRequestPage — on-behalf banner (Group 7)', () => {
  beforeEach(() => {
    pushMock.mockReset();
    replaceMock.mockReset();
    searchParamsGet.mockReset();
    mockGetActive.mockReset();
    mockGetRequest.mockReset();
  });

  it('renders the on-behalf banner with partyName + buyerCode when ?draftId=X resumes', async () => {
    searchParamsGet.mockImplementation((key) => (key === 'draftId' ? '99' : null));
    mockGetActive.mockReturnValue({
      id: 7,
      code: '20399',
      buyerName: 'Rep Owner Co.',
      buyerCodeType: 'Wholesale',
      codeType: 'PWS',
    });
    mockGetRequest.mockResolvedValue(buildResumedDraft());

    render(<StartCreditRequestPage />);

    await waitFor(() => {
      expect(mockGetRequest).toHaveBeenCalledWith(99);
    });

    const banner = await screen.findByRole('note');
    expect(banner).toHaveTextContent('Submitting on behalf of');
    expect(banner).toHaveTextContent('Acme Buyer Co.');
    expect(banner).toHaveTextContent('for code');
    expect(banner).toHaveTextContent('20399');
  });

  it('does NOT render the on-behalf banner on a fresh wizard load (no draftId)', async () => {
    searchParamsGet.mockReturnValue(null);
    mockGetActive.mockReturnValue({
      id: 7,
      code: '20399',
      buyerName: 'Rep Owner Co.',
      buyerCodeType: 'Wholesale',
      codeType: 'PWS',
    });

    render(<StartCreditRequestPage />);

    // The note role belongs only to the on-behalf banner.
    expect(screen.queryByRole('note')).not.toBeInTheDocument();
    // The resume fetch must not be invoked when ?draftId is absent.
    expect(mockGetRequest).not.toHaveBeenCalled();
  });

  it('does NOT render a Cancel button on Step 1 (Figma anomaly #15)', async () => {
    searchParamsGet.mockReturnValue(null);
    mockGetActive.mockReturnValue({
      id: 7,
      code: '20399',
      buyerName: 'Rep Owner Co.',
      buyerCodeType: 'Wholesale',
      codeType: 'PWS',
    });

    render(<StartCreditRequestPage />);

    expect(screen.queryByRole('button', { name: /^Cancel$/i })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: /^Next$/i })).toBeInTheDocument();
  });
});
