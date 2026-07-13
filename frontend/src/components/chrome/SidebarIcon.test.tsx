// @vitest-environment jsdom
import { render } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import SidebarIcon from './SidebarIcon';

describe('SidebarIcon', () => {
  it('renders the bespoke legacy asset for the given name', () => {
    const { container } = render(<SidebarIcon name="users" />);
    const img = container.querySelector('img');
    expect(img).not.toBeNull();
    expect(img?.getAttribute('src')).toBe('/icons/sidebar/users.svg');
  });

  it('is decorative — empty alt + aria-hidden (the nav label is the accessible name)', () => {
    const { container } = render(<SidebarIcon name="auction" />);
    const img = container.querySelector('img')!;
    expect(img.getAttribute('alt')).toBe('');
    expect(img.getAttribute('aria-hidden')).toBe('true');
  });

  it('renders at the 34x34 legacy ring-diameter box', () => {
    const { container } = render(<SidebarIcon name="reports" />);
    const img = container.querySelector('img')!;
    expect(img.getAttribute('width')).toBe('34');
    expect(img.getAttribute('height')).toBe('34');
  });

  it.each([
    'users', 'buyers', 'inventory', 'purchase-order', 'reserve-bids',
    'auction', 'bid-as-bidder', 'credit-requests', 'reports',
    'settings', 'admin', 'buyer-guide',
  ])('maps %s to /icons/sidebar/%s.svg', (name) => {
    const { container } = render(<SidebarIcon name={name} />);
    expect(container.querySelector('img')?.getAttribute('src')).toBe(`/icons/sidebar/${name}.svg`);
  });
});
