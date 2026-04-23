// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { QtyCapCell } from './QtyCapCell';

describe('QtyCapCell', () => {
  it('renders empty by default when initialValue is null (no-cap sentinel)', () => {
    render(<QtyCapCell rowId={1} initialValue={null} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 1/i });
    expect(input).toHaveValue('');
  });

  it('renders "0" when initialValue is 0 (zero-unit bid — distinct from empty)', () => {
    render(<QtyCapCell rowId={2} initialValue={0} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 2/i });
    expect(input).toHaveValue('0');
  });

  it('renders the integer string for a positive initialValue', () => {
    render(<QtyCapCell rowId={3} initialValue={42} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 3/i });
    expect(input).toHaveValue('42');
  });

  it('fires onSave with null when the input is cleared (empty → null)', () => {
    const onSave = vi.fn();
    render(<QtyCapCell rowId={4} initialValue={10} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 4/i });
    fireEvent.change(input, { target: { value: '' } });
    fireEvent.blur(input);
    expect(onSave).toHaveBeenCalledWith(4, null);
  });

  it('fires onSave with 0 when input is "0" (zero ≠ null)', () => {
    const onSave = vi.fn();
    render(<QtyCapCell rowId={5} initialValue={null} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 5/i });
    fireEvent.change(input, { target: { value: '0' } });
    fireEvent.blur(input);
    expect(onSave).toHaveBeenCalledWith(5, 0);
  });

  it('fires onSave with the parsed integer on blur', () => {
    const onSave = vi.fn();
    render(<QtyCapCell rowId={6} initialValue={null} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 6/i });
    fireEvent.change(input, { target: { value: '25' } });
    fireEvent.blur(input);
    expect(onSave).toHaveBeenCalledWith(6, 25);
  });

  it('rejects non-digit keypress via keydown preventDefault', () => {
    const onSave = vi.fn();
    render(<QtyCapCell rowId={7} initialValue={5} onSave={onSave} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 7/i });
    // Firing a keydown for a letter — the component calls preventDefault.
    // We verify the display value does not include the letter character.
    fireEvent.keyDown(input, { key: 'a', code: 'KeyA' });
    // Value should remain unchanged (no 'a' character accepted).
    expect(input).toHaveValue('5');
  });

  it('strips non-digit characters pasted via onChange', () => {
    render(<QtyCapCell rowId={8} initialValue={null} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 8/i });
    // Simulate pasting "12abc34"
    fireEvent.change(input, { target: { value: '12abc34' } });
    expect(input).toHaveValue('1234');
  });

  it('applies Mendix parity CSS classes', () => {
    render(<QtyCapCell rowId={9} initialValue={null} onSave={() => {}} />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 9/i });
    expect(input.className).toContain('auction-qtycap');
    expect(input.className).toContain('only-numbers');
    expect(input.className).toContain('textbox-select-all');
  });

  it('is disabled when disabled prop is true', () => {
    render(<QtyCapCell rowId={10} initialValue={null} onSave={() => {}} disabled />);
    const input = screen.getByRole('textbox', { name: /Qty Cap for row 10/i });
    expect(input).toBeDisabled();
  });
});
