// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { EmailTemplateEditor } from './EmailTemplateEditor';
import type { EmailTemplateView } from '@/lib/adminEmailTemplatesClient';

// Mock the preview HTTP call. The editor renders deterministic UI when
// the preview tab is opened; we assert on the rendered output rather
// than the wire shape.
vi.mock('@/lib/adminEmailTemplatesClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/adminEmailTemplatesClient')>(
    '@/lib/adminEmailTemplatesClient',
  );
  return {
    ...actual,
    previewEmailTemplate: vi.fn().mockResolvedValue({
      subject: 'Rendered subject',
      bodyHtml: '<p>Rendered body</p>',
      bodyText: 'Rendered text',
    }),
  };
});

const sampleRow: EmailTemplateView = {
  id: 1,
  templateKey: 'ReviewCompleted_Approved',
  subject: 'Original subject',
  bodyHtml: '<p>Original html</p>',
  bodyText: 'Original text',
  enabled: true,
  description: 'Sample',
  changedDate: '2026-05-12T00:00:00Z',
  changedById: 99,
};

describe('EmailTemplateEditor', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders Edit tab by default with current values', () => {
    render(<EmailTemplateEditor row={sampleRow} onSave={vi.fn()} onClose={vi.fn()} />);

    const subjectInput = screen.getByLabelText(/Subject/i) as HTMLInputElement;
    expect(subjectInput.value).toBe('Original subject');
    const htmlArea = screen.getByLabelText(/Body \(HTML\)/i) as HTMLTextAreaElement;
    expect(htmlArea.value).toBe('<p>Original html</p>');
  });

  it('Save button is disabled when no changes have been made', () => {
    render(<EmailTemplateEditor row={sampleRow} onSave={vi.fn()} onClose={vi.fn()} />);

    const save = screen.getByRole('button', { name: /^Save$/i });
    expect(save).toBeDisabled();
  });

  it('Save button enables when subject changes; calls onSave with only that field', async () => {
    const onSave = vi.fn().mockResolvedValue(undefined);
    const onClose = vi.fn();
    render(<EmailTemplateEditor row={sampleRow} onSave={onSave} onClose={onClose} />);

    const subjectInput = screen.getByLabelText(/Subject/i);
    fireEvent.change(subjectInput, { target: { value: 'New subject' } });

    const save = screen.getByRole('button', { name: /^Save$/i });
    expect(save).not.toBeDisabled();
    fireEvent.click(save);

    await waitFor(() => {
      expect(onSave).toHaveBeenCalledWith(1, { subject: 'New subject' });
    });
    // After a successful save the editor closes itself.
    await waitFor(() => {
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('toggling Enabled is captured in the patch', async () => {
    const onSave = vi.fn().mockResolvedValue(undefined);
    render(<EmailTemplateEditor row={sampleRow} onSave={onSave} onClose={vi.fn()} />);

    const toggle = screen.getByRole('checkbox');
    fireEvent.click(toggle);

    fireEvent.click(screen.getByRole('button', { name: /^Save$/i }));

    await waitFor(() => {
      expect(onSave).toHaveBeenCalledWith(1, { enabled: false });
    });
  });

  it('switching to Preview tab loads and renders the rendered output', async () => {
    render(<EmailTemplateEditor row={sampleRow} onSave={vi.fn()} onClose={vi.fn()} />);

    fireEvent.click(screen.getByRole('tab', { name: /Preview/i }));

    // Preview HTML rendered into the panel via dangerouslySetInnerHTML.
    await waitFor(() => {
      expect(screen.getByText('Rendered subject')).toBeInTheDocument();
      expect(screen.getByText('Rendered body')).toBeInTheDocument();
    });
  });

  it('shows a "preview reflects saved template" hint when there are unsaved edits', async () => {
    render(<EmailTemplateEditor row={sampleRow} onSave={vi.fn()} onClose={vi.fn()} />);

    fireEvent.change(screen.getByLabelText(/Subject/i), {
      target: { value: 'Dirty subject' },
    });
    fireEvent.click(screen.getByRole('tab', { name: /Preview/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Preview reflects the saved template/i),
      ).toBeInTheDocument();
    });
  });

  it('Cancel button invokes onClose without saving', () => {
    const onSave = vi.fn();
    const onClose = vi.fn();
    render(<EmailTemplateEditor row={sampleRow} onSave={onSave} onClose={onClose} />);

    fireEvent.click(screen.getByRole('button', { name: /Cancel/i }));

    expect(onSave).not.toHaveBeenCalled();
    expect(onClose).toHaveBeenCalled();
  });

  it('surfaces save errors inline; editor stays open', async () => {
    const onSave = vi.fn().mockRejectedValue(new Error('server boom'));
    const onClose = vi.fn();
    render(<EmailTemplateEditor row={sampleRow} onSave={onSave} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText(/Subject/i), { target: { value: 'X' } });
    fireEvent.click(screen.getByRole('button', { name: /^Save$/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('server boom');
    });
    expect(onClose).not.toHaveBeenCalled();
  });
});
