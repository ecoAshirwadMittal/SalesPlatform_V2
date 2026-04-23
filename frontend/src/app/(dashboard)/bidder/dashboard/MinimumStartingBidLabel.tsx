'use client';

interface MinimumStartingBidLabelProps {
  amount?: number;
}

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

export function MinimumStartingBidLabel({ amount = 2.5 }: MinimumStartingBidLabelProps) {
  return (
    <span
      style={{
        color: 'var(--color-warning-red)',
        fontWeight: 500,
        fontSize: 'var(--font-size-body)',
      }}
    >
      Minimum starting bid - {currencyFormatter.format(amount)}
    </span>
  );
}
