// @vitest-environment jsdom
/**
 * Task 10 — Email Admin Log tab.
 *
 * Pins the `EmailLogView` camelCase contract and the Spring `Page` JSON
 * envelope (`{content, totalElements, ...}` — backend
 * dto/email/EmailLogView.java + AdminEmailController#listLog). The
 * highest-signal case is the last one: M-3 (security review 2026-07-10)
 * requires the log-detail HTML preview to run through a real sanitizer —
 * a `<script>` embedded in `contentHtml` must never reach the DOM.
 * Rendered in isolation (not the full page shell) so the mock fetch queue
 * only has to account for this tab's own calls.
 */
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { EmailLogTab } from '@/app/(dashboard)/admin/app-control-center/email-admin/EmailLogTab';

function jsonResponse(status: number, body: unknown): Response {
  return {
    ok: status >= 200 && status < 300,
    status,
    statusText: status >= 200 && status < 300 ? 'OK' : 'Error',
    json: async () => body,
    text: async () => JSON.stringify(body),
  } as Response;
}

const SAMPLE_LOG_ROW = {
  id: 7,
  templateKey: 'WELCOME_EMAIL',
  fromAddress: 'noreply@ecoatm.com',
  toAddress: 'buyer@example.com',
  cc: null,
  bcc: null,
  subject: 'Welcome',
  contentHtml: '<p>Hi</p>',
  status: 'SENT',
  errorMessage: null,
  retryCount: 0,
  nextAttemptAt: null,
  sourceModule: null,
  sourceId: null,
  sentDate: '2026-07-01T12:00:00Z',
  createdDate: '2026-07-01T11:59:00Z',
};

function pageOf(rows: unknown[]) {
  return { content: rows, totalElements: rows.length, totalPages: 1, number: 0, size: 50 };
}

function fetchCalls() {
  return (global.fetch as ReturnType<typeof vi.fn>).mock.calls;
}

beforeEach(() => {
  vi.restoreAllMocks();
});

describe('Email Admin — Log tab', () => {
  it('lists log entries from the GET /log Page envelope using camelCase fields', async () => {
    global.fetch = vi.fn().mockResolvedValue(jsonResponse(200, pageOf([SAMPLE_LOG_ROW])));

    render(<EmailLogTab onBanner={vi.fn()} />);

    expect(await screen.findByText('buyer@example.com')).toBeInTheDocument();
    expect(screen.getByText('Welcome')).toBeInTheDocument();
    expect(screen.getByText('WELCOME_EMAIL')).toBeInTheDocument();
    expect(screen.getByText('SENT')).toBeInTheDocument();
    expect(screen.getByText('1 email(s)')).toBeInTheDocument();
  });

  it('changing the status filter re-fetches with the PENDING/SENT/FAILED enum value', async () => {
    global.fetch = vi.fn().mockResolvedValue(jsonResponse(200, pageOf([SAMPLE_LOG_ROW])));

    render(<EmailLogTab onBanner={vi.fn()} />);
    await screen.findByText('buyer@example.com');

    fireEvent.change(screen.getByLabelText('Status:'), { target: { value: 'FAILED' } });

    await waitFor(() => expect(fetchCalls().length).toBeGreaterThanOrEqual(2));
    const [url] = fetchCalls()[fetchCalls().length - 1];
    expect(String(url)).toContain('status=FAILED');
  });

  it('opens a row detail via GET /log/{id} and sanitizes contentHtml — an embedded <script> is stripped', async () => {
    const detail = {
      ...SAMPLE_LOG_ROW,
      contentHtml: '<p>Hello Buyer</p><script>window.__xss = true;</script><img src=x onerror="window.__xss2=true">',
    };
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, pageOf([SAMPLE_LOG_ROW])))
      .mockResolvedValueOnce(jsonResponse(200, detail));

    const { container } = render(<EmailLogTab onBanner={vi.fn()} />);
    await screen.findByText('buyer@example.com');

    fireEvent.click(screen.getByRole('button', { name: 'View' }));

    await waitFor(() => expect(screen.getByText('Hello Buyer')).toBeInTheDocument());

    // The security-critical M-3 assertion: no <script> element and no
    // dangling onerror handler ever reach the rendered DOM.
    expect(container.querySelector('script')).toBeNull();
    expect(container.querySelector('[onerror]')).toBeNull();
    expect(container.innerHTML).not.toContain('<script>');
    expect(container.innerHTML).not.toContain('onerror');
  });

  it('Resend posts to POST /log/{id}/resend and reloads the list', async () => {
    const failedRow = { ...SAMPLE_LOG_ROW, id: 8, status: 'FAILED' };
    const resentRow = { ...failedRow, status: 'SENT', retryCount: 0 };
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, pageOf([failedRow]))) // initial list
      .mockResolvedValueOnce(jsonResponse(200, resentRow)) // POST /resend
      .mockResolvedValueOnce(jsonResponse(200, pageOf([resentRow]))); // reload after resend

    render(<EmailLogTab onBanner={vi.fn()} />);
    await screen.findByText('buyer@example.com');

    fireEvent.click(screen.getByRole('button', { name: 'Resend' }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(3));
    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/log/8/resend');
    expect(init.method).toBe('POST');
  });
});
