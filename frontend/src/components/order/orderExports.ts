import { apiFetch } from '@/lib/apiFetch';
import { getSelectedBuyerCode } from '@/lib/session';
import { API_BASE } from '@/lib/apiRoutes';
import type { DeviceItem } from './InventoryGrid';
import type { CaseLotItem } from './CaseLotsGrid';

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

function todayStamp(compact = false): string {
  const iso = new Date().toISOString().slice(0, 10);
  return compact ? iso.replace(/-/g, '') : iso;
}

export function pickAndUploadOfferExcel(authParams: string, onSuccess: () => void): void {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.xlsx,.xls,.csv';
  input.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart/import?${authParams}`, {
        method: 'POST',
        body: formData,
      });
      if (res.ok) {
        onSuccess();
      } else {
        console.error('Upload failed:', res.status);
      }
    } catch (err) {
      console.error('Upload failed', err);
    }
  };
  input.click();
}

export async function exportOrderData(authParams: string): Promise<void> {
  try {
    const res = await apiFetch(`${API_BASE}/pws/offers/cart/export?${authParams}`);
    if (!res.ok) throw new Error('Export failed');
    const blob = await res.blob();
    const bcCode = getSelectedBuyerCode()?.code ?? null;
    downloadBlob(blob, `${bcCode || 'order'}_${todayStamp(true)}.csv`);
  } catch (err) {
    console.error('Export failed', err);
  }
}

export function downloadFilteredListing(
  activeTab: string,
  filteredDevices: DeviceItem[],
  filteredCaseLots: CaseLotItem[],
): void {
  let csv = '';
  if (activeTab === 'caselots') {
    csv = 'SKU,Model Family,Carrier,Capacity,Color,Grade,Case Pack Qty,Avl. Cases,Unit Price,Case Price\n';
    for (const cl of filteredCaseLots) {
      csv += [cl.sku, cl.model, cl.carrier, cl.capacity, cl.color, cl.grade,
              cl.caseLotSize, cl.caseLotAtpQty, cl.unitPrice, cl.caseLotPrice].join(',') + '\n';
    }
  } else {
    csv = 'SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Avl. Qty,Price\n';
    for (const d of filteredDevices) {
      csv += [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade,
              d.availableQty, d.listPrice].join(',') + '\n';
    }
  }
  downloadBlob(new Blob([csv], { type: 'text/csv' }), `listing_${activeTab}_${todayStamp()}.csv`);
}

export function exportAllListing(devices: DeviceItem[]): void {
  let csv = 'SKU,Category,Brand,Model,Carrier,Capacity,Color,Grade,Avl. Qty,Price\n';
  for (const d of devices) {
    if (d.availableQty <= 0) continue;
    csv += [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade,
            d.availableQty, d.listPrice].join(',') + '\n';
  }
  const bcCode = getSelectedBuyerCode()?.code ?? null;
  downloadBlob(new Blob([csv], { type: 'text/csv' }), `${bcCode || 'all'}_listing_${todayStamp()}.csv`);
}
