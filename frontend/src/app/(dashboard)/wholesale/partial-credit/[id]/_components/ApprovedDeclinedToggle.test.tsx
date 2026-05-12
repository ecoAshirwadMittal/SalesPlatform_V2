// @vitest-environment jsdom
import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { ApprovedDeclinedToggle } from './ApprovedDeclinedToggle';

describe('ApprovedDeclinedToggle', () => {
  it('renders both buttons with the count labels', () => {
    render(
      <ApprovedDeclinedToggle
        value="APPROVED"
        onChange={() => {}}
        approvedCount={10}
        declinedCount={6}
      />,
    );

    expect(screen.getByRole('button', { name: 'Approved (10)' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Declined (6)' })).toBeInTheDocument();
  });

  it('marks the active option with aria-pressed=true', () => {
    render(
      <ApprovedDeclinedToggle
        value="DECLINED"
        onChange={() => {}}
        approvedCount={2}
        declinedCount={4}
      />,
    );

    const declined = screen.getByRole('button', { name: 'Declined (4)' });
    const approved = screen.getByRole('button', { name: 'Approved (2)' });
    expect(declined).toHaveAttribute('aria-pressed', 'true');
    expect(approved).toHaveAttribute('aria-pressed', 'false');
  });

  it('calls onChange with the clicked option', () => {
    const onChange = vi.fn();
    render(
      <ApprovedDeclinedToggle
        value="APPROVED"
        onChange={onChange}
        approvedCount={1}
        declinedCount={1}
      />,
    );

    fireEvent.click(screen.getByRole('button', { name: 'Declined (1)' }));
    expect(onChange).toHaveBeenCalledWith('DECLINED');
  });
});
