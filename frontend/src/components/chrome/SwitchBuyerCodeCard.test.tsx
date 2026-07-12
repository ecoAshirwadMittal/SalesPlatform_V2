// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import SwitchBuyerCodeCard from './SwitchBuyerCodeCard';
import type { ActiveBuyerCode } from '@/lib/activeBuyerCode';

const mockCode: ActiveBuyerCode = {
  id: 42,
  code: 'HN',
  buyerName: 'Nadia Boonnayanont',
  buyerCodeType: 'Wholesale',
  codeType: 'AUCTION',
};

describe('SwitchBuyerCodeCard', () => {
  it('renders the buyer name and code when an active code is provided', () => {
    render(<SwitchBuyerCodeCard activeBuyerCode={mockCode} onSwitch={vi.fn()} />);
    expect(screen.getByText('Nadia Boonnayanont')).toBeDefined();
    expect(screen.getByText('HN')).toBeDefined();
  });

  it('exposes a "Switch Buyer Code" button (accessible name intact)', () => {
    render(<SwitchBuyerCodeCard activeBuyerCode={mockCode} onSwitch={vi.fn()} />);
    // "Switch" (accent span) + " Buyer Code" compose the accessible name.
    expect(screen.getByRole('button', { name: 'Switch Buyer Code' })).toBeDefined();
  });

  it('calls onSwitch when the label is clicked', () => {
    const onSwitch = vi.fn();
    render(<SwitchBuyerCodeCard activeBuyerCode={mockCode} onSwitch={onSwitch} />);
    fireEvent.click(screen.getByRole('button', { name: 'Switch Buyer Code' }));
    expect(onSwitch).toHaveBeenCalledTimes(1);
  });

  it('renders nothing when there is no active buyer code', () => {
    const { container } = render(
      <SwitchBuyerCodeCard activeBuyerCode={null} onSwitch={vi.fn()} />
    );
    expect(container.firstChild).toBeNull();
    expect(screen.queryByText('Switch Buyer Code')).toBeNull();
  });
});
