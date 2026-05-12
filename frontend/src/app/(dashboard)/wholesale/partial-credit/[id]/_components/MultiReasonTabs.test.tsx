// @vitest-environment jsdom
import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { MultiReasonTabs } from './MultiReasonTabs';

describe('MultiReasonTabs', () => {
  it('renders one tab per supplied entry with a [role="tab"] surface', () => {
    render(
      <MultiReasonTabs
        active="MISSING"
        onChange={() => {}}
        tabs={[
          { key: 'MISSING', label: 'Missing Devices', count: 5 },
          { key: 'WRONG', label: 'Wrong Devices', count: 3 },
        ]}
      />,
    );

    const tabs = screen.getAllByRole('tab');
    expect(tabs).toHaveLength(2);
    expect(screen.getByText('Missing Devices (5)')).toBeInTheDocument();
    expect(screen.getByText('Wrong Devices (3)')).toBeInTheDocument();
  });

  it('marks the active tab with aria-selected=true', () => {
    render(
      <MultiReasonTabs
        active="WRONG"
        onChange={() => {}}
        tabs={[
          { key: 'MISSING', label: 'Missing Devices', count: 1 },
          { key: 'WRONG', label: 'Wrong Devices', count: 2 },
        ]}
      />,
    );

    const wrong = screen.getByRole('tab', { name: /Wrong Devices/ });
    expect(wrong).toHaveAttribute('aria-selected', 'true');
    const missing = screen.getByRole('tab', { name: /Missing Devices/ });
    expect(missing).toHaveAttribute('aria-selected', 'false');
  });

  it('invokes onChange with the clicked key', () => {
    const onChange = vi.fn();
    render(
      <MultiReasonTabs
        active="MISSING"
        onChange={onChange}
        tabs={[
          { key: 'MISSING', label: 'Missing Devices', count: 1 },
          { key: 'ENCUMBERED', label: 'Encumbered Devices', count: 1 },
        ]}
      />,
    );

    fireEvent.click(screen.getByRole('tab', { name: /Encumbered Devices/ }));
    expect(onChange).toHaveBeenCalledWith('ENCUMBERED');
  });
});
