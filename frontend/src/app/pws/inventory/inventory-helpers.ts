export interface DeviceItem {
  id: number;
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  availableQty: number;
  atpQty: number;
  listPrice: number;
  description: string;
}

export interface CaseLotItem {
  id: number;
  deviceId: number;
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  caseLotSize: number;
  caseLotAtpQty: number;
  unitPrice: number;
  caseLotPrice: number;
}

export interface ColumnFilters {
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  price: string;
  qty: string;
}

export const emptyFilters: ColumnFilters = {
  sku: '', category: '', brand: '', model: '',
  carrier: '', capacity: '', color: '', grade: '',
  price: '', qty: '',
};

export type SortField = string | null;
export type SortDir = 'asc' | 'desc';
export type TabId = 'functional' | 'caseLots';

export function parseDevices(raw: unknown[]): DeviceItem[] {
  return (raw || []).map((el) => {
    const d = el as Record<string, unknown>;
    return {
      id: d.id as number,
      sku: (d.sku as string) || '',
      category: (d.categoryName as string) || '',
      brand: (d.brandName as string) || '',
      model: (d.modelName as string) || '',
      carrier: (d.carrierName as string) || '',
      capacity: (d.capacityName as string) || '',
      color: (d.colorName as string) || '',
      grade: (d.gradeName as string) || '',
      availableQty: (d.availableQty as number) || 0,
      atpQty: (d.atpQty as number) || 0,
      listPrice: (d.listPrice as number) || 0,
      description: (d.description as string) || '',
    };
  });
}

export function parseCaseLots(raw: unknown[]): CaseLotItem[] {
  return (raw || []).map((el) => {
    const c = el as Record<string, unknown>;
    return {
      id: c.id as number,
      deviceId: (c.deviceId as number) || 0,
      sku: (c.sku as string) || '',
      category: (c.categoryName as string) || '',
      brand: (c.brandName as string) || '',
      model: (c.modelName as string) || '',
      carrier: (c.carrierName as string) || '',
      capacity: (c.capacityName as string) || '',
      color: (c.colorName as string) || '',
      grade: (c.gradeName as string) || '',
      caseLotSize: (c.caseLotSize as number) || 0,
      caseLotAtpQty: (c.caseLotAtpQty as number) || 0,
      unitPrice: (c.unitPrice as number) || 0,
      caseLotPrice: (c.caseLotPrice as number) || 0,
    };
  });
}

export function distinct(items: string[]): string[] {
  return Array.from(new Set(items.filter(Boolean))).sort();
}

export function matchesSearch(item: DeviceItem, tokens: string[]): boolean {
  if (tokens.length === 0) return true;
  const searchStr = `${item.sku} ${item.category} ${item.brand} ${item.model} ${item.carrier} ${item.capacity} ${item.color} ${item.grade} ${item.description}`.toLowerCase();
  return tokens.every((t) => searchStr.includes(t.toLowerCase()));
}

export function matchesCLSearch(item: CaseLotItem, tokens: string[]): boolean {
  if (tokens.length === 0) return true;
  const searchStr = `${item.sku} ${item.category} ${item.brand} ${item.model} ${item.carrier} ${item.capacity} ${item.color} ${item.grade}`.toLowerCase();
  return tokens.every((t) => searchStr.includes(t.toLowerCase()));
}
