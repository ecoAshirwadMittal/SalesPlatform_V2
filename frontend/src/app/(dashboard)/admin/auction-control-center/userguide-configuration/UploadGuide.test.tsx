// @vitest-environment jsdom
/**
 * Vitest unit tests for the Userguide Configuration upload flow.
 *
 * We test the client-side validation logic (content-type, size) and the
 * upload interaction. Server round-trips are mocked via global.fetch.
 */

import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import UserguideConfigurationPage from './page';

// ── Global fetch mock ──────────────────────────────────────────────────────────

beforeEach(() => {
  vi.restoreAllMocks();
});

function mockFetch(status: number, body: unknown) {
  global.fetch = vi.fn().mockResolvedValue({
    ok: status >= 200 && status < 300,
    status,
    statusText: status === 200 ? 'OK' : 'Error',
    json: async () => body,
    text: async () => JSON.stringify(body),
  } as Response);
}

// Default: list returns empty state
function mockEmptyList() {
  mockFetch(200, { active: null, history: [] });
}

// ── Test helpers ───────────────────────────────────────────────────────────────

function makePdfFile(name = 'guide.pdf', size = 1024): File {
  const bytes = new Uint8Array(size);
  // %PDF- magic bytes
  bytes[0] = 0x25; bytes[1] = 0x50; bytes[2] = 0x44; bytes[3] = 0x46; bytes[4] = 0x2D;
  return new File([bytes], name, { type: 'application/pdf' });
}

// ── Rendering ──────────────────────────────────────────────────────────────────

describe('UserguideConfigurationPage', () => {
  it('renders the page heading', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    expect(await screen.findByText('Userguide Configuration')).toBeInTheDocument();
  });

  it('shows "No guide has been uploaded yet" when active is null', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    expect(await screen.findByText(/No guide has been uploaded yet/i)).toBeInTheDocument();
  });

  it('shows the active guide filename when one is configured', async () => {
    mockFetch(200, {
      active: {
        id: 1,
        fileName: 'buyer-guide-v3.pdf',
        contentType: 'application/pdf',
        byteSize: 204800,
        uploadedBy: 1,
        uploaderName: 'Admin User',
        uploadedAt: new Date().toISOString(),
        isActive: true,
      },
      history: [],
    });
    render(<UserguideConfigurationPage />);
    expect(await screen.findByText('buyer-guide-v3.pdf')).toBeInTheDocument();
  });

  // ── File selection ─────────────────────────────────────────────────────────

  it('shows file name after a valid PDF is selected', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    const input = screen.getByLabelText('Choose PDF file to upload');
    const file = makePdfFile('my-guide.pdf', 512);
    fireEvent.change(input, { target: { files: [file] } });

    expect(screen.getByText(/my-guide\.pdf/)).toBeInTheDocument();
  });

  it('shows error for non-PDF file type', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    const input = screen.getByLabelText('Choose PDF file to upload');
    const file = new File(['content'], 'doc.txt', { type: 'text/plain' });
    fireEvent.change(input, { target: { files: [file] } });

    expect(await screen.findByRole('alert')).toHaveTextContent(/Only PDF files are accepted/i);
  });

  it('shows error for file exceeding 20 MB', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    const input = screen.getByLabelText('Choose PDF file to upload');
    // Create a 21 MB file (client-side check)
    const bigBytes = new Uint8Array(21 * 1024 * 1024 + 1);
    bigBytes[0] = 0x25; bigBytes[1] = 0x50; bigBytes[2] = 0x44; bigBytes[3] = 0x46; bigBytes[4] = 0x2D;
    const file = new File([bigBytes], 'big.pdf', { type: 'application/pdf' });
    fireEvent.change(input, { target: { files: [file] } });

    expect(await screen.findByRole('alert')).toHaveTextContent(/File too large/i);
  });

  // ── Upload button ──────────────────────────────────────────────────────────

  it('Upload & Activate button appears only after valid file selected', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    // No button yet
    expect(screen.queryByRole('button', { name: /Upload & Activate/i })).not.toBeInTheDocument();

    const input = screen.getByLabelText('Choose PDF file to upload');
    fireEvent.change(input, { target: { files: [makePdfFile()] } });

    expect(screen.getByRole('button', { name: /Upload & Activate/i })).toBeInTheDocument();
  });

  it('shows success banner after a successful upload', async () => {
    // First call: list. Second call: upload. Third call: reload list.
    const uploaded = {
      id: 2,
      fileName: 'new-guide.pdf',
      contentType: 'application/pdf',
      byteSize: 1024,
      uploadedBy: 1,
      uploaderName: 'Admin',
      uploadedAt: new Date().toISOString(),
      isActive: true,
    };

    global.fetch = vi
      .fn()
      .mockResolvedValueOnce({
        ok: true, json: async () => ({ active: null, history: [] }),
        text: async () => '{}',
      } as Response)
      .mockResolvedValueOnce({
        ok: true, json: async () => uploaded,
        text: async () => JSON.stringify(uploaded),
      } as Response)
      .mockResolvedValueOnce({
        ok: true, json: async () => ({ active: uploaded, history: [uploaded] }),
        text: async () => '{}',
      } as Response);

    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    const input = screen.getByLabelText('Choose PDF file to upload');
    fireEvent.change(input, { target: { files: [makePdfFile('new-guide.pdf')] } });
    fireEvent.click(screen.getByRole('button', { name: /Upload & Activate/i }));

    await waitFor(() => {
      expect(screen.getByText(/Guide uploaded and activated successfully/i)).toBeInTheDocument();
    });
  });

  it('shows error banner when upload fails', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce({
        ok: true, json: async () => ({ active: null, history: [] }),
        text: async () => '{}',
      } as Response)
      .mockResolvedValueOnce({
        ok: false, status: 400, statusText: 'Bad Request',
        json: async () => ({ message: 'Invalid content type' }),
        text: async () => JSON.stringify({ message: 'Invalid content type' }),
      } as Response);

    render(<UserguideConfigurationPage />);
    await screen.findByText(/No guide has been uploaded yet/i);

    const input = screen.getByLabelText('Choose PDF file to upload');
    fireEvent.change(input, { target: { files: [makePdfFile()] } });
    fireEvent.click(screen.getByRole('button', { name: /Upload & Activate/i }));

    await waitFor(() => {
      expect(screen.getByText(/Invalid content type/i)).toBeInTheDocument();
    });
  });

  // ── History table ──────────────────────────────────────────────────────────

  it('renders history table rows when history is present', async () => {
    const entry = {
      id: 10,
      fileName: 'old-guide.pdf',
      contentType: 'application/pdf',
      byteSize: 512000,
      uploadedBy: 1,
      uploaderName: 'Alice',
      uploadedAt: new Date().toISOString(),
      isActive: false,
    };
    mockFetch(200, { active: null, history: [entry] });
    render(<UserguideConfigurationPage />);
    expect(await screen.findByText('old-guide.pdf')).toBeInTheDocument();
    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Inactive')).toBeInTheDocument();
  });

  it('shows empty history message when history is empty', async () => {
    mockEmptyList();
    render(<UserguideConfigurationPage />);
    expect(await screen.findByText('No upload history yet.')).toBeInTheDocument();
  });
});
