// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { ImportBidsModal } from './ImportBidsModal';
import type { BidImportResult } from '@/lib/bidder';

const noop = () => {};
const successResult: BidImportResult = { updated: 42, errors: [] };
const errorResult: BidImportResult = {
  updated: 5,
  errors: [
    { row: 3, message: 'Price cannot be negative' },
    { row: 7, message: 'Row id=999 does not belong to this round' },
  ],
};

describe('ImportBidsModal', () => {
  // ---------------------------------------------------------------------------
  // State 1: idle
  // ---------------------------------------------------------------------------

  it('renders with aria-label "Import Your Bids"', () => {
    const mockSubmit = vi.fn();
    render(
      <ImportBidsModal
        isOpen
        onClose={noop}
        onSubmit={mockSubmit}
        onComplete={noop}
      />,
    );
    expect(screen.getByRole('dialog', { name: 'Import Your Bids' })).toBeInTheDocument();
  });

  it('idle: shows heading "Import Your Bids"', () => {
    const mockSubmit = vi.fn();
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={mockSubmit} onComplete={noop} />,
    );
    expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent('Import Your Bids');
  });

  it('idle: shows body heading "To bulk import your bids:"', () => {
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    expect(screen.getByText('To bulk import your bids:')).toBeInTheDocument();
  });

  it('idle: step 3 text is "3.Upload your file here" (no space — verbatim Mendix artifact)', () => {
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    // Must match the exact concatenated form from the spec.
    expect(screen.getByText('3.Upload your file here')).toBeInTheDocument();
  });

  it('idle: helper text "Supported format: .xlsx" is shown', () => {
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    expect(screen.getByText('Supported format: .xlsx')).toBeInTheDocument();
  });

  it('idle: Import button is disabled when no file is selected', () => {
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    expect(screen.getByRole('button', { name: 'Import' })).toBeDisabled();
  });

  it('idle: Import button is enabled after a file is selected', () => {
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    const input = screen.getByLabelText('Choose xlsx file to import');
    const file = new File(['data'], 'bids.xlsx', {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });
    fireEvent.change(input, { target: { files: [file] } });
    expect(screen.getByRole('button', { name: 'Import' })).not.toBeDisabled();
  });

  it('returns null and renders nothing when isOpen=false', () => {
    const { container } = render(
      <ImportBidsModal isOpen={false} onClose={noop} onSubmit={vi.fn()} onComplete={noop} />,
    );
    expect(container.firstChild).toBeNull();
  });

  // ---------------------------------------------------------------------------
  // State 2: uploading
  // ---------------------------------------------------------------------------

  it('uploading: shows "Uploading…" text and hides the Import button', async () => {
    // onSubmit never resolves so we stay in the uploading state
    const neverResolves = () => new Promise<BidImportResult>(() => {});
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={neverResolves} onComplete={noop} />,
    );

    // Select a file first
    const input = screen.getByLabelText('Choose xlsx file to import');
    fireEvent.change(input, {
      target: {
        files: [new File(['x'], 'b.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })],
      },
    });

    fireEvent.click(screen.getByRole('button', { name: 'Import' }));

    await waitFor(() => {
      expect(screen.getByText('Uploading…')).toBeInTheDocument();
    });
    // Import button should be replaced by the uploading indicator
    expect(screen.queryByRole('button', { name: 'Import' })).not.toBeInTheDocument();
  });

  // ---------------------------------------------------------------------------
  // State 3: result
  // ---------------------------------------------------------------------------

  it('result (success): shows updated count + Done button, no errors list', async () => {
    const mockSubmit = vi.fn().mockResolvedValue(successResult);
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={mockSubmit} onComplete={noop} />,
    );

    const input = screen.getByLabelText('Choose xlsx file to import');
    fireEvent.change(input, {
      target: {
        files: [new File(['x'], 'b.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })],
      },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Import' }));

    await waitFor(() => {
      expect(screen.getByText('Successfully updated 42 bids.')).toBeInTheDocument();
    });
    expect(screen.getByRole('button', { name: 'Done' })).toBeInTheDocument();
    expect(screen.queryByRole('list', { name: 'Import errors' })).not.toBeInTheDocument();
  });

  it('result (errors): shows partial count + error list', async () => {
    const mockSubmit = vi.fn().mockResolvedValue(errorResult);
    render(
      <ImportBidsModal isOpen onClose={noop} onSubmit={mockSubmit} onComplete={noop} />,
    );

    const input = screen.getByLabelText('Choose xlsx file to import');
    fireEvent.change(input, {
      target: {
        files: [new File(['x'], 'b.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })],
      },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Import' }));

    await waitFor(() => {
      expect(screen.getByText(/Updated 5 bids, 2 rows had errors/)).toBeInTheDocument();
    });
    expect(screen.getByRole('list', { name: 'Import errors' })).toBeInTheDocument();
    expect(screen.getByText('Price cannot be negative')).toBeInTheDocument();
  });

  it('result: Done button calls onComplete and onClose', async () => {
    const mockSubmit = vi.fn().mockResolvedValue(successResult);
    const onComplete = vi.fn();
    const onClose = vi.fn();
    render(
      <ImportBidsModal
        isOpen
        onClose={onClose}
        onSubmit={mockSubmit}
        onComplete={onComplete}
      />,
    );

    const input = screen.getByLabelText('Choose xlsx file to import');
    fireEvent.change(input, {
      target: {
        files: [new File(['x'], 'b.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })],
      },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Import' }));

    await waitFor(() => screen.getByRole('button', { name: 'Done' }));
    fireEvent.click(screen.getByRole('button', { name: 'Done' }));

    expect(onComplete).toHaveBeenCalledTimes(1);
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('idle: Escape key calls onClose', () => {
    const onClose = vi.fn();
    render(
      <ImportBidsModal isOpen onClose={onClose} onSubmit={vi.fn()} onComplete={noop} />,
    );
    fireEvent.keyDown(document, { key: 'Escape' });
    expect(onClose).toHaveBeenCalledTimes(1);
  });
});
