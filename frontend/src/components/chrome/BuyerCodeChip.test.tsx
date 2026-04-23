// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import BuyerCodeChip from './BuyerCodeChip';

describe('BuyerCodeChip', () => {
  it('renders code and company name', () => {
    render(<BuyerCodeChip code="AD" companyName="CHS Technology (HK) Ltd" />);
    expect(screen.getByText('AD')).toBeDefined();
    expect(screen.getByText('CHS Technology (HK) Ltd')).toBeDefined();
  });

  it('framed variant renders as a non-interactive div when no onClick provided', () => {
    render(<BuyerCodeChip code="AD" companyName="Acme" variant="framed" />);
    // No button should be present when there is no click handler
    expect(screen.queryByRole('button')).toBeNull();
  });

  it('filled variant with onClick renders as a button and calls onClick', () => {
    const onClick = vi.fn();
    render(
      <BuyerCodeChip code="DDWS" companyName="Acme" variant="filled" onClick={onClick} />
    );
    const btn = screen.getByRole('button');
    expect(btn).toBeDefined();
    fireEvent.click(btn);
    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('filled variant shows the arrow icon', () => {
    render(<BuyerCodeChip code="DDWS" companyName="Acme" variant="filled" onClick={vi.fn()} />);
    expect(screen.getByText('→')).toBeDefined();
  });

  it('framed variant does not show the arrow icon', () => {
    render(<BuyerCodeChip code="AD" companyName="Acme" variant="framed" />);
    expect(screen.queryByText('→')).toBeNull();
  });
});
