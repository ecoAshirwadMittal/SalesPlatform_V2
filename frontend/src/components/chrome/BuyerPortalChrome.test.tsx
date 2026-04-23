// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import BuyerPortalChrome from './BuyerPortalChrome';
import type { AuthUser } from '@/lib/session';
import type { ActiveBuyerCode } from '@/lib/activeBuyerCode';

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

const mockCode: ActiveBuyerCode = {
  id: 42,
  code: 'AD',
  buyerName: 'CHS Technology (HK) Ltd',
  buyerCodeType: 'Wholesale',
  codeType: 'AUCTION',
};

const defaultPopoverItems = [
  { label: 'Submit Feedback', onClick: vi.fn() },
  { label: 'Logout', onClick: vi.fn() },
];

describe('BuyerPortalChrome', () => {
  it('renders the logo image', () => {
    render(
      <BuyerPortalChrome
        activeBuyerCode={mockCode}
        user={mockUser}
        onSwitchBuyerCode={vi.fn()}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    const logo = screen.getByAltText('ecoATM DIRECT');
    expect(logo).toBeDefined();
  });

  it('renders the Switch Buyer Code link and chip when activeBuyerCode is provided', () => {
    render(
      <BuyerPortalChrome
        activeBuyerCode={mockCode}
        user={mockUser}
        onSwitchBuyerCode={vi.fn()}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    expect(screen.getByText('Switch Buyer Code')).toBeDefined();
    expect(screen.getByText('AD')).toBeDefined();
    expect(screen.getByText('CHS Technology (HK) Ltd')).toBeDefined();
  });

  it('hides the Switch Buyer Code link and chip when activeBuyerCode is null', () => {
    render(
      <BuyerPortalChrome
        activeBuyerCode={null}
        user={mockUser}
        onSwitchBuyerCode={vi.fn()}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    expect(screen.queryByText('Switch Buyer Code')).toBeNull();
    expect(screen.queryByText('AD')).toBeNull();
  });

  it('calls onSwitchBuyerCode when the link is clicked', () => {
    const onSwitch = vi.fn();
    render(
      <BuyerPortalChrome
        activeBuyerCode={mockCode}
        user={mockUser}
        onSwitchBuyerCode={onSwitch}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    fireEvent.click(screen.getByText('Switch Buyer Code'));
    expect(onSwitch).toHaveBeenCalledTimes(1);
  });

  it('renders the avatar with user initials', () => {
    render(
      <BuyerPortalChrome
        activeBuyerCode={mockCode}
        user={mockUser}
        onSwitchBuyerCode={vi.fn()}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    expect(screen.getByText('AS')).toBeDefined();
  });

  it('avatar popover shows exactly the passed items', () => {
    render(
      <BuyerPortalChrome
        activeBuyerCode={mockCode}
        user={mockUser}
        onSwitchBuyerCode={vi.fn()}
        avatarPopoverItems={defaultPopoverItems}
      />
    );
    fireEvent.click(screen.getByRole('button', { name: /user menu/i }));
    const menuItems = screen.getAllByRole('menuitem');
    expect(menuItems).toHaveLength(2);
    expect(menuItems[0].textContent).toBe('Submit Feedback');
    expect(menuItems[1].textContent).toBe('Logout');
  });
});
