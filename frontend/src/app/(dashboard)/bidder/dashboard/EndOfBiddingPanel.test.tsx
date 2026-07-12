// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { EndOfBiddingPanel } from './EndOfBiddingPanel';

describe('EndOfBiddingPanel', () => {
  it('ended state: renders auction heading, "Bidding has ended.", subtitle and download button (BDD-P1)', () => {
    const onClick = vi.fn();
    render(
      <EndOfBiddingPanel
        auctionTitle="Auction 2026 / Wk13"
        subtitle="Your bids from round 1 can be found below."
        actions={[{ label: 'Download your Round 1 Bids', onClick }]}
      />,
    );

    // Legacy heading "Auction {year} / Wk{week}".
    expect(
      screen.getByRole('heading', { name: /^Auction 2026 \/ Wk13$/ }),
    ).toBeInTheDocument();
    // "Bidding has ended." — "Bidding" is a span, so the accessible name joins.
    expect(screen.getByRole('heading', { name: /Bidding has ended\./ })).toBeInTheDocument();
    expect(
      screen.getByText('Your bids from round 1 can be found below.'),
    ).toBeInTheDocument();

    const button = screen.getByRole('button', { name: 'Download your Round 1 Bids' });
    fireEvent.click(button);
    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('ended state: renders one download button per participated round', () => {
    render(
      <EndOfBiddingPanel
        auctionTitle="Auction 2026 / Wk13"
        subtitle="Your bids from rounds 1 and 2 can be found below."
        actions={[
          { label: 'Download your Round 1 Bids', onClick: vi.fn() },
          { label: 'Download your Round 2 Bids', onClick: vi.fn() },
        ]}
      />,
    );

    expect(
      screen.getByRole('button', { name: 'Download your Round 1 Bids' }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: 'Download your Round 2 Bids' }),
    ).toBeInTheDocument();
  });

  it('truly-empty state: bare "Bidding has ended." with no auction heading or download button', () => {
    render(<EndOfBiddingPanel subtitle="No scheduled auction is available." />);

    expect(screen.getByRole('heading', { name: /Bidding has ended\./ })).toBeInTheDocument();
    expect(screen.getByText('No scheduled auction is available.')).toBeInTheDocument();
    // The kept truly-empty branch has no auction heading and no download affordance.
    expect(screen.queryByRole('heading', { name: /^Auction / })).not.toBeInTheDocument();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });
});
