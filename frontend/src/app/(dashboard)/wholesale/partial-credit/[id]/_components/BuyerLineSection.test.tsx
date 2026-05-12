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

  it('renders missing-device lines with Box Number column instead of Grade', () => {
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
              boxNumber: 'BOX-42',
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

    // Plural heading + count badge.
    expect(screen.getByText(/Missing Devices/)).toBeInTheDocument();
    expect(screen.getByText('(1)')).toBeInTheDocument();
    // Box Number replaces Grade.
    expect(screen.getByText('Box Number')).toBeInTheDocument();
    expect(screen.queryByText('Grade')).not.toBeInTheDocument();
    expect(screen.getByText('BOX-42')).toBeInTheDocument();
    // Model column renamed to "Model Description".
    expect(screen.getByText('Model Description')).toBeInTheDocument();
    expect(screen.getByText('BC-100')).toBeInTheDocument();
    expect(screen.getByText('iPhone 12')).toBeInTheDocument();
    // Decision column header must NOT render pre-finalisation.
    expect(screen.queryByText('Decision')).not.toBeInTheDocument();
  });

  it('wrong-device table uses Figma columns (no Latest Price, includes Photos)', () => {
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
              actualImeiOrModel: 'IMEI-999',
              actualBrand: 'Apple',
              actualModel: 'iPhone XR',
              actualGrade: 'B',
              latestPrice: 30,
              actionRecommendation: 'ACCEPT',
              lineStatus: 'VALID',
              reviewDecision: 'ACCEPTED',
              amountToCredit: 20,
            },
          ],
        })}
      />,
    );

    expect(screen.getByText('Expected Device Barcode')).toBeInTheDocument();
    expect(screen.getByText('Expected Device Description')).toBeInTheDocument();
    expect(screen.getByText('Received Device IMEI/Serial')).toBeInTheDocument();
    expect(screen.getByText('Received Device Description')).toBeInTheDocument();
    expect(screen.getByText('Photos')).toBeInTheDocument();
    expect(screen.queryByText('Latest Price')).not.toBeInTheDocument();
    // DTO still uses actualImeiOrModel until a dedicated receivedImei
    // field lands — we surface it under the new column header for now.
    expect(screen.getByText('IMEI-999')).toBeInTheDocument();
    // Final-state pill renders.
    expect(screen.getByText('Decision')).toBeInTheDocument();
    expect(screen.getByText('Accepted')).toBeInTheDocument();
  });

  it('encumbered-device table collapses Brand+Model and Amount Paid + Actual Value', () => {
    render(
      <BuyerLineSection
        kind="ENCUMBERED"
        detail={detail({
          hasEncumberedDevice: true,
          systemStatus: 'PENDING_APPROVAL',
          encumberedLines: [
            {
              id: 1,
              barcodeSubmitted: 'BC-E1',
              brand: 'Samsung',
              model: 'Galaxy S20',
              grade: 'A',
              boxNumber: null,
              amountPaid: 200,
              prologResult: null,
              actualValue: 120,
              lineStatus: 'VALID',
              reviewDecision: 'PENDING',
              amountToCredit: null,
            },
          ],
        })}
      />,
    );

    expect(screen.getByText(/Encumbered Devices/)).toBeInTheDocument();
    // Three columns: Barcode | Device Description | Credit Due.
    expect(screen.getByText('Barcode')).toBeInTheDocument();
    expect(screen.getByText('Device Description')).toBeInTheDocument();
    expect(screen.getByText('Credit Due')).toBeInTheDocument();
    // Brand+Model collapsed.
    expect(screen.getByText('Samsung Galaxy S20')).toBeInTheDocument();
    // Credit Due falls back to amountPaid - actualValue when amountToCredit is null.
    expect(screen.getByText('$80.00')).toBeInTheDocument();
  });

  it('renders Accepted/Declined pills after the request is APPROVED', () => {
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

  it('filters rows by decision when decisionFilter is set to APPROVED', () => {
    render(
      <BuyerLineSection
        kind="WRONG"
        decisionFilter="APPROVED"
        detail={detail({
          hasWrongDevice: true,
          systemStatus: 'APPROVED',
          wrongLines: [
            {
              id: 1,
              expectedBarcode: 'BC-PASS',
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
              expectedBarcode: 'BC-FAIL',
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

    expect(screen.getByText('BC-PASS')).toBeInTheDocument();
    expect(screen.queryByText('BC-FAIL')).not.toBeInTheDocument();
  });
});
