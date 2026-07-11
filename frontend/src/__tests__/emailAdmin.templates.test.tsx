// @vitest-environment jsdom
/**
 * Task 10 — Email Admin Templates tab.
 *
 * Pins the `EmailTemplateView` (read) / `EmailTemplateUpsert` (write)
 * camelCase contract (backend dto/email/EmailTemplateView.java +
 * EmailTemplateUpsert.java), plus the two render-only actions the stub was
 * entirely missing: preview and send-test. Rendered in isolation (not the
 * full page shell) so the mock fetch queue only has to account for this
 * tab's own calls.
 */
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { TemplatesTab } from '@/app/(dashboard)/admin/app-control-center/email-admin/TemplatesTab';

function jsonResponse(status: number, body: unknown): Response {
  return {
    ok: status >= 200 && status < 300,
    status,
    statusText: status >= 200 && status < 300 ? 'OK' : 'Error',
    json: async () => body,
    text: async () => JSON.stringify(body),
  } as Response;
}

const SAMPLE_TEMPLATE = {
  id: 5,
  templateKey: 'WELCOME_EMAIL',
  templateName: 'Welcome Email',
  subject: 'Welcome to ecoATM',
  contentHtml: '<p>Hi {{name}}</p>',
  contentPlain: 'Hi {{name}}',
  fromAddress: 'noreply@ecoatm.com',
  fromDisplayName: 'ecoATM',
  replyTo: null,
  toDefault: null,
  ccDefault: null,
  bccDefault: null,
  hasAttachment: false,
  enabled: true,
  description: 'Sent on signup',
  createdDate: '2026-06-01T00:00:00Z',
  changedDate: '2026-06-01T00:00:00Z',
};

function fetchCalls() {
  return (global.fetch as ReturnType<typeof vi.fn>).mock.calls;
}

beforeEach(() => {
  vi.restoreAllMocks();
});

describe('Email Admin — Templates tab', () => {
  it('lists templates from GET /templates using camelCase fields', async () => {
    global.fetch = vi.fn().mockResolvedValue(jsonResponse(200, [SAMPLE_TEMPLATE]));

    render(<TemplatesTab onBanner={vi.fn()} />);

    expect(await screen.findByText('WELCOME_EMAIL')).toBeInTheDocument();
    expect(screen.getByText('Welcome Email')).toBeInTheDocument();
    expect(screen.getByText('Welcome to ecoATM')).toBeInTheDocument();
  });

  it('editing a template sends a PUT with a camelCase EmailTemplateUpsert body', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, [SAMPLE_TEMPLATE]))
      .mockResolvedValueOnce(jsonResponse(200, { ...SAMPLE_TEMPLATE, subject: 'Welcome aboard!' }));

    render(<TemplatesTab onBanner={vi.fn()} />);
    fireEvent.click(await screen.findByText('WELCOME_EMAIL'));

    const subjectInput = screen.getByDisplayValue('Welcome to ecoATM');
    fireEvent.change(subjectInput, { target: { value: 'Welcome aboard!' } });
    fireEvent.click(screen.getByRole('button', { name: /^Save Template$/i }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(2));
    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/templates/5');
    expect(init.method).toBe('PUT');

    const body = JSON.parse(init.body as string);
    expect(body.templateKey).toBe('WELCOME_EMAIL');
    expect(body.templateName).toBe('Welcome Email');
    expect(body.subject).toBe('Welcome aboard!');
    expect(body.contentHtml).toBe('<p>Hi {{name}}</p>');
    expect(body).not.toHaveProperty('template_name');
    expect(body).not.toHaveProperty('content_html');
    expect(body).not.toHaveProperty('has_attachment');
  });

  it('creating a template sends a POST with templateKey + contentHtml populated', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, []))
      .mockResolvedValueOnce(jsonResponse(201, { ...SAMPLE_TEMPLATE, id: 9, templateKey: 'NEW_KEY' }));

    render(<TemplatesTab onBanner={vi.fn()} />);
    await screen.findByText(/No email templates found/i);

    fireEvent.click(screen.getByRole('button', { name: /New Template/i }));

    fireEvent.change(screen.getByLabelText('Template Key'), { target: { value: 'NEW_KEY' } });
    fireEvent.change(screen.getByLabelText('Template Name'), { target: { value: 'New Template' } });
    fireEvent.change(screen.getByLabelText('Subject'), { target: { value: 'Hello' } });
    fireEvent.change(screen.getByLabelText('HTML Content'), { target: { value: '<p>Hi</p>' } });

    fireEvent.click(screen.getByRole('button', { name: /Create Template/i }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(2));
    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/templates');
    expect(init.method).toBe('POST');

    const body = JSON.parse(init.body as string);
    expect(body.templateKey).toBe('NEW_KEY');
    expect(body.templateName).toBe('New Template');
    expect(body.contentHtml).toBe('<p>Hi</p>');
  });

  it('deleting a template calls DELETE after confirmation', async () => {
    vi.stubGlobal('confirm', vi.fn().mockReturnValue(true));
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, [SAMPLE_TEMPLATE]))
      .mockResolvedValueOnce(jsonResponse(204, null))
      .mockResolvedValueOnce(jsonResponse(200, []));

    render(<TemplatesTab onBanner={vi.fn()} />);
    await screen.findByText('WELCOME_EMAIL');

    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(3));
    const [url, init] = fetchCalls()[1];
    expect(String(url)).toContain('/api/v1/admin/email/templates/5');
    expect(init.method).toBe('DELETE');
  });

  it('Preview renders {subject, html, text} and Send Test posts toAddress', async () => {
    const onBanner = vi.fn();
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, [SAMPLE_TEMPLATE]))
      .mockResolvedValueOnce(
        jsonResponse(200, { subject: 'Rendered', html: '<p>Hi Bob</p>', text: 'Hi Bob' }),
      )
      .mockResolvedValueOnce(jsonResponse(200, { success: true, logId: 42, status: 'SENT' }));

    render(<TemplatesTab onBanner={onBanner} />);
    fireEvent.click(await screen.findByText('WELCOME_EMAIL'));

    fireEvent.click(screen.getByRole('button', { name: /^Preview$/i }));
    await waitFor(() => expect(screen.getByText('Rendered')).toBeInTheDocument());
    expect(screen.getByText('Hi Bob')).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText('Send test to'), { target: { value: 'qa@ecoatm.com' } });
    fireEvent.click(screen.getByRole('button', { name: /Send Test/i }));

    await waitFor(() => expect(fetchCalls()).toHaveLength(3));
    const [url, init] = fetchCalls()[2];
    expect(String(url)).toContain('/templates/5/send-test');
    const body = JSON.parse(init.body as string);
    expect(body.toAddress).toBe('qa@ecoatm.com');

    await waitFor(() => {
      expect(onBanner).toHaveBeenCalledWith(
        expect.objectContaining({ type: 'success', message: expect.stringContaining('qa@ecoatm.com') }),
      );
    });
  });
});
