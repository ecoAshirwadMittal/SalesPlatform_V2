// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import type { CreditRequestDetail } from '@/lib/partialCreditClient';

const pushMock = vi.fn();
const replaceMock = vi.fn();
const searchParamsGet = vi.fn(() => '42');

vi.mock('next/navigation', () => ({
  useRouter: () => ({ push: pushMock, replace: replaceMock, back: vi.fn() }),
  useSearchParams: () => ({ get: searchParamsGet }),
}));

vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return {
    ...actual,
    getRequest: vi.fn(),
    listPhotos: vi.fn(),
    submitRequest: vi.fn(),
  };
});

import { getRequest, listPhotos, submitRequest } from '@/lib/partialCreditClient';
import { SummaryStep } from './SummaryStep';

const mockGetRequest = getRequest as unknown as ReturnType<typeof vi.fn>;
const mockListPhotos = listPhotos as unknown as ReturnType<typeof vi.fn>;
const mockSubmitRequest = submitRequest as unknown as ReturnType<typeof vi.fn>;

function buildDetail(overrides: Partial<CreditRequestDetail> = {}): CreditRequestDetail {
  return {
    id: 42,
    requestNumber: 'PCR-42',
    orderNumber: 'ORD-123-45',
    partyName: 'Acme Buyer Co.',
    orderCreatedDate: '2026-04-01',
    orderShippedDate: '2026-04-02',
    systemStatus: 'DRAFT',
    displayStatus: 'Draft',
    shipmentDamaged: 'NO',
    hasMissingDevice: true,
    hasWrongDevice: true,
    hasEncumberedDevice: true,
    totalDevices: 3,
    requestedTotal: null,
    approvedTotal: null,
    reviewedById: null,
    reviewCompletedOn: null,
    missingLines: [
      {
        id: 1,
        barcodeSubmitted: 'BC-MISS-001',
        brand: 'Samsung',
        model: 'Galaxy S26',
        grade: 'A',
        boxNumber: null,
        amountPaid: 250.5,
        shipStatus: null,
        lineStatus: null,
        reviewDecision: null,
        amountToCredit: null,
      },
    ],
    wrongLines: [
      {
        id: 2,
        expectedBarcode: 'BC-EXP-001',
        expectedBrand: 'Samsung',
        expectedModel: 'Galaxy S21',
        expectedGrade: 'B',
        expectedAmountPaid: 150,
        actualImeiOrModel: '356938035643809',
        actualBrand: null,
        actualModel: null,
        actualGrade: null,
        latestPrice: null,
        actionRecommendation: null,
        lineStatus: null,
        reviewDecision: null,
        amountToCredit: null,
      },
    ],
    encumberedLines: [
      {
        id: 3,
        barcodeSubmitted: 'BC-ENC-001',
        brand: 'Apple',
        model: 'iPhone 15',
        grade: 'A',
        boxNumber: null,
        amountPaid: 600,
        prologResult: null,
        actualValue: null,
        lineStatus: null,
        reviewDecision: null,
        amountToCredit: null,
      },
    ],
    ...overrides,
  };
}

describe('SummaryStep', () => {
  beforeEach(() => {
    mockGetRequest.mockReset();
    mockListPhotos.mockReset();
    mockSubmitRequest.mockReset();
    pushMock.mockReset();
    replaceMock.mockReset();
    mockListPhotos.mockResolvedValue([]);
  });

  it('renders the wizard-wide H1 "Submit a Credit Request" (not "Review and submit")', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(
        screen.getByRole('heading', { level: 1, name: 'Submit a Credit Request' }),
      ).toBeInTheDocument();
    });
    expect(screen.queryByText('Review and submit')).not.toBeInTheDocument();
  });

  it('renders the metadata card with Order Number, Request Reasons, Total Devices', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByText('Order Number')).toBeInTheDocument();
    });
    expect(screen.getByText('ORD-123-45')).toBeInTheDocument();
    expect(screen.getByText('Request Reasons')).toBeInTheDocument();
    expect(
      screen.getByText('Missing Device, Wrong Device, Encumbered Device'),
    ).toBeInTheDocument();
    expect(screen.getByText('Total Devices')).toBeInTheDocument();
    // 1 missing + 1 wrong + 1 encumbered = 3 total
    expect(screen.getByText('3')).toBeInTheDocument();
  });

  it('renders per-reason tables (not <ul>) with the canonical column headers', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);

    await waitFor(() => {
      expect(screen.getByRole('region', { name: /missing devices/i })).toBeInTheDocument();
    });

    // Each section has a real table.
    const tables = screen.getAllByRole('table');
    expect(tables).toHaveLength(3);

    // Missing columns
    const missingSection = screen.getByRole('region', { name: /missing devices/i });
    expect(within(missingSection).getByRole('columnheader', { name: 'Barcode' })).toBeInTheDocument();
    expect(
      within(missingSection).getByRole('columnheader', { name: 'Device Description' }),
    ).toBeInTheDocument();
    expect(within(missingSection).getByRole('columnheader', { name: 'Amount Paid' })).toBeInTheDocument();
    expect(within(missingSection).getByText('BC-MISS-001')).toBeInTheDocument();
    expect(within(missingSection).getByText('$250.50')).toBeInTheDocument();

    // Encumbered columns
    const encSection = screen.getByRole('region', { name: /encumbered devices/i });
    expect(within(encSection).getByRole('columnheader', { name: 'Barcode' })).toBeInTheDocument();
    expect(within(encSection).getByRole('columnheader', { name: 'Device Description' })).toBeInTheDocument();
  });

  it('shows plural section headings with parenthesized count badge', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /Missing Devices/ })).toBeInTheDocument();
    });
    expect(screen.getByRole('heading', { name: /Missing Devices/ })).toHaveTextContent('(1)');
    expect(screen.getByRole('heading', { name: /Wrong Devices/ })).toHaveTextContent('(1)');
    expect(screen.getByRole('heading', { name: /Encumbered Devices/ })).toHaveTextContent('(1)');
  });

  it('Show/Hide Details toggle collapses the section table', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('region', { name: /missing devices/i })).toBeInTheDocument();
    });
    const missingSection = screen.getByRole('region', { name: /missing devices/i });
    const toggle = within(missingSection).getByRole('button', { name: /hide details/i });
    expect(toggle).toHaveAttribute('aria-expanded', 'true');
    expect(within(missingSection).getByRole('table')).toBeInTheDocument();

    fireEvent.click(toggle);
    expect(toggle).toHaveAttribute('aria-expanded', 'false');
    expect(within(missingSection).queryByRole('table')).not.toBeInTheDocument();
    expect(within(missingSection).getByRole('button', { name: /show details/i })).toBeInTheDocument();
  });

  it('per-group Edit button routes to /new/{reason}?id=X', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('region', { name: /wrong devices/i })).toBeInTheDocument();
    });
    const wrongSection = screen.getByRole('region', { name: /wrong devices/i });
    const editBtn = within(wrongSection).getByRole('button', { name: /edit wrong devices/i });
    fireEvent.click(editBtn);
    expect(pushMock).toHaveBeenCalledWith('/wholesale/partial-credit/new/wrong?id=42');
  });

  it('bottom button row renders Edit | Cancel | Submit Request (no Back)', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /submit request/i })).toBeInTheDocument();
    });
    expect(screen.getByRole('button', { name: /edit credit request/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /^back$/i })).not.toBeInTheDocument();
  });

  it('Cancel button routes back to the landing', async () => {
    mockGetRequest.mockResolvedValue(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
    });
    fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(pushMock).toHaveBeenCalledWith('/wholesale/partial-credit');
  });

  it('confirmation modal has NO buttons and dismisses via scrim click', async () => {
    const detail = buildDetail();
    mockGetRequest.mockResolvedValue(detail);
    mockSubmitRequest.mockResolvedValue({ ...detail, systemStatus: 'SUBMITTED' });
    render(<SummaryStep />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /submit request/i })).toBeInTheDocument();
    });
    fireEvent.click(screen.getByRole('button', { name: /submit request/i }));

    const dialog = await screen.findByRole('dialog');
    // The modal renders icon + heading only — no buttons.
    expect(within(dialog).queryAllByRole('button')).toHaveLength(0);
    expect(within(dialog).getByRole('heading', { name: 'Request submitted!' })).toBeInTheDocument();

    // Scrim click dismisses to the landing.
    fireEvent.click(dialog);
    expect(pushMock).toHaveBeenCalledWith('/wholesale/partial-credit');
  });

  it('does not render the damage Y/N prompt on Summary', async () => {
    mockGetRequest.mockResolvedValueOnce(buildDetail());
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('region', { name: /missing devices/i })).toBeInTheDocument();
    });
    expect(screen.queryByText(/shipment damaged/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/was the shipment damaged/i)).not.toBeInTheDocument();
  });

  it('omits reason sections when the corresponding hasX flag is false', async () => {
    mockGetRequest.mockResolvedValue(
      buildDetail({
        hasMissingDevice: true,
        hasWrongDevice: false,
        hasEncumberedDevice: false,
        wrongLines: [],
        encumberedLines: [],
      }),
    );
    render(<SummaryStep />);
    await waitFor(() => {
      expect(screen.getByRole('region', { name: /missing devices/i })).toBeInTheDocument();
    });
    expect(screen.queryByRole('region', { name: /wrong devices/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('region', { name: /encumbered devices/i })).not.toBeInTheDocument();
    // Request Reasons label reflects only the single active reason — no comma join.
    const reasonsLabel = screen.getByText('Request Reasons');
    const reasonsValue = reasonsLabel.nextElementSibling;
    expect(reasonsValue).toHaveTextContent('Missing Device');
    expect(reasonsValue).not.toHaveTextContent('Wrong Device');
    expect(reasonsValue).not.toHaveTextContent('Encumbered Device');
  });
});
