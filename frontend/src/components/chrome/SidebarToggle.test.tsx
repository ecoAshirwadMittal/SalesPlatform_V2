// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import SidebarToggle from './SidebarToggle';

describe('SidebarToggle', () => {
  it('renders an expand button with aria-expanded=false when collapsed', () => {
    render(<SidebarToggle collapsed={true} onToggle={vi.fn()} />);
    const btn = screen.getByRole('button', { name: /expand sidebar/i });
    expect(btn).toBeDefined();
    expect(btn.getAttribute('aria-expanded')).toBe('false');
  });

  it('renders a collapse button with aria-expanded=true when expanded', () => {
    render(<SidebarToggle collapsed={false} onToggle={vi.fn()} />);
    const btn = screen.getByRole('button', { name: /collapse sidebar/i });
    expect(btn).toBeDefined();
    expect(btn.getAttribute('aria-expanded')).toBe('true');
  });

  it('calls onToggle when clicked', () => {
    const onToggle = vi.fn();
    render(<SidebarToggle collapsed={false} onToggle={onToggle} />);
    fireEvent.click(screen.getByRole('button'));
    expect(onToggle).toHaveBeenCalledTimes(1);
  });
});
