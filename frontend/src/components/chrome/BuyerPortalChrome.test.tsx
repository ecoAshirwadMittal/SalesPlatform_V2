// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import BuyerPortalChrome from './BuyerPortalChrome';
import type { AuthUser } from '@/lib/session';

// next/image in a jsdom environment renders as a standard <img>.
// No mocking needed — the tests assert on accessible text and roles.

const mockUser: AuthUser = {
  userId: 1,
  firstName: 'Akshay',
  lastName: 'Singhal',
  fullName: 'Akshay Singhal',
  email: 'akshay@test.com',
  initials: 'AS',
};

const defaultPopoverItems = [
  { label: 'Submit Feedback', onClick: vi.fn() },
  { label: 'Logout', onClick: vi.fn() },
];

// BDD-P3 (2026-07-12): the chrome is now logo + identity only. The
// "Switch Buyer Code" link + code chip moved to the in-content
// <SwitchBuyerCodeCard> (covered by SwitchBuyerCodeCard.test.tsx).
describe('BuyerPortalChrome', () => {
  it('renders the ecoATM DIRECT logo image', () => {
    render(
      <BuyerPortalChrome user={mockUser} avatarPopoverItems={defaultPopoverItems} />
    );
    const logo = screen.getByAltText('ecoATM DIRECT');
    expect(logo).toBeDefined();
  });

  it('renders the avatar with user initials', () => {
    render(
      <BuyerPortalChrome user={mockUser} avatarPopoverItems={defaultPopoverItems} />
    );
    expect(screen.getByText('AS')).toBeDefined();
  });

  it('renders the user full name next to the avatar', () => {
    render(
      <BuyerPortalChrome user={mockUser} avatarPopoverItems={defaultPopoverItems} />
    );
    expect(screen.getByText('Akshay Singhal')).toBeDefined();
  });

  it('does NOT render a Switch Buyer Code control (moved to the in-content card)', () => {
    render(
      <BuyerPortalChrome user={mockUser} avatarPopoverItems={defaultPopoverItems} />
    );
    expect(screen.queryByText('Switch Buyer Code')).toBeNull();
  });

  it('avatar popover shows exactly the passed items', () => {
    render(
      <BuyerPortalChrome user={mockUser} avatarPopoverItems={defaultPopoverItems} />
    );
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    const menuItems = screen.getAllByRole('menuitem');
    expect(menuItems).toHaveLength(2);
    expect(menuItems[0].textContent).toBe('Submit Feedback');
    expect(menuItems[1].textContent).toBe('Logout');
  });
});
