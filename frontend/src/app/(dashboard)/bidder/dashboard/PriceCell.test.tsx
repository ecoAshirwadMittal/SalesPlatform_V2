// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { PriceCell } from './PriceCell';

describe('PriceCell', () => {
  it('renders $ 0.00 by default when initialValue is 0', () => {
    render(<PriceCell rowId={1} initialValue={0} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Price for row 1/i });
    expect(input).toHaveValue('$ 0.00');
  });

  it('renders the formatted dollar value for a non-zero initialValue', () => {
    render(<PriceCell rowId={2} initialValue={42.5} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Price for row 2/i });
    expect(input).toHaveValue('$ 42.50');
  });

  it('strips $ on focus so the user edits the numeric portion', () => {
    render(<PriceCell rowId={3} initialValue={15} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Price for row 3/i });
    fireEvent.focus(input);
    // After focus the display should contain only the numeric part (no $ prefix).
    expect(input).not.toHaveValue('$ 15.00');
    expect(input.value).toMatch(/^[\d.]+$/);
  });

  it('fires onSave with the parsed number on blur', () => {
    const onSave = vi.fn();
    render(<PriceCell rowId={4} initialValue={0} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Price for row 4/i });
    fireEvent.focus(input);
    fireEvent.change(input, { target: { value: '38.75' } });
    fireEvent.blur(input);
    expect(onSave).toHaveBeenCalledWith(4, 38.75);
  });

  it('reformats to dollar display on blur', () => {
    const onSave = vi.fn();
    render(<PriceCell rowId={5} initialValue={0} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Price for row 5/i });
    fireEvent.focus(input);
    fireEvent.change(input, { target: { value: '20' } });
    fireEvent.blur(input);
    expect(input).toHaveValue('$ 20.00');
  });

  it('clamps negative values to 0 on blur', () => {
    const onSave = vi.fn();
    render(<PriceCell rowId={6} initialValue={5} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Price for row 6/i });
    fireEvent.focus(input);
    // Negative amounts should be clamped
    fireEvent.change(input, { target: { value: '-5' } });
    fireEvent.blur(input);
    expect(onSave).toHaveBeenCalledWith(6, 0);
    expect(input).toHaveValue('$ 0.00');
  });

  it('applies Mendix parity CSS classes', () => {
    render(<PriceCell rowId={7} initialValue={0} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Price for row 7/i });
    expect(input.className).toContain('auction-price');
    expect(input.className).toContain('textbox-select-all');
    expect(input.className).toContain('text-dollar');
  });

  it('is disabled when disabled prop is true', () => {
    render(<PriceCell rowId={8} initialValue={0} onSave={() => {}} disabled />);
    const input = screen.getByRole('textbox', { name: /Price for row 8/i });
    expect(input).toBeDisabled();
  });
});
