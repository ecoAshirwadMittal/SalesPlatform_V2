// @vitest-environment jsdom
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { BuyerLineSection } from './BuyerLineSection';
import type { CreditRequestDetail } from '@/lib/partialCreditClient';

function detail(overrides: Partial<CreditRequestDetail> = {}): CreditRequestDetail {
  return {
    id: 1,
    requestNumber: 'PCR-1',
    orderNumber: 'SO-1',
    partyName: 'Acme',
    orderCreatedDate: null,
    orderShippedDate: null,
    systemStatus: 'PENDING_APPROVAL',
    displayStatus: 'Pending Approval',
    shipmentDamaged: 'NO',
    hasMissingDevice: false,
    hasWrongDevice: false,
    hasEncumberedDevice: false,
    totalDevices: 0,
    requestedTotal: 0,
    approvedTotal: null,
    reviewedById: null,
    reviewCompletedOn: null,
    missingLines: [],
    wrongLines: [],
    encumberedLines: [],
    ...overrides,
  };
}

describe('BuyerLineSection', () => {
  it('returns null when the corresponding reason flag is off', () => {
    const { container } = render(
      <BuyerLineSection kind="MISSING" detail={detail()} />,
    );
    expect(container.firstChild).toBeNull();
  });

  it('returns null when the reason flag is on but there are no lines', () => {
    const { container } = render(
      <BuyerLineSection
        kind="WRONG"
        detail={detail({ hasWrongDevice: true, wrongLines: [] })}
      />,
    );
    expect(container.firstChild).toBeNull();
  });

  it('renders missing-device lines without a decision column while pending', () => {
    render(
      <BuyerLineSection
        kind="MISSING"
        detail={detail({
          hasMissingDevice: true,
          systemStatus: 'PENDING_APPROVAL',
          missingLines: [
            {
              id: 1,
              barcodeSubmitted: 'BC-100',
              brand: 'Apple',
              model: 'iPhone 12',
              grade: 'A',
              boxNumber: null,
              amountPaid: 50,
              shipStatus: null,
              lineStatus: 'VALID',
              reviewDecision: 'PENDING',
              amountToCredit: null,
            },
          ],
        })}
      />,
    );

    expect(screen.getByText('Missing Device')).toBeInTheDocument();
    expect(screen.getByText('BC-100')).toBeInTheDocument();
    expect(screen.getByText('iPhone 12')).toBeInTheDocument();
    // Decision column header must NOT render pre-finalisation.
    expect(screen.queryByText('Decision')).not.toBeInTheDocument();
  });

  it('renders decision column with Accepted/Declined pills after the request is APPROVED', () => {
    render(
      <BuyerLineSection
        kind="WRONG"
        detail={detail({
          hasWrongDevice: true,
          systemStatus: 'APPROVED',
          wrongLines: [
            {
              id: 1,
              expectedBarcode: 'BC-X',
              expectedBrand: 'Apple',
              expectedModel: 'iPhone 12',
              expectedGrade: 'A',
              expectedAmountPaid: 50,
              actualImeiOrModel: null,
              actualBrand: 'Apple',
              actualModel: 'iPhone XR',
              actualGrade: 'B',
              latestPrice: 30,
              actionRecommendation: 'ACCEPT',
              lineStatus: 'VALID',
              reviewDecision: 'ACCEPTED',
              amountToCredit: 20,
            },
            {
              id: 2,
              expectedBarcode: 'BC-Y',
              expectedBrand: 'Samsung',
              expectedModel: 'Galaxy S20',
              expectedGrade: 'A',
              expectedAmountPaid: 80,
              actualImeiOrModel: null,
              actualBrand: 'Samsung',
              actualModel: 'Galaxy S10',
              actualGrade: 'B',
              latestPrice: 60,
              actionRecommendation: 'DECLINE',
              lineStatus: 'VALID',
              reviewDecision: 'DECLINED',
              amountToCredit: 0,
            },
          ],
        })}
      />,
    );

    expect(screen.getByText('Decision')).toBeInTheDocument();
    expect(screen.getByText('Accepted')).toBeInTheDocument();
    expect(screen.getByText('Declined')).toBeInTheDocument();
  });

  it('finalised request with a stray PENDING line renders a neutral dash, not "Pending"', () => {
    render(
      <BuyerLineSection
        kind="MISSING"
        detail={detail({
          hasMissingDevice: true,
          systemStatus: 'APPROVED',
          missingLines: [
            {
              id: 1,
              barcodeSubmitted: 'BC-Z',
              brand: 'Apple',
              model: 'iPhone 11',
              grade: 'A',
              boxNumber: null,
              amountPaid: 40,
              shipStatus: null,
              lineStatus: 'VALID',
              reviewDecision: 'PENDING',
              amountToCredit: null,
            },
          ],
        })}
      />,
    );

    // The decision-column header should be present, but the literal
    // word "Pending" must never leak into the buyer-side table.
    expect(screen.getByText('Decision')).toBeInTheDocument();
    expect(screen.queryByText('Pending')).not.toBeInTheDocument();
    expect(screen.getByLabelText('No decision')).toBeInTheDocument();
  });
});
