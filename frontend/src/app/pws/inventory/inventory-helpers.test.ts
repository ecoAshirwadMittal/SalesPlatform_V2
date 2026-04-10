import { describe, it, expect } from 'vitest';
import {
  parseDevices,
  parseCaseLots,
  distinct,
  matchesSearch,
  matchesCLSearch,
  type DeviceItem,
  type CaseLotItem,
} from './inventory-helpers';

// ── parseDevices ──

describe('parseDevices', () => {
  it('maps API response fields to DeviceItem shape', () => {
    const raw = [
      {
        id: 1,
        sku: 'PWS000000100001',
        categoryName: 'Phone',
        brandName: 'Apple',
        modelName: 'iPhone 14',
        carrierName: 'Verizon',
        capacityName: '128GB',
        colorName: 'Black',
        gradeName: 'A',
        availableQty: 10,
        atpQty: 8,
        listPrice: 399.99,
        description: 'Test device',
      },
    ];

    const result = parseDevices(raw);

    expect(result).toHaveLength(1);
    expect(result[0]).toEqual({
      id: 1,
      sku: 'PWS000000100001',
      category: 'Phone',
      brand: 'Apple',
      model: 'iPhone 14',
      carrier: 'Verizon',
      capacity: '128GB',
      color: 'Black',
      grade: 'A',
      availableQty: 10,
      atpQty: 8,
      listPrice: 399.99,
      description: 'Test device',
    });
  });

  it('handles null/missing fields with defaults', () => {
    const raw = [{ id: 42 }];
    const result = parseDevices(raw);

    expect(result[0].id).toBe(42);
    expect(result[0].sku).toBe('');
    expect(result[0].category).toBe('');
    expect(result[0].brand).toBe('');
    expect(result[0].model).toBe('');
    expect(result[0].carrier).toBe('');
    expect(result[0].capacity).toBe('');
    expect(result[0].color).toBe('');
    expect(result[0].grade).toBe('');
    expect(result[0].availableQty).toBe(0);
    expect(result[0].atpQty).toBe(0);
    expect(result[0].listPrice).toBe(0);
    expect(result[0].description).toBe('');
  });

  it('returns empty array for empty input', () => {
    expect(parseDevices([])).toEqual([]);
  });

  it('maps multiple items', () => {
    const raw = [
      { id: 1, sku: 'SKU1' },
      { id: 2, sku: 'SKU2' },
      { id: 3, sku: 'SKU3' },
    ];
    const result = parseDevices(raw);
    expect(result).toHaveLength(3);
    expect(result.map((d) => d.sku)).toEqual(['SKU1', 'SKU2', 'SKU3']);
  });
});

// ── parseCaseLots ──

describe('parseCaseLots', () => {
  it('maps API response fields to CaseLotItem shape', () => {
    const raw = [
      {
        id: 10,
        deviceId: 5,
        sku: 'PWS000000100001',
        categoryName: 'Phone',
        brandName: 'Samsung',
        modelName: 'Galaxy S24',
        carrierName: 'T-Mobile',
        capacityName: '256GB',
        colorName: 'White',
        gradeName: 'B',
        caseLotSize: 25,
        caseLotAtpQty: 3,
        unitPrice: 250.0,
        caseLotPrice: 6250.0,
      },
    ];

    const result = parseCaseLots(raw);

    expect(result).toHaveLength(1);
    expect(result[0]).toEqual({
      id: 10,
      deviceId: 5,
      sku: 'PWS000000100001',
      category: 'Phone',
      brand: 'Samsung',
      model: 'Galaxy S24',
      carrier: 'T-Mobile',
      capacity: '256GB',
      color: 'White',
      grade: 'B',
      caseLotSize: 25,
      caseLotAtpQty: 3,
      unitPrice: 250.0,
      caseLotPrice: 6250.0,
    });
  });

  it('handles null/missing fields with defaults', () => {
    const raw = [{ id: 99 }];
    const result = parseCaseLots(raw);

    expect(result[0].id).toBe(99);
    expect(result[0].deviceId).toBe(0);
    expect(result[0].sku).toBe('');
    expect(result[0].caseLotSize).toBe(0);
    expect(result[0].caseLotAtpQty).toBe(0);
    expect(result[0].unitPrice).toBe(0);
    expect(result[0].caseLotPrice).toBe(0);
  });

  it('returns empty array for empty input', () => {
    expect(parseCaseLots([])).toEqual([]);
  });
});

// ── distinct ──

describe('distinct', () => {
  it('returns unique sorted values', () => {
    expect(distinct(['Banana', 'Apple', 'Cherry', 'Apple', 'Banana'])).toEqual([
      'Apple',
      'Banana',
      'Cherry',
    ]);
  });

  it('filters out empty strings and falsy values', () => {
    expect(distinct(['A', '', 'B', '', 'A'])).toEqual(['A', 'B']);
  });

  it('returns empty array for empty input', () => {
    expect(distinct([])).toEqual([]);
  });

  it('returns single item for all-same input', () => {
    expect(distinct(['X', 'X', 'X'])).toEqual(['X']);
  });

  it('sorts case-sensitively', () => {
    const result = distinct(['banana', 'Apple', 'cherry']);
    expect(result).toEqual(['Apple', 'banana', 'cherry']);
  });
});

// ── matchesSearch ──

describe('matchesSearch', () => {
  const device: DeviceItem = {
    id: 1,
    sku: 'PWS000000100297',
    category: 'Phone',
    brand: 'Apple',
    model: 'iPhone 14 Pro',
    carrier: 'Verizon',
    capacity: '256GB',
    color: 'Gold',
    grade: 'A',
    availableQty: 5,
    atpQty: 3,
    listPrice: 499.99,
    description: 'Refurbished smartphone',
  };

  it('returns true when no tokens (empty search)', () => {
    expect(matchesSearch(device, [])).toBe(true);
  });

  it('matches single token case-insensitively', () => {
    expect(matchesSearch(device, ['apple'])).toBe(true);
    expect(matchesSearch(device, ['APPLE'])).toBe(true);
    expect(matchesSearch(device, ['iphone'])).toBe(true);
  });

  it('matches multiple tokens (AND logic)', () => {
    expect(matchesSearch(device, ['apple', 'iphone', '256gb'])).toBe(true);
  });

  it('fails when any token does not match', () => {
    expect(matchesSearch(device, ['apple', 'samsung'])).toBe(false);
  });

  it('matches against description field', () => {
    expect(matchesSearch(device, ['refurbished'])).toBe(true);
  });

  it('matches against SKU', () => {
    expect(matchesSearch(device, ['pws000000100297'])).toBe(true);
  });

  it('returns false for completely unrelated token', () => {
    expect(matchesSearch(device, ['nonexistent'])).toBe(false);
  });
});

// ── matchesCLSearch ──

describe('matchesCLSearch', () => {
  const caseLot: CaseLotItem = {
    id: 10,
    deviceId: 5,
    sku: 'PWS000000100001',
    category: 'Tablet',
    brand: 'Samsung',
    model: 'Galaxy Tab S9',
    carrier: 'Unlocked',
    capacity: '128GB',
    color: 'Gray',
    grade: 'B',
    caseLotSize: 20,
    caseLotAtpQty: 2,
    unitPrice: 300,
    caseLotPrice: 6000,
  };

  it('returns true when no tokens (empty search)', () => {
    expect(matchesCLSearch(caseLot, [])).toBe(true);
  });

  it('matches single token case-insensitively', () => {
    expect(matchesCLSearch(caseLot, ['samsung'])).toBe(true);
    expect(matchesCLSearch(caseLot, ['tablet'])).toBe(true);
  });

  it('matches multiple tokens (AND logic)', () => {
    expect(matchesCLSearch(caseLot, ['samsung', 'galaxy', '128gb'])).toBe(true);
  });

  it('fails when any token does not match', () => {
    expect(matchesCLSearch(caseLot, ['samsung', 'apple'])).toBe(false);
  });

  it('matches against SKU', () => {
    expect(matchesCLSearch(caseLot, ['pws000000100001'])).toBe(true);
  });
});
