// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { BidsSubmittedModal } from './BidsSubmittedModal';

describe('BidsSubmittedModal', () => {
  it('renders with aria-label "Bids submitted"', () => {
    render(<BidsSubmittedModal onClose={() => {}} />);
    expect(screen.getByRole('dialog', { name: 'Bids submitted' })).toBeInTheDocument();
  });

  it('heading contains "Your", "Bids", and "have been Submitted!"', () => {
    render(<BidsSubmittedModal onClose={() => {}} />);
    const heading = screen.getByRole('heading', { level: 2 });
    expect(heading).toHaveTextContent('Your');
    expect(heading).toHaveTextContent('Bids');
    expect(heading).toHaveTextContent('have been Submitted!');
  });

  it('"Bids" in the heading is styled with brand-green color', () => {
    render(<BidsSubmittedModal onClose={() => {}} />);
    const heading = screen.getByRole('heading', { level: 2 });
    const greenSpan = Array.from(heading.querySelectorAll('span')).find(
      (el) => el.textContent?.trim() === 'Bids',
    );
    expect(greenSpan).toBeDefined();
    expect(greenSpan?.style.color).toBe('var(--color-brand-green)');
  });

  it('body copy is present', () => {
    render(<BidsSubmittedModal onClose={() => {}} />);
    expect(
      screen.getByText(
        /Please review your updated bids, quantity caps and resubmit for any changes/,
      ),
    ).toBeInTheDocument();
  });

  it('Close button calls onClose', () => {
    const onClose = vi.fn();
    render(<BidsSubmittedModal onClose={onClose} />);
    fireEvent.click(screen.getByRole('button', { name: 'Close' }));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('Escape key calls onClose', () => {
    const onClose = vi.fn();
    render(<BidsSubmittedModal onClose={onClose} />);
    fireEvent.keyDown(document, { key: 'Escape' });
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('close X button calls onClose', () => {
    const onClose = vi.fn();
    render(<BidsSubmittedModal onClose={onClose} />);
    fireEvent.click(screen.getByRole('button', { name: 'Close dialog' }));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('accepts resubmit prop without changing visible copy', () => {
    render(<BidsSubmittedModal onClose={() => {}} resubmit={true} />);
    const heading = screen.getByRole('heading', { level: 2 });
    expect(heading).toHaveTextContent('have been Submitted!');
  });
});
