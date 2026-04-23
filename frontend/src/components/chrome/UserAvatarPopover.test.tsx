// @vitest-environment jsdom
import { render, screen, fireEvent, act } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import UserAvatarPopover from './UserAvatarPopover';

const defaultUser = { fullName: 'Akshay Singhal', initials: 'AS' };
const defaultItems = [
  { label: 'Submit Feedback', onClick: vi.fn() },
  { label: 'Logout', onClick: vi.fn() },
];

describe('UserAvatarPopover', () => {
  it('renders the user full name and initials', () => {
    render(<UserAvatarPopover user={defaultUser} items={defaultItems} />);
    expect(screen.getByText('Akshay Singhal')).toBeDefined();
    expect(screen.getByText('AS')).toBeDefined();
  });

  it('popover is hidden on initial render', () => {
    render(<UserAvatarPopover user={defaultUser} items={defaultItems} />);
    expect(screen.queryByRole('menu')).toBeNull();
  });

  it('opens the popover when the avatar button is clicked', () => {
    render(<UserAvatarPopover user={defaultUser} items={defaultItems} />);
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    expect(screen.getByRole('menu')).toBeDefined();
    expect(screen.getByText('Submit Feedback')).toBeDefined();
    expect(screen.getByText('Logout')).toBeDefined();
  });

  it('calls item onClick and closes the popover', () => {
    const logoutFn = vi.fn();
    const items = [
      { label: 'Submit Feedback', onClick: vi.fn() },
      { label: 'Logout', onClick: logoutFn },
    ];
    render(<UserAvatarPopover user={defaultUser} items={items} />);
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    fireEvent.click(screen.getByRole('menuitem', { name: 'Logout' }));
    expect(logoutFn).toHaveBeenCalledTimes(1);
    expect(screen.queryByRole('menu')).toBeNull();
  });

  it('closes the popover when Escape is pressed', () => {
    render(<UserAvatarPopover user={defaultUser} items={defaultItems} />);
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    expect(screen.getByRole('menu')).toBeDefined();
    act(() => {
      fireEvent.keyDown(document, { key: 'Escape' });
    });
    expect(screen.queryByRole('menu')).toBeNull();
  });

  it('renders exactly the items passed in (no hard-coded extras)', () => {
    const items = [{ label: 'Only Item', onClick: vi.fn() }];
    render(<UserAvatarPopover user={defaultUser} items={items} />);
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    const menuItems = screen.getAllByRole('menuitem');
    expect(menuItems).toHaveLength(1);
    expect(menuItems[0].textContent).toBe('Only Item');
  });

  it('avatar button has correct aria-expanded attribute', () => {
    render(<UserAvatarPopover user={defaultUser} items={defaultItems} />);
    const btn = screen.getByRole('button', { name: /user menu/i });
    expect(btn.getAttribute('aria-expanded')).toBe('false');
    fireEvent.click(btn);
    expect(btn.getAttribute('aria-expanded')).toBe('true');
  });
});
