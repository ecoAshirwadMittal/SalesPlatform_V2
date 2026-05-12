// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { OnBehalfModal } from './OnBehalfModal';

vi.mock('@/lib/onBehalfPartialCreditClient', () => ({
  listEligibleBuyerCodes: vi.fn(),
  listBuyersForCode: vi.fn(),
  createDraftOnBehalf: vi.fn(),
}));

import {
  listEligibleBuyerCodes,
  listBuyersForCode,
  createDraftOnBehalf,
} from '@/lib/onBehalfPartialCreditClient';

const mockListCodes = listEligibleBuyerCodes as unknown as ReturnType<typeof vi.fn>;
const mockListUsers = listBuyersForCode as unknown as ReturnType<typeof vi.fn>;
const mockCreate = createDraftOnBehalf as unknown as ReturnType<typeof vi.fn>;

describe('OnBehalfModal', () => {
  beforeEach(() => {
    mockListCodes.mockReset();
    mockListUsers.mockReset();
    mockCreate.mockReset();
  });

  it('returns null when closed and does not fetch', () => {
    const { container } = render(
      <OnBehalfModal open={false} onClose={vi.fn()} onDraftCreated={vi.fn()} />,
    );
    expect(container.firstChild).toBeNull();
    expect(mockListCodes).not.toHaveBeenCalled();
  });

  it('fetches buyer codes on open and renders one option per row', async () => {
    mockListCodes.mockResolvedValueOnce([
      { id: 1, code: '20399', buyerName: 'Acme' },
      { id: 2, code: '30001', buyerName: 'Beta' },
    ]);
    render(<OnBehalfModal open onClose={vi.fn()} onDraftCreated={vi.fn()} />);

    await waitFor(() => {
      expect(screen.getByText('20399')).toBeInTheDocument();
      expect(screen.getByText('30001')).toBeInTheDocument();
    });
    expect(mockListCodes).toHaveBeenCalledTimes(1);
  });

  it('walks code → user → order and calls createDraftOnBehalf with the right args', async () => {
    mockListCodes.mockResolvedValueOnce([
      { id: 1, code: '20399', buyerName: 'Acme' },
    ]);
    mockListUsers.mockResolvedValueOnce([
      { userId: 101, displayName: 'Nadia', email: 'nadia@example.com' },
    ]);
    mockCreate.mockResolvedValueOnce({
      id: 500,
      requestNumber: 'PCR-1',
      orderNumber: 'SO-1',
      partyName: null,
      orderCreatedDate: null,
      orderShippedDate: null,
      systemStatus: 'DRAFT',
      displayStatus: 'Draft',
      shipmentDamaged: 'NOT_ANSWERED',
      hasMissingDevice: false,
      hasWrongDevice: false,
      hasEncumberedDevice: false,
      totalDevices: 0,
      requestedTotal: 0,
      approvedTotal: null,
      reviewedById: null,
      reviewCompletedOn: null,
      missingLines: [],
      wrongLines: [],
      encumberedLines: [],
    });
    const onDraftCreated = vi.fn();
    const onClose = vi.fn();
    render(
      <OnBehalfModal open onClose={onClose} onDraftCreated={onDraftCreated} />,
    );

    // Step CODE → pick the only code
    await waitFor(() => screen.getByText('20399'));
    fireEvent.click(screen.getByText('20399'));

    // Step USER → pick the only user (listed after the code-pick triggers fetch)
    await waitFor(() => {
      expect(mockListUsers).toHaveBeenCalledWith(1);
      expect(screen.getByText('Nadia')).toBeInTheDocument();
    });
    fireEvent.click(screen.getByText('Nadia'));

    // Step ORDER → type + Create
    const orderInput = screen.getByPlaceholderText(/SO-12345/);
    fireEvent.change(orderInput, { target: { value: 'SO-7' } });
    fireEvent.click(screen.getByRole('button', { name: /Create draft/i }));

    await waitFor(() => {
      expect(mockCreate).toHaveBeenCalledWith('SO-7', 1, 101);
      expect(onDraftCreated).toHaveBeenCalledWith(500);
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('disables Create until the order number is non-blank', async () => {
    mockListCodes.mockResolvedValueOnce([{ id: 1, code: '20399', buyerName: 'Acme' }]);
    mockListUsers.mockResolvedValueOnce([
      { userId: 101, displayName: 'Nadia', email: 'nadia@example.com' },
    ]);
    render(<OnBehalfModal open onClose={vi.fn()} onDraftCreated={vi.fn()} />);

    await waitFor(() => screen.getByText('20399'));
    fireEvent.click(screen.getByText('20399'));
    await waitFor(() => screen.getByText('Nadia'));
    fireEvent.click(screen.getByText('Nadia'));

    const createButton = screen.getByRole('button', { name: /Create draft/i });
    expect(createButton).toBeDisabled();

    // Whitespace-only input must still be considered empty.
    fireEvent.change(screen.getByPlaceholderText(/SO-12345/), {
      target: { value: '   ' },
    });
    expect(createButton).toBeDisabled();

    fireEvent.change(screen.getByPlaceholderText(/SO-12345/), {
      target: { value: 'SO-1' },
    });
    expect(createButton).not.toBeDisabled();
  });

  it('surfaces server errors inline; modal stays open', async () => {
    mockListCodes.mockResolvedValueOnce([{ id: 1, code: '20399', buyerName: 'Acme' }]);
    mockListUsers.mockResolvedValueOnce([
      { userId: 101, displayName: 'Nadia', email: 'nadia@example.com' },
    ]);
    mockCreate.mockRejectedValueOnce(new Error('User not associated with code'));
    const onClose = vi.fn();
    render(<OnBehalfModal open onClose={onClose} onDraftCreated={vi.fn()} />);

    await waitFor(() => screen.getByText('20399'));
    fireEvent.click(screen.getByText('20399'));
    await waitFor(() => screen.getByText('Nadia'));
    fireEvent.click(screen.getByText('Nadia'));
    fireEvent.change(screen.getByPlaceholderText(/SO-12345/), {
      target: { value: 'SO-1' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Create draft/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('User not associated with code');
    });
    expect(onClose).not.toHaveBeenCalled();
  });

  it('renders an empty-state when the buyer-code list is empty', async () => {
    mockListCodes.mockResolvedValueOnce([]);
    render(<OnBehalfModal open onClose={vi.fn()} onDraftCreated={vi.fn()} />);

    await waitFor(() => {
      expect(screen.getByText(/No buyer codes available/i)).toBeInTheDocument();
    });
  });

  it('Back button on the order step returns to user picker without losing state', async () => {
    mockListCodes.mockResolvedValueOnce([{ id: 1, code: '20399', buyerName: 'Acme' }]);
    mockListUsers.mockResolvedValueOnce([
      { userId: 101, displayName: 'Nadia', email: 'nadia@example.com' },
    ]);
    render(<OnBehalfModal open onClose={vi.fn()} onDraftCreated={vi.fn()} />);

    await waitFor(() => screen.getByText('20399'));
    fireEvent.click(screen.getByText('20399'));
    await waitFor(() => screen.getByText('Nadia'));
    fireEvent.click(screen.getByText('Nadia'));

    expect(screen.getByPlaceholderText(/SO-12345/)).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: /Back/i }));

    // Back to user picker — the previously-loaded users list is still there.
    expect(screen.getByText('Nadia')).toBeInTheDocument();
    expect(screen.queryByPlaceholderText(/SO-12345/)).not.toBeInTheDocument();
  });
});
