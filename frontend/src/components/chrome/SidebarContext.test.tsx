// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import { SidebarProvider, useSidebar } from './SidebarContext';

function Consumer() {
  const { collapsed, toggle } = useSidebar();
  return (
    <div>
      <span data-testid="state">{collapsed ? 'collapsed' : 'expanded'}</span>
      <button onClick={toggle}>Toggle</button>
    </div>
  );
}

describe('SidebarContext', () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it('provides default expanded state', () => {
    render(<SidebarProvider><Consumer /></SidebarProvider>);
    expect(screen.getByTestId('state')).toHaveTextContent('expanded');
  });

  it('toggle flips collapsed state', () => {
    render(<SidebarProvider><Consumer /></SidebarProvider>);
    fireEvent.click(screen.getByRole('button', { name: 'Toggle' }));
    expect(screen.getByTestId('state')).toHaveTextContent('collapsed');
    fireEvent.click(screen.getByRole('button', { name: 'Toggle' }));
    expect(screen.getByTestId('state')).toHaveTextContent('expanded');
  });

  it('persists collapsed state to localStorage', () => {
    render(<SidebarProvider><Consumer /></SidebarProvider>);
    fireEvent.click(screen.getByRole('button', { name: 'Toggle' }));
    expect(window.localStorage.getItem('bidder.sidebarCollapsed')).toBe('true');
  });

  it('reads initial state from localStorage', () => {
    window.localStorage.setItem('bidder.sidebarCollapsed', 'true');
    render(<SidebarProvider><Consumer /></SidebarProvider>);
    expect(screen.getByTestId('state')).toHaveTextContent('collapsed');
  });
});
