// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useState } from 'react';
import { BarcodeEntryCard } from './BarcodeEntryCard';

// Stub the client module so the test drives parseBarcodesFromFile.
vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return {
    ...actual,
    parseBarcodesFromFile: vi.fn(),
  };
});

import { parseBarcodesFromFile } from '@/lib/partialCreditClient';

const mockParse = parseBarcodesFromFile as unknown as ReturnType<typeof vi.fn>;

function ControlledCard(props: { initialValue?: string }) {
  const [value, setValue] = useState(props.initialValue ?? '');
  return <BarcodeEntryCard value={value} onChange={setValue} />;
}

function makeFile(name: string, type: string): File {
  return new File(['barcode-blob'], name, { type });
}

describe('BarcodeEntryCard', () => {
  beforeEach(() => {
    mockParse.mockReset();
  });

  it('renders the canonical Figma subtitle', () => {
    render(<ControlledCard />);
    expect(
      screen.getByText(
        /Copy and paste the barcodes into the text field below or upload a file listing the barcodes\./,
      ),
    ).toBeInTheDocument();
  });

  it('renders the textarea and the file dropzone together', () => {
    render(<ControlledCard />);
    // Textarea is reachable via the sr-only "Barcodes" label.
    expect(screen.getByLabelText('Barcodes')).toBeInTheDocument();
    // The dropzone exposes its input through an aria-label.
    expect(screen.getByLabelText('Upload barcodes file')).toBeInTheDocument();
    // OR divider must be visible between the two.
    expect(screen.getByText('OR')).toBeInTheDocument();
  });

  it('merges parsed barcodes from the uploaded file with the textarea value', async () => {
    mockParse.mockResolvedValueOnce({
      barcodes: ['111', '222'],
      warnings: [],
    });

    render(<ControlledCard initialValue="999" />);

    const input = screen.getByLabelText('Upload barcodes file') as HTMLInputElement;
    fireEvent.change(input, {
      target: {
        files: [makeFile('codes.csv', 'text/csv')],
      },
    });

    await waitFor(() => {
      expect(mockParse).toHaveBeenCalledTimes(1);
      const textarea = screen.getByLabelText('Barcodes') as HTMLTextAreaElement;
      // Existing "999" preserved; parsed "111, 222" merged in.
      expect(textarea.value).toContain('999');
      expect(textarea.value).toContain('111');
      expect(textarea.value).toContain('222');
    });
  });

  it('surfaces parser warnings inline as a status list', async () => {
    mockParse.mockResolvedValueOnce({
      barcodes: ['111'],
      warnings: ['Dropped 2 duplicate barcodes'],
    });

    render(<ControlledCard />);
    fireEvent.change(screen.getByLabelText('Upload barcodes file'), {
      target: { files: [makeFile('codes.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')] },
    });

    await waitFor(() => {
      expect(screen.getByRole('status')).toHaveTextContent(
        /Dropped 2 duplicate barcodes/,
      );
    });
  });

  it('re-uploading the same file is idempotent (Set dedupes)', async () => {
    mockParse.mockResolvedValue({
      barcodes: ['111', '222'],
      warnings: [],
    });

    render(<ControlledCard initialValue="111" />);

    const input = screen.getByLabelText('Upload barcodes file') as HTMLInputElement;
    fireEvent.change(input, {
      target: { files: [makeFile('codes.csv', 'text/csv')] },
    });

    await waitFor(() => {
      const textarea = screen.getByLabelText('Barcodes') as HTMLTextAreaElement;
      // "111" should appear only once even though it was in both the
      // textarea and the parsed result.
      const occurrences = textarea.value.split('111').length - 1;
      expect(occurrences).toBe(1);
      expect(textarea.value).toContain('222');
    });
  });

  it('renders inline errorText prop when provided', () => {
    render(
      <BarcodeEntryCard
        value=""
        onChange={() => {}}
        errorText="Enter or upload the missing device barcodes"
      />,
    );
    expect(
      screen.getByText('Enter or upload the missing device barcodes'),
    ).toBeInTheDocument();
  });
});
