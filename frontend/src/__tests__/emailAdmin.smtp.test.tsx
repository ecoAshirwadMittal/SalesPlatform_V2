// @vitest-environment jsdom
/**
 * Task 10 — Email Admin SMTP tab.
 *
 * The backend has no Jackson snake_case naming strategy, so every
 * `/api/v1/admin/email/**` request/response field is camelCase
 * (`SmtpConfigView`/`SmtpConfigUpdate` — see backend
 * dto/email/SmtpConfigView.java + SmtpConfigUpdate.java). These tests pin
 * that contract at the UI boundary: the loaded config must render into the
 * form, and the saved PUT body must be camelCase with no leaked
 * password/username field (D2 — the SMTP password is env-only and neither
 * DTO has a password component).
 *
 * The tab component is rendered in isolation (not the full page shell) so
 * its own GET-on-mount is the only fetch call in play — the page shell
 * (which also mounts the Templates/Log tabs' siblings) is exercised
 * separately and is not needed to prove this tab's wiring.
 */
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { SmtpConfigTab } from '@/app/(dashboard)/admin/app-control-center/email-admin/SmtpConfigTab';

function jsonResponse(status: number, body: unknown): Response {
  return {
    ok: status >= 200 && status < 300,
    status,
    statusText: status >= 200 && status < 300 ? 'OK' : 'Error',
    json: async () => body,
    text: async () => JSON.stringify(body),
  } as Response;
}

const SAMPLE_CONFIG = {
  id: 1,
  serverHost: 'smtp.example.com',
  serverPort: 587,
  protocol: 'SMTP',
  fromAddress: 'noreply@ecoatm.com',
  fromDisplayName: 'ecoATM',
  replyTo: 'support@ecoatm.com',
  useSsl: false,
  useTls: true,
  enabled: true,
  maxRetryAttempts: 3,
  timeoutMs: 10000,
  changedDate: '2026-07-01T12:00:00Z',
};

function fetchCalls() {
  return (global.fetch as ReturnType<typeof vi.fn>).mock.calls;
}

beforeEach(() => {
  vi.restoreAllMocks();
});

describe('Email Admin — SMTP tab', () => {
  it('loads the SMTP configuration from GET /smtp and renders camelCase fields', async () => {
    global.fetch = vi.fn().mockResolvedValue(jsonResponse(200, SAMPLE_CONFIG));

    render(<SmtpConfigTab onBanner={vi.fn()} />);

    expect(await screen.findByDisplayValue('smtp.example.com')).toBeInTheDocument();
    expect(screen.getByDisplayValue('noreply@ecoatm.com')).toBeInTheDocument();
    expect(screen.getByDisplayValue('ecoATM')).toBeInTheDocument();
    expect(screen.getByDisplayValue('support@ecoatm.com')).toBeInTheDocument();

    // D2: no password/username field exists in the backend contract — the
    // form must not offer one (it can never be persisted).
    expect(screen.queryByLabelText(/password/i)).not.toBeInTheDocument();
    expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
  });

  it('saves the SMTP configuration with a camelCase PUT body matching SmtpConfigUpdate', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, SAMPLE_CONFIG))
      .mockResolvedValueOnce(jsonResponse(200, { ...SAMPLE_CONFIG, fromDisplayName: 'ecoATM Direct' }));

    render(<SmtpConfigTab onBanner={vi.fn()} />);
    await screen.findByDisplayValue('smtp.example.com');

    fireEvent.change(screen.getByDisplayValue('ecoATM'), { target: { value: 'ecoATM Direct' } });
    fireEvent.click(screen.getByRole('button', { name: /Save Configuration/i }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(2));

    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/smtp');
    expect(init.method).toBe('PUT');

    const body = JSON.parse(init.body as string);
    expect(body).toEqual({
      serverHost: 'smtp.example.com',
      serverPort: 587,
      protocol: 'SMTP',
      fromAddress: 'noreply@ecoatm.com',
      fromDisplayName: 'ecoATM Direct',
      replyTo: 'support@ecoatm.com',
      useSsl: false,
      useTls: true,
      enabled: true,
      maxRetryAttempts: 3,
      timeoutMs: 10000,
    });
    // Regression guard: no snake_case drift, no password ever leaves the client.
    expect(body).not.toHaveProperty('from_display_name');
    expect(body).not.toHaveProperty('server_host');
    expect(body).not.toHaveProperty('use_ssl');
    expect(body).not.toHaveProperty('password');
    expect(body).not.toHaveProperty('encryptedPassword');
  });

  it('toggling "SMTP Config Enabled" flips the saved payload', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, SAMPLE_CONFIG))
      .mockResolvedValueOnce(jsonResponse(200, { ...SAMPLE_CONFIG, enabled: false }));

    render(<SmtpConfigTab onBanner={vi.fn()} />);
    await screen.findByDisplayValue('smtp.example.com');

    fireEvent.click(screen.getByRole('button', { name: 'SMTP Config Enabled' }));
    fireEvent.click(screen.getByRole('button', { name: /Save Configuration/i }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(2));
    const [, init] = fetchCalls()[1];
    const body = JSON.parse(init.body as string);
    expect(body.enabled).toBe(false);
  });

  it('Test Connection posts to /smtp/test and reports the {success, message} result', async () => {
    const onBanner = vi.fn();
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, SAMPLE_CONFIG))
      .mockResolvedValueOnce(jsonResponse(200, { success: true, message: 'Connection succeeded' }));

    render(<SmtpConfigTab onBanner={onBanner} />);
    await screen.findByDisplayValue('smtp.example.com');

    fireEvent.click(screen.getByRole('button', { name: /Test Connection/i }));

    await waitFor(() => {
      expect(onBanner).toHaveBeenCalledWith({ type: 'success', message: 'Connection succeeded' });
    });

    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/smtp/test');
    expect(init.method).toBe('POST');
  });
});
